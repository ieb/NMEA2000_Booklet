package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.ElectricalMessageHandler;
import uk.co.tfd.kindle.nmea2000.can.N2KReference;
import uk.co.tfd.kindle.nmea2000.can.NavMessageHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class BMSFirmware {

    private int instance = 1;
    private double voltage = 13.4;
    private double current = -23.4;
    private double temperature = 24.2+273.15;
    private int stateOfCharge = 80;
    private int stateOfHealth = 95;
    private double timeRemaining = 120;
    private double rippleVoltage = 0.21;
    private double capacity = 304.0;
    private double remainingCapacity = 297.3;
    private Date manufactureDate = new Date(2023,12,24);
    private int chargeCycles = 23;
    private int ballanceStatus0 = 0x00;
    private int ballanceStatus1 = 0x00;
    private int protectionStatus = 0x00;
    private int softwareVersion  = 10;
    private int fetControl = 0x55;
    private int nCells = 4;
    private double[] temperatures = new double[] { 23.4+273.15, 24.4+273.15, 25.4+273.15,};
    private int humidity = CanMessageData.n2kUInt8NA;
    private int alarmStatus = 0x00;
    private double remaincCapacity= 297.3;
    private double ballanceCurrent =  0.421;
    private double[] cellVoltages = new double[] { 3.145, 3.212, 3.321, 3,452};
    private String hwVersion = "hw:1.212, sw:32.ds";

    public BMSFirmware(FirmwareSimulator simulator) {
        simulator.addSender(new BatteryVoltage(this));
        simulator.addSender(new BatteryConfiguration(this));
        simulator.addSender(new BatteryStatus(this));
        simulator.addSender(new BMSRegister03(this));
        simulator.addSender(new BMSRegister04(this));
        simulator.addSender(new BMSRegister05(this));
    }

    public static class BatteryVoltage extends  Sender {
        byte sid = 0;
        private final BMSFirmware bms;
        public BatteryVoltage(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException{
            if (filter.shouldSend(ElectricalMessageHandler.PGN127508DCBatteryStatus.PGN)) {
                byte[] message = new byte[8];
                send(out, ElectricalMessageHandler.PGN127508DCBatteryStatus.encode(
                        sid,
                        bms.instance,
                        bms.voltage,
                        bms.current,
                        bms.temperature));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }

    public static class BatteryStatus extends  Sender {
        byte sid = 0;
        private final BMSFirmware bms;
        public BatteryStatus(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)  throws IOException {
            if (filter.shouldSend(ElectricalMessageHandler.PGN127506DCStatus.PGN)) {
                byte[] message = new byte[11];

                send(out, ElectricalMessageHandler.PGN127506DCStatus.encode(
                        sid,
                        bms.instance,
                        N2KReference.DcSourceType.Battery,
                        bms.stateOfCharge,
                        bms.stateOfHealth,
                        bms.timeRemaining,
                        bms.rippleVoltage,
                        bms.capacity));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class BatteryConfiguration extends  Sender {
        private final BMSFirmware bms;
        public BatteryConfiguration(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)  throws IOException {
            if (filter.shouldSend(ElectricalMessageHandler.PGN127513BatteryConfigStatus.PGN)) {
                byte[] message = new byte[8];

                send(out, ElectricalMessageHandler.PGN127513BatteryConfigStatus.encode(
                        bms.instance,
                        N2KReference.BatteryType.LiFePO4,
                        N2KReference.YesNo.No,
                        N2KReference.BatteryVoltage.BatteryVoltage12V,
                        N2KReference.BatteryChemistry.Li,
                        100,
                        100,
                        99.0,
                        bms.capacity*3600));
            }
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }

    public static class BMSRegister03 extends  Sender {
        private final BMSFirmware bms;
        public BMSRegister03(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException{
            if (filter.shouldSend(ElectricalMessageHandler.PGN130829BMSRegO3.PGN)) {
                byte[] message = new byte[100];

                send(out, ElectricalMessageHandler.PGN130829BMSRegO3.encode(
                        bms.instance,
                        bms.voltage,
                        bms.current,
                        bms.remainingCapacity,
                        bms.capacity,
                        bms.manufactureDate,
                        bms.chargeCycles,
                        bms.ballanceStatus0,
                        bms.ballanceStatus1,
                        bms.protectionStatus,
                        bms.softwareVersion,
                        bms.stateOfCharge,
                        bms.fetControl,
                        bms.nCells,
                        bms.temperatures,
                        bms.humidity,
                        bms.alarmStatus,
                        bms.capacity,
                        bms.remaincCapacity,
                        bms.ballanceCurrent));
            }
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }

    public static class BMSRegister04 extends  Sender {
        private final BMSFirmware bms;
        public BMSRegister04(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)  throws IOException {
            if (filter.shouldSend(ElectricalMessageHandler.PGN130829BMSRegO4.PGN)) {
                byte[] message = new byte[100];
                send(out, ElectricalMessageHandler.PGN130829BMSRegO4.encode(
                        bms.instance,
                        bms.cellVoltages));
            }
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }
    public static class BMSRegister05 extends  Sender {
        private final BMSFirmware bms;
        public BMSRegister05(BMSFirmware bms) {
            this.bms = bms;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter) throws IOException {
            if (filter.shouldSend(ElectricalMessageHandler.PGN130829BMSRegO5.PGN)) {
                byte[] message = new byte[100];

                send(out, ElectricalMessageHandler.PGN130829BMSRegO5.encode(
                        bms.instance,
                        bms.hwVersion));
            }
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }
}
