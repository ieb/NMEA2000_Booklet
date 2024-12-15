package uk.co.tfd.kindle.nmea2000.can;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Simulator implements Runnable {

    private static Logger log = LoggerFactory.getLogger(Simulator.class);
    private final CanMessageProducer producer;
    private final Polar polar;
    private long lastWind = 0;
    private long lastSpeed = 0;
    private long lastAttitude = 0;
    private long lastHeading = 0;
    private long lastVariation = 0;
    private boolean running;
    private int sid;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double stw = CanMessageData.n2kDoubleNA;
    private double roll = CanMessageData.n2kDoubleNA;
    private double hdt = CanMessageData.n2kDoubleNA;
    private double var = CanMessageData.n2kDoubleNA;

    public Simulator(CanMessageProducer producer, Polar polar) {
        this.producer = producer;
        this.polar = polar;

    }

    public void start() {
        if ( !running ) {
            running = true;
            Thread thread = new Thread(this);
            thread.start();
        }
    }
    public void stop() {
        running = false;
    }

    public void updateModel() {
        long now = System.currentTimeMillis();
        double scale = 1.2+Math.sin((now%60000)*2*Math.PI/60000)+(Math.random()-0.5)*0.05;
        aws = ((scale * 10) + 3)/ CanMessageData.scaleToKnots ;  // 10kn mean, +- 7kn
        roll = (((now%15000)*Math.PI/3)/15000)-(Math.PI/6); // 15s 30 degree roll
        hdt = (((now%120000)*2*Math.PI)/120000); // one revolution every 120s
        awa = hdt - Math.PI/6;
        if ( awa > Math.PI) {
            awa = awa - Math.PI*2;
        }
        if ( Math.abs(awa) < 35/CanMessageData.scaleToDegrees) {
            if ( awa < 0 ) {
                awa = awa - 35/CanMessageData.scaleToDegrees;
            } else {
                awa = awa +  35/CanMessageData.scaleToDegrees;
            }
        }
        double apparentX = Math.cos(awa) * aws;
        double apparentY = Math.sin(awa) * aws;
        double twa = Math.atan2(apparentY, -stw + apparentX);  // twa in radian
        double tws = Math.sqrt(Math.pow(apparentY, 2) + Math.pow(-stw + apparentX, 2)); // tws in m/s
        stw = polar.calcPolarSpeed(tws, twa) * (1.1-(Math.random()*0.4)); // from 70% to 110%
        var = 3/CanMessageData.scaleToDegrees; // fixed 3 degrees
    }


    public void run() {
        while(running) {
            long now = System.currentTimeMillis();
            sid++;
            updateModel();
            if (now - lastWind > 505) {
                NavMessageHandler.PGN130306Wind wind = new NavMessageHandler.PGN130306Wind(
                        sid, awa, aws, N2KReference.WindReference.Apparent);
                producer.emitMessage(wind);
            }
            if (now - lastSpeed > 542) {
                NavMessageHandler.PGN128259Speed speed = new NavMessageHandler.PGN128259Speed(
                        sid,
                        stw,
                        CanMessageData.n2kDoubleNA,
                        N2KReference.SwrtType.PaddleWheel,
                        1
                );
                producer.emitMessage(speed);
            }
            if (now - lastAttitude > 5003) {
                NavMessageHandler.PGN127257Attitude attitude = new NavMessageHandler.PGN127257Attitude(
                        sid,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA,
                        roll
                );
                producer.emitMessage(attitude);
            }
            if (now - lastHeading > 1023) {
                NavMessageHandler.PGN127250Heading heading = new NavMessageHandler.PGN127250Heading(
                        sid,
                        hdt,
                        CanMessageData.n2kDoubleNA,
                        CanMessageData.n2kDoubleNA,
                        N2KReference.HeadingReference.Magnetic
                );
                producer.emitMessage(heading);
            }
            if (now - lastVariation > 15000) {
                NavMessageHandler.PGN127258MagneticVariation variation = new NavMessageHandler.PGN127258MagneticVariation(
                        sid,
                        var,
                        (int)(System.currentTimeMillis()/(24*3600000)),
                        N2KReference.VariationSource.Wmm2020
                );
                producer.emitMessage(variation);
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("awa:").append(awa*CanMessageData.scaleToDegrees);
        sb.append(" aws:").append(aws*CanMessageData.scaleToKnots);
        sb.append(" stw:").append(stw*CanMessageData.scaleToKnots);
        sb.append(" roll:").append(roll*CanMessageData.scaleToDegrees);
        sb.append(" hdt:").append(hdt*CanMessageData.scaleToDegrees);
        sb.append(" var:").append(var*CanMessageData.scaleToDegrees);
        return sb.toString();
    }

}
