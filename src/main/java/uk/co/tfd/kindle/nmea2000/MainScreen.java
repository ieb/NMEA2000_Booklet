package uk.co.tfd.kindle.nmea2000;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class MainScreen {

    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);

    public static class Theme {
        private final Color foreground;
        private final Color background;
        private final Color controlForeground;

        public Theme( Color foreground, Color background, Color controlForeground) {

            this.foreground = foreground;
            this.background = background;
            this.controlForeground = controlForeground;

        }

        public Color getBackground() {
            return background;
        }

        public Color getControlForeground() {
            return controlForeground;
        }

        public Color getForeground() {
            return foreground;
        }
    }

    private static Theme[] THEMES = {
            new Theme(Color.BLACK, Color.WHITE, Color.BLACK),
            new Theme(Color.WHITE, Color.BLACK, Color.GRAY),
            new Theme(Color.RED, Color.BLACK, Color.GRAY),
            new Theme(new Color(33, 158, 121), Color.BLACK,Color.GRAY),
            new Theme(new Color(148, 146, 38), Color.BLACK,Color.GRAY)
    };
    private int NTHEMES = 5;
    private int theme;

    private final Calcs calcs;
    private final NMEA2000Client nmea2000Client;
    private final Store store;
    private final PageLayout layout;
    private final ControlPage controlPage;

    public interface MainScreenExit {

        void exit();
    }

    public MainScreen(Container root, String configFile,  MainScreenExit exitHook) throws IOException, NoSuchMethodException, ParseException {

        controlPage = new ControlPage(new ControlPage.ControlHook() {
            @Override
            public void invertColors() {
                theme = (theme+1)%NTHEMES;
                layout.setForeground(THEMES[theme].getForeground());
                layout.setBackground(THEMES[theme].getBackground());
                controlPage.setTheme(THEMES[theme]);
            }

            @Override
            public void exit() {
                MainScreen.this.stop();
                exitHook.exit();
            }
        });
        store = new Store();
        calcs = new Calcs(store);
        store.addStatusUpdateListener(controlPage);
        layout = new PageLayout(configFile, store, calcs);
        layout.addControl(controlPage);
        layout.setPreferredSize(root.getMaximumSize());



        String configSource = layout.loadConfig();
        controlPage.onStatusChange("Config from " + configSource);
        Map<String, Object> config = layout.getConfiguration();
        if ( !Util.isKindle() ) {
            Map<String, Object> screensize = (Map<String, Object>) config.get("screensize");
            if ( screensize != null) {
                root.setSize(Integer.valueOf(String.valueOf(screensize.get("w")))/2, Integer.valueOf(String.valueOf(screensize.get("h")))/2);
                log.info("Set Screensize to {} ", root.getSize());
            }
        }
        root.add(layout);
        root.doLayout();
        root.setVisible(true);

        calcs.addStatusUpdateListener(controlPage);

        NMEA2000HttpClient NMEA2000HttpClient =  new NMEA2000HttpClient(store);
        nmea2000Client = new NMEA2000Client(store, NMEA2000HttpClient, config);
        nmea2000Client.addStatusUpdateListener(controlPage);
        NMEA2000HttpClient.addStatusUpdateListener(controlPage);



    }

    public void stop() {
        calcs.stop();
        store.stop();
        nmea2000Client.stop();
     }


    public void start() throws IOException {
        log.info("Starting");
        calcs.start(); // No threads
        store.start(); // AWT Timer
        nmea2000Client.start();
        log.info("Started");
    }
}
