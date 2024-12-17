package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.ElectricalMessageHandler;
import uk.co.tfd.kindle.nmea2000.can.EngineMessageHandler;
import uk.co.tfd.kindle.nmea2000.can.N2KReference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Emits the messages that the N2KEngine firmware emits.
 */
public class N2KEngineFirmware {
    public double engineSpeed  = 2300.0;
    private int engineInstance = 1;
    private double engineOilPressure = 55.0/CanMessageData.scaleToPsi;
    private double engineCoolantTemperature = 73.2+273.15;
    private double alternatorVoltage = 13.8;
    private double enginHours = 145.32;
    private int status1 = 0x00;
    private int status2 = 0x00;
    private double engineRoomTemperature = 55.2+273.15;
    private double exhaustTemperature = 33.8+273.15;
    private double alternatorTemperature = 44.8+273.15;
    private double chargerTemperature = 28.8+273.15;
    private double fuelTankLevel = 76.3;
    private double fuelTankCapacity = 58.5;
    private double batteryVoltage = 13.2;

    public N2KEngineFirmware(FirmwareSimulator simulator) {
        simulator.addSender(new EngineDynamicParams(this));
        simulator.addSender(new EngineRPMSender(this));
        simulator.addSender(new EngineFuel(this));
        simulator.addSender(new EngineTemperatures(this));
        simulator.addSender(new EngineVoltage(this));
    }

    public static class EngineRPMSender extends  Sender {
        private final N2KEngineFirmware engine;
        public EngineRPMSender(N2KEngineFirmware engine) {
            this.engine = engine;
        }
        byte sid = 0;
        @Override
        public int send(OutputStream out, PGNFilter filter)  throws IOException {
            if (filter.shouldSend(EngineMessageHandler.PGN127488RapidEngineData.PGN)) {
                send(out, EngineMessageHandler.PGN127488RapidEngineData.encode(
                        sid++,
                        engine.engineSpeed,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kInt8NA));
            }
            nextSend = System.currentTimeMillis() + 501;
            return 501;
        }
    }

    public static class EngineDynamicParams extends  Sender {
        private final N2KEngineFirmware engine;
        public EngineDynamicParams(N2KEngineFirmware engine) {
            this.engine = engine;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)  throws IOException {
            if (filter.shouldSend(EngineMessageHandler.PGN127489EngineDynamicParam.PGN)) {

                send( out, EngineMessageHandler.PGN127489EngineDynamicParam.encode(
                        engine.engineInstance,
                        engine.engineOilPressure,
                        CanMessageData.n2kDoubleNA,
                        engine.engineCoolantTemperature,
                        engine.alternatorVoltage,
                        CanMessageData.n2kDoubleNA,
                        engine.enginHours,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA,
                        (byte)CanMessageData.n2kUInt8NA,
                        (byte)CanMessageData.n2kUInt8NA,
                        engine.status1,
                        engine.status2));
            }
            nextSend = System.currentTimeMillis() + 1001;
            return 1001;
        }
    }

    public  static  class EngineFuel extends  Sender {
        byte sid = 0;
        private final N2KEngineFirmware engine;
        public EngineFuel(N2KEngineFirmware engine) {
            this.engine = engine;
        }

        @Override
        public int send(OutputStream out, PGNFilter filter) throws IOException {
            if (filter.shouldSend(EngineMessageHandler.PGN127505FluidLevel.PGN)) {

                send( out, EngineMessageHandler.PGN127505FluidLevel.encode(
                        engine.engineInstance,
                        N2KReference.TankType.Fuel,
                        engine.fuelTankLevel,
                        engine.fuelTankCapacity));
            }
            nextSend = System.currentTimeMillis() + 5001;
            return 5001;
        }
    }

    public static class EngineTemperatures extends  Sender {

        byte sid = 0;

        private final N2KEngineFirmware engine;
        public EngineTemperatures(N2KEngineFirmware engine) {
            this.engine = engine;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter) throws IOException {
            if (filter.shouldSend(EngineMessageHandler.PGN130312Temperature.PGN)) {

                send( out, EngineMessageHandler.PGN130312Temperature.encode(
                        sid,
                        engine.engineInstance,
                        engine.engineRoomTemperature,
                        CanMessageData.n2kDoubleNA,
                        N2KReference.TemperatureSource.EngineRoomTemperature));

                send( out, EngineMessageHandler.PGN130312Temperature.encode(
                        sid,
                        engine.engineInstance,
                        engine.exhaustTemperature,
                        CanMessageData.n2kDoubleNA,
                        N2KReference.TemperatureSource.ExhaustGasTemperature));

                send( out, EngineMessageHandler.PGN130312Temperature.encode(
                        sid,
                        engine.engineInstance,
                        engine.alternatorTemperature,
                        CanMessageData.n2kDoubleNA,
                        30));

                send( out, EngineMessageHandler.PGN130312Temperature.encode(
                        sid,
                        engine.engineInstance,
                        engine.chargerTemperature,
                        CanMessageData.n2kDoubleNA,
                        31));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 5001;
            return 15000;
        }
    }

    public static class EngineVoltage extends  Sender {
        byte sid = 0;
        private final N2KEngineFirmware engine;
        public EngineVoltage(N2KEngineFirmware engine) {
            this.engine = engine;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter) throws IOException {
            if (filter.shouldSend(ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN)) {
                send( out, ElectricalMessageHandler.PGN127508DCBatteryStatus.encode(
                        sid,
                        0,
                        engine.batteryVoltage,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA));

                send( out, ElectricalMessageHandler.PGN127508DCBatteryStatus.encode(
                        sid,
                        3,
                        engine.alternatorVoltage,
                        CanMessageData.n2kDoubleNA, 
                        engine.alternatorTemperature));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1001;
            return 1001;
        }
    }
}
