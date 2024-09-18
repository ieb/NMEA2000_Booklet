package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 19/06/2020.
 */
public class NMEA2000Client extends StatusUpdates  {
    private static final Logger log = LoggerFactory.getLogger(NMEA2000Client.class);
    private final NMEA2000Discovery discovery;
    private List<CanDiagnoseServer> servers;
    private final NMEA2000HttpClient httpClient;
    private int serverNo = -1;
    private final Store store;
    private boolean running;
    private long nextFetch = 0;
    private Thread thread;
    private Object waitForServers = new Object();
    private boolean httpOnly = true;

    public NMEA2000Client(Store store, NMEA2000HttpClient httpClient, Map<String, Object> config) {
        this.httpClient = httpClient;
        this.store = store;
        this.discovery = new NMEA2000Discovery(this);
        this.servers = new ArrayList<>();
        if (config.containsKey("httpOnly")) {
            httpOnly = (boolean) config.get("httpOnly");
        } else {
            // with secure enabled I dont see any updates over tcp, so have to poll on http.
            httpOnly = true;
        }
        List<Map<String, Object>> configServers = (List<Map<String, Object>>) config.get("servers");
        if (configServers != null) {
            for(Map<String, Object> configServer : configServers) {
                servers.add(new CanDiagnoseServer(configServer));
            }
        }

    }

    public void removeServer(String id) {
        synchronized (waitForServers) {
            for(int i = 0; i < servers.size(); i++) {
                CanDiagnoseServer server = servers.get(i);
                if ( server.isId(id)) {
                    log.info("Remove {} ",id);
                    servers.remove(i);
                    return;
                }
            }
        }
    }

    public void addHttp(String id, String url) {
        synchronized (waitForServers) {
            for (CanDiagnoseServer server : servers) {
                if (id.equals(server.getId())) {
                    server.setUrl(url);
                    log.info("Update http {}  {} ", id, url);
                    if (server.isComplete()) {
                        log.info("Notify ");
                        waitForServers.notifyAll();
                    }
                    return;
                }
            }
            log.info("Adding http {}  {} ", id, url);
            CanDiagnoseServer server = new CanDiagnoseServer(id, url, servers.get(0));
            servers.add(server);
        }

    }




    private CanDiagnoseServer getServer() {
        synchronized (waitForServers) {

            for (int i = 0; i < servers.size(); i++) {
                serverNo = (serverNo + 1) % servers.size();
                CanDiagnoseServer server = servers.get(serverNo);
                if (server.isAvailable()) {
                    return server;
                }
            }
            log.info("Waiting for servers to be discovered");
            try {
                waitForServers.wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




    public void start() {
        if ( thread == null ) {
            if ( servers.size() == 0 ) {
                try {
                    discovery.startDiscovery();
                    NMEA2000Client.this.updateStatus("Service Discovery started ");

                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("Starting");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("Running");
                    while (running) {
                        CanDiagnoseServer server = getServer();
                        if (server != null) {
                            try {
                                NMEA2000Client.this.updateStatus("Trying server " + server.getUrl());
                                log.info("Server {}  ", server);
                                connect(server);
                            } catch (Exception ex) {
                                log.error("Discovery Failed ", ex);
                                log.info("Sleep 5s");
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    log.info("Not Running");
                }
            });
            running = true;
            thread.start();
        }
    }

    public void stop() {
        discovery.endDiscovery();
        NMEA2000Client.this.updateStatus("Service Discovery stopped ");
        log.info("Stopping");
        running = false;
        if ( thread != null ) {
            thread.interrupt();
        }
        log.info("Stoped");
    }

    private void connect(CanDiagnoseServer server) {
        if (!fetchState(server.getUrl(), server.getSection())) {
            NMEA2000Client.this.updateStatus(" No server at {} " + server);
            server.failed();
            return;
        };
        NMEA2000Client.this.updateStatus("Server found {} " + server);
        while (running && httpClient.fetch(server.getUrl(), server.getSection())) {
            server.next();
            try {
                long delay = server.delay();
                if ( delay > 0 ) {
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                log.debug(e.getMessage(), e);
            }
        }
        NMEA2000Client.this.updateStatus("Server Lost {} " + server);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.debug(e.getMessage(), e);
        }
    }

    private boolean fetchState(String url, String section) {
        if (!running) {
            return false;
        }
        if ( url != null) {
            if (nextFetch < System.currentTimeMillis()) {
                nextFetch = System.currentTimeMillis()+30000;
                return httpClient.fetch(url, section);
            }
        }
        return true;
    }


    private class CanDiagnoseServer {
        private Map<String,  Map<String,Object>> sections;
        private String id;
        private String url = null;
        private long lastFail = -1;
        private String currentSection;
        private long nextDeadline;


        public CanDiagnoseServer(Map<String, Object> configServer) {
            url = (String) configServer.get("url");
            id = (String) configServer.get("id");
            sections = (Map<String, Map<String,Object>>) configServer.get("sections");
            init();
        }

        public CanDiagnoseServer(String id, String url, CanDiagnoseServer from) {
            this.id = id;
            this.url = url;
            sections = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> e : from.sections.entrySet()) {
                Map<String, Object> section = new HashMap<>();
                section.put("ttl", e.getValue().get("section"));
                sections.put(e.getKey(), section);
            }
            init();
        }


        public boolean isId(String id) {
            return this.id.equals(id);
        }

        public String getId() {
            return id;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        private boolean isFile() {
            return ( url != null && url.startsWith("file:"));
        }

        public boolean isComplete() {
            return isFile() || (url != null);

        }


        public boolean isAvailable() {
            return isFile() || ( isComplete() && lastFail+30000L < System.currentTimeMillis());
        }



        public String getUrl() {
            return url;
        }

        public void failed() {
            lastFail = System.currentTimeMillis();
        }

        public String getSection() {
            return currentSection;
        }

        public long delay() {
            return nextDeadline - System.currentTimeMillis();
        }

        public void init() {
            nextDeadline = System.currentTimeMillis()+600000;
            for (Map.Entry<String, Map<String,Object>> e : sections.entrySet()) {
                long ttl = (long) e.getValue().get("ttl");
                e.getValue().put("deadline", System.currentTimeMillis() + ttl);
                long dl = (long) e.getValue().get("deadline");
                if ( dl < nextDeadline ) {
                    currentSection = e.getKey();
                    nextDeadline = dl;
                }
            }

        }

        public void next() {
            Map<String, Object> section = sections.get(currentSection);
            long ttl = (long) section.get("ttl");
            section.put("deadline", System.currentTimeMillis() + ttl);
            nextDeadline = (long) section.get("deadline");
            long now = System.currentTimeMillis();
            for (Map.Entry<String, Map<String,Object>> e : sections.entrySet()) {
                long dl = (long) e.getValue().get("deadline");
                if ( dl < nextDeadline ) {
                    currentSection = e.getKey();
                    nextDeadline = dl;
                }
            }
        }
    }
}