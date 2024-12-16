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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ElectricalMessageHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(ElectricalMessageHandlerTest.class);
    private NMEA0183Client nmea0183CLient;
    private SeaSmartHandler seaSmartHandler;

    private CanMessage lastMessage;
    private CanMessageProducer canMessageProducer;


    @Before
    public void before() throws UnknownHostException {
        InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, 1, 21});
        nmea0183CLient = new NMEA0183Client(address, 10110);
        canMessageProducer = new CanMessageProducer();
        seaSmartHandler = new SeaSmartHandler(canMessageProducer);
        nmea0183CLient.addHandler("DIN", seaSmartHandler);
        ElectricalMessageHandler electricalMessageHandler = new ElectricalMessageHandler();
        seaSmartHandler.addHandler(electricalMessageHandler);
        canMessageProducer.addListener(new CanMessageListener() {

            @Override
            public int[] getPgns() {
                return new int[]{ -1 };
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
        EngineMessageHandler engineMessageHandler = new EngineMessageHandler();
        for(int pgn : engineMessageHandler.getPgns()) {
            log.info("PGN {} {}", pgn, Integer.toHexString(pgn).toUpperCase());
        }
    }


    @Test
    public void testPGN127508DCBatteryStatus() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,01F214,0000B07B,0F,01D8042D00647303"));
        Assert.assertTrue(lastMessage instanceof ElectricalMessageHandler.PGN127508DCBatteryStatus);
        ElectricalMessageHandler.PGN127508DCBatteryStatus mgs = (ElectricalMessageHandler.PGN127508DCBatteryStatus) lastMessage;
        Assert.assertEquals(15, mgs.src);
        Assert.assertEquals(1, mgs.instance);
        Assert.assertEquals(12.4, mgs.batteryVoltage, 0.01);
        Assert.assertEquals(4.5, mgs.batteryCurrent, 0.01);
        Assert.assertEquals(295.4, mgs.batteryTemperature, 0.01);
        // [main] INFO uk.co.tfd.kindle.nmea2000.can.IsoMessageHandlerTest - Got Message  Class:PGN127508DCBatteryStatus sid:255 instance:0 batteryVoltage:0.0 batteryCurrent:-1.0E9 batteryTemperature:294.85 pgn:127508 src:24 count:1 messageName:DCBatteryStatus
    }
    @Test
    public void createPGN127508DCBatteryStatus() {

        byte[] message = new byte[8];
        ElectricalMessageHandler.PGN127508DCBatteryStatus.encode(message, 12,1,13.34,4.5, 295.4);
        ElectricalMessageHandler.PGN127508DCBatteryStatus mgs2 = new ElectricalMessageHandler.PGN127508DCBatteryStatus(127508, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,mgs2.src);
        Assert.assertEquals(1,mgs2.instance);
        Assert.assertEquals(13.34,mgs2.batteryVoltage, 0.01);
        Assert.assertEquals(4.5,mgs2.batteryCurrent,0.01);
        Assert.assertEquals(295.4,mgs2.batteryTemperature, 0.01);
    }


    @Test
    public void testPGN127506DCStatus() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,01F212,0000B07C,0F,030100595F950EFFFF1801"));
        Assert.assertTrue(lastMessage instanceof ElectricalMessageHandler.PGN127506DCStatus);
        ElectricalMessageHandler.PGN127506DCStatus msg = (ElectricalMessageHandler.PGN127506DCStatus) lastMessage;
        log.info(msg.toString());

    }
    @Test
    public void createPGN127506DCStatus() throws UnsupportedEncodingException {
        byte[] message = new byte[12];
        ElectricalMessageHandler.PGN127506DCStatus.encode(message,
                12,
                1,
                N2KReference.DcSourceType.Battery,
                80,
                70,
                 120.0,
                0.2,
                340*3600);
        ElectricalMessageHandler.PGN127506DCStatus msg = new ElectricalMessageHandler.PGN127506DCStatus(
                127506, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(N2KReference.DcSourceType.Battery,msg.dcType);
        Assert.assertEquals(80,msg.stateOfCharge);
        Assert.assertEquals(120.0,msg.timeRemaining, 0.01);
        Assert.assertEquals(0.2,msg.rippleVoltage, 0.01);
        Assert.assertEquals(340*3600,msg.capacity, 1);
    }

    @Test
    public void testPGN130829BMSRegO3() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,01FF0D,000075E9,0F,FE9F010326D804C201606DC076820198290500F0F0F0F01159030403BC0B940B8A0B370000C076606D2E02"));
        Assert.assertTrue(lastMessage instanceof ElectricalMessageHandler.PGN130829BMSRegO3);
        ElectricalMessageHandler.PGN130829BMSRegO3 msg = (ElectricalMessageHandler.PGN130829BMSRegO3) lastMessage;

        //     [2 1 C2]  [16 3] Reg03 dataLength:38 [16 3]   calcLength:38
        Assert.assertEquals(38, msg.registerLength);
        //     voltage: [0 4 D8] 12.40
        Assert.assertEquals(12.4, msg.packVoltage, 0.01);
        //     current: [2 1 C2] 4.50
        Assert.assertEquals(4.5, msg.packCurrent, 0.01);
        //     remaining: [4 6D 60] 280.00
        Assert.assertEquals(280, msg.remainingCapacity, 0.01);
        //     full: [6 76 C0] 304.00
        Assert.assertEquals(304, msg.fullChargeCapacity, 0.01);
        //     cycles: [8 1 82] 386
        Assert.assertEquals(386, msg.chargeCycles);
        //     production: [10 29 98] 2998
        Assert.assertEquals(2020, msg.manufactureDate.getYear()+1900);
        Assert.assertEquals(12, msg.manufactureDate.getMonth()+1);
        Assert.assertEquals(24, msg.manufactureDate.getDate());
        //     status0: [12 0 5] 5
        Assert.assertEquals(0x05, msg.ballanceStatus0);
        //     status1: [14 F0 F0] F0F0
        Assert.assertEquals(0xF0F0, msg.ballanceStatus1);
        //     errors: [16 F0 F0] F0F0
        Assert.assertEquals(0xF0F0, msg.protectionStatus);
        //     version: [12 11] 11
        Assert.assertEquals(0x11, msg.softwareVersion);
        //     soc: [13 59] 89
        Assert.assertEquals(89, msg.stateOfCharge);
        //     fet: [14 3] 3
        Assert.assertEquals(0x03, msg.fetControl);
        //     cells: [15 4] 4
        Assert.assertEquals(4, msg.nCells);
        //     ntcCount: [16 3] 3
        Assert.assertEquals(3, msg.nNTC);
        //     ntc0: [23 B BC] 300.40  ntc1: [25 B 94] 296.40  ntc2: [27 B 8A] 295.40
        Assert.assertEquals(3, msg.temperatures.length);
        Assert.assertEquals(300.40, msg.temperatures[0], 0.01);
        Assert.assertEquals(296.40, msg.temperatures[1], 0.01);
        Assert.assertEquals(295.40, msg.temperatures[2], 0.01);
        //     humidity: [1D 37] 55
        Assert.assertEquals(55, msg.humidity);
        //     alarm: [30 0 0] 0
        Assert.assertEquals(0, msg.alarmStatus);
        //     fullcharge: [32 76 C0] 304.00
        Assert.assertEquals(304.0, msg.fullChargeCapacity, 0.01);
        //     remaining: [34 6D 60] 280.00
        Assert.assertEquals(280, msg.remainingChargeCapacity, 0.01);
        //     ballance: [36 2 2E] 0.56
        Assert.assertEquals( 0.558, msg.ballanceCurrent, 0.0005);
    }

    @Test
    public void createPGN130829BMSRegO3() throws UnsupportedEncodingException {
        byte[] message = new byte[45];
        Calendar manufactureDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        manufactureDate.set(2017,8,24,8,5,21);

        ElectricalMessageHandler.PGN130829BMSRegO3.encode(message,
                12,
                12.4,
                -23.4,
                298.7,
                 304.0,
                manufactureDate.getTime(),
                12,
                9,
                10,
                12,
                10,
                85,
                0xf2,
                4,
                new double[] { 310,312,314},
                74,
                0x55,
                304.1,
                298.7,
                0.421);
        ElectricalMessageHandler.PGN130829BMSRegO3 msg = new ElectricalMessageHandler.PGN130829BMSRegO3(
                130829, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.instance);
        Assert.assertEquals(12.4, msg.packVoltage, 0.001);
        Assert.assertEquals(-23.4, msg.packCurrent, 0.001);
        Assert.assertEquals(298.7, msg.remainingCapacity, 0.001);
        Assert.assertEquals(304.0, msg.nominalCapacity, 0.001);
        log.info("{} {} ", manufactureDate, msg.manufactureDate);
        Assert.assertEquals(manufactureDate.get(Calendar.YEAR), msg.manufactureDate.getYear()+1900);
        Assert.assertEquals(manufactureDate.get(Calendar.MONTH), msg.manufactureDate.getMonth());
        Assert.assertEquals(manufactureDate.get(Calendar.DATE), msg.manufactureDate.getDate());
        Assert.assertEquals(12, msg.chargeCycles);
        Assert.assertEquals(9, msg.ballanceStatus0);
        Assert.assertEquals(10, msg.ballanceStatus1);
        Assert.assertEquals(12, msg.protectionStatus);
        Assert.assertEquals(10, msg.softwareVersion);
        Assert.assertEquals(85, msg.stateOfCharge);
        Assert.assertEquals(0xf2, msg.fetControl);
        Assert.assertEquals(4, msg.nCells);
        Assert.assertEquals(3, msg.nNTC);
        Assert.assertArrayEquals(new double[]{310,312,314}, msg.temperatures, 0.001);
        Assert.assertEquals(74, msg.humidity);
        Assert.assertEquals(0x55, msg.alarmStatus);
        Assert.assertEquals(304.1, msg.fullChargeCapacity, 0.001);
        Assert.assertEquals(298.7, msg.remainingChargeCapacity, 0.001);
        Assert.assertEquals(0.421, msg.ballanceCurrent, 0.001);

    }

    @Test
    public void testPGN130829BMSRegO4() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,01FF0D,000075EA,0F,FE9F010408330C440C4E0C580C"));
        Assert.assertTrue(lastMessage instanceof ElectricalMessageHandler.PGN130829BMSRegO4);
        ElectricalMessageHandler.PGN130829BMSRegO4 msg = (ElectricalMessageHandler.PGN130829BMSRegO4) lastMessage;
    }

    @Test
    public void createPGN130829BMSRegO4() throws UnsupportedEncodingException {
        byte[] message = new byte[20];
        ElectricalMessageHandler.PGN130829BMSRegO4.encode(message,
                12,
                new double[] { 3.412, 2.897, 3.213, 3.111 });
        ElectricalMessageHandler.PGN130829BMSRegO4 msg = new ElectricalMessageHandler.PGN130829BMSRegO4(
                130829, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.instance);
        Assert.assertArrayEquals(new double[] { 3.412, 2.897, 3.213, 3.111 }, msg.cellVoltage, 0.0001);
    }
    @Test
    public void testPGN130829BMSRegO5() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,01FF0D,000075EB,0F,FE9F0105054D59424D53"));
        Assert.assertTrue(lastMessage instanceof ElectricalMessageHandler.PGN130829BMSRegO5);
        ElectricalMessageHandler.PGN130829BMSRegO5 msg = (ElectricalMessageHandler.PGN130829BMSRegO5) lastMessage;
    }
    @Test
    public void createPGN130829BMSRegO5() throws UnsupportedEncodingException {
        byte[] message = new byte[20];
        ElectricalMessageHandler.PGN130829BMSRegO5.encode(message,
                12,
                "V:12.9 sys:8.4");
        ElectricalMessageHandler.PGN130829BMSRegO5 msg = new ElectricalMessageHandler.PGN130829BMSRegO5(
                130829, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.instance);
        Assert.assertEquals("V:12.9 sys:8.4", msg.hwVersion);
    }



}
