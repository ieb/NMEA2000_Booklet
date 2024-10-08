package uk.co.tfd.kindle.nmea2000.can;

public class NavMessageHandler implements CanMessageHandler {


    public static class PGN126992SystemTime extends BaseCanMessage {

        public final int sid;
        public final N2KReference.TimeSource timeSource;
        public final int systemDate;
        public final double systemTime;

        public final static int PGN = 126992;

        PGN126992SystemTime(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K System Time");
            sid = CanMessageData.get1ByteUInt(data, 0);
            timeSource =  N2KReference.TimeSource.lookup( CanMessageData.get1ByteUInt(data,  1) & 0x0f);
            systemDate = CanMessageData.get2ByteUInt(data, 2);
            systemTime = CanMessageData.get4ByteUDouble(data, 4, 0.0001);
        }
    }

    public static class PGN127245Rudder extends BaseCanMessage {

        public final int instance;
        public final N2KReference.RudderDirectionOrder rudderDirectionOrder;
        public final double angleOrder;
        public final double rudderPosition;

        public final static int PGN = 127245;

        PGN127245Rudder(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Rudder");
            instance = CanMessageData.get1ByteUInt(data, 0);
            rudderDirectionOrder =  N2KReference.RudderDirectionOrder.lookup( CanMessageData.get1ByteUInt(data,  1) & 0x07);
            angleOrder = CanMessageData.get2ByteDouble(data, 2, 0.0001);
            rudderPosition = CanMessageData.get2ByteDouble(data, 4, 0.0001);
        }
    }


    public static class PGN127250Heading extends BaseCanMessage {


        public final int sid;
        public final double heading;
        public final double deviation;
        public final double variation;
        public final N2KReference.HeadingReference ref;

        public final static int PGN = 127250;

        PGN127250Heading(int sid, double heading, double deviation, double variation, N2KReference.HeadingReference ref ) {
            super(PGN, PGN, 0, (int)System.currentTimeMillis(), "N2K Heading");
            this.sid = sid;
            this.heading = heading;
            this.deviation = deviation;
            this.variation = variation;
            this.ref = ref;
        }

        PGN127250Heading(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Heading");
            sid = CanMessageData.get1ByteUInt(data, 0);
            heading = CanMessageData.get2ByteUDouble(data, 1, 0.0001);
            deviation = CanMessageData.get2ByteDouble(data, 3, 0.0001);
            variation = CanMessageData.get2ByteDouble(data, 5, 0.0001);
            ref =  N2KReference.HeadingReference.lookup( CanMessageData.get1ByteUInt(data,  7) & 0x03);
        }
    }


    public static class PGN127251RateOfTurn extends BaseCanMessage {


        public final int sid;
        public final double rateOfTurn;

        public final static int PGN = 127251;

        PGN127251RateOfTurn(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Rate of Turn");
            sid = CanMessageData.get1ByteUInt(data, 0);
            rateOfTurn = CanMessageData.get4ByteDouble(data, 1, 3.125E-08);
        }
    }

    public static class PGN127257Attitude extends BaseCanMessage {


        public final int sid;
        public final double yaw;
        public final double pitch;
        public final double roll;

        public final static int PGN = 127257;

        PGN127257Attitude(int sid, double yaw, double pitch, double roll) {
            super(PGN, PGN, 0, (int) System.currentTimeMillis(), "N2K Attitude");
            this.sid = sid;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
        PGN127257Attitude(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Attitude");
            sid = CanMessageData.get1ByteUInt(data, 0);
            yaw = CanMessageData.get2ByteDouble(data, 1, 0.0001);
            pitch = CanMessageData.get2ByteDouble(data, 3, 0.0001);
            roll = CanMessageData.get2ByteDouble(data, 5, 0.0001);
        }
    }


    public static class PGN127258MagneticVariation extends BaseCanMessage {


        public final int sid;
        public final N2KReference.VariationSource source;
        public final int daysSince1970;
        public final double variation;

        public final static int PGN = 127258;

        PGN127258MagneticVariation(int sid, double variation, int daysSince1970,  N2KReference.VariationSource source) {
            super(PGN, PGN, 0, (int)System.currentTimeMillis(), "N2K Magnetic Variation");
            this.sid = sid;
            this.variation = variation;
            this.daysSince1970 = daysSince1970;
            this.source = source;
        }
        PGN127258MagneticVariation(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Magnetic Variation");
            sid = CanMessageData.get1ByteUInt(data, 0);
            this.source =  N2KReference.VariationSource.lookup( CanMessageData.get1ByteUInt(data,  1) & 0x0f);
            daysSince1970 = CanMessageData.get2ByteUInt(data, 2);
            variation = CanMessageData.get2ByteDouble(data, 4, 0.0001);

        }
    }


    public static class PGN128259Speed extends BaseCanMessage {
        public final int sid;
        public final double waterReferenced;
        public final double groundReferenced;
        public final N2KReference.SwrtType swrt;
        public final int speedDirection;

        public final static int PGN = 128259;

        PGN128259Speed(int sid,  double waterReferenced,  double groundReferenced, N2KReference.SwrtType swrt, int speedDirection ) {
            super(PGN, PGN, 0, (int)System.currentTimeMillis(), "N2K Speed");
            this.sid = sid;
            this.waterReferenced = waterReferenced;
            this.groundReferenced = groundReferenced;
            this.swrt = swrt;
            this.speedDirection = speedDirection;
        }

        PGN128259Speed(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Speed");
            sid = CanMessageData.get1ByteUInt(data, 0);
            waterReferenced = CanMessageData.get2ByteDouble(data, 1, 0.01);
            groundReferenced = CanMessageData.get2ByteDouble(data, 3, 0.01);
            swrt =  N2KReference.SwrtType.lookup( CanMessageData.get1ByteUInt(data,  5));
            speedDirection = (CanMessageData.get1ByteUInt(data, 6) >> 4) & 0x0f;
        }
    }

    public static class PGN128267WaterDepth extends BaseCanMessage {
        public final int sid;
        public final double depthBelowTransducer;
        public final double offset;
        public final double maxRange;

        public final static int PGN = 128267;

        PGN128267WaterDepth(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Water Depth");
            sid = CanMessageData.get1ByteUInt(data, 0);
            depthBelowTransducer = CanMessageData.get4ByteUDouble(data, 1, 0.01);
            offset = CanMessageData.get2ByteDouble(data, 5, 0.001);
            maxRange = CanMessageData.get1ByteUDouble(data, 7, 10);
        }
    }

    public static class PGN128275DistanceLog extends BaseCanMessage {

        public final int daysSince1970;
        public final double secondsSinceMidnight;
        public final double log;
        public final double tripLog;

        public final static int PGN = 128275;

        PGN128275DistanceLog(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Distance Log");
            daysSince1970 = CanMessageData.get2ByteUInt(data, 0);
            secondsSinceMidnight = CanMessageData.get4ByteUDouble(data, 2, 0.0001);
            log = CanMessageData.get4ByteUDouble(data, 6, 1);
            tripLog = CanMessageData.get4ByteUDouble(data, 10, 1);
        }
    }

    public static class PGN129026COGSOGRapid extends BaseCanMessage {
        public final int sid;
        public final N2KReference.HeadingReference ref;
        public final double cog;
        public final double sog;

        public final static int PGN = 129026;

        PGN129026COGSOGRapid(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K COG SOG Rapid");
            sid = CanMessageData.get1ByteUInt(data, 0);
            ref =  N2KReference.HeadingReference.lookup( CanMessageData.get1ByteUInt(data,  1) & 0x03);
            cog = CanMessageData.get2ByteUDouble(data, 2, 0.0001);
            sog = CanMessageData.get2ByteUDouble(data, 4, 0.01);
        }
    }

    public static class PGN129539GNSDOPS extends BaseCanMessage {
        public final int sid;
        public final N2KReference.GnssMode desiredMode;
        public final N2KReference.GnssMode actualMode;
        public final int reserved;
        public final double hdop;
        public final double vdop;
        public final double tdop;

        public final static int PGN = 129539;

        PGN129539GNSDOPS(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K GNSS DOPS");
            int modes = CanMessageData.get1ByteUInt(data, 1);
            sid = CanMessageData.get1ByteUInt(data, 0);
            desiredMode =  N2KReference.GnssMode.lookup( (modes >> 5) & 0x07);
            actualMode =  N2KReference.GnssMode.lookup((modes >> 2) & 0x07);
            reserved = modes & 0x03;
            hdop = CanMessageData.get2ByteDouble(data, 2, 0.01);
            vdop = CanMessageData.get2ByteDouble(data, 4, 0.01);
            tdop = CanMessageData.get2ByteDouble(data, 6, 0.01);

        }
    }

    public static class PGN129025RapidPosition extends BaseCanMessage {

        public final double latitude;
        public final double longitude;

        public final static int PGN = 129025;

        PGN129025RapidPosition(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K GNSS DOPSN2K Rapid Position");
            latitude = CanMessageData.get4ByteDouble(data, 0, 1e-7);
            longitude = CanMessageData.get4ByteDouble(data, 4, 1e-7);
        }
    }

    public static class ReferenceStation {
        public final double ageOfCorrection;
        public final N2KReference.GnssType referenceStationType;
        public final int referenceSationID;


        ReferenceStation(int ind, double ageOfCorrection) {
            referenceStationType =  N2KReference.GnssType.lookup(ind & 0x0f);
            referenceSationID = (ind >> 4);
            this.ageOfCorrection = ageOfCorrection;
        }

    }

    public static class PGN129029GNSS extends BaseCanMessage {

        public final int nReferenceStations;
        public final ReferenceStation[] stations;
        public final int sid;
        public final int daysSince1970;
        public final double secondsSinceMidnight;
        public final double latitude;
        public final double longitude;
        public final double altitude;
        public final N2KReference.GnssType GNSStype;
        public final N2KReference.GnssMethod GNSSmethod;
        public final N2KReference.GnssIntegrity integrety;
        public final int nSatellites;
        public final double hdop;
        public final double pdop;
        public final double geoidalSeparation;

        public final static int PGN = 129029;

        PGN129029GNSS(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K GNSS");
            sid = CanMessageData.get1ByteUInt(data, 0);
            daysSince1970 = CanMessageData.get2ByteUInt(data, 1);
            secondsSinceMidnight = CanMessageData.get4ByteUDouble(data, 3, 0.0001);
            latitude = CanMessageData.get8ByteDouble(data, 7, 1e-16); // 7+8=15
            longitude = CanMessageData.get8ByteDouble(data, 15, 1e-16); // 15+8=23
            altitude = CanMessageData.get8ByteDouble(data, 23, 1e-6); // 23+8=31
            int typeMethod = CanMessageData.get1ByteUInt(data, 31);
            GNSStype = N2KReference.GnssType.lookup( typeMethod & 0x0f);
            GNSSmethod = N2KReference.GnssMethod.lookup( (typeMethod >> 4) & 0x0f);
            integrety = N2KReference.GnssIntegrity.lookup( CanMessageData.get1ByteUInt(data, 32) & 0x03);
            nSatellites = CanMessageData.get1ByteUInt(data, 33);
            hdop = CanMessageData.get2ByteDouble(data, 34, 0.01);
            pdop = CanMessageData.get2ByteDouble(data, 36, 0.01);
            geoidalSeparation = CanMessageData.get4ByteDouble(data, 38, 0.01); // 38+4=42

            nReferenceStations = CanMessageData.get1ByteUInt(data, 42);
            if (nReferenceStations != CanMessageData.n2kUInt8NA) {
                stations = new ReferenceStation[nReferenceStations];
                for (int i = 0; i < nReferenceStations; i++) {
                    int ind = CanMessageData.get2ByteInt(data, 43 + i * 4);
                    stations[i] = new ReferenceStation(ind, CanMessageData.get2ByteUDouble(data, 45 + i * 4, 0.01));
                }
            } else {
                stations = new ReferenceStation[0];
            }

        }
    }


    public static class PGN129283CrossTrackError extends BaseCanMessage {
        public final int sid;
        public final N2KReference.XteMode xteMode;
        public final N2KReference.YesNo navigationTerminated;
        public final double xte;

        public final static int PGN = 129283;

        PGN129283CrossTrackError(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"Cross Track Error");
            sid = CanMessageData.get1ByteUInt(data, 0);
            int xteModeNav = CanMessageData.get1ByteUInt(data, 1);
            xteMode = N2KReference.XteMode.lookup( xteModeNav & 0x0f);
            navigationTerminated = N2KReference.YesNo.lookup( ((xteModeNav >> 6) & 0x01));
            xte = CanMessageData.get4ByteDouble(data, 2, 0.01);

        }
    }

    public static class PGN130306Wind extends BaseCanMessage {
        public final int sid;
        public final N2KReference.WindReference windReference;
        public final double windSpeed;
        public final double windAngle;

        public final static int PGN = 130306;

        PGN130306Wind(int sid,  double windAngle, double windSpeeed, N2KReference.WindReference windReference ) {
            super(PGN, PGN, 0, (int)System.currentTimeMillis(),"Wind");
            this.sid = sid;
            this.windSpeed = windSpeeed;
            this.windAngle = windAngle;
            this.windReference = windReference;
        }
        PGN130306Wind(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"Wind");
            sid = CanMessageData.get1ByteUInt(data, 0);
            windSpeed = CanMessageData.get2ByteUDouble(data, 1, 0.01);
            windReference = N2KReference.WindReference.lookup( CanMessageData.get1ByteUInt(data, 5) & 0x07);
            double wa = CanMessageData.get2ByteUDouble(data, 3, 0.0001);
            if (windReference.id > 1
                 && wa > Math.PI) {
                    wa = wa - (2 * Math.PI);
            }
            windAngle = wa;
        }
    }

    public static class PGN130310OutsideEnvironmentParameters extends BaseCanMessage {
        public final int sid;
        public final double outsideAmbientAirTemperature;
        public final double waterTemperature;
        public final double atmosphericPressure;

        public final static int PGN = 130310;

        PGN130310OutsideEnvironmentParameters(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Outside Environment Parameters");
            sid = CanMessageData.get1ByteUInt(data, 0);
            waterTemperature = CanMessageData.get2ByteUDouble(data, 1, 0.01);
            outsideAmbientAirTemperature = CanMessageData.get2ByteUDouble(data, 3, 0.01);
            atmosphericPressure = CanMessageData.get2ByteUDouble(data, 5, 100);
        }
    }



    public static class PGN130311EnvironmentParameters extends BaseCanMessage {

        public final int sid;
        public final N2KReference.TemperatureSource tempSource;
        public final N2KReference.HumiditySource humiditySource;
        public final double temperature;
        public final double humidity;
        public final double atmosphericPressure;

        public final static int PGN = 130311;

        PGN130311EnvironmentParameters(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Environment Parameters");

            sid = CanMessageData.get1ByteUInt(data, 0);
            int vb = CanMessageData.get1ByteUInt(data, 1);
            tempSource = N2KReference.TemperatureSource.lookup( (vb & 0x3f));
            humiditySource = N2KReference.HumiditySource.lookup( ((vb >> 6) & 0x03));
            temperature = CanMessageData.get2ByteUDouble(data, 2, 0.01);
            humidity = CanMessageData.get2ByteDouble(data, 4, 0.004);
            atmosphericPressure = CanMessageData.get2ByteUDouble(data, 6, 100);
        }
    }

    public static class PGN130313Humidity extends BaseCanMessage {

        public final int sid;
        public final int humidityInstance;
        public final double actualHumidity;
        public final double setHumidity;
        public final N2KReference.HumiditySource humiditySource;

        public final static int PGN = 130313;

        PGN130313Humidity(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Humidity");
            sid = CanMessageData.get1ByteUInt(data, 0);
            humidityInstance = CanMessageData.get1ByteUInt(data, 1);
            humiditySource = N2KReference.HumiditySource.lookup( CanMessageData.get1ByteUInt(data, 2));
            actualHumidity = CanMessageData.get2ByteDouble(data, 3, 0.004);
            setHumidity = CanMessageData.get2ByteDouble(data, 5, 0.004);
        }
    }


    public static class PGN130314Pressure extends BaseCanMessage {

        public final int sid;
        public final int pressureInstance;
        public final double actualPressure;
        public final N2KReference.PressureSource pressureSource;

        public final static int PGN = 130314;

        PGN130314Pressure(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Pressure");
            sid = CanMessageData.get1ByteUInt(data, 0);
            pressureInstance = CanMessageData.get1ByteUInt(data, 1);
            pressureSource = N2KReference.PressureSource.lookup( CanMessageData.get1ByteUInt(data, 2));
            actualPressure = CanMessageData.get4ByteDouble(data, 3, 0.1);
        }
    }


    public static class PGN130315SetPressure extends BaseCanMessage {

        public final int sid;
        public final int pressureInstance;
        public final double setPressure;
        public final N2KReference.PressureSource pressureSource;

        public final static int PGN = 130315;

        PGN130315SetPressure(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Set Pressure");
            sid = CanMessageData.get1ByteUInt(data, 0);
            pressureInstance = CanMessageData.get1ByteUInt(data, 1);
            pressureSource = N2KReference.PressureSource.lookup( CanMessageData.get1ByteUInt(data, 2));
            setPressure = CanMessageData.get4ByteDouble(data, 3, 0.1);
        }
    }

    public static class PGN130316TemperatureExtended extends BaseCanMessage {

        public final int sid;
        public final int tempInstance;
        public final double setTemperature;
        public final N2KReference.TemperatureSource tempSource;
        public final double actualTemperature;

        public final static int PGN = 130316;

        PGN130316TemperatureExtended(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K Temperature Extended");
            sid = CanMessageData.get1ByteUInt(data, 0);
            tempInstance = CanMessageData.get1ByteUInt(data, 1);
            tempSource = N2KReference.TemperatureSource.lookup( CanMessageData.get1ByteUInt(data, 2));
            actualTemperature = CanMessageData.get3ByteUDouble(data, 3, 0.001);
            setTemperature = CanMessageData.get2ByteUDouble(data, 6, 0.1);
        }
    }
    
    
    // typically from a chart plotter, eg e7.
// set and drift are the most interesting and from an e7 these are the only fields
// set.
    public static class PGN130577DirectionData extends BaseCanMessage {


        public final N2KReference.ResidualMode residualMode;
        public final N2KReference.DirectionReference cogReference;
        public final int sid;
        public final double cog;
        public final double sog;
        public final double heading;
        public final double stw;
        public final double set;
        public final double drift;

        public final static int PGN = 130577;

        PGN130577DirectionData(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp,"N2K DirectionData");
            int dataModeReference = CanMessageData.get1ByteUInt(data, 0);
            residualMode = N2KReference.ResidualMode.lookup(dataModeReference & 0x0f);
            cogReference = N2KReference.DirectionReference.lookup( ((dataModeReference >> 4) & 0x03));
            sid = CanMessageData.get1ByteUInt(data, 1);
            cog = CanMessageData.get2ByteUDouble(data, 2, 0.0001); // rad
            sog = CanMessageData.get2ByteUDouble(data, 4, 0.01); // m/s
            heading = CanMessageData.get2ByteUDouble(data, 6, 0.0001); // rad
            stw = CanMessageData.get2ByteUDouble(data, 8, 0.01); // m/s
            set = CanMessageData.get2ByteUDouble(data, 10, 0.0001); // rad
            drift = CanMessageData.get2ByteUDouble(data, 12, 0.01); // m/s
        }
    }




    @Override
    public CanMessage handleMessage(int pgn, int timeStamp, byte source, byte[] data) {
        switch (pgn) {
            case PGN126992SystemTime.PGN: return new PGN126992SystemTime(pgn, timeStamp, source, data);
            case PGN127245Rudder.PGN: return new PGN127245Rudder(pgn, timeStamp, source, data);
            case PGN127250Heading.PGN: return new PGN127250Heading(pgn, timeStamp, source, data);
            case PGN127251RateOfTurn.PGN: return new PGN127251RateOfTurn(pgn, timeStamp, source, data);
            case PGN127257Attitude.PGN: return new PGN127257Attitude(pgn, timeStamp, source, data);
            case PGN127258MagneticVariation.PGN: return new PGN127258MagneticVariation(pgn, timeStamp, source, data);
            case PGN128259Speed.PGN: return new PGN128259Speed(pgn, timeStamp, source, data);
            case PGN128267WaterDepth.PGN: return new PGN128267WaterDepth(pgn, timeStamp, source, data);
            case PGN128275DistanceLog.PGN: return new PGN128275DistanceLog(pgn, timeStamp, source, data);
            case PGN129026COGSOGRapid.PGN: return new PGN129026COGSOGRapid(pgn, timeStamp, source, data);
            case PGN129539GNSDOPS.PGN: return new PGN129539GNSDOPS(pgn, timeStamp, source, data);
            case PGN129025RapidPosition.PGN: return new PGN129025RapidPosition(pgn, timeStamp, source, data);
            case PGN129029GNSS.PGN: return new PGN129029GNSS(pgn, timeStamp, source, data);
            case PGN129283CrossTrackError.PGN: return new PGN129283CrossTrackError(pgn, timeStamp, source, data);
            case PGN130306Wind.PGN: return new PGN130306Wind(pgn, timeStamp, source, data);
            case PGN130310OutsideEnvironmentParameters.PGN: return new PGN130310OutsideEnvironmentParameters(pgn, timeStamp, source, data);
            case PGN130311EnvironmentParameters.PGN: return new PGN130311EnvironmentParameters(pgn, timeStamp, source, data);
            case PGN130313Humidity.PGN: return new PGN130313Humidity(pgn, timeStamp, source, data);
            case PGN130314Pressure.PGN: return new PGN130314Pressure(pgn, timeStamp, source, data);
            case PGN130315SetPressure.PGN: return new PGN130315SetPressure(pgn, timeStamp, source, data);
            case PGN130316TemperatureExtended.PGN: return new PGN130316TemperatureExtended(pgn, timeStamp, source, data);
            case PGN130577DirectionData.PGN: return new PGN130577DirectionData(pgn, timeStamp, source, data);
        }
        return null;
    }

    private static final int[] pgns = {
            PGN126992SystemTime.PGN,
            PGN127245Rudder.PGN,
            PGN127250Heading.PGN,
            PGN127251RateOfTurn.PGN,
            PGN127257Attitude.PGN,
            PGN127258MagneticVariation.PGN,
            PGN128259Speed.PGN,
            PGN128267WaterDepth.PGN,
            PGN128275DistanceLog.PGN,
            PGN129026COGSOGRapid.PGN,
            PGN129539GNSDOPS.PGN,
            PGN129025RapidPosition.PGN,
            PGN129029GNSS.PGN,
            PGN129283CrossTrackError.PGN,
            PGN130306Wind.PGN,
            PGN130310OutsideEnvironmentParameters.PGN,
            PGN130311EnvironmentParameters.PGN,
            PGN130313Humidity.PGN,
            PGN130314Pressure.PGN,
            PGN130315SetPressure.PGN,
            PGN130316TemperatureExtended.PGN,
            PGN130577DirectionData.PGN
    };
    @Override
    public int[] getPgns() {
        return pgns;
    }

}

