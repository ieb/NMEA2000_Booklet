package uk.co.tfd.kindle.nmea2000;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;
import uk.co.tfd.kindle.nmea2000.canwidgets.CanPageLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public class MainScreen {

    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);
    private final NMEA0183Client nmea0183Client;
    private final SeaSmartHandler seaSmartHandler;
    private final NMEA0183Discovery discovery;
    private final Timer timer;
    private final CanMessageProducer messageProducer;
    private String lastCommandMessage = "";

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

    private final CanPageLayout layout;
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
                if ( timer != null) {
                    timer.stop();
                }
                if ( discovery != null) {
                    discovery.endDiscovery();
                }
                MainScreen.this.stop();
                exitHook.exit();
            }
        });

        /*
        store = new Store();
        calcs = new Calcs(store);
         */
        nmea0183Client = new NMEA0183Client();
        messageProducer = new CanMessageProducer();
        seaSmartHandler = new SeaSmartHandler(messageProducer);

        nmea0183Client.addHandler("DIN", seaSmartHandler);
        seaSmartHandler.addHandler(new EngineMessageHandler());
        seaSmartHandler.addHandler(new IsoMessageHandler());
        seaSmartHandler.addHandler(new NavMessageHandler());
        seaSmartHandler.addIgnore(59904); // Request Address
        seaSmartHandler.addIgnore(126720); // Proprietary raymarine
        seaSmartHandler.addIgnore(126208); // Function Group Handler
        seaSmartHandler.addIgnore(65379); //  Seatalk Pilot mode
        seaSmartHandler.addIgnore(127237); // Heading Track control
        seaSmartHandler.addIgnore(130916); // Proprietary b;
        seaSmartHandler.addIgnore(129044); // Datum
        seaSmartHandler.addIgnore(65384); // Proprietary
        seaSmartHandler.addIgnore(65359); // Seatalk Pilot heading
        seaSmartHandler.addStatusUpdateListener(controlPage);
        nmea0183Client.addStatusUpdateListener(controlPage);

        // create calculators, and add them as listeners to the producers
        // calculators create internal can messages derived from the messages received.
        // generally the calculations are cheap relative to the network cost so at the moment
        // they are not enabled or disabled, however they could be in the same
        // way that traffic is enabled and disabled.
        messageProducer.addListener(new WindCalculator(messageProducer));


        discovery = new NMEA0183Discovery(nmea0183Client);
        discovery.startDiscovery();


        layout = new CanPageLayout(configFile, messageProducer);
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
        layout.addControl(controlPage);
        root.add(layout);
        root.doLayout();
        root.setVisible(true);

        /*
        calcs.addStatusUpdateListener(controlPage);

        NMEA2000HttpClient nmea2000HttpClient =  new NMEA2000HttpClient(store);
        nmea2000Client = new NMEA2000Client(store, nmea2000HttpClient, config);
        nmea2000Client.addStatusUpdateListener(controlPage);
        nmea2000HttpClient.addStatusUpdateListener(controlPage);
         */



        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String commandMessage = seaSmartHandler.getCommandMessage();
                if ( !lastCommandMessage.equals(commandMessage)) {

                    try {
                        nmea0183Client.send(nmea0183Client.addCheckSum(commandMessage));
                    } catch (IOException e) {
                        log.info("Failed to send nmea0183 command {}", e);
                    }
                    lastCommandMessage = commandMessage;
                }
                seaSmartHandler.emitStatus();
            }
        });
        timer.start();





    }

    public void stop() {
        nmea0183Client.stop();
        nmea0183Client.setAddress(null);
        nmea0183Client.setPort(-1);
     }


    public void start(InetAddress address, int port) throws IOException {
        log.info("Starting");
        nmea0183Client.setAddress(address);
        nmea0183Client.setPort(port);
        nmea0183Client.start();
        log.info("Started");
    }


}
