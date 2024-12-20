package uk.co.tfd.kindle.nmea2000;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;
import uk.co.tfd.kindle.nmea2000.canwidgets.CanPageLayout;
import uk.co.tfd.kindle.nmea2000.canwidgets.PolarPage;
import uk.co.tfd.kindle.nmea2000.canwidgets.WidgetComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by ieb on 20/06/2020.
 */
public class MainScreen {

    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);
    private final NMEA0183Client nmea0183Client;
    private final SeaSmartHandler seaSmartHandler;
    private final Timer timer;
    private final CanMessageProducer messageProducer;
    private final String simulatorMode = "normal";
    private String lastCommandMessage = "";
    private int currentEndpoint = 0;
    private String[] endpoints = null;

    public static class Theme {
        private final Color foreground;
        private final Color background;
        private final Color controlForeground;
        private final Color controlBackground;

        public Theme( Color foreground, Color background, Color controlForeground
                , Color controlBackground) {

            this.foreground = foreground;
            this.background = background;
            this.controlForeground = controlForeground;
            this.controlBackground = controlBackground;

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

        public Color getControlBackground() { return controlBackground;
        }
    }

    private static Theme[] THEMES = {
            new Theme(Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE),
            new Theme(Color.WHITE, Color.BLACK, Color.WHITE, Color.DARK_GRAY),
            new Theme(Color.RED, Color.BLACK, Color.RED, Color.DARK_GRAY),
            new Theme(new Color(33, 158, 121), Color.BLACK,new Color(33, 158, 121), Color.DARK_GRAY),
            new Theme(new Color(148, 146, 38), Color.BLACK,new Color(148, 146, 38), Color.DARK_GRAY)
    };
    private int NTHEMES = 5;
    private int theme;

    private final CanPageLayout mainPanel;
    private final ControlPage controlPage;

    public interface MainScreenExit {

        void exit();
    }

    public MainScreen(Container root, String configFile,  MainScreenExit exitHook) throws IOException, NoSuchMethodException, ParseException {


        controlPage = new ControlPage(new ControlPage.ControlHook() {
            @Override
            public void invertColors() {
                theme = (theme+1)%NTHEMES;
                mainPanel.setForeground(THEMES[theme].getForeground());
                mainPanel.setBackground(THEMES[theme].getBackground());
                controlPage.setTheme(THEMES[theme]);
            }

            @Override
            public void exit() {

                if ( timer != null) {
                    timer.stop();
                }
                MainScreen.this.stop();
                exitHook.exit();
            }
        });

        messageProducer = new CanMessageProducer();
        mainPanel = new CanPageLayout(messageProducer, controlPage);
        mainPanel.setPreferredSize(root.getMaximumSize());
        mainPanel.setForeground(THEMES[theme].getForeground());
        mainPanel.setBackground(THEMES[theme].getBackground());
        controlPage.setTheme(THEMES[theme]);



        Configuration config = new Configuration(configFile);
        if ( !Util.isKindle() ) {
            Dimension d = config.getScreenSize();
            if ( d != null) {
                root.setSize(d.width/2, d.height/2);
                log.info("Set Screensize to {} ", root.getSize());
            }
        }

        endpoints = config.getEndpoints();

        controlPage.onStatusChange("Config from " + config.getConfigName());




        // now setup components etc.


        nmea0183Client = new NMEA0183Client();
        seaSmartHandler = new SeaSmartHandler(messageProducer);

        nmea0183Client.addHandler("DIN", seaSmartHandler);
        seaSmartHandler.addHandler(new EngineMessageHandler());
        seaSmartHandler.addHandler(new IsoMessageHandler());
        seaSmartHandler.addHandler(new NavMessageHandler());
        seaSmartHandler.addHandler(new ElectricalMessageHandler());
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
        
        mainPanel.loadConfiguration(config);

        WidgetComponentListener listener = new WidgetComponentListener(messageProducer);
        controlPage.addAncestorListener(listener);
        Polar polar = new Polar(config.getConfiguration());





        if ( "manual".equals(simulatorMode) ) {
            // drive with a manual simulator
            PolarPage polarPage = new PolarPage(false);
            ManualPerformanceSimulator simulator = new ManualPerformanceSimulator(polar);
            simulator.addListener(polarPage);
            root.addKeyListener(simulator);
            nmea0183Client.disable();
            polarPage.addAncestorListener(listener);
            root.add(polarPage);
        } else if ( "random".equals(simulatorMode) ) {
            PolarPage polarPage = new PolarPage(false);
            Simulator simulator = new Simulator(messageProducer, polar);
            simulator.start();
            nmea0183Client.disable();
            polarPage.addAncestorListener(listener);
            root.add(polarPage);
        } else {
            messageProducer.addListener(new WindCalculator(messageProducer));
            messageProducer.addListener(new PerformanceCalculator(messageProducer, polar));
        }
        // set the theme on the main pannel again, so that all the components
        // get the current theme
        mainPanel.setForeground(THEMES[theme].getForeground());
        mainPanel.setBackground(THEMES[theme].getBackground());


        mainPanel.showPage("control");
        // the CanPageLayout layout must be set visible before it will show.
        // failing to do this leads to a blank screen on the kindle, but on OSX no problem.
        mainPanel.setVisible(true);
        root.add(mainPanel);
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
                controlPage.updateConnectionMessage(nmea0183Client.getStatusMessage()+" "+seaSmartHandler.getStatusMessage());
                if ( nmea0183Client.hasStalled()) {
                    nmea0183Client.stop();
                    if ( !nmea0183Client.start() ) {
                        nmea0183Client.stop();
                        MainScreen.this.currentEndpoint = (MainScreen.this.currentEndpoint+1)%MainScreen.this.endpoints.length;
                        MainScreen.this.configureEndpointAndStart();
                    }
                }
            }
        });
        timer.start();
    }

    public void stop() {
        nmea0183Client.stop();
        nmea0183Client.setSocketAddress(null);
     }

     public void restart() {
         nmea0183Client.stop();
         nmea0183Client.start();
     }


    public void start() {
        if ( !nmea0183Client.isRunning() ) {
            currentEndpoint = 0;
            for(int i = 0; i < endpoints.length; i++) {
                currentEndpoint = i;
                if (configureEndpointAndStart()) {
                    break;
                }

            }
        }
    }

    private boolean configureEndpointAndStart()  {
        String[] endpoint = endpoints[currentEndpoint].split(":");
        log.info("Starting on {}", endpoint);
        SocketAddress socketAddress = new InetSocketAddress(endpoint[0], Integer.parseInt(endpoint[1]));
        nmea0183Client.setSocketAddress(socketAddress);
        return nmea0183Client.start();
    }


}
