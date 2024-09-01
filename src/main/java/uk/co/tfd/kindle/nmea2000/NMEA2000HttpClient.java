package uk.co.tfd.kindle.nmea2000;

import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by ieb on 14/03/2021.
 */
public class NMEA2000HttpClient extends StatusUpdates  {

    private static final Logger log = LoggerFactory.getLogger(NMEA2000HttpClient.class);


    private final Store store;
    private Set<String> rejectedPaths = new HashSet<String>();

    public NMEA2000HttpClient(Store store) {
        this.store = store;

    }

    private long findRequestEndTime(Object o, long t) {
        if ( o instanceof Map ) {
            Map<String, Object> m = (Map<String, Object>) o;
            if (m.containsKey("t")) {
                t = Math.max(t, (long) m.get("t"));
            }
            for (Map.Entry<String, Object> e : m.entrySet()) {
                t = Math.max(t, findRequestEndTime(e.getValue(), t));
            }
        } else if ( o instanceof  List) {
            for ( Object v : (List)o) {
                t = Math.max(t, findRequestEndTime(v, t));
            }
        }
        return t;
    }

    public boolean fetch(String url, String section) {
        BufferedReader in = null;
        Map<String, Object> update = new HashMap<>();
        try {
            // fetch the data from the ESP server API.
            if ( url.startsWith("data:") ) {
                CSVParser parser = new CSVParser();
                in = new BufferedReader(new StringReader(url.substring("data:".length())));
                update.put("candiag", parser.parseCSV(in));
            } else {
                if (url.startsWith("http")) {
                    URL u = new URL(url + "/api/data/" + section);
                    in = new BufferedReader(new InputStreamReader(u.openStream()));
                } else if (url.startsWith("file:")) {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(url.substring("file:".length()) + section)));
                    //"/Users/ieb/timefields/PlatformIO/Projects/CanDiagnose/ui/einkweb/src/api.json"));
                } else {
                    return false;
                }
                if (section.endsWith(".json")) {
                    JSONParser parser = new JSONParser();
                    //updateStatus("Fetched state from "+url);
                    //log.info("Fetched state from {} ", url);
                    update.put("candiag", parser.parse(in));
                } else {
                    CSVParser parser = new CSVParser();
                    update.put("candiag", parser.parseCSV(in));
                }
            }

            long timeOffset = System.currentTimeMillis() - findRequestEndTime(update, 0);

            Map<String, Object> rejects = store.update("", update, timeOffset);
            for(String e : rejects.keySet()) {
                if ( !rejectedPaths.contains(e)) {
                    rejectedPaths.add(e);
                }
            }
            return true;
        } catch (Exception ex) {
            log.error("Fetch from {} failed with {} ", url, update, ex);
            updateStatus("Fetch from "+url+" failed with "+ex.getMessage());
            return false;
        } finally {
            if ( in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    log.error("Error Closing Stream {} ", e1.getMessage());
                }
            }
        }
    }





}
