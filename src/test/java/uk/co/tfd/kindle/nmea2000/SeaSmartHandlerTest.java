package uk.co.tfd.kindle.nmea2000;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SeaSmartHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(SeaSmartHandlerTest.class);
    private NMEA0183Client nmea0183CLient;
    private SeaSmartHandler seaSmartHandler;
    private int lastPgn;
    private int lastTimeStamp;
    private byte lastSource;
    private byte[] lastData;
    private int lastDroppedPgn;
    private int lastUnhandledPgn;
    private CanMessage lastCanMessage;
    private CanMessageProducer canMessageProducer;

    @Before
    public void setUp() throws Exception {
        InetAddress address = InetAddress.getByAddress(new byte[] { (byte)192, (byte)168, 1,21 });
        nmea0183CLient = new NMEA0183Client(address, 10110);
        canMessageProducer = new CanMessageProducer();
        seaSmartHandler = new SeaSmartHandler(canMessageProducer);
        nmea0183CLient.addHandler("DIN", seaSmartHandler);
        CanMessageHandler canMessageHandler = new CanMessageHandler() {

            @Override
            public CanMessage handleMessage(int pgn, int timestamp, byte source, byte[] data) {
                lastPgn = pgn;
                lastTimeStamp = timestamp;
                lastSource = source;
                lastData = data;
                return  new CanMessage() {
                    @Override
                    public int getPgn() {
                        return pgn;
                    }
                };
            }

            private int[] pgns= { -1 };
            @Override
            public int[] getPgns() {
                return pgns;
            }
        };
        seaSmartHandler.addHandler(canMessageHandler);
        canMessageProducer.addListener(new CanMessageListener() {
            @Override
            public void onDrop(int pgn) {
                lastDroppedPgn = pgn;
            }

            @Override
            public void onUnhandled(int pgn) {
                lastUnhandledPgn = pgn;

            }

            @Override
            public void onMessage(CanMessage message) {
                lastCanMessage = message;
            }

            @Override
            public int[] getPgns() {
                return new int[0];
            }
        });
    }

    // $PCDIN,pgn,ts,source,data

    private String[] messages = {
            "$PCDIN,1F513,8B8C47,16,1D,4A,50,AE,42,0,D7,14,0,0,D7,14,0,0",
            "$PCDIN,1FD06,8B8C48,15,1,8C,72,95,74,F6,3,FF",
            "$PCDIN,1F513,8B8C49,22,1D,4A,50,AE,42,0,D7,14,0,0,D7,14,0,0",
            "$PCDIN,1FD06,8B8C4A,22,1,8C,72,95,74,F6,3,FF",
            "$PCDIN,130311,8B8C4B,22,1,4,CC,73,B6,35,F6,3",
            "$PCDIN,1FD02,8B8C4C,22,0,8E,3,32,D1,2,FF,FF",
            "$PCDIN,1F113,8B8C4D,22,0,A9,85,8,0,FF,FF,FF",
            "$PCDIN,1F805,8B8C4E,22,1,1D,4A,50,AE,42,0,C0,7C,3D,75,AD,73,34,7,B8,33,97,33,E4,89,32,0,A0,37,A0,0,0,0,0,0,10,FD,C,50,0,32,0,DC,5,0,0,1,F0,0,C8,0",
            "$PCDIN,1F503,8B8C4F,22,22,FF,2,FF,FF,0,FF,FF",
            "$PCDIN,1F50B,8B8C50,22,22,47,7,0,0,64,0,FF",
            "$PCDIN,1F10D,8B8C51,22,1,F9,97,FC,8C,FE,FF,FF",
            "$PCDIN,1F112,8B8C52,22,0,FE,A2,F4,FD,C0,3,FD",
            "$PCDIN,1F200,8B8C53,22,0,F8,43,FF,FF,7F,FF,FF",
            "$PCDIN,1F10D,8B8C54,22,1,F9,97,FC,7C,FE,FF,FF",
            "$PCDIN,1F112,8B8C55,22,0,FE,A2,F4,FD,C0,3,FD",
            "$PCDIN,1F200,8B8C56,22,0,F8,43,FF,FF,7F,FF,FF",
            "$PCDIN,1F802,8B8C57,22,1,FC,FE,A2,3D,3,FF,FF",
            "$PCDIN,1F10D,8B8C58,22,1,F9,97,FC,6C,FE,FF,FF",
            "$PCDIN,1F112,8B8C59,22,0,FE,A2,F4,FD,C0,3,FD",
            "$PCDIN,1F200,8B8C5A,22,0,F8,43,FF,FF,7F,FF,FF",
            "$PCDIN,1F10D,8B8C5B,22,1,F9,97,FC,5C,FE,FF,FF",
            "$PCDIN,1F112,8B8C5C,22,0,FE,A2,F4,FD,C0,3,FD",
    };


    @Test
    public void parseMessage() throws UnsupportedEncodingException {
        for (int i = 0; i < messages.length; i++) {
            String withCheckSum = Utils.addCheckSum(messages[i]);
            lastCanMessage = null;
            lastDroppedPgn = -1;
            lastUnhandledPgn = -1;
            nmea0183CLient.processLine(withCheckSum);
            log.info("PGN {} {} ",Integer.toHexString(lastPgn), lastPgn);
            List<String> l = new ArrayList<>();
            l.add("$PCDIN");
            l.add(Integer.toHexString(lastPgn));
            l.add(Integer.toHexString(lastTimeStamp));
            l.add(Integer.toHexString(lastSource));
            for (int j = 0; j < lastData.length; j++) {
                l.add(Integer.toHexString((int)(lastData[j]&0xff)));
            }
            String test = String.join(",", l).toUpperCase();
            Assert.assertEquals(messages[i], test);

            Assert.assertNotNull(lastCanMessage);
            Assert.assertEquals(-1, lastDroppedPgn);
            Assert.assertEquals(-1, lastUnhandledPgn);
        }
    }




    /**
     * This tests to see what the saving are for a packed format, either no commas or base64.
     * No commas in the data gives a 10% saving, and base64 encoding gives a 30% saving.
     * Better to filter at the origin and keep the standard format rather than
     * add additional processing at the origin.
     * Conclusion: not worth it diverging from the standard.
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testEncoding() throws UnsupportedEncodingException {
        double sumFactor2 = 0.0;
        double sumFactor3 = 0.0;
        for (int i = 0; i < messages.length; i++) {
            String withCheckSum = Utils.addCheckSum(messages[i]);
            nmea0183CLient.processLine(withCheckSum);
            List<String> l = new ArrayList<>();
            l.add("$PCDIN");
            l.add(Integer.toHexString(lastPgn));
            l.add(Integer.toHexString(lastTimeStamp));
            l.add(Integer.toHexString(lastSource));
            for (int j = 0; j < lastData.length; j++) {
                l.add(Integer.toHexString((int)(lastData[j]&0xff)));
            }
            String format1 = String.join(",", l).toUpperCase();
            l.clear();
            l.add("$PCDIN");
            l.add(Integer.toHexString(lastPgn));
            l.add(Integer.toHexString(lastTimeStamp));
            l.add(Integer.toHexString(lastSource));
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < lastData.length; j++) {
                int v = (lastData[j]&0xff);
                if (v < 16) {
                    sb.append("0");
                    sb.append(Integer.toHexString(v));
                } else {
                    sb.append(Integer.toHexString(v));
                }
            }
            l.add(sb.toString());
            String format2 = String.join(",", l).toUpperCase();
            l.clear();
            l.add("!PCDIN");
            byte[] topack = new byte[8+lastData.length];
            topack[0] = (byte)lastPgn;
            topack[1] = (byte)((lastPgn>>8)&0xff);
            topack[2] = (byte)((lastPgn>>16)&0xff);
            topack[3] = lastSource;
            topack[4] = (byte)((lastTimeStamp>>0)&0xff);
            topack[5] = (byte)((lastTimeStamp>>8)&0xff);
            topack[6] = (byte)((lastTimeStamp>>16)&0xff);
            topack[7] = (byte)((lastTimeStamp>>24)&0xff);
            for (int j = 0; j < lastData.length; j++) {
                topack[j+8] = lastData[j];
            }
            l.add(Base64.getEncoder().encodeToString(topack));
            String format3 = String.join(",", l);

            double factor2 = 1.0*format2.length()/format1.length();
            double factor3 = 1.0*format3.length()/format1.length();
            sumFactor2 += factor2;
            sumFactor3 += factor3;
            log.info("Format 1:{} {} {} ",format1.length(), 1,  format1);
            log.info("Format 2:{} {} {} ",format2.length(), factor2, format2);
            log.info("Format 3:{} {} {} ",format3.length(), factor3, format3);
        }
        log.info("Averages 1: {}", 1.0);
        log.info("Averages 2: {}", sumFactor2/messages.length);
        log.info("Averages 3: {}", sumFactor3/messages.length);
    }
}