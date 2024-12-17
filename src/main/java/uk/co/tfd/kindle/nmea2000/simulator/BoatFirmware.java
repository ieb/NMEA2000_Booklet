package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.N2KReference;
import uk.co.tfd.kindle.nmea2000.can.NavMessageHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class BoatFirmware {
    private double aws = 20.3/CanMessageData.scaleToKnots;
    private double awa = 44.2/CanMessageData.scaleToDegrees;
    private double dbt = 12.3;
    private double offset = 0.01;
    private double hdm = 182.3/CanMessageData.scaleToDegrees;
    private double deviation = 0.5/CanMessageData.scaleToDegrees;
    private double variation = 1.1/CanMessageData.scaleToDegrees;
    private double rudderPossition = -5.3/CanMessageData.scaleToDegrees;
    private double yaw = 182.3/CanMessageData.scaleToDegrees;
    private double pitch = 5/CanMessageData.scaleToDegrees;
    private double roll = 25/CanMessageData.scaleToDegrees;
    private double stw = 8.9/CanMessageData.scaleToKnots;
    private double cog = 190/CanMessageData.scaleToDegrees;
    private double set = 2.3/CanMessageData.scaleToDegrees;
    private double sog = 8.5/CanMessageData.scaleToKnots;
    private double drift = 1.2/CanMessageData.scaleToKnots;
    private int daysSince1970 = (int)(System.currentTimeMillis()/(24*3600000));
    private double secondsSinceMidnight = (System.currentTimeMillis()%(24*3600000))/1000;
    private double log = 1232.5/CanMessageData.scaleToNm;
    private double trip = 892/CanMessageData.scaleToNm;
    private double xte = 0.12/CanMessageData.scaleToNm;
    private double latitude = 55.32234234234;
    private double longitude = 01.2342342323;
    private double altitude = 2.3;
    private int nsatelites = 15;
    private double hdop = 2.12;
    private double pdop = 1.23;
    private double geodalSeperation = 1.232;
    private NavMessageHandler.ReferenceStation[] stations = new NavMessageHandler.ReferenceStation[] {
            new NavMessageHandler.ReferenceStation(12, N2KReference.GnssType.GPSSBASWAAS, 12.3),
            new NavMessageHandler.ReferenceStation(15, N2KReference.GnssType.GPSSBASWAAS, 12.3),
    };
    private double vdop = 2.31;
    private double tdop = 2.42;
    private double humidity = 67.3;
    private double atmosphericPressure = 1023/CanMessageData.scaleToMBar;
    private double outsideTemperature = 20.2 + 273.15;

    public BoatFirmware(FirmwareSimulator simulator) {
        simulator.addSender(new BoatFirmware.Wind(this));
        simulator.addSender(new BoatFirmware.Depth(this));
        simulator.addSender(new BoatFirmware.Heading(this));
        simulator.addSender(new BoatFirmware.Rudder(this));
        simulator.addSender(new BoatFirmware.Attitude(this));
        simulator.addSender(new BoatFirmware.Speed(this));
        simulator.addSender(new BoatFirmware.DirectionData(this));
        simulator.addSender(new BoatFirmware.Log(this));
        simulator.addSender(new BoatFirmware.SystemTime(this));
        simulator.addSender(new BoatFirmware.XTE(this));
        simulator.addSender(new BoatFirmware.CogSog(this));
        simulator.addSender(new BoatFirmware.RapidPosition(this));
        simulator.addSender(new BoatFirmware.GNSS(this));
    }
    public static class Wind extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Wind(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN130306Wind.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN130306Wind.encode(
                        sid,
                        N2KReference.WindReference.Apparent,
                        boat.aws,
                        boat.awa));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Depth extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Depth(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN128267WaterDepth.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN128267WaterDepth.encode(
                        sid,
                        boat.dbt,
                        boat.offset,
                        CanMessageData.n2kDoubleNA));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Heading extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Heading(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN127250Heading.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN127250Heading.encode(
                        sid,
                        boat.hdm,
                        boat.deviation,
                        boat.variation,
                        N2KReference.HeadingReference.Magnetic));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Rudder extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Rudder(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN127245Rudder.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN127245Rudder.encode(
                        sid,
                        N2KReference.RudderDirectionOrder.NoDirectionOrder,
                        CanMessageData.n2kDoubleNA,
                        boat.rudderPossition));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Attitude extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Attitude(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN127257Attitude.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN127257Attitude.encode(
                        sid,
                        boat.yaw,
                        boat.pitch,
                        boat.roll));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Speed extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Speed(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN128259Speed.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN128259Speed.encode(
                        sid,
                        boat.stw,
                        CanMessageData.n2kDoubleNA,
                        N2KReference.SwrtType.PaddleWheel,
                        1));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class DirectionData extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public DirectionData(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN130577DirectionData.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN130577DirectionData.encode(
                        sid,
                        N2KReference.ResidualMode.Differential,
                        N2KReference.DirectionReference.Magnetic,
                        boat.cog,
                        boat.sog,
                        boat.hdm,
                        boat.stw,
                        boat.set,
                        boat.drift));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class Log extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Log(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN128275DistanceLog.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN128275DistanceLog.encode(
                        boat.daysSince1970,
                        boat.secondsSinceMidnight,
                        boat.log,
                        boat.trip));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class SystemTime extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public SystemTime(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN126992SystemTime.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN126992SystemTime.encode(
                        sid,
                        N2KReference.TimeSource.GPS,
                        boat.daysSince1970,
                        boat.secondsSinceMidnight));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class XTE extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public XTE(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN129283CrossTrackError.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN129283CrossTrackError.encode(
                        sid,
                        N2KReference.XteMode.Simulator,
                        N2KReference.YesNo.Yes,
                        boat.xte));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }
    public static class CogSog extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public CogSog(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN129026COGSOGRapid.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN129026COGSOGRapid.encode(
                        sid,
                        N2KReference.HeadingReference.TRue,
                        boat.cog,
                        boat.sog));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 1501;
            return 1501;
        }
    }

    public static class RapidPosition extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public RapidPosition(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN129025RapidPosition.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN129025RapidPosition.encode(
                        boat.latitude,
                        boat.longitude));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 250;
            return 250;
        }
    }

    public static class  GNSS extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public GNSS(BoatFirmware boat) {
            this.boat = boat;
        }
        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN129029GNSS.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN129029GNSS.encode(
                        sid,
                        boat.daysSince1970,
                        boat.secondsSinceMidnight,
                        boat.latitude,
                        boat.longitude,
                        boat.altitude,
                        N2KReference.GnssType.GPSSBASWAASGLONASS,
                        N2KReference.GnssMethod.DGNSS,
                        N2KReference.GnssIntegrity.Safe,
                        boat.nsatelites,
                        boat.hdop,
                        boat.pdop,
                        boat.geodalSeperation,
                        boat.stations
                        ));
                sid++;
            }
            if (filter.shouldSend(NavMessageHandler.PGN129539GNSDOPS.PGN)) {
                byte[] message = new byte[8];

                send(out, NavMessageHandler.PGN129539GNSDOPS.encode(
                        sid,
                        N2KReference.GnssMode.Mode3D,
                        N2KReference.GnssMode.Mode3D,
                        boat.hdop,
                        boat.vdop,
                        boat.tdop
                ));
                sid++;
            }
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }

    public static class  Environment extends  Sender {
        byte sid = 0;
        private final BoatFirmware boat;
        public Environment(BoatFirmware boat) {
            this.boat = boat;
        }

        @Override
        public int send(OutputStream out, PGNFilter filter)   throws IOException {
            if (filter.shouldSend(NavMessageHandler.PGN130313Humidity.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN130313Humidity.encode(
                        sid,
                        1,
                        N2KReference.HumiditySource.Outside,
                        boat.humidity,
                        CanMessageData.n2kDoubleNA));
                sid++;
            }
            if (filter.shouldSend(NavMessageHandler.PGN130314Pressure.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN130314Pressure.encode(
                        sid,
                        1,
                        N2KReference.PressureSource.Atmospheric,
                        boat.atmosphericPressure));
            }
            if (filter.shouldSend(NavMessageHandler.PGN130311EnvironmentParameters.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN130311EnvironmentParameters.encode(
                            sid,
                            N2KReference.TemperatureSource.OutsideTemperature,
                            N2KReference.HumiditySource.Outside,
                            boat.outsideTemperature,
                            boat.humidity,
                            boat.atmosphericPressure));
            }
            if (filter.shouldSend(NavMessageHandler.PGN130316TemperatureExtended.PGN)) {
                byte[] message = new byte[8];
                send(out, NavMessageHandler.PGN130316TemperatureExtended.encode(
                        sid,
                        1,
                        N2KReference.TemperatureSource.OutsideTemperature,
                        boat.outsideTemperature,
                        CanMessageData.n2kDoubleNA));
            }
            sid++;
            nextSend = System.currentTimeMillis() + 5000;
            return 5000;
        }
    }
}
