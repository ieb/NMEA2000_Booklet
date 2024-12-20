package uk.co.tfd.kindle.nmea2000;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private final Map<String, Object> configuration;
    private final String configName;

    public  Configuration(String configFile) throws IOException, ParseException {
        File f = new File(configFile);
        JSONParser jsonParser = new JSONParser();
        Map<String, Object> configMap = null;
        String name = "none";
        if ( f.exists() )  {
            name = f.getAbsolutePath();
            log.info("Loading config file {} ", f.getAbsolutePath());
            FileReader config = new FileReader(f);
            try {
                configMap = (Map<String, Object>) jsonParser.parse(config);
            } catch( Throwable t) {
                log.info("Supplied configuration failed, loading defaults ");
            }
            config.close();
        }
        if (configMap == null) {
            name = "defaults";
            log.info("Loading defaults ");
            InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getResourceAsStream("/defaultconfig.json"));
            configMap = (Map<String, Object>) jsonParser.parse(inputStreamReader);
            inputStreamReader.close();
        }
        configuration = configMap;
        configName = name;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public Dimension getScreenSize() {
        Map<String, Object> screensize = (Map<String, Object>) configuration.get("screensize");
        if ( screensize == null ) {
            return null;
        }
        return new Dimension(Integer.parseInt(String.valueOf(screensize.get("w"))),
                    Integer.parseInt(String.valueOf(screensize.get("h"))));
    }

    public String getConfigName() {
        return configName;
    }

    public String[] getEndpoints() {
        JSONArray useEndpoints = (JSONArray) configuration.get("endpoints");
        if (useEndpoints == null) {
            return new String[] {
                    "boatsystems.local:10110",
                    "boatsystems.local:10112",
                    "localhost:10110",
                    "localhost:10112"
            };
        }
        String[] endpoints = new String[useEndpoints.size()];
        for (int i = 0; i < endpoints.length; i++) {
            endpoints[i] = (String) useEndpoints.get(i);
        }
        return endpoints;
    }
}
