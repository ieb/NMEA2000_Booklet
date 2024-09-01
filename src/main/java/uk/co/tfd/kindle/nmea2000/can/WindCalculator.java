package uk.co.tfd.kindle.nmea2000.can;


/**
 * If this is added as a listener it will emit CanMessage implementations of the following PGNs
 * 130306 True Boat wind when BoatSpeed + Apparent Wind are received.
 */
public class WindCalculator implements CanMessageListener {

    private final CanMessageProducer producer;
    private long lastUpdate = 0;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double twa = CanMessageData.n2kDoubleNA;
    private double tws = CanMessageData.n2kDoubleNA;
    private long lastWindUpdate = 0;
    private long lastStwUpdate = 0;


    public WindCalculator(CanMessageProducer producer) {

        this.producer = producer;
    }

    public static int[] getSourcePgns() {
        return new int[]{
                NavMessageHandler.PGN130306Wind.PGN,
                NavMessageHandler.PGN128259Speed.PGN
        };
    }

    /**
     * From a listener point of view, this
     * component does not require any source PGNs since any component
     * that will consume can messages must depend on the source PGNs
     * That way the component can be present as a listener, but will not
     * begin to emit messages until the sources become available.
     * @return
     */
    @Override
    public int[] getPgns() {
        return new int[0];
    }

    @Override
    public void onDrop(int pgn) {

    }

    @Override
    public void onUnhandled(int pgn) {

    }

    public void calculateTrueWind() {
        if (awa == CanMessageData.n2kDoubleNA
                || aws == CanMessageData.n2kDoubleNA
                || stw == CanMessageData.n2kDoubleNA) {
            twa = CanMessageData.n2kDoubleNA;
            tws = CanMessageData.n2kDoubleNA;
        } else {
            double apparentX = Math.cos(awa) * aws;
            double apparentY = Math.sin(awa) * aws;
            tws = Math.atan2(apparentY, -stw + apparentX);  // twa in radian
            twa = Math.sqrt(Math.pow(apparentY, 2) + Math.pow(-stw + apparentX, 2)); // tws in m/s
            lastUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public void onMessage(CanMessage message) {
        if (message instanceof NavMessageHandler.PGN130306Wind) {
            NavMessageHandler.PGN130306Wind wind = (NavMessageHandler.PGN130306Wind) message;
            if (wind.windReference == N2KReference.WindReference.Apparent) {
                awa = wind.windAngle;
                aws = wind.windSpeed;
                lastWindUpdate = System.currentTimeMillis();
            }
            calculateTrueWind();
            producer.emitMessage(new PGN130306Wind(wind.sid, wind.timestamp, twa, tws, N2KReference.WindReference.TrueBoat));
        } else if (message instanceof NavMessageHandler.PGN128259Speed) {
            NavMessageHandler.PGN128259Speed speed = (NavMessageHandler.PGN128259Speed) message;
            stw = speed.waterReferenced;
            lastStwUpdate = System.currentTimeMillis();
            calculateTrueWind();
            producer.emitMessage(new PGN130306Wind(speed.sid, speed.timestamp, twa, tws, N2KReference.WindReference.TrueBoat));
        } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
            if ( System.currentTimeMillis() - lastWindUpdate  > 30000 ) {
                awa = CanMessageData.n2kDoubleNA;
                aws = CanMessageData.n2kDoubleNA;
            }
            if ( System.currentTimeMillis() - lastStwUpdate  > 30000 ) {
                stw = CanMessageData.n2kDoubleNA;
            }
        }
    }

    public static class PGN130306Wind implements CanMessage {
        public final int pgn;
        public final int src = -1;
        public final String messageName = "Calculated Wind";
        public final int timestamp;
        public final int sid;
        public final N2KReference.WindReference windReference;
        public final double windSpeed;
        public final double windAngle;

        public final static int PGN = 130306;

        PGN130306Wind(int sid, int timeStamp, double windAngle, double windSpeed, N2KReference.WindReference reference) {
            this.pgn = PGN;
            this.sid = sid;
            this.timestamp = timeStamp;
            this.windAngle = windAngle;
            this.windSpeed = windSpeed;
            this.windReference = reference;
        }

        @Override
        public int getPgn() {
            return pgn;
        }
    }


}
