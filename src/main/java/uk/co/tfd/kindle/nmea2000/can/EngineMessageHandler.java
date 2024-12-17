package uk.co.tfd.kindle.nmea2000.can;

import java.util.ArrayList;
import java.util.List;

public class EngineMessageHandler implements CanMessageHandler {


    public static class PGN127489EngineDynamicParam extends BaseCanMessage {
        public final int status1;
        public final int status2;
        public final String statusMessages;
        public final int engineInstance;
        public final double engineOilPressure;
        public final double engineOilTemperature;
        public final double engineCoolantTemperature;
        public final double alternatorVoltage;
        public final double fuelRate;
        public final double engineHours;
        public final double engineCoolantPressure;
        public final double engineFuelPressure;
        public final int reserved;
        public final int engineLoad;
        public final int engineTorque;

        public final static int PGN = 127489;

        PGN127489EngineDynamicParam(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp, "EngineDynamicParam");
            this.status1 = CanMessageData.get2ByteUInt(data, 20);
            this.status2 = CanMessageData.get2ByteUInt(data, 22);
            this.engineInstance = CanMessageData.get1ByteUInt(data, 0);
            this.engineOilPressure = CanMessageData.get2ByteUDouble(data, 1, 100);
            this.engineOilTemperature = CanMessageData.get2ByteUDouble(data, 3, 0.1);
            this.engineCoolantTemperature = CanMessageData.get2ByteUDouble(data, 5, 0.01);
            this.alternatorVoltage = CanMessageData.get2ByteDouble(data, 7, 0.01);
            this.fuelRate = CanMessageData.get2ByteDouble(data, 9, 0.1);
            this.engineHours = CanMessageData.get4ByteUDouble(data, 11, 1);
            this.engineCoolantPressure = CanMessageData.get2ByteUDouble(data, 15, 100);
            this.engineFuelPressure = CanMessageData.get2ByteUDouble(data, 17, 1000);
            this.reserved = CanMessageData.get1ByteUInt(data, 19);
            this.engineLoad = CanMessageData.get1ByteUInt(data, 24);
            this.engineTorque = CanMessageData.get1ByteUInt(data, 25);

            List<String> statusMesssagesList = new ArrayList<String>();
            if ((status1&0x01) == 0x01 ) statusMesssagesList.add("Check Engine");
            if ((status1 & 0x02) == 0x02) statusMesssagesList.add("Over Temperature");
            if ((status1 & 0x04) == 0x04) statusMesssagesList.add("Low Oil Pressure");
            if ((status1 & 0x08) == 0x08) statusMesssagesList.add("Low Oil Level");
            if ((status1 & 0x10) == 0x10) statusMesssagesList.add("Low Fuel Pressure");
            if ((status1 & 0x20) == 0x20) statusMesssagesList.add("Low System Voltage");
            if ((status1 & 0x40) == 0x40) statusMesssagesList.add("Low Coolant Level");
            if ((status1 & 0x80) == 0x80) statusMesssagesList.add("Water Flow");
            if ((status1 & 0x100) == 0x100) statusMesssagesList.add("Water In Fuel");
            if ((status1 & 0x200) == 0x200) statusMesssagesList.add("Charge Indicator");
            if ((status1 & 0x400) == 0x400) statusMesssagesList.add("Preheat Indicator");
            if ((status1 & 0x800) == 0x800) statusMesssagesList.add("High Boost Pressure");
            if ((status1 & 0x1000) == 0x1000) statusMesssagesList.add("Rev Limit Exceeded");
            if ((status1 & 0x2000) == 0x2000) statusMesssagesList.add("EGR System");
            if ((status1 & 0x4000) == 0x4000) statusMesssagesList.add("Throttle Position Sensor");
            if ((status1 & 0x8000) == 0x8000) statusMesssagesList.add("Emergency Stop");        
            if ((status2 & 0x01) == 0x01) statusMesssagesList.add("Warning Level 1");
            if ((status2 & 0x02) == 0x02) statusMesssagesList.add("Warning Level 2");
            if ((status2 & 0x04) == 0x04) statusMesssagesList.add("Power Reduction");
            if ((status2 & 0x08) == 0x08) statusMesssagesList.add(" Maintenance Needed");
            if ((status2 & 0x10) == 0x10) statusMesssagesList.add("Engine Comm Error");
            if ((status2 & 0x20) == 0x20) statusMesssagesList.add("Sub or Secondary Throttle");
            if ((status2 & 0x40) == 0x40) statusMesssagesList.add("Neutral Start Protect");
            if ((status2 & 0x80) == 0x80) statusMesssagesList.add("Engine Shutting Down");
            this.statusMessages = String.join(",",statusMesssagesList);
        }

        public static CanMessageData encode( int engineInstance,
                                  double engineOilPressure,
                                  double engineOilTemperature,
                                  double engineCoolantTemperature,
                                  double alternatorVoltage,
                                  double fuelRate,
                                  double engineHours,
                                  double engineCoolantPressure,
                                  double engineFuelPressure,
                                  byte engineLoad,
                                  byte engineTorque,
                                  int status1,
                                  int status2) {
            CanMessageData b = new CanMessageData(PGN, 26);
            b.set1ByteUInt(0, engineInstance);
            b.set2ByteUDouble(1, engineOilPressure, 100);
            b.set2ByteUDouble(3, engineOilTemperature, 0.1);
            b.set2ByteUDouble(5, engineCoolantTemperature, 0.01);
            b.set2ByteDouble(7, alternatorVoltage, 0.01);
            b.set2ByteDouble(9, fuelRate, 0.1);
            b.set4ByteUDouble(11, engineHours, 1);
            b.set2ByteUDouble(15, engineCoolantPressure, 100);
            b.set2ByteUDouble(17, engineFuelPressure, 1000);
            b.set1ByteUInt(19, 0xff); // reserved
            b.set2ByteUInt(20, status1);
            b.set2ByteUInt(22, status2);
            b.set1ByteUInt(24, engineLoad);
            b.set1ByteUInt(25, engineTorque);
            return b;
        }

    }

    public static class PGN127488RapidEngineData extends BaseCanMessage {

        public final int engineInstance;
        public final double engineSpeed;
        public final double engineBoostPressure;
        public final int engineTiltTrim;

        public final static int PGN = 127488;

        PGN127488RapidEngineData(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"'RapidEngineData'");
            engineInstance = CanMessageData.get1ByteUInt(data, 0);
            engineSpeed = CanMessageData.get2ByteUDouble(data, 1, 0.25); // RPM
            engineBoostPressure = CanMessageData.get2ByteUDouble(data, 3, 100);
            engineTiltTrim = CanMessageData.get1ByteInt(data, 5);
        }

        public static CanMessageData encode( int engineInstance, double engineSpeed, double engineBoostPressure, int engineTiltTrim) {
            CanMessageData b = new CanMessageData(PGN, 8);
            b.set1ByteUInt(0, engineInstance);
            b.set2ByteUDouble(1, engineSpeed, 0.25);
            b.set2ByteUDouble(3, engineBoostPressure, 100);
            b.set1ByteInt(5, engineTiltTrim);
            b.set1ByteUInt(7, CanMessageData.n2kUInt8NA);
            return b;
        }
    }

    public static class PGN130312Temperature extends BaseCanMessage {
        public final int sid;
        public final int instance;
        public  final double actualTemperature;
        public final double requestedTemperature;
        public final  N2KReference.TemperatureSource source;

        public final static int PGN = 130312;

        PGN130312Temperature(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp, "Temperature");
            sid = CanMessageData.get1ByteUInt(data, 0);
            instance = CanMessageData.get1ByteUInt(data, 1);
            this.source =  N2KReference.TemperatureSource.lookup(CanMessageData.get1ByteUInt(data, 2));
            actualTemperature = CanMessageData.get2ByteUDouble(data, 3, 0.01);
            requestedTemperature = CanMessageData.get2ByteUDouble(data, 5, 0.01);
        }

        public static CanMessageData encode( int sid, int instance, double actualTemperature, double requestedTemperature, N2KReference.TemperatureSource source) {
            return encode(sid,instance, actualTemperature,  requestedTemperature, source.id);
        }
        public static CanMessageData encode( int sid, int instance, double actualTemperature, double requestedTemperature, int sourceId) {
            CanMessageData b = new CanMessageData(PGN, 8);
            b.set1ByteUInt(0, sid);
            b.set1ByteUInt(1, instance);
            b.set1ByteUInt(2, sourceId);
            b.set2ByteUDouble(3, actualTemperature, 0.01);
            b.set2ByteUDouble(5, requestedTemperature, 0.01);
            b.set1ByteUInt(7, CanMessageData.n2kUInt8NA);
            return b;
        }


    }



    public static class PGN127505FluidLevel extends BaseCanMessage {
        public final int instance;
        public final N2KReference.TankType fluidType;
        public final double fluidLevel;
        public final double fluidCapacity;

        public final static int PGN = 127505;

        PGN127505FluidLevel(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"FluidLevel");
            int b = CanMessageData.get1ByteUInt(data, 0);
            instance = b & 0x0f;
            fluidType =  N2KReference.TankType.lookup(  (b >> 4) & 0x0f);
            fluidLevel = CanMessageData.get2ByteDouble(data, 1, 0.004);
            fluidCapacity = CanMessageData.get4ByteUDouble(data, 3, 0.1);
        }
        public static CanMessageData encode( int instance, N2KReference.TankType fluidType, double fluidLevel, double fluidCapacity) {
            CanMessageData b = new CanMessageData(PGN, 8);
            b.set1ByteUInt(0, ((fluidType.id<<4) & 0xf0) | (instance & 0x0f));
            b.set2ByteDouble(1, fluidLevel, 0.004);
            b.set4ByteUDouble(3, fluidCapacity, 0.1);
            b.set1ByteUInt(7, CanMessageData.n2kUInt8NA);
            return b;
        }


    }

    @Override
    public CanMessage handleMessage(int pgn, int timeStamp, byte source, byte[] data) {
        switch (pgn) {
            case PGN127489EngineDynamicParam.PGN: return new PGN127489EngineDynamicParam(pgn, timeStamp, source, data);
            case PGN127488RapidEngineData.PGN: return new PGN127488RapidEngineData(pgn, timeStamp, source, data);
            case PGN130312Temperature.PGN: return new PGN130312Temperature(pgn, timeStamp, source, data);
            case PGN127505FluidLevel.PGN: return new PGN127505FluidLevel(pgn, timeStamp, source, data);
        }
        return null;
    }

    private static final int[] pgns = {
            PGN127489EngineDynamicParam.PGN,
            PGN127488RapidEngineData.PGN,
            PGN130312Temperature.PGN,
            PGN127505FluidLevel.PGN};

    @Override
    public int[] getPgns() {
        return pgns;
    }
}

