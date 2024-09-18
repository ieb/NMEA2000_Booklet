package uk.co.tfd.kindle.nmea2000.can;

import java.lang.reflect.Field;

public class LeewayCalculator implements CanMessageListener {

    //private static final Logger log = LoggerFactory.getLogger(WindCalculator.class);
    private final CanMessageProducer producer;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double roll = CanMessageData.n2kDoubleNA;
    private double leeway = CanMessageData.n2kDoubleNA;
    private long lastWindUpdate = 0;
    private long lastStwUpdate = 0;
    private int sid = 0;
    private int timestamp = 0;
    private long lastRollUpdate = 0;

    public LeewayCalculator(CanMessageProducer producer) {
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

    public void calculateLeeway() {
        if (awa == CanMessageData.n2kDoubleNA
                || aws == CanMessageData.n2kDoubleNA
                || stw == CanMessageData.n2kDoubleNA
                || roll == CanMessageData.n2kDoubleNA) {
            leeway = CanMessageData.n2kDoubleNA;
        } else {
            if (aws < 30.0/1.943844) {
                if (Math.abs(awa) < Math.PI/2 && stw > 0.5) {
                    // This comes from Pedrick see http://www.sname.org/HigherLogic/System/DownloadDocumentFile.ashx?DocumentFileKey=5d932796-f926-4262-88f4-aaca17789bb0
                    // for aws < 30 and awa < 90. UK  =15 for masthead and 5 for fractional
                    leeway = (5 * roll / (stw * stw));
                } else {
                    leeway = 0;
                }
                CanMessage leewayMessage = new PGN128000Leeway(sid, timestamp, leeway);
                producer.emitMessage(leewayMessage);
            }
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
                calculateLeeway();
            }
        } else if (message instanceof NavMessageHandler.PGN128259Speed) {
            NavMessageHandler.PGN128259Speed speed = (NavMessageHandler.PGN128259Speed) message;
            stw = speed.waterReferenced;
            sid = speed.sid;
            timestamp = speed.timestamp;
            lastStwUpdate = System.currentTimeMillis();
            calculateLeeway();
        } else if (message instanceof NavMessageHandler.PGN127257Attitude) {
            NavMessageHandler.PGN127257Attitude attitude = (NavMessageHandler.PGN127257Attitude) message;
            roll = attitude.roll;
            sid = attitude.sid;
            lastRollUpdate = System.currentTimeMillis();
            calculateLeeway();
        } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
            if ( System.currentTimeMillis() - lastWindUpdate  > 30000 ) {
                awa = CanMessageData.n2kDoubleNA;
                aws = CanMessageData.n2kDoubleNA;
            }
            if ( System.currentTimeMillis() - lastStwUpdate  > 30000 ) {
                stw = CanMessageData.n2kDoubleNA;
            }
            if ( System.currentTimeMillis() - lastRollUpdate  > 30000 ) {
                roll = CanMessageData.n2kDoubleNA;
            }
        }
    }

    public static class PGN128000Leeway implements CanMessage {
        public final int pgn;
        public final int src = -1;
        public final String messageName = "Calculated Leeway";
        public final int timestamp;
        public final int sid;
        public final double leeway;

        public final static int PGN = 128000;

        PGN128000Leeway(int sid, int timeStamp, double leeway) {
            this.pgn = PGN;
            this.sid = sid;
            this.timestamp = timeStamp;
            this.leeway = leeway;
        }



        public static int[] getSourcePGNS() {
            return new int[]{
                    NavMessageHandler.PGN130306Wind.PGN,
                    NavMessageHandler.PGN128259Speed.PGN,
                    NavMessageHandler.PGN127257Attitude.PGN
            };
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
