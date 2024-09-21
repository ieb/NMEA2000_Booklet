package uk.co.tfd.kindle.nmea2000.can;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * If this is added as a listener it will emit CanMessage implementations of the following PGNs
 * 130306 True Boat wind when BoatSpeed + Apparent Wind are received.
 */
public class WindCalculator implements CanMessageListener {

    private static final Logger log = LoggerFactory.getLogger(WindCalculator.class);
    private final CanMessageProducer producer;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double twa = CanMessageData.n2kDoubleNA;
    private double tws = CanMessageData.n2kDoubleNA;
    private long lastWindUpdate = 0;
    private long lastStwUpdate = 0;
    private int sid = 0;
    private int timestamp = 0;


    public WindCalculator(CanMessageProducer producer) {

        this.producer = producer;
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
            if ( stw < 0.2/CanMessageData.scaleToKnots) {
                twa = awa;
                tws = aws;
            } else {
                double apparentX = Math.cos(awa) * aws;
                double apparentY = Math.sin(awa) * aws;
                twa = Math.atan2(apparentY, -stw + apparentX);  // twa in radian
                tws = Math.sqrt(Math.pow(apparentY, 2) + Math.pow(-stw + apparentX, 2)); // tws in m/s
            }
            CanMessage trueWind = new PGN130306Wind(sid, timestamp, twa, tws, N2KReference.WindReference.TrueBoat,
                    stw,
                    awa,
                    aws);
            producer.emitMessage(trueWind);
        }
    }

    @Override
    public void onMessage(CanMessage message) {
        if (message instanceof NavMessageHandler.PGN130306Wind) {
            NavMessageHandler.PGN130306Wind wind = (NavMessageHandler.PGN130306Wind) message;
            if (wind.windReference == N2KReference.WindReference.Apparent) {
                awa = wind.windAngle;
                aws = wind.windSpeed;
                sid = wind.sid;
                timestamp = wind.timestamp;
                lastWindUpdate = System.currentTimeMillis();
                calculateTrueWind();
            }
        } else if (message instanceof NavMessageHandler.PGN128259Speed) {
            NavMessageHandler.PGN128259Speed speed = (NavMessageHandler.PGN128259Speed) message;
            stw = speed.waterReferenced;
            sid = speed.sid;
            timestamp = speed.timestamp;
            lastStwUpdate = System.currentTimeMillis();
            calculateTrueWind();
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
        public final double stw;
        public final double awa;
        public final double aws;

        public static int[] getSourcePGNS() {
            return new int[]{
                    NavMessageHandler.PGN130306Wind.PGN,
                    NavMessageHandler.PGN128259Speed.PGN
            };
        }



        PGN130306Wind(int sid, int timeStamp, double windAngle, double windSpeed, N2KReference.WindReference reference,
                      double stw,
                      double awa,
                      double aws) {
            this.pgn = PGN;
            this.sid = sid;
            this.timestamp = timeStamp;
            this.windAngle = windAngle;
            this.windSpeed = windSpeed;
            this.stw = stw;
            this.awa = awa;
            this.aws = aws;
            this.windReference = reference;
        }

        @Override
        public int getPgn() {
            return pgn;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" Class:").append(this.getClass().getSimpleName());
            for (Field f : this.getClass().getFields()) {
                Object v;
                try {
                    v = f.get(this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                sb.append(" ").append(f.getName()).append(":").append(v);
            }
            return sb.toString();
        }

    }


}
