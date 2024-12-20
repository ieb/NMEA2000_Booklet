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

public class EngineMessageHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(EngineMessageHandlerTest.class);
    private NMEA0183Client nmea0183CLient;
    private SeaSmartHandler seaSmartHandler;

    private CanMessage lastMessage;
    private CanMessageProducer canMessageProducer;


    @Before
    public void before() throws UnknownHostException {
        nmea0183CLient = new NMEA0183Client();
        canMessageProducer = new CanMessageProducer();
        seaSmartHandler = new SeaSmartHandler(canMessageProducer);
        nmea0183CLient.addHandler("DIN", seaSmartHandler);
        EngineMessageHandler isoMessageHandler = new EngineMessageHandler();
        seaSmartHandler.addHandler(isoMessageHandler);
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
    public void testPGN127489EngineDynamicParam() throws UnsupportedEncodingException {

        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F201,8B8C48,18,0,FF,FF,FF,FF,2F,80,87,5,FF,7F,14,30,16,0,FF,FF,FF,FF,FF,0,0,0,0,7F,7F"));
        Assert.assertTrue(lastMessage instanceof EngineMessageHandler.PGN127489EngineDynamicParam);
        EngineMessageHandler.PGN127489EngineDynamicParam msg = (EngineMessageHandler.PGN127489EngineDynamicParam) lastMessage;
// [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message
// Class:PGN127489EngineDynamicParam
// status1:0
// status2:0
// statusMessages:
// engineInstance:0
// engineOilPressure:-1.0E9
// engineOilTemperature:-1.0E9
// engineCoolantTemperature:328.15000000000003
// alternatorVoltage:14.15
// fuelRate:-1.0E9
// engineHours:1454100.0
// engineCoolantPressure:-1.0E9
// engineFuelPressure:-1.0E9
// reserved:255 engineLoad:127
// engineTorque:255
// pgn:127489
// src:24 count:1 messageName:EngineDynamicParam
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.engineOilPressure, 0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.engineOilTemperature, 0);
        Assert.assertEquals(328.15, msg.engineCoolantTemperature, 0.0001);
        Assert.assertEquals(14.15, msg.alternatorVoltage, 0.0001);
        Assert.assertEquals(1454100.0, msg.engineHours, 0.0001);
        Assert.assertEquals(CanMessageData.n2kInt8NA, msg.engineTorque, 0);

    }
    @Test
    public void createPGN127489EngineDynamicParam() throws UnsupportedEncodingException {
        CanMessageData b = EngineMessageHandler.PGN127489EngineDynamicParam.encode(
                12,
                123490.0,
                80+273.15,
                73+273.15,
                13.81,
                CanMessageData.n2kDoubleNA,
                36001.0,
                CanMessageData.n2kDoubleNA,
                CanMessageData.n2kDoubleNA,
                (byte)84,
                (byte)34,
                0x0011,
                0x0505);
        EngineMessageHandler.PGN127489EngineDynamicParam msg = new EngineMessageHandler.PGN127489EngineDynamicParam(
                127489, (int)(System.currentTimeMillis()/1000), (byte)15, b.message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.engineInstance);
        Assert.assertEquals(123500.0, msg.engineOilPressure, 0.1);
        Assert.assertEquals(80+273.15, msg.engineOilTemperature,0.1);
        Assert.assertEquals(73+273.15, msg.engineCoolantTemperature,0.1);
        Assert.assertEquals(13.81, msg.alternatorVoltage, 0.001);
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.fuelRate,0.1);
        Assert.assertEquals(36001.0, msg.engineHours, 0.1);
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.engineCoolantPressure, 0.1);
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.engineFuelPressure, 0.1);
        Assert.assertEquals(84, msg.engineLoad);
        Assert.assertEquals(34, msg.engineTorque);
        Assert.assertEquals(0x0011, msg.status1);
        Assert.assertEquals(0x0505, msg.status2);
    }

    @Test
    public void testPGN127488RapidEngineData() throws UnsupportedEncodingException {

        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1F200,8B8C49,18,0,68,D,FF,FF,7F,FF,FF"));
        Assert.assertTrue(lastMessage instanceof EngineMessageHandler.PGN127488RapidEngineData);
        EngineMessageHandler.PGN127488RapidEngineData msg = (EngineMessageHandler.PGN127488RapidEngineData) lastMessage;
        Assert.assertEquals(24,msg.src);
//[main] INFO uk.co.tfd.kindle.nmea2000.can.IsoMessageHandlerTest - Got Message  Class:PGN127488RapidEngineData
// engineInstance:0
// engineSpeed:858.0
// engineBoostPressure:-1.0E9
// engineTiltTrim:127
// pgn:127488 src:24 count:1 messageName:'RapidEngineData'
        Assert.assertEquals(0,msg.engineInstance);
        Assert.assertEquals(858.0,msg.engineSpeed,0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,msg.engineBoostPressure,0);
        Assert.assertEquals( CanMessageData.n2kInt8NA,msg.engineTiltTrim ,0);
    }
    @Test
    public void createPGN127488RapidEngineData() throws UnsupportedEncodingException {
        CanMessageData b = EngineMessageHandler.PGN127488RapidEngineData.encode(
                12,
                2342.2,
                CanMessageData.n2kDoubleNA,
                CanMessageData.n2kInt8NA);

        EngineMessageHandler.PGN127488RapidEngineData msg = new EngineMessageHandler.PGN127488RapidEngineData(
                127488, (int)(System.currentTimeMillis()/1000), (byte)15, b.message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.engineInstance);
        Assert.assertEquals(2342.2, msg.engineSpeed, 0.1);
        Assert.assertEquals(CanMessageData.n2kDoubleNA, msg.engineBoostPressure,0.1);
        Assert.assertEquals(CanMessageData.n2kInt8NA, msg.engineTiltTrim,0.1);
    }
    @Test
    public void testPGN130312Temperature() throws UnsupportedEncodingException {
        nmea0183CLient.processLine(Utils.addCheckSum("$PCDIN,1FD08,8B8C50,18,2,3,F,A5,73,FF,FF,FF"));
        Assert.assertTrue(lastMessage instanceof EngineMessageHandler.PGN130312Temperature);
        EngineMessageHandler.PGN130312Temperature mgs = (EngineMessageHandler.PGN130312Temperature) lastMessage;
        Assert.assertEquals(24,mgs.src);
// [main] INFO uk.co.tfd.kindle.nmea2000.can.EngineMessageHandlerTest - Got Message
// Class:PGN130312Temperature
// sid:2
// instance:3
// actualTemperature:296.05
// requestedTemperature:-1.0E9
// source:name:Shaft Seal Temperature (id:15, priority:-1)
// pgn:130312
// src:24
// count:1
// messageName:Temperature

    }
    @Test
    public void createPGN130312Temperature() throws UnsupportedEncodingException {
        CanMessageData b = EngineMessageHandler.PGN130312Temperature.encode(
                12,
                8,
                21.45+273.15,
                18.54+273.15,
                N2KReference.TemperatureSource.EngineRoomTemperature);

        EngineMessageHandler.PGN130312Temperature msg = new EngineMessageHandler.PGN130312Temperature(
                130312, (int)(System.currentTimeMillis()/1000), (byte)15, b.message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.sid);
        Assert.assertEquals(8, msg.instance);
        Assert.assertEquals(21.45+273.15, msg.actualTemperature, 0.1);
        Assert.assertEquals(18.54+273.15, msg.requestedTemperature, 0.1);
        Assert.assertEquals(N2KReference.TemperatureSource.EngineRoomTemperature, msg.source);
    }
    @Test
    public void testPGN127505FluidLevel() throws UnsupportedEncodingException {

        nmea0183CLient.processLine( Utils.addCheckSum("$PCDIN,1F211,8B8C52,18,0,AE,60,58,2,0,0,FF"));
        Assert.assertTrue(lastMessage instanceof EngineMessageHandler.PGN127505FluidLevel);
        EngineMessageHandler.PGN127505FluidLevel mgs = (EngineMessageHandler.PGN127505FluidLevel) lastMessage;
        Assert.assertEquals(24,mgs.src);
//[main] INFO uk.co.tfd.kindle.nmea2000.can.IsoMessageHandlerTest - Got Message  Class:PGN127505FluidLevel instance:0 fluidType:uk.co.tfd.kindle.nmea2000.can.N2KReference$N2KRef@31610302 fluidLevel:99.0 fluidCapacity:60.0 pgn:127505 src:24 count:1 messageName:FluidLevel
    }

    @Test
    public void createPGN127505FluidLevel() throws UnsupportedEncodingException {
        CanMessageData b = EngineMessageHandler.PGN127505FluidLevel.encode(
                12,
                N2KReference.TankType.Fuel,
                85.2,
                58.0);

        EngineMessageHandler.PGN127505FluidLevel msg = new EngineMessageHandler.PGN127505FluidLevel(
                127505, (int)(System.currentTimeMillis()/1000), (byte)15, b.message);
        Assert.assertEquals(15,msg.src);
        Assert.assertEquals(12, msg.instance);
        Assert.assertEquals(N2KReference.TankType.Fuel, msg.fluidType);
        Assert.assertEquals(85.2, msg.fluidLevel, 0.1);
        Assert.assertEquals(58.0, msg.fluidCapacity, 0.1);
    }
}
