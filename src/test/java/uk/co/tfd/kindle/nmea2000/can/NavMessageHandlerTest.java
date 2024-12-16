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
import java.util.Arrays;

public class NavMessageHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(EngineMessageHandlerTest.class);
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
        NavMessageHandler navMessageHandler = new NavMessageHandler();
        seaSmartHandler.addHandler(navMessageHandler);
        canMessageProducer.addListener(new CanMessageListener() {

            @Override
            public int[] getPgns() {
                return new int[]{ -1};
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
        NavMessageHandler navMessageHandler = new NavMessageHandler();
        for(int pgn : navMessageHandler.getPgns()) {
            log.info("PGN {} {}", pgn, Integer.toHexString(pgn).toUpperCase());
        }
    }



    @Test
    public void testPGN127245Rudder() throws UnsupportedEncodingException {

        //INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 126992 1F010

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127245 1F10D
        //236429 : Pri:2 PGN:127245 Source:204 Dest:255 Len:8 Data:0,FF,FF,7F,7E,7,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F10D,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127245Rudder);
        NavMessageHandler.PGN127245Rudder msg = (NavMessageHandler.PGN127245Rudder) lastMessage;
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417
    }

    @Test
    public void createPGN127245Rudder()  {
        byte[] message = new byte[8];
        NavMessageHandler.PGN127245Rudder.encode(message,
                12,
                N2KReference.RudderDirectionOrder.NoDirectionOrder,
                6.2/CanMessageData.scaleToDegrees,
                5.8/CanMessageData.scaleToDegrees);

        NavMessageHandler.PGN127245Rudder msg = new NavMessageHandler.PGN127245Rudder(
                127245, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.instance);
        Assert.assertEquals(N2KReference.RudderDirectionOrder.NoDirectionOrder, msg.rudderDirectionOrder);
        Assert.assertEquals(6.2, msg.angleOrder*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(5.8, msg.rudderPosition*CanMessageData.scaleToDegrees, 0.01);
    }
    @Test
    public void testPGN127250Heading() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127250 1F112
        //236408 : Pri:2 PGN:127250 Source:204 Dest:255 Len:8 Data:FF,2F,8,FF,7F,FF,7F,FD
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F112,8B8C49,18,FF,2F,8,FF,7F,FF,7F,FD"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127250Heading);
        NavMessageHandler.PGN127250Heading msg = (NavMessageHandler.PGN127250Heading) lastMessage;
    //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127250Heading sid:255 heading:0.20950000000000002 deviation:-1.0E9 variation:-1.0E9 ref:name:Unavailable (id:3) pgn:127250 src:24 count:1 messageName:N2K Heading timestamp:9145417
    }

    @Test
    public void createPGN127250Heading()  {
        byte[] message = new byte[10];
        NavMessageHandler.PGN127250Heading.encode(message,
                12,
                272/CanMessageData.scaleToDegrees,
                1.1/CanMessageData.scaleToDegrees,
                -1.3/CanMessageData.scaleToDegrees,
                N2KReference.HeadingReference.Magnetic);

        NavMessageHandler.PGN127250Heading msg = new NavMessageHandler.PGN127250Heading(
                127250, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(272, msg.heading*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(1.1, msg.deviation*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(-1.3, msg.variation*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(N2KReference.HeadingReference.Magnetic, msg.ref);
    }
    @Test
    public void testPGN127251RateOfTurn() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127251 1F113
        //236415 : Pri:2 PGN:127251 Source:204 Dest:255 Len:8 Data:FF,8F,1E,0,0,FF,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum( "$PCDIN,1F113,8B8C49,18,FF,8F,1E,0,0,FF,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127251RateOfTurn);
        NavMessageHandler.PGN127251RateOfTurn msg = (NavMessageHandler.PGN127251RateOfTurn) lastMessage;
        // [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127251TateOfTurn sid:255 rateOfTurn:2.4446875E-4 pgn:127251 src:24 count:1 messageName:N2K Rate of Turn timestamp:9145417
    }
    @Test
    public void createPGN127251RateOfTurn()  {
        byte[] message = new byte[10];
        NavMessageHandler.PGN127251RateOfTurn.encode(message,
                12,
                5.12/CanMessageData.scaleToDegrees);

        NavMessageHandler.PGN127251RateOfTurn msg = new NavMessageHandler.PGN127251RateOfTurn(
                127251, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(5.12, msg.rateOfTurn*CanMessageData.scaleToDegrees, 0.001);
    }
    @Test
    public void testPGN127257Attitude() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127257 1F119
        //236422 : Pri:2 PGN:127257 Source:204 Dest:255 Len:8 Data:FF,2F,8,CD,0,10,0,FF
        nmea0183CLient.processLine(Utils.addCheckSum( "$PCDIN,1F119,8B8C49,18,FF,2F,8,CD,0,10,0,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127257Attitude);
        NavMessageHandler.PGN127257Attitude msg = (NavMessageHandler.PGN127257Attitude) lastMessage;
    //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127257Attitude sid:255 yaw:0.20950000000000002 pitch:0.0205 roll:0.0016 pgn:127257 src:24 count:1 messageName:N2K Attitude timestamp:9145417
    }

    @Test
    public void createPGN127257Attitude() {
        byte[] message = new byte[10];
        NavMessageHandler.PGN127257Attitude.encode(message,
                12,
                5.12/CanMessageData.scaleToDegrees,
                10.12/CanMessageData.scaleToDegrees,
                23.2/CanMessageData.scaleToDegrees
                );

        NavMessageHandler.PGN127257Attitude msg = new NavMessageHandler.PGN127257Attitude(
                127257, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(5.12, msg.yaw*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(10.12, msg.pitch*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(23.2, msg.roll*CanMessageData.scaleToDegrees, 0.01);
    }

    @Test
    public void testPGN127258MagneticVariation() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127258 1F11A
        //228787 : Pri:6 PGN:127258 Source:3 Dest:255 Len:8 Data:FF,FF,FF,FF,FF,7F,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F11A,8B8C49,18,FF,FF,FF,FF,FF,7F,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127258MagneticVariation);
        NavMessageHandler.PGN127258MagneticVariation msg = (NavMessageHandler.PGN127258MagneticVariation) lastMessage;
 //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127258MagneticVariation sid:255 source:name:unknown_15 (id:15) daysSince1970:65535 variation:-1.0E9 pgn:127258 src:24 count:1 messageName:N2K Magnetic Variation timestamp:9145417

    }

    @Test
    public void createPGN127258MagneticVariation()  {
        byte[] message = new byte[10];
        NavMessageHandler.PGN127258MagneticVariation.encode(message,
                12,
                N2KReference.VariationSource.Wmm2020,
                365*50+200,
                1.1/CanMessageData.scaleToDegrees
        );

        NavMessageHandler.PGN127258MagneticVariation msg = new NavMessageHandler.PGN127258MagneticVariation(
                127258, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(365*50+200, msg.daysSince1970);
        Assert.assertEquals(1.1, msg.variation*CanMessageData.scaleToDegrees, 0.001);
    }

    @Test
    public void testPGN128259Speed() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 128259 1F503
        //231001 : Pri:2 PGN:128259 Source:105 Dest:255 Len:8 Data:0,0,0,FF,FF,0,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F503,8B8C49,18,0,0,0,FF,FF,0,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN128259Speed);
        NavMessageHandler.PGN128259Speed msg = (NavMessageHandler.PGN128259Speed) lastMessage;
 //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN128259Speed sid:0 waterReferenced:0.0 groundReferenced:-0.01 swrt:name:Paddle wheel (id:0) speedDirection:15 pgn:128259 src:24 count:1 messageName:N2K Speed timestamp:9145417
    }

    @Test
    public void createPGN128259Speed() {
        byte[] message = new byte[10];
        NavMessageHandler.PGN128259Speed.encode(message,
                12,
                12.2/CanMessageData.scaleToKnots,
                12.9/CanMessageData.scaleToKnots,
                N2KReference.SwrtType.PaddleWheel,
                1
        );

        NavMessageHandler.PGN128259Speed msg = new NavMessageHandler.PGN128259Speed(
                128259, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(12.2, msg.waterReferenced*CanMessageData.scaleToKnots, 0.01);
        Assert.assertEquals(12.9, msg.groundReferenced*CanMessageData.scaleToKnots, 0.01);
        Assert.assertEquals(N2KReference.SwrtType.PaddleWheel, msg.swrt);
        Assert.assertEquals(1, msg.speedDirection);
    }
    @Test
    public void testPGN128267WaterDepth() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 128267 1F50B
        ///231979 : Pri:3 PGN:128267 Source:105 Dest:255 Len:8 Data:0,C,1,0,0,0,0,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F50B,8B8C49,18,0,C,1,0,0,0,0,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN128267WaterDepth);
        NavMessageHandler.PGN128267WaterDepth msg = (NavMessageHandler.PGN128267WaterDepth) lastMessage;
        Assert.assertEquals(128267, msg.pgn);
    //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN128267WaterDepth sid:0 depthBelowTransducer:2.68 offset:0.0 maxRange:-1.0E9 pgn:128267 src:24 count:1 messageName:N2K Water Depth timestamp:9145417
    }

    @Test
    public void createPGN128267WaterDepth()  {
        byte[] message = new byte[10];
        NavMessageHandler.PGN128267WaterDepth.encode(message,
                12,
                15.21,
                0.12,
                189.0
        );

        NavMessageHandler.PGN128267WaterDepth msg = new NavMessageHandler.PGN128267WaterDepth(
                128267, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(15.21, msg.depthBelowTransducer, 0.01);
        Assert.assertEquals(0.12, msg.offset, 0.01);
        Assert.assertEquals(190.0, msg.maxRange, 0.01);
    }
    @Test
    public void testPGN128275DistanceLog() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 128275 1F513
        //236928 : Pri:6 PGN:128275 Source:105 Dest:255 Len:14 Data:FF,FF,FF,FF,FF,FF,FC,85,9,0,FC,85,9,0
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F513,8B8C49,18,FF,FF,FF,FF,FF,FF,FC,85,9,0,FC,85,9,0"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN128275DistanceLog);
        NavMessageHandler.PGN128275DistanceLog msg = (NavMessageHandler.PGN128275DistanceLog) lastMessage;
        Assert.assertEquals(128275, msg.pgn);
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN128275DistanceLog daysSince1970:65535 secondsSinceMidnight:-1.0E9 log:624124.0 tripLog:624124.0 pgn:128275 src:24 count:1 messageName:N2K Distance Log timestamp:9145417
    }
    @Test
    public void createPGN128275DistanceLog()  {
        byte[] message = new byte[16];
        NavMessageHandler.PGN128275DistanceLog.encode(message,
                365*50+200,
                12.4*3600,
                15012.21/CanMessageData.scaleToNm,
                342.1/CanMessageData.scaleToNm
        );

        NavMessageHandler.PGN128275DistanceLog msg = new NavMessageHandler.PGN128275DistanceLog(
                128275, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(365*50+200, msg.daysSince1970);
        Assert.assertEquals(12.4*3600, msg.secondsSinceMidnight, 0.1);
        Assert.assertEquals(15012.21, msg.log*CanMessageData.scaleToNm, 0.01);
        Assert.assertEquals(342.1, msg.tripLog*CanMessageData.scaleToNm, 0.01);
    }
    @Test
    public void testPGN129026COGSOGRapid() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129026 1F802
        // 9144865 : Pri:2 PGN:129026 Source:22 Dest:255 Len:8 Data:1,FC,1C,A3,46,3,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F802,8B8C49,18,1,FC,1C,A3,46,3,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129026COGSOGRapid);
        NavMessageHandler.PGN129026COGSOGRapid msg = (NavMessageHandler.PGN129026COGSOGRapid) lastMessage;
    }
    @Test
    public void createPGN129026COGSOGRapid(){
        byte[] message = new byte[16];
        NavMessageHandler.PGN129026COGSOGRapid.encode(message,
                84,
                N2KReference.HeadingReference.TRue,
                181.2/CanMessageData.scaleToDegrees,
                8.91/CanMessageData.scaleToKnots
        );

        NavMessageHandler.PGN129026COGSOGRapid msg = new NavMessageHandler.PGN129026COGSOGRapid(
                129026, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(181.2, msg.cog*CanMessageData.scaleToDegrees, 0.01);
        Assert.assertEquals(8.91, msg.sog*CanMessageData.scaleToKnots, 0.01);
    }

    @Test
    public void testPGN129539GNSDOPS() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129539 1FA03
        //  {n: 26, pgn: 129539, src: 30, msg: '481243005d003700'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FA03,8B8C49,18,48,12,43,0,5D,0,37,0"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129539GNSDOPS);
        NavMessageHandler.PGN129539GNSDOPS msg = (NavMessageHandler.PGN129539GNSDOPS) lastMessage;
    }
    @Test
    public void createPGN129539GNSDOPS()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN129539GNSDOPS.encode(message,
                84,
                N2KReference.GnssMode.Mode3D,
                N2KReference.GnssMode.Mode2D,
                3.12,
                9.32,
                4.51
        );

        NavMessageHandler.PGN129539GNSDOPS msg = new NavMessageHandler.PGN129539GNSDOPS(
                129539, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3.12, msg.hdop, 0.01);
        Assert.assertEquals(9.32, msg.vdop, 0.01);
        Assert.assertEquals(4.51, msg.tdop, 0.01);
    }
    @Test
    public void testPGN129025RapidPosition() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129025 1F801
        //  {n: 19, pgn: 129025, src: 206, msg: '14cee01e706f9800'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F801,8B8C49,18,14,CE,E0,1E,70,6F,98,00"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129025RapidPosition);
        NavMessageHandler.PGN129025RapidPosition msg = (NavMessageHandler.PGN129025RapidPosition) lastMessage;
        //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417

    }
    @Test
    public void createPGN129025RapidPosition()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN129539GNSDOPS.encode(message,
                84,
                N2KReference.GnssMode.Mode3D,
                N2KReference.GnssMode.Mode2D,
                3.12,
                9.32,
                4.51
        );

        NavMessageHandler.PGN129539GNSDOPS msg = new NavMessageHandler.PGN129539GNSDOPS(
                129539, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3.12, msg.hdop, 0.01);
        Assert.assertEquals(9.32, msg.vdop, 0.01);
        Assert.assertEquals(4.51, msg.tdop, 0.01);
    }
    @Test
    public void testPGN129029GNSS() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129029 1F805
        // {n: 23, pgn: 129029, src: 30, msg: '48ab4d32f0be1100ee8b511d7b300700bc9b40df7d2300302023000000000014fd0f430072000000000000'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F805,8B8C49,18,48,AB,4D,32,F0,BE,11,0,EE,8B,51,1D,7B,30,7,0,BC,9B,40,DF,7D,23,0,30,20,23,0,0,0,0,0,14,fD,F,43,0,72,0,0,0,0,0,0"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129029GNSS);
        NavMessageHandler.PGN129029GNSS msg = (NavMessageHandler.PGN129029GNSS) lastMessage;
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417
    }

    @Test
    public void createPGN129029GNSS()  {
        byte[] message = new byte[55];
        NavMessageHandler.PGN129029GNSS.encode(message,
                84,
                365*50+200,
                12.4*3600,
                53.21322321,
                -12.211211,
                3,
                N2KReference.GnssType.GPSSBASWAASGLONASS,
                N2KReference.GnssMethod.DGNSS,
                N2KReference.GnssIntegrity.Safe,
                15,
                1.21,
                3.21,
                3.89,
                new NavMessageHandler.ReferenceStation[] {
                        new NavMessageHandler.ReferenceStation(12, N2KReference.GnssType.GPSSBASWAASGLONASS,  12.21),
                        new NavMessageHandler.ReferenceStation(8, N2KReference.GnssType.GPSGLONASS, 12.34)}
        );

        NavMessageHandler.PGN129029GNSS msg = new NavMessageHandler.PGN129029GNSS(
                129029, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(365*50+200, msg.daysSince1970, 0.01);
        Assert.assertEquals(12.4*3600, msg.secondsSinceMidnight, 0.01);
        Assert.assertEquals(53.21322321000001, msg.latitude, 1E-15);
        Assert.assertEquals(-12.211211, msg.longitude, 1E-15);
        Assert.assertEquals(3.0, msg.altitude, 0.01);
        Assert.assertEquals(N2KReference.GnssType.GPSSBASWAASGLONASS, msg.GNSStype);
        Assert.assertEquals(N2KReference.GnssMethod.DGNSS, msg.GNSSmethod);
        Assert.assertEquals(N2KReference.GnssIntegrity.Safe, msg.integrety);
        Assert.assertEquals(15, msg.nSatellites);
        Assert.assertEquals(1.21, msg.hdop, 0.01);
        Assert.assertEquals(3.21, msg.pdop, 0.01);
        Assert.assertEquals(3.89, msg.geoidalSeparation, 0.01);
        Assert.assertEquals(2, msg.nReferenceStations);
        Assert.assertEquals(2, msg.stations.length);
        Assert.assertEquals(12, msg.stations[0].referenceSationID);
        Assert.assertEquals(N2KReference.GnssType.GPSSBASWAASGLONASS, msg.stations[0].referenceStationType);
        Assert.assertEquals(12.21, msg.stations[0].ageOfCorrection, 0.001);
        Assert.assertEquals(8, msg.stations[1].referenceSationID);
        Assert.assertEquals(N2KReference.GnssType.GPSGLONASS, msg.stations[1].referenceStationType);
        Assert.assertEquals(12.34, msg.stations[1].ageOfCorrection,0.001);
    }
    @Test
    public void testPGN129283CrossTrackError() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129283 1F903
        //237672 : Pri:3 PGN:129283 Source:3 Dest:255 Len:8 Data:FF,7F,FF,FF,FF,7F,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F903,8B8C49,18,FF,7F,FF,FF,FF,7F,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129283CrossTrackError);
        NavMessageHandler.PGN129283CrossTrackError msg = (NavMessageHandler.PGN129283CrossTrackError) lastMessage;
//        [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN129283CrossTrackError sid:255 xteMode:name:unknown_15 (id:15) navigationTerminated:name:Yes (id:1) xte:-1.0E9 pgn:129283 src:24 count:1 messageName:Cross Track Error timestamp:9145417

    }

    @Test
    public void createPGN129283CrossTrackError()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN129283CrossTrackError.encode(message,
                84,
                N2KReference.XteMode.Autonomous,
                N2KReference.YesNo.No,
                0.51/CanMessageData.scaleToNm
        );

        NavMessageHandler.PGN129283CrossTrackError msg = new NavMessageHandler.PGN129283CrossTrackError(
                129283, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(N2KReference.XteMode.Autonomous, msg.xteMode);
        Assert.assertEquals(N2KReference.YesNo.No, msg.navigationTerminated);
        Assert.assertEquals(0.51, msg.xte*CanMessageData.scaleToNm, 0.001);
    }
    @Test
    public void testPGN130306Wind() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130306 1FD02
        //237725 : Pri:2 PGN:130306 Source:105 Dest:255 Len:8 Data:0,9F,0,F6,E1,FA,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD02,8B8C49,18,0,9F,0,F6,E1,FA,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130306Wind);
        NavMessageHandler.PGN130306Wind msg = (NavMessageHandler.PGN130306Wind) lastMessage;
// [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN130306Wind sid:0 windReference:name:Apparent (id:2) windSpeed:1.59 windAngle:-0.49858530717958605 pgn:130306 src:24 count:1 messageName:Wind timestamp:9145417
    }
    @Test
    public void createPGN130306Wind()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130306Wind.encode(message,
                84,
                N2KReference.WindReference.Apparent,
                23.3/CanMessageData.scaleToKnots,
                -45.3/CanMessageData.scaleToDegrees
        );

        NavMessageHandler.PGN130306Wind msg = new NavMessageHandler.PGN130306Wind(
                130306, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(N2KReference.WindReference.Apparent, msg.windReference);
        Assert.assertEquals(23.3, msg.windSpeed*CanMessageData.scaleToKnots, 0.01);
        Assert.assertEquals(-45.3, msg.windAngle*CanMessageData.scaleToDegrees, 0.1);
    }
    @Test
    public void testPGN130310OutsideEnvironmentParameters() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130310 1FD06
        //228901 : Pri:5 PGN:130310 Source:105 Dest:255 Len:8 Data:0,33,6D,FF,FF,FF,FF,FF
        //  {n: 11, pgn: 130310, src: 199, msg: '000771ffffffffff'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD06,8B8C49,18,0,33,6D,FF,FF,FF,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130310OutsideEnvironmentParameters);
        NavMessageHandler.PGN130310OutsideEnvironmentParameters msg = (NavMessageHandler.PGN130310OutsideEnvironmentParameters) lastMessage;
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN130306Wind sid:0 windReference:name:Apparent (id:2) windSpeed:1.59 windAngle:-0.49858530717958605 pgn:130306 src:24 count:1 messageName:Wind timestamp:9145417

    }

    @Test
    public void createPGN130310OutsideEnvironmentParameters()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130310OutsideEnvironmentParameters.encode(message,
                84,
                24.2+273.15,
                15.2+273.15,
                102300
        );

        NavMessageHandler.PGN130310OutsideEnvironmentParameters msg = new NavMessageHandler.PGN130310OutsideEnvironmentParameters(
                130310, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(24.2+273.15, msg.outsideAmbientAirTemperature, 0.1);
        Assert.assertEquals(15.2+273.15, msg.waterTemperature, 0.1);
        Assert.assertEquals(102300, msg.atmosphericPressure, 0.1);
    }
    @Test
    public void testPGN130311EnvironmentParameters() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130311 1FD07
        // fake message
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD07,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130311EnvironmentParameters);
        NavMessageHandler.PGN130311EnvironmentParameters msg = (NavMessageHandler.PGN130311EnvironmentParameters) lastMessage;
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417
    }
    @Test
    public void createPGN130311EnvironmentParameters()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130311EnvironmentParameters.encode(message,
                84,
                N2KReference.TemperatureSource.MainCabinTemperature,
                N2KReference.HumiditySource.Outside,
                15.2+273.15,
                87.3,
                102200
        );

        NavMessageHandler.PGN130311EnvironmentParameters msg = new NavMessageHandler.PGN130311EnvironmentParameters(
                130311, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(N2KReference.TemperatureSource.MainCabinTemperature,msg.tempSource);
        Assert.assertEquals(N2KReference.HumiditySource.Outside,msg.humiditySource);
        Assert.assertEquals(15.2+273.15, msg.temperature, 0.1);
        Assert.assertEquals(87.3, msg.humidity, 0.1);
        Assert.assertEquals(102200, msg.atmosphericPressure, 0.1);
    }
    @Test
    public void testPGN130313Humidity() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130313 1FD09
        // fake message, produces invalid values
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD09,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130313Humidity);
        NavMessageHandler.PGN130313Humidity msg = (NavMessageHandler.PGN130313Humidity) lastMessage;
 //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417
    }
    @Test
    public void createPGN130313Humidity()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130313Humidity.encode(message,
                84,
                3,
                N2KReference.HumiditySource.Outside,
                74.5,
                70.2
        );

        NavMessageHandler.PGN130313Humidity msg = new NavMessageHandler.PGN130313Humidity(
                130313, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3,msg.humidityInstance);
        Assert.assertEquals(N2KReference.HumiditySource.Outside,msg.humiditySource);
        Assert.assertEquals(74.5, msg.actualHumidity, 0.1);
        Assert.assertEquals(70.2, msg.setHumidity, 0.1);
    }
    @Test
    public void testPGN130314Pressure() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130314 1FD0A
        // fake message, invalid values.
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD0A,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130314Pressure);
        NavMessageHandler.PGN130314Pressure msg = (NavMessageHandler.PGN130314Pressure) lastMessage;
    }
    @Test
    public void createPGN130314Pressure()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130314Pressure.encode(message,
                84,
                3,
                N2KReference.PressureSource.Oil,
                123101
        );

        NavMessageHandler.PGN130314Pressure msg = new NavMessageHandler.PGN130314Pressure(
                130314, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3,msg.pressureInstance);
        Assert.assertEquals(N2KReference.PressureSource.Oil,msg.pressureSource);
        Assert.assertEquals(123101, msg.actualPressure, 0.1);
    }
    @Test
    public void testPGN130315SetPressure() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130315 1FD0B
        // fake message, invalid values.
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD0B,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130315SetPressure);
        NavMessageHandler.PGN130315SetPressure msg = (NavMessageHandler.PGN130315SetPressure) lastMessage;
    }

    @Test
    public void createPGN130315SetPressure()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130315SetPressure.encode(message,
                84,
                3,
                N2KReference.PressureSource.CompressedAir,
                12332
        );

        NavMessageHandler.PGN130315SetPressure msg = new NavMessageHandler.PGN130315SetPressure(
                130315, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3,msg.pressureInstance);
        Assert.assertEquals(N2KReference.PressureSource.CompressedAir,msg.pressureSource);
        Assert.assertEquals(12332, msg.setPressure, 0.1);
    }

    @Test
    public void testPGN130316TemperatureExtended() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130316 1FD0C
        // fake message, invalid values.
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD0C,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130316TemperatureExtended);
        NavMessageHandler.PGN130316TemperatureExtended msg = (NavMessageHandler.PGN130316TemperatureExtended) lastMessage;
    }
    @Test
    public void createPGN130316TemperatureExtended()  {
        byte[] message = new byte[12];
        NavMessageHandler.PGN130316TemperatureExtended.encode(message,
                84,
                3,
                N2KReference.TemperatureSource.EngineRoomTemperature,
                54.3+273.15,
                50.3+273.15
        );

        NavMessageHandler.PGN130316TemperatureExtended msg = new NavMessageHandler.PGN130316TemperatureExtended(
                130316, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(3,msg.tempInstance);
        Assert.assertEquals(N2KReference.TemperatureSource.EngineRoomTemperature,msg.tempSource);
        Assert.assertEquals(54.3+273.15, msg.actualTemperature, 0.1);
        Assert.assertEquals(50.3+273.15, msg.setTemperature, 0.1);
    }
    @Test
    public void testPGN130577DirectionData() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130577 1FE11
        //  {n: 17, pgn: 130577, src: 3, msg: 'c0dfffffffffffffffff8e751b00'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FE11,8B8C49,18,C0,DF,FF,FF,FF,FF,FF,FF,FF,FF,8E,75,1B,0"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130577DirectionData);
        NavMessageHandler.PGN130577DirectionData msg = (NavMessageHandler.PGN130577DirectionData) lastMessage;
    }
    @Test
    public void createPGN130577DirectionData()  {
        byte[] message = new byte[20];
        NavMessageHandler.PGN130577DirectionData.encode(message,
                84,
                N2KReference.ResidualMode.Differential,
                N2KReference.DirectionReference.True,
                94.2/CanMessageData.scaleToDegrees,
                5.4/CanMessageData.scaleToKnots,
                84.5/CanMessageData.scaleToDegrees,
                8.4/CanMessageData.scaleToKnots,
                 3.2/CanMessageData.scaleToDegrees,
                1.2/CanMessageData.scaleToKnots
        );

        NavMessageHandler.PGN130577DirectionData msg = new NavMessageHandler.PGN130577DirectionData(
                130577, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(N2KReference.ResidualMode.Differential,msg.residualMode);
        Assert.assertEquals( N2KReference.DirectionReference.True,msg.cogReference);
        Assert.assertEquals(94.2, msg.cog*CanMessageData.scaleToDegrees, 0.1);
        Assert.assertEquals(5.4, msg.sog*CanMessageData.scaleToKnots, 0.1);
        Assert.assertEquals(84.5, msg.heading*CanMessageData.scaleToDegrees, 0.1);
        Assert.assertEquals(8.4, msg.stw*CanMessageData.scaleToKnots, 0.1);
        Assert.assertEquals(3.2, msg.set*CanMessageData.scaleToDegrees, 0.1);
        Assert.assertEquals(1.2, msg.drift*CanMessageData.scaleToKnots, 0.1);
    }

    @Test
    public void testHeading() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130577 1FE11
        //  {n: 14, pgn: 127250, src: 205, msg: 'ffd6a8ff7fff7ffd'}
        //  {n: 48, pgn: 127250, src: 205, msg: 'ff2aa9ff7fff7ffd'}
        log.info("PGN {} {} ", 127250, Integer.toHexString(127250));
        byte[] data = CanMessageData.asByteArray("0F,D6,A8,FF,7F,FF,7F,FD".split(","), 0);
        log.info(" data {} ", Arrays.toString(data));

        int ref = CanMessageData.get1ByteUInt(data, 7);
        log.info(" ref {} {} ", Integer.toUnsignedString(ref,2), ref & 0x03 );
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F112,8B8C49,18,0F,D6,A8,FF,7F,FF,7F,FD"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127250Heading);
        NavMessageHandler.PGN127250Heading msg = (NavMessageHandler.PGN127250Heading) lastMessage;
        Assert.assertEquals(15, msg.sid);
        int angleInt = Integer.parseUnsignedInt("D6", 16) & 0xff | (Integer.parseUnsignedInt("A8", 16) & 0xff) << 8;
        double angleDouble = angleInt*0.0001;
        Assert.assertEquals(angleDouble, msg.heading, 0.01);
    }

    @Test
    public void createHeading()  {
        byte[] message = new byte[20];
        NavMessageHandler.PGN127250Heading.encode(message,
                84,
                84.5/CanMessageData.scaleToDegrees,
                0.4/CanMessageData.scaleToDegrees,
                -1.2/CanMessageData.scaleToDegrees,
                N2KReference.HeadingReference.TRue
        );

        NavMessageHandler.PGN127250Heading msg = new NavMessageHandler.PGN127250Heading(
                127250, (int)(System.currentTimeMillis()/1000), (byte)15, message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(84,msg.sid);
        Assert.assertEquals(N2KReference.HeadingReference.TRue,msg.ref);
        Assert.assertEquals(84.5, msg.heading*CanMessageData.scaleToDegrees, 0.1);
        Assert.assertEquals(0.4, msg.deviation*CanMessageData.scaleToDegrees, 0.1);
        Assert.assertEquals(-1.2, msg.variation*CanMessageData.scaleToDegrees, 0.1);
    }




}
