package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class PerformanceCalculator implements CanMessageListener {

    private static final Logger log = LoggerFactory.getLogger(PerformanceCalculator.class);
    private final CanMessageProducer producer;
    private final Polar polar;
    private int sid = 0;
    private int timestamp = 0;
    private long lastWindUpdate = 0;
    private long lastMessageSend = 0;
    private long lastLeewayUpdate = 0;
    private long lastHeadingUpdate = 0;
    private long lastVariationUpdate = 0;

    // input fields.
    private double twaInput = CanMessageData.n2kDoubleNA;
    private double twsInput = CanMessageData.n2kDoubleNA;
    private double stwInput = CanMessageData.n2kDoubleNA;
    private double awa = CanMessageData.n2kDoubleNA;
    private double aws = CanMessageData.n2kDoubleNA;
    private double hdtInput = CanMessageData.n2kDoubleNA;
    private double variationInput = CanMessageData.n2kDoubleNA;
    private double leewayInput = CanMessageData.n2kDoubleNA;

    // output fields.
    private double polarSpeed = CanMessageData.n2kDoubleNA;
    private double vmg = CanMessageData.n2kDoubleNA;
    private double polarSpeedRatio  = CanMessageData.n2kDoubleNA;
    private double polarVmg = CanMessageData.n2kDoubleNA;
    private Polar.PolarTarget downwindTarget = Polar.PolarTarget.polarTargetNA;
    private Polar.PolarTarget upwindTarget = Polar.PolarTarget.polarTargetNA;
    private double polarVmgRatio = CanMessageData.n2kDoubleNA;
    private double windDirectionTrue = CanMessageData.n2kDoubleNA;
    private double oppositeHeadingTrue = CanMessageData.n2kDoubleNA;
    private double windDirectionMagnetic = CanMessageData.n2kDoubleNA;
    private double oppositeHeadingMagnetic = CanMessageData.n2kDoubleNA;
    private double oppositeTrackTrue = CanMessageData.n2kDoubleNA;
    private double oppositTrackMagnetic = CanMessageData.n2kDoubleNA;
    private long lastCalculate = 0;

    public PerformanceCalculator(CanMessageProducer producer, Polar polar) {
        this.producer = producer;
        this.polar = polar;
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

    /**
     *
     * tws, twa depends on aws, awa, stw
     * leeway depends on aws, aws, stw, roll
     * polarSpeed depends on tws, twa
     *
     *
     */

    private void calculatePerformance() {
        CalculationChanges changes = new CalculationChanges(lastMessageSend, lastCalculate);
        if ( !changes.shouldCalculate() ) {
            return;
        }
        lastCalculate = System.currentTimeMillis();
        polarSpeed = changes.detectChange(polarSpeed,
                polar.calcPolarSpeed(twsInput, twaInput));
        upwindTarget = changes.detectChange(upwindTarget,
                polar.calcPolarTarget(twsInput, twaInput, true));
        downwindTarget = changes.detectChange(downwindTarget,
                polar.calcPolarTarget(twsInput, twaInput, false));
        vmg = changes.detectChange(vmg,
                stwInput *Math.cos(Math.abs((twaInput))),
                stwInput, twaInput);
        if ( CanMessageData.isNa(polarSpeed, stwInput)
            || polarSpeed < 0.01 ) {
            polarSpeedRatio = changes.detectChange(polarSpeedRatio, CanMessageData.n2kDoubleNA);
            polarVmg = changes.detectChange(polarVmg, CanMessageData.n2kDoubleNA);
        } else {
            polarSpeedRatio = changes.detectChange(polarSpeedRatio,
                    stwInput /polarSpeed);
            polarVmg = changes.detectChange(polarVmg,
                    polarSpeed * Math.cos(Math.abs(twaInput)));
        }

        Polar.PolarTarget polarTarget = (Math.abs(twaInput) < Math.PI)?upwindTarget:downwindTarget;
        if ( CanMessageData.isNa(vmg, polarTarget.vmg)
            || polarTarget.vmg < 0.1 ) {
            polarVmgRatio = changes.detectChange(polarVmgRatio, CanMessageData.n2kDoubleNA);
        } else {
            polarVmgRatio = changes.detectChange(polarVmgRatio,
                    vmg/polarTarget.vmg);
        }
        windDirectionTrue = changes.detectChange(windDirectionTrue,
                hdtInput + twaInput,
                hdtInput, twaInput);
        oppositeHeadingTrue = changes.detectChange(oppositeHeadingTrue,
                windDirectionTrue + polarTarget.twa,
                windDirectionTrue, polarTarget.twa );
        windDirectionMagnetic = changes.detectChange(windDirectionMagnetic,
                windDirectionTrue + variationInput,
                windDirectionTrue, variationInput);
        oppositeHeadingMagnetic = changes.detectChange(oppositeHeadingMagnetic,
                oppositeHeadingTrue + variationInput,
                oppositeHeadingTrue, variationInput);
        if ( twaInput > 0 ) {
            oppositeTrackTrue = changes.detectChange(oppositeTrackTrue,
                    oppositeHeadingTrue + leewayInput *2, twaInput,
                    oppositeHeadingTrue, leewayInput) ;
        } else {
            oppositeTrackTrue = changes.detectChange(oppositeTrackTrue,
                    oppositeHeadingTrue - leewayInput *2, twaInput,
                    oppositeHeadingTrue, leewayInput);
        }
        oppositTrackMagnetic =  changes.detectChange(oppositTrackMagnetic,
                oppositeTrackTrue + variationInput,
                oppositeTrackTrue, variationInput);
        if ( changes.shouldSend() ) {
            lastMessageSend = changes.getTimestamp();
            PerformanceCanMessage performanceCanMessage = new PerformanceCanMessage(this);
            log.debug("Emit Performance Message {} ", performanceCanMessage);
            producer.emitMessage(performanceCanMessage);
        }
    }





    @Override
    public void onMessage(CanMessage message) {
        boolean recalculate = false;
        if (message instanceof WindCalculator.PGN130306Wind) {
            WindCalculator.PGN130306Wind trueWind = (WindCalculator.PGN130306Wind) message;
            if ( !CanMessageData.isNa(trueWind.windAngle, trueWind.windSpeed, trueWind.stw)) {
                twaInput = trueWind.windAngle;
                twsInput = trueWind.windSpeed;
                stwInput = trueWind.stw;
                awa = trueWind.awa;
                aws = trueWind.aws;
                sid = trueWind.sid;
                timestamp = trueWind.timestamp;
                lastWindUpdate = System.currentTimeMillis();
                log.debug("Updated Wind {} ", message );
                recalculate = true;
            }
        } else if (message instanceof LeewayCalculator.PGN128000Leeway) {
            LeewayCalculator.PGN128000Leeway leeway = (LeewayCalculator.PGN128000Leeway) message;
            if ( leeway.leeway != CanMessageData.n2kDoubleNA) {
                this.leewayInput = leeway.leeway;
                timestamp = leeway.timestamp;
                sid = leeway.sid;
                lastLeewayUpdate = System.currentTimeMillis();
                recalculate = true;
            }
        } else if (message instanceof NavMessageHandler.PGN127250Heading) {
            NavMessageHandler.PGN127250Heading heading = (NavMessageHandler.PGN127250Heading) message;
            if ( heading.ref == N2KReference.HeadingReference.TRue ) {
                if ( heading.heading != CanMessageData.n2kDoubleNA ) {
                    hdtInput = heading.heading;
                    timestamp = heading.timestamp;
                    sid = heading.sid;
                    lastHeadingUpdate = System.currentTimeMillis();
                    recalculate = true;
                }
            } else if ( heading.ref == N2KReference.HeadingReference.Magnetic ) {
                if (heading.variation != CanMessageData.n2kDoubleNA) {
                    variationInput = heading.variation;
                    timestamp = heading.timestamp;
                    sid = heading.sid;

                    lastVariationUpdate = System.currentTimeMillis();
                    recalculate = true;
                }
                if ( variationInput != CanMessageData.n2kDoubleNA ) {
                    // true to magnetic add variation
                    // magnetic to true subtract variation
                    hdtInput = heading.heading - variationInput;
                    timestamp = heading.timestamp;
                    sid = heading.sid;

                    lastHeadingUpdate = System.currentTimeMillis();
                    recalculate = true;
                }
            }
        } else if (message instanceof NavMessageHandler.PGN127258MagneticVariation) {
            NavMessageHandler.PGN127258MagneticVariation variation = (NavMessageHandler.PGN127258MagneticVariation) message;
            if ( variation.variation != CanMessageData.n2kDoubleNA) {
                variationInput = variation.variation;
                lastVariationUpdate = System.currentTimeMillis();
                log.debug("Updated Variationng {} ", message );
                recalculate = true;
            }
        } else if (message instanceof IsoMessageHandler.CanBusStatus ) {
            if ( System.currentTimeMillis() - lastWindUpdate  > 30000 ) {
                twaInput = CanMessageData.n2kDoubleNA;
                twsInput = CanMessageData.n2kDoubleNA;
                stwInput = CanMessageData.n2kDoubleNA;
                lastWindUpdate = System.currentTimeMillis();
                recalculate = true;
                log.info("True Wind timeout ");
            }
            if ( System.currentTimeMillis() - lastLeewayUpdate  > 30000 ) {
                leewayInput = CanMessageData.n2kDoubleNA;
                lastLeewayUpdate = System.currentTimeMillis();
                recalculate = true;
                log.info("Leeway timeout ");
            }
            if ( System.currentTimeMillis() - lastHeadingUpdate  > 30000 ) {
                hdtInput = CanMessageData.n2kDoubleNA;
                lastHeadingUpdate = System.currentTimeMillis();
                recalculate = true;
                log.info("Leeway timeout ");
            }
            if ( System.currentTimeMillis() - lastVariationUpdate  > 30000 ) {
                variationInput = CanMessageData.n2kDoubleNA;
                lastVariationUpdate = System.currentTimeMillis();
                recalculate = true;
                log.info("Variation timeout ");
            }
        }
        if (recalculate) {
            calculatePerformance();
        }
    }



    public static class CalculationChanges {
        private final long lastMessageSend;
        private final long lastCalculate;
        private final long now = System.currentTimeMillis();
        private int change = 0;

        CalculationChanges(long lastMessageSend, long lastCalculate) {
            this.lastMessageSend = lastMessageSend;
            this.lastCalculate = lastCalculate;
        }
        Polar.PolarTarget detectChange(Polar.PolarTarget before, Polar.PolarTarget after ) {
            if ( before.vmg  != after.vmg
            || before.stw != after.stw
            || before.twa != after.twa) {
                change++;
            }
            return after;
        }

        double detectChange(double before, double after, double ... va ) {
            if ( CanMessageData.isNa(va)) {
                after = CanMessageData.n2kDoubleNA;
            }
            if ( before != after ) {
                change++;
            }
            return after;
        }

        public boolean shouldCalculate() {
            return ((now - lastCalculate) > 500);
        }

        public boolean shouldSend() {
            return ((now - lastMessageSend) > 1000) || (change > 0);
        }

        public long getTimestamp() {
            return now;
        }
    }

    public static class  PerformanceCanMessage implements CanMessage {

        public final double twa;
        public final double tws;
        public final double stw;
        public final double awa;
        public final double aws;
        public final double polarSpeed;
        public final double vmg;
        public final double polarSpeedRatio;
        public final double polarVmg;
        public final double polarVmgRatio;
        public final double windDirectionTrue;
        public final double oppositeHeadingTrue;
        public final double windDirectionMagnetic;
        public final double oppositeHeadingMagnetic;
        public final double oppositeTrackTrue;
        public final double oppositTrackMagnetic;
        public final int timestamp;
        public final int sid;
        public Polar.PolarTarget downwindTarget;
        public Polar.PolarTarget upwindTarget;

        public PerformanceCanMessage(PerformanceCalculator performanceCalculator) {
            polarSpeed = performanceCalculator.polarSpeed;
            vmg = performanceCalculator.vmg;
            polarSpeedRatio = performanceCalculator.polarSpeedRatio;
            polarVmg = performanceCalculator.polarVmg;
            polarVmgRatio = performanceCalculator.polarVmgRatio;
            windDirectionTrue = performanceCalculator.windDirectionTrue;
            oppositeHeadingTrue = performanceCalculator.oppositeHeadingTrue;
            windDirectionMagnetic  = performanceCalculator.windDirectionMagnetic;
            oppositeHeadingMagnetic = performanceCalculator.oppositeHeadingMagnetic;
            oppositeTrackTrue = performanceCalculator.oppositeTrackTrue;
            oppositTrackMagnetic = performanceCalculator.oppositTrackMagnetic;
            timestamp = performanceCalculator.timestamp;
            sid = performanceCalculator.sid;
            twa = performanceCalculator.twaInput;
            tws = performanceCalculator.twsInput;
            stw = performanceCalculator.stwInput;
            awa = performanceCalculator.awa;
            aws = performanceCalculator.aws;
            downwindTarget = performanceCalculator.downwindTarget;
            upwindTarget = performanceCalculator.upwindTarget;
        }

        @Override
        public int getPgn() {
            return 10;
        }

        public static int[] getSourcePGNS() {
            Set<Integer> s = new HashSet<>();
            for ( int pgn : WindCalculator.PGN130306Wind.getSourcePGNS()) {
                s.add(pgn);
            }
            for ( int pgn : LeewayCalculator.PGN128000Leeway.getSourcePGNS()) {
                s.add(pgn);
            }
            s.add(NavMessageHandler.PGN127250Heading.PGN);
            s.add(NavMessageHandler.PGN127258MagneticVariation.PGN);
            // cant cast Integer[] to int[] for some reason;
            int[] pgns = new int[s.size()];
            Integer[] tpgns = s.toArray(new Integer[0]);
            for(int i = 0; i < s.size(); i++) {
                pgns[i] = tpgns[i];
            }
            return pgns;
        }
    }
}
