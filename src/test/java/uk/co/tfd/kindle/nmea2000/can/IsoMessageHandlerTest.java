package uk.co.tfd.kindle.nmea2000.can;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.NMEA0183Client;
import uk.co.tfd.kindle.nmea2000.SeaSmartHandler;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IsoMessageHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(IsoMessageHandlerTest.class);
    private NMEA0183Client nmea0183CLient;
    private SeaSmartHandler seaSmartHandler;

    private CanMessage lastMessage;
    private CanMessageProducer canMessageProducer = new CanMessageProducer();


    @Before
    public void before() throws UnknownHostException {
        InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, 1, 21});
        nmea0183CLient = new NMEA0183Client(address, 10110);
        seaSmartHandler = new SeaSmartHandler(canMessageProducer);
        nmea0183CLient.addHandler("DIN", seaSmartHandler);
        IsoMessageHandler isoMessageHandler = new IsoMessageHandler();
        seaSmartHandler.addHandler(isoMessageHandler);
        canMessageProducer.addListener(new CanMessageListener() {

            @Override
            public int[] getPgns() {
                return new int[]{-1};
            }

            @Override
            public void onDrop(int pgn) {
                Assert.fail("Should have been handled");
            }

            @Override
            public void onUnhandled(int pgn) {
                Assert.fail("Should have been handled");
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
    public void testListPgn() {
        IsoMessageHandler isoMessageHandler = new IsoMessageHandler();
        for(int pgn : isoMessageHandler.getPgns()) {
            log.info("PGN {} {}", pgn, Integer.toHexString(pgn).toUpperCase());
        }
    }


    @Test
    public void testPGN60928() throws UnsupportedEncodingException {

        nmea0183CLient.processLine( Utils.addCheckSum("$PCDIN,1F011,8B8C48,2,60,EA,2F,FC,FF,FF,FF,FF"));
        Assert.assertTrue(lastMessage instanceof IsoMessageHandler.PGN126993HeartBeat);
        IsoMessageHandler.PGN126993HeartBeat mgs = (IsoMessageHandler.PGN126993HeartBeat) lastMessage;
        Assert.assertEquals(2,mgs.src);

        nmea0183CLient.processLine( Utils.addCheckSum("$PCDIN,EE00,8B8C49,18,3,0,0,0,0,0,0,0"));
        Assert.assertTrue(lastMessage instanceof IsoMessageHandler.PGN60928IsoAddressClaim);
        IsoMessageHandler.PGN60928IsoAddressClaim mgs1 = (IsoMessageHandler.PGN60928IsoAddressClaim) lastMessage;
        Assert.assertEquals(24, mgs1.src);
        Assert.assertEquals(N2KReference.Industry.Global, mgs1.industryGroup);
        nmea0183CLient.processLine( Utils.addCheckSum("$PCDIN,EE00,8B8C49,2,6C,AB,6C,E7,1E,96,A0,C0"));
        Assert.assertTrue(lastMessage instanceof IsoMessageHandler.PGN60928IsoAddressClaim);
        mgs1 = (IsoMessageHandler.PGN60928IsoAddressClaim) lastMessage;
        Assert.assertEquals(2,mgs1.src);
        Assert.assertEquals(N2KReference.ManufacturerCode.Raymarine,mgs1.manufacturerCode);
        Assert.assertEquals(830316,mgs1.uniqueNumber);
        nmea0183CLient.processLine( Utils.addCheckSum("$PCDIN,EE00,8B8C49,1,33,33,65,E7,1E,96,A0,C0"));
        Assert.assertTrue(lastMessage instanceof IsoMessageHandler.PGN60928IsoAddressClaim);
        mgs1 = (IsoMessageHandler.PGN60928IsoAddressClaim) lastMessage;
        Assert.assertEquals(1,mgs1.src);
        Assert.assertEquals(N2KReference.ManufacturerCode.Raymarine,mgs1.manufacturerCode);
        Assert.assertEquals(340787,mgs1.uniqueNumber);
    }
}
