package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;

public class ElectricalMessageHandler  implements CanMessageHandler {

    public static class PGN127508DCBatteryStatus extends BaseCanMessage {
        public final int sid;
        public final int instance;
        public final double batteryVoltage;
        public final double batteryCurrent;
        public final double batteryTemperature;

        public final static int PGN = 127508;

        PGN127508DCBatteryStatus(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"DCBatteryStatus");
            instance =  CanMessageData.get1ByteUInt(data, 0);
            batteryVoltage = CanMessageData.get2ByteUDouble(data, 1, 0.01);
            batteryCurrent = CanMessageData.get2ByteDouble(data, 3, 0.1);
            batteryTemperature = CanMessageData.get2ByteUDouble(data, 5, 0.01);
            sid = CanMessageData.get1ByteUInt(data, 7);
        }
        public static int encode(byte[] message,
                                  int sid, 
                                  int instance, 
                                  double batteryVoltage, 
                                  double batteryCurrent, 
                                  double batteryTemperature

        ) {
            CanMessageData.set1ByteUInt(message, 0, instance);
            CanMessageData.set2ByteUDouble(message, 1, batteryVoltage, 0.01);
            CanMessageData.set2ByteDouble(message, 3, batteryCurrent, 0.1);
            CanMessageData.set2ByteUDouble(message, 5, batteryTemperature, 0.01);
            CanMessageData.set1ByteUInt(message, 7, sid);
            return 8;
        }

    }



    public static class PGN127506DCStatus extends BaseCanMessage {


        public final int sid;
        public final int dcInstance;
        public final N2KReference.DcSourceType dcType;
        public final int stateOfCharge;
        public final int stateOfHealth;
        public final double timeRemaining;
        public final double rippleVoltage;
        public final double capacity;

        public final static int PGN = 127506;

        PGN127506DCStatus(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K DC Status");

            sid = CanMessageData.get1ByteUInt(data, 0);
            dcInstance = CanMessageData.get1ByteUInt(data, 1);
            dcType = N2KReference.DcSourceType.lookup( CanMessageData.get1ByteUInt(data, 2)); // lookup
            stateOfCharge = CanMessageData.get1ByteUInt(data, 3);
            stateOfHealth = CanMessageData.get1ByteUInt(data, 4);
            timeRemaining = CanMessageData.get2ByteUDouble(data, 5, 60);
            rippleVoltage = CanMessageData.get2ByteUDouble(data, 7, 0.001);
            capacity = CanMessageData.get2ByteUDouble(data, 9, 3600);
        }
        public static int encode(byte[] message,
                                  int sid, 
                                  int dcInstance, 
                                  N2KReference.DcSourceType dcType, 
                                  int stateOfCharge, 
                                  int stateOfHealth, 
                                  double timeRemaining, 
                                  double rippleVoltage, 
                                  double capacity
        ) {
            CanMessageData.set1ByteUInt(message, 0, sid);
            CanMessageData.set1ByteUInt(message, 1, dcInstance);
            CanMessageData.set1ByteUInt(message, 2, dcType.id);
            CanMessageData.set1ByteUInt(message, 3, stateOfCharge);
            CanMessageData.set1ByteUInt(message, 4, stateOfHealth);
            CanMessageData.set2ByteUDouble(message, 5, timeRemaining, 60);
            CanMessageData.set2ByteUDouble(message, 7, rippleVoltage, 0.001);
            CanMessageData.set2ByteUDouble(message, 9, capacity, 3600);
            return 11;
        }

    }


    public static class PGN127513BatteryConfigStatus extends BaseCanMessage {


        public final int instance;
        public final N2KReference.BatteryType batteryType;
        public final N2KReference.YesNo supportsEqualisation;
        public final N2KReference.BatteryVoltage nominalVoltage;
        public final N2KReference.BatteryChemistry chemistry;
        public final int temperatureCoeffieint;
        public final int chargeEfficiencyFactor;
        public final double peukertExponent;
        public final double capacity;

        public final static int PGN = 127513;

        PGN127513BatteryConfigStatus(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Battery Config Status");

            instance = CanMessageData.get1ByteUInt(data, 0);
            int type = CanMessageData.get1ByteUInt(data, 1);
            batteryType = N2KReference.BatteryType.lookup( type*0x0f);
            supportsEqualisation = N2KReference.YesNo.lookup((type>>6)&0x03);
            int config = CanMessageData.get1ByteUInt(data, 2);
            nominalVoltage = N2KReference.BatteryVoltage.lookup( config&0x0f); // lookup
            chemistry = N2KReference.BatteryChemistry.lookup( (config>>4)&0x0f); // lookup
            capacity = CanMessageData.get2ByteUDouble(data, 3, 60);
            temperatureCoeffieint = CanMessageData.get1ByteInt(data, 5);
            peukertExponent = (500+CanMessageData.get1ByteUInt(data, 6))*0.002;
            chargeEfficiencyFactor = CanMessageData.get1ByteInt(data, 7);
        }
        public static int encode(byte[] message,
                                  int instance, 
                                  N2KReference.BatteryType batteryType, 
                                  N2KReference.YesNo supportsEqualisation, 
                                  N2KReference.BatteryVoltage nominalVoltage, 
                                  N2KReference.BatteryChemistry chemistry, 
                                  int temperatureCoeffieint, 
                                  int chargeEfficiencyFactor, 
                                  double peukertExponent, 
                                  double capacity
        ) {
            CanMessageData.set1ByteUInt(message, 0, instance);
            CanMessageData.set1ByteUInt(message, 1,
                    ((supportsEqualisation.id&0x03)<<6) 
                            | (batteryType.id&0x0f));
            CanMessageData.set1ByteUInt(message, 2,
                    ((chemistry.id&0x0f)<<4)
                            | (nominalVoltage.id&0x0f));
            CanMessageData.set2ByteUDouble(message, 3, capacity, 3600);
            CanMessageData.set1ByteInt(message, 5, temperatureCoeffieint);
            CanMessageData.set1ByteUInt(message, 6, (int)Math.round(peukertExponent/0.002)-500);
            CanMessageData.set1ByteInt(message, 7, chargeEfficiencyFactor);
            return 8;
        }

    }

    public static class PGN130829BMSRegO3 extends BaseCanMessage {


        public final int instance;



        public final int registerLength;
        public final double packVoltage;
        public final double packCurrent;
        public final double remainingCapacity;
        public final double nominalCapacity;
        public final Date manufactureDate;
        public final int chargeCycles;
        public final int ballanceStatus0;
        public final int ballanceStatus1;
        public final int protectionStatus;
        public final int softwareVersion;
        public final int stateOfCharge;
        public final int fetControl;
        public final int nCells;
        public final int nNTC;
        public final double[] temperatures;
        public final int humidity;
        public final int alarmStatus;
        public final double fullChargeCapacity;
        public final double remainingChargeCapacity;
        public final double ballanceCurrent;


        public final static int PGN = 130829;

        public static boolean matches(int pgn, byte[] data) {
            if ( pgn != PGN ) {
                return false;
            }
            if (CanMessageData.get2ByteUInt(data, 0) != 0x9ffe ) {
                return false;
            }
            if (CanMessageData.get1ByteUInt(data,3) != 3) {
                return false;
            }
            return true;
        }

        PGN130829BMSRegO3(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K BMS Status");
            if ( !PGN130829BMSRegO3.matches(pgn, data)) {
                throw new IllegalArgumentException("Proprietary packet doesn;t match class");
            }
            instance = CanMessageData.get1ByteUInt(data, 2);
            registerLength = CanMessageData.get1ByteUInt(data, 4);
            LoggerFactory.getLogger(this.getClass()).info(
                    String.format(" packVoltage %02x %02x  %d %f", data[5], data[6],
                            CanMessageData.get2ByteUInt(data, 5),
                            CanMessageData.get2ByteUDouble(data, 5, 0.01)
                            ));
            packVoltage = CanMessageData.get2ByteUDouble(data, 5, 0.01);
            packCurrent = CanMessageData.get2ByteDouble(data, 7, 0.01);
            remainingCapacity = CanMessageData.get2ByteUDouble(data, 9, 0.01);
            nominalCapacity = CanMessageData.get2ByteUDouble(data, 11, 0.01);
            chargeCycles = CanMessageData.get2ByteUInt(data, 13);
            int md = CanMessageData.get2ByteUInt(data,15);
            GregorianCalendar gc = new GregorianCalendar();
            gc.clear();
//            return (d&0x1f) | (((m&0xff)<<5)&0x1e0) | ((((y-2000)&0xff)<<9)&0xfe00);
            gc.set((((md&0xfe00)>>9)&0xff)+2000, (((md&0x01e0)>>5)&0xff)-1, md&0x1f);
            manufactureDate = gc.getTime();
            ballanceStatus0 = CanMessageData.get2ByteUInt(data, 17);
            ballanceStatus1 = CanMessageData.get2ByteUInt(data, 19);
            protectionStatus = CanMessageData.get2ByteUInt(data, 21);
            softwareVersion = CanMessageData.get1ByteUInt(data, 23);
            stateOfCharge = CanMessageData.get1ByteUInt(data, 24);
            fetControl = CanMessageData.get1ByteUInt(data, 25);
            nCells = CanMessageData.get1ByteUInt(data, 26);
            nNTC = CanMessageData.get1ByteUInt(data, 27);
            if ( nNTC < 20 ) {
                List<Double> t = new ArrayList<>();
                temperatures = new double[nNTC];
                for (int i = 0; i < nNTC; i++) {
                    temperatures[i] = CanMessageData.get2ByteUDouble(data, 28 + 2 * i, 0.1);
                }
                humidity = CanMessageData.get1ByteUInt(data, 28 + 2 * nNTC);
                alarmStatus = CanMessageData.get2ByteUInt(data, 28 + 2 * nNTC + 1);
                fullChargeCapacity = CanMessageData.get2ByteUDouble(data, 28 + 2 * nNTC + 3, 0.01);
                remainingChargeCapacity = CanMessageData.get2ByteUDouble(data, 28 + 2 * nNTC + 5, 0.01);
                ballanceCurrent = CanMessageData.get2ByteUDouble(data, 28 + 2 * nNTC + 7, 0.001);
            } else {
                temperatures = new double[0];
                humidity = CanMessageData.n2kUInt8NA;
                alarmStatus = CanMessageData.n2kUInt16NA;
                fullChargeCapacity = CanMessageData.n2kDoubleNA;
                remainingChargeCapacity = CanMessageData.n2kDoubleNA;
                ballanceCurrent = CanMessageData.n2kDoubleNA;
            }
        }

        public static int encode(byte[] message,
                                  int instance,
                                  double packVoltage,
                                  double packCurrent,
                                  double remainingCapacity,
                                  double nominalCapacity,
                                  Date manufactureDate,
                                  int chargeCycles,
                                  int ballanceStatus0,
                                  int ballanceStatus1,
                                  int protectionStatus,
                                  int softwareVersion,
                                  int stateOfCharge,
                                  int fetControl,
                                  int nCells,
                                  double[] temperatures,
                                  int humidity,
                                  int alarmStatus,
                                  double fullChargeCapacity,
                                  double remainingChargeCapacity,
                                  double ballanceCurrent
        ) {
            CanMessageData.set2ByteUInt(message, 0, 0x9ffe);
            CanMessageData.set1ByteUInt(message, 2, instance);
            CanMessageData.set1ByteUInt(message, 3, 0x03);

            int endTemp = 28+2*temperatures.length;
            CanMessageData.set1ByteUInt(message, 4, endTemp-5+9); // register Length
            CanMessageData.set2ByteUDouble(message, 5, packVoltage,0.01);
            CanMessageData.set2ByteDouble(message, 7, packCurrent,0.01);
            CanMessageData.set2ByteUDouble(message, 9, remainingCapacity,0.01);
            CanMessageData.set2ByteUDouble(message, 11, nominalCapacity,0.01);
            CanMessageData.set2ByteUInt(message, 13, chargeCycles);


            Calendar gc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            gc.setTime(manufactureDate);

            //            return (d&0x1f) | (((m&0xff)<<5)&0x1e0) | ((((y-2000)&0xff)<<9)&0xfe00);
            // gc.set((((md&0xfe00)>>9)&0xff)+2000, (((md&0x01e0)>>5)&0xff)-1, md&0x1f);

            CanMessageData.set2ByteUInt(message, 15,
                    (gc.get(Calendar.DAY_OF_MONTH)&0x1f )
                            | ((((gc.get(Calendar.MONTH)+1)&0xff)<<5)&0x1e0)
                            | ((((gc.get(Calendar.YEAR)-2000)&0xff)<<9)&0xfe00));
            CanMessageData.set2ByteUInt(message, 17, ballanceStatus0);
            CanMessageData.set2ByteUInt(message, 19, ballanceStatus1);
            CanMessageData.set2ByteUInt(message, 21, protectionStatus);
            CanMessageData.set1ByteUInt(message, 23, softwareVersion);
            CanMessageData.set1ByteUInt(message, 24, stateOfCharge);
            CanMessageData.set1ByteUInt(message, 25, fetControl);
            CanMessageData.set1ByteUInt(message, 26, nCells);
            CanMessageData.set1ByteUInt(message, 27, temperatures.length);
            for(int i = 0; i < temperatures.length; i++) {
                CanMessageData.set2ByteUDouble(message, 28+2*i, temperatures[i], 0.1);
            }
            CanMessageData.set1ByteUInt(message, endTemp, humidity);
            CanMessageData.set2ByteUInt(message, endTemp+1, alarmStatus);
            CanMessageData.set2ByteUDouble(message, endTemp+3, fullChargeCapacity, 0.01);
            CanMessageData.set2ByteUDouble(message, endTemp+5, remainingChargeCapacity, 0.01);
            CanMessageData.set2ByteUDouble(message, endTemp+7, ballanceCurrent, 0.001);
            return endTemp+9;
        }

    }


    public static class PGN130829BMSRegO4 extends BaseCanMessage {


        public final int instance;
        public final int registerLength;
        public final double[] cellVoltage;


        public final static int PGN = 130829;

        public static boolean matches(int pgn, byte[] data) {
            if ( pgn != PGN ) {
                return false;
            }
            if (CanMessageData.get2ByteUInt(data, 0) != 0x9ffe ) {
                return false;
            }
            if (CanMessageData.get1ByteUInt(data,3) != 4) {
                return false;
            }
            return true;
        }

        PGN130829BMSRegO4(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K BMS Cell voltages");
            if ( !PGN130829BMSRegO4.matches(pgn, data)) {
                throw new IllegalArgumentException("Proprietary packet doesnt match class");
            }
            instance = CanMessageData.get1ByteUInt(data, 2);
            registerLength = CanMessageData.get1ByteUInt(data, 4);
            cellVoltage = new double[registerLength/2];
            for(int i = 0; i < registerLength/2; i++) {
                 cellVoltage[i] = CanMessageData.get2ByteUDouble(data, 5+i*2, 0.001);
            }
        }
        public static int encode(byte[] message,
                                  int instance,
                                  double[] cellVoltage

        ) {
            CanMessageData.set2ByteUInt(message, 0, 0x9ffe);
            CanMessageData.set1ByteUInt(message, 2, instance);
            CanMessageData.set1ByteUInt(message, 3, 0x04);
            CanMessageData.set1ByteUInt(message, 4, 2*cellVoltage.length);
            for(int i = 0; i <  cellVoltage.length; i++) {
                CanMessageData.set2ByteUDouble(message, 5+i*2, cellVoltage[i],0.001);
            }
            return 5*cellVoltage.length*2;
        }
    }

    public static class PGN130829BMSRegO5 extends BaseCanMessage {


        public final int instance;
        public final int registerLength;
        public final String hwVersion;


        public final static int PGN = 130829;

        public static boolean matches(int pgn, byte[] data) {
            if ( pgn != PGN ) {
                return false;
            }
            if (CanMessageData.get2ByteUInt(data, 0) != 0x9ffe ) {
                return false;
            }
            if (CanMessageData.get1ByteUInt(data,3) != 5) {
                return false;
            }
            return true;
        }

        PGN130829BMSRegO5(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K BMS Hardware Version");
            if ( !PGN130829BMSRegO5.matches(pgn, data)) {
                throw new IllegalArgumentException("Proprietary packet doesn't match class");
            }
            instance = CanMessageData.get1ByteUInt(data, 2);
            registerLength = CanMessageData.get1ByteUInt(data, 4);
            hwVersion = new String(data,5,registerLength, Charset.forName("UTF-8"));
        }
        public static int encode(byte[] message,
                                  int instance,
                                  String hwVersion

        ) {
            CanMessageData.set2ByteUInt(message, 0, 0x9ffe);
            CanMessageData.set1ByteUInt(message, 2, instance);
            CanMessageData.set1ByteUInt(message, 3, 0x05);
            try {
                byte[] b = hwVersion.getBytes("UTF-8");
                CanMessageData.set1ByteUInt(message, 4, b.length);
                for (int i = 0; i < b.length; i++) {
                    message[5 + i] = b[i];
                }
                return 5+b.length;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("UTF8 not supported");
            }
        }

    }




    @Override
    public CanMessage handleMessage(int pgn, int timeStamp, byte source, byte[] data) {
        switch (pgn) {
            case ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN: return new ElectricalMessageHandler.PGN127508DCBatteryStatus(pgn, timeStamp, source, data);
            case ElectricalMessageHandler.PGN127506DCStatus.PGN: return new ElectricalMessageHandler.PGN127506DCStatus(pgn, timeStamp, source, data);
            case ElectricalMessageHandler.PGN127513BatteryConfigStatus.PGN: return new ElectricalMessageHandler.PGN127513BatteryConfigStatus(pgn, timeStamp, source, data);
        }
        if (ElectricalMessageHandler.PGN130829BMSRegO3.matches(pgn, data)) {
            return new ElectricalMessageHandler.PGN130829BMSRegO3(pgn, timeStamp, source, data);
        } else if (ElectricalMessageHandler.PGN130829BMSRegO4.matches(pgn, data)) {
            return new ElectricalMessageHandler.PGN130829BMSRegO4(pgn, timeStamp, source, data);
        } else if (ElectricalMessageHandler.PGN130829BMSRegO5.matches(pgn, data)) {
            return new ElectricalMessageHandler.PGN130829BMSRegO5(pgn, timeStamp, source, data);
        }
        return null;
    }

    private static final int[] pgns = {
            ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN,
            ElectricalMessageHandler.PGN127506DCStatus.PGN,
            ElectricalMessageHandler.PGN127513BatteryConfigStatus.PGN,
            ElectricalMessageHandler.PGN130829BMSRegO3.PGN,
    };

    @Override
    public int[] getPgns() {
        return pgns;
    }

}
