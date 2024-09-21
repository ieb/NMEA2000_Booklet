package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by ieb on 19/06/2020.
 */
public class NMEA0183Discovery  extends StatusUpdates {
    private static final Logger log = LoggerFactory.getLogger(NMEA0183Discovery.class);
    public static final String CAN_TCP_TCP_LOCAL = "_can-tcp._tcp.local.";
    private final NMEA0183Client client;
    private JmDNS jmdns;
    private ServiceListener tcpListener;

    public NMEA0183Discovery(NMEA0183Client client) {
        this.client = client;
    }

    public void startDiscovery() throws IOException {
        InetAddress in = null;

        // find the lowest numbered ingerface that supports multicast and is up.
        // this is on the basis that default interfaces will be first.
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        interfaces.sort(new Comparator<NetworkInterface>() {
            @Override
            public int compare(NetworkInterface o1, NetworkInterface o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        for (NetworkInterface intf : interfaces) {
            log.debug("Interface {} ", intf);
            if (intf.isUp() && intf.supportsMulticast() && !intf.isLoopback()) {
                String name = intf.getName();
                // only use names that are expected to be connected to a valid network.
                if (name.startsWith("en") || name.startsWith("wlan") || name.startsWith("eth")) {
                    for (InterfaceAddress addr : intf.getInterfaceAddresses()) {
                        InetAddress inaddr = addr.getAddress();
                        if (inaddr instanceof Inet4Address) {
                            in = inaddr;
                            updateStatus("Discovering CanTCP Server on "+in+" "+name);
                            log.info("Discovering CanTCP Server on {} {} ", in, name);
                            break;
                        }
                    }
                }
                if (in != null) {
                    break;
                }
            }

        }
        if (in == null) {
            in = InetAddress.getLocalHost();
            updateStatus("Discovering CanTCP Server on "+in);
            log.info("Discovering CanTCP Server default interface {} ",in);
        }

        jmdns = JmDNS.create(in);

        /*
        tcpListener = new ServiceListener() {

            @Override
            public void serviceAdded(ServiceEvent serviceEvent) {

            }

            @Override
            public void serviceRemoved(ServiceEvent serviceEvent) {
                log.info("Remove Http Info {} ", serviceEvent.getInfo());
                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                client.removeServer(host);
            }

            @Override
            public void serviceResolved(ServiceEvent serviceEvent) {
                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                client.addTcp(host, host, info.getPort());
                log.debug("TCP Info {} ", info);
            }
        };
        */
        tcpListener = new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent serviceEvent) {

            }

            @Override
            public void serviceRemoved(ServiceEvent serviceEvent) {

                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];

                int port = info.getPort();

                log.debug("Remove CanTcp Info {} ", info);
                client.stop();
                client.setAddress(null);
                client.setPort(-1);

            }

            @Override
            public void serviceResolved(ServiceEvent serviceEvent) {
                ServiceInfo info = serviceEvent.getInfo();
                InetAddress host = info.getInetAddresses()[0];
                int port = info.getPort();
                updateStatus("Server resolved at  "+host+":"+port);
                client.stop();
                client.setAddress(host);
                client.setPort(port);;
                client.start();
                log.info("Resolved CanTCP Info {} ", info);
            }
        };

        jmdns.addServiceListener(CAN_TCP_TCP_LOCAL, tcpListener);
    }

    public void endDiscovery() {
        if ( jmdns != null ) {
            jmdns.removeServiceListener(CAN_TCP_TCP_LOCAL, tcpListener);
            try {
                jmdns.close();
                jmdns = null;
            } catch (IOException e) {
                log.error("Failed to end discovery", e);
            }
        }
    }



}