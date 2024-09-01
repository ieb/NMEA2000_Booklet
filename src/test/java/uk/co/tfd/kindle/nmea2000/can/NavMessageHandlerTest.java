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
    public void testPGN127250Heading() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127250 1F112
        //236408 : Pri:2 PGN:127250 Source:204 Dest:255 Len:8 Data:FF,2F,8,FF,7F,FF,7F,FD
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F112,8B8C49,18,FF,2F,8,FF,7F,FF,7F,FD"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127250Heading);
        NavMessageHandler.PGN127250Heading msg = (NavMessageHandler.PGN127250Heading) lastMessage;
    //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127250Heading sid:255 heading:0.20950000000000002 deviation:-1.0E9 variation:-1.0E9 ref:name:Unavailable (id:3) pgn:127250 src:24 count:1 messageName:N2K Heading timestamp:9145417
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
    public void testPGN127257Attitude() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 127257 1F119
        //236422 : Pri:2 PGN:127257 Source:204 Dest:255 Len:8 Data:FF,2F,8,CD,0,10,0,FF
        nmea0183CLient.processLine(Utils.addCheckSum( "$PCDIN,1F119,8B8C49,18,FF,2F,8,CD,0,10,0,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN127257Attitude);
        NavMessageHandler.PGN127257Attitude msg = (NavMessageHandler.PGN127257Attitude) lastMessage;
    //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127257Attitude sid:255 yaw:0.20950000000000002 pitch:0.0205 roll:0.0016 pgn:127257 src:24 count:1 messageName:N2K Attitude timestamp:9145417
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
    public void testPGN128259Speed() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 128259 1F503
        //231001 : Pri:2 PGN:128259 Source:105 Dest:255 Len:8 Data:0,0,0,FF,FF,0,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F503,8B8C49,18,0,0,0,FF,FF,0,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN128259Speed);
        NavMessageHandler.PGN128259Speed msg = (NavMessageHandler.PGN128259Speed) lastMessage;
 //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN128259Speed sid:0 waterReferenced:0.0 groundReferenced:-0.01 swrt:name:Paddle wheel (id:0) speedDirection:15 pgn:128259 src:24 count:1 messageName:N2K Speed timestamp:9145417
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
    public void testPGN129026COGSOGRapid() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129026 1F802
        // 9144865 : Pri:2 PGN:129026 Source:22 Dest:255 Len:8 Data:1,FC,1C,A3,46,3,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F802,8B8C49,18,1,FC,1C,A3,46,3,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129026COGSOGRapid);
        NavMessageHandler.PGN129026COGSOGRapid msg = (NavMessageHandler.PGN129026COGSOGRapid) lastMessage;
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
    public void testPGN129025RapidPosition() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129025 1F801
        //  {n: 19, pgn: 129025, src: 206, msg: '14cee01e706f9800'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F801,8B8C49,18,14,CE,E0,1E,70,6F,98,00"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129025RapidPosition);
        NavMessageHandler.PGN129025RapidPosition msg = (NavMessageHandler.PGN129025RapidPosition) lastMessage;
        //[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417

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
    public void testPGN129283CrossTrackError() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 129283 1F903
        //237672 : Pri:3 PGN:129283 Source:3 Dest:255 Len:8 Data:FF,7F,FF,FF,FF,7F,FF,FF
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F903,8B8C49,18,FF,7F,FF,FF,FF,7F,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN129283CrossTrackError);
        NavMessageHandler.PGN129283CrossTrackError msg = (NavMessageHandler.PGN129283CrossTrackError) lastMessage;
//        [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN129283CrossTrackError sid:255 xteMode:name:unknown_15 (id:15) navigationTerminated:name:Yes (id:1) xte:-1.0E9 pgn:129283 src:24 count:1 messageName:Cross Track Error timestamp:9145417

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
    public void twstPGN130311EnvironmentParameters() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130311 1FD07
        // fake message
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD07,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130311EnvironmentParameters);
        NavMessageHandler.PGN130311EnvironmentParameters msg = (NavMessageHandler.PGN130311EnvironmentParameters) lastMessage;
//[main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message  Class:PGN127245Rudder instance:0 rudderDirectionOrder:name:Unavailable (id:7) angleOrder:-1.0E9 rudderPosition:0.1918 pgn:127245 src:24 count:1 messageName:N2K Rudder timestamp:9145417
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
    public void testPGN130314Pressure() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130314 1FD0A
        // fake message, invalid values.
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD0A,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130314Pressure);
        NavMessageHandler.PGN130314Pressure msg = (NavMessageHandler.PGN130314Pressure) lastMessage;
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
    public void testPGN130316TemperatureExtended() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130316 1FD0C
        // fake message, invalid values.
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD0C,8B8C49,18,0,FF,FF,7F,7E,7,FF,FF"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130316TemperatureExtended);
        NavMessageHandler.PGN130316TemperatureExtended msg = (NavMessageHandler.PGN130316TemperatureExtended) lastMessage;
    }
    @Test
    public void testPGN130577DirectionData() throws UnsupportedEncodingException {

        //        [main] INFO uk.co.tfd.kindle.nmea2000.SeaSmartHandler - Added handler at 130577 1FE11
        //  {n: 17, pgn: 130577, src: 3, msg: 'c0dfffffffffffffffff8e751b00'}
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FE11,8B8C49,18,C0,DF,FF,FF,FF,FF,FF,FF,FF,FF,8E,75,1B,0"));
        Assert.assertTrue(lastMessage instanceof NavMessageHandler.PGN130577DirectionData);
        NavMessageHandler.PGN130577DirectionData msg = (NavMessageHandler.PGN130577DirectionData) lastMessage;
    }

}
