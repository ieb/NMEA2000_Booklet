package uk.co.tfd.kindle.nmea2000.can;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.NMEA0183Client;
import uk.co.tfd.kindle.nmea2000.SeaSmartHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileReaderTest {

    private static final Logger log = LoggerFactory.getLogger(EngineMessageHandlerTest.class);
    private NMEA0183Client nmea0183CLient;
    private SeaSmartHandler seaSmartHandler;

    private CanMessage lastMessage;

    private Set<Integer> dropped = new HashSet<>();
    private Set<Integer> unhandled = new HashSet<>();
    private HashMap<Integer, String> expectedUnhandled = new HashMap<Integer, String>();
    private CanMessageProducer canMessageProducer;

    @Before
    public void before() throws UnknownHostException {



        InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, 1, 21});
        nmea0183CLient = new NMEA0183Client(address, 10110);
        canMessageProducer = new CanMessageProducer();
        seaSmartHandler = new SeaSmartHandler(canMessageProducer);
        nmea0183CLient.addHandler("DIN", seaSmartHandler);
        seaSmartHandler.addHandler(new ElectricalMessageHandler());
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
        canMessageProducer.addListener(new CanMessageListener() {


            @Override
            public int[] getPgns() {
                return new int[]{-1};
            }

            @Override
            public void onDrop(int pgn) {
                dropped.add(pgn);
            }

            @Override
            public void onUnhandled(int pgn) {
                unhandled.add(pgn);
            }

            @Override
            public void onMessage(CanMessage message) {
                Assert.assertNotNull(message);
                lastMessage = message;
                log.info("Got Message {}", message);
            }
        });
    }



    @Test
    public void testPGN127489EngineDynamicParam() throws IOException {
        InputStream in = getClass().getResourceAsStream("/samplecandata.txt");
        Assert.assertNotNull("Cant find sample data", in);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while(true) {
            String line = br.readLine();
            if ( line == null) {
                break;
            }
            String pcdin = convertToPCDIN(line);
            if ( pcdin != null ) {
                nmea0183CLient.processLine(Utils.addCheckSum(pcdin));
            }
        }
        Assert.assertEquals(0, dropped.size());
        Assert.assertEquals(0, unhandled.size());
    }

    private String convertToPCDIN(String sampleLine) {
        int pgn = -1;
        int source = -1;
        String data = null;
        int timestamp = (int)(System.currentTimeMillis() & 0x7fffffff);
        // 228849 : Pri:2 PGN:127257 Source:204 Dest:255 Len:8 Data:FF,9C,8,C7,0,21,0,FF
        for ( String p : sampleLine.split(" ") ) {
            if (p.startsWith("PGN:")) {
                pgn = Integer.parseInt(p.substring(4));
            } else if ( p.startsWith("Source:")) {
                source = Integer.parseInt(p.substring(7));
            } else if ( p.startsWith("Data:")) {
                data = p.substring(5);
            }
        }
        if ( pgn != -1 && source != -1 && data != null) {
            return "$PCDIN,"
                    +Integer.toHexString(pgn).toUpperCase()+","
                    +Integer.toHexString(timestamp).toUpperCase()+","
                    +Integer.toHexString(source).toUpperCase()+","
                    +data;
        }
        return null;
    }

}
