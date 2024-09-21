package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Polar {
    private final static Logger log = LoggerFactory.getLogger(Polar.class);
    private final String name;
    private final double[] tws;
    private final double[] twa;
    private final double[] stwMatrix; // rows first, containing speed varying by tws for constant twa.
    private final int twsCols;
    private final int twaRows;
    private final double[] upwindVmg; // 3 rows varying by tws.  twa, stw, vmg
    private final double[] downwindVmg; //
    private boolean debug = false; // pairs of twa, vmg for each tws

    public Polar(Map<String, Object> configuration) {
        if ( !configuration.containsKey("polar")) {
            throw new IllegalArgumentException("Polar not specified in configuration");
        }
        Map<String, Object> config = (Map<String, Object>) configuration.get("polar");
        if ( !config.containsKey("polarMap")) {
            throw new IllegalArgumentException("Polar  map not specified in configuration");
        }
        Map<String, Object> polarMap = (Map<String, Object>) config.get("polarMap");
        name = (String) polarMap.get("name");
        tws = toArray(polarMap, "tws", 1.0/CanMessageData.scaleToKnots);
        twa = toArray(polarMap, "twa", 1.0/CanMessageData.scaleToDegrees);
        twsCols = tws.length;
        twaRows = twa.length;
        stwMatrix = loadStw(polarMap,"stw", 1.0/CanMessageData.scaleToKnots);
        if ( polarMap.containsKey("upwindVmg")) {
            upwindVmg = toArray(polarMap, "upwindVmg", 1.0);
            if ( upwindVmg.length != tws.length*3) {
                throw new IllegalArgumentException("Upwing VMG should have been "+(tws.length*3)+" elemants, was "+upwindVmg.length);
            }
            for(int i = 0; i < tws.length; i++) {
                upwindVmg[i] = upwindVmg[i]/CanMessageData.scaleToDegrees; // twa
                upwindVmg[tws.length+i] = upwindVmg[tws.length+i]/CanMessageData.scaleToKnots; //stw
                upwindVmg[tws.length*2+i] = upwindVmg[tws.length*2+i]/CanMessageData.scaleToKnots; //vmg
            }
        } else {
            upwindVmg = buildVmgLookup(true);
        }
        if ( polarMap.containsKey("upwindVmg")) {
            downwindVmg = toArray(polarMap, "downwindVmg", 1.0);
            if ( downwindVmg.length != tws.length*3) {
                throw new IllegalArgumentException("Downwind VMG should have been "+(tws.length*3)+" elemants, was "+downwindVmg.length);
            }
            for(int i = 0; i < tws.length; i++) {
                downwindVmg[i] = downwindVmg[i]/CanMessageData.scaleToDegrees; // twa
                downwindVmg[tws.length+i] = downwindVmg[tws.length+i]/CanMessageData.scaleToKnots; //stw
                downwindVmg[tws.length*2+i] = downwindVmg[tws.length*2+i]/CanMessageData.scaleToKnots; //vmg
            }
        } else {
            downwindVmg = buildVmgLookup(false);
        }
    }


    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    private double[] toArray(Map<String, Object> polarMap, String key, double factor) {
        if ( polarMap.containsKey(key)) {
            List<Double> inputArray = (List<Double>) polarMap.get(key);
            double[] array = new double[inputArray.size()];
            for(int i = 0; i < array.length; i++) {
                array[i] = inputArray.get(i).doubleValue() * factor;
            }
            return array;
        } else {
            throw new IllegalArgumentException("Polar Map array "+key+" missing from configuration");
        }
    }

    /**
     * Read json [] [] organised bu rows first, into a linear array indexed by rows.
     * @param polarMap
     * @param key name of key
     * @return
     */
    private double[] loadStw(Map<String, Object> polarMap, String key, double factor) {
        if ( polarMap.containsKey(key)) {
            List<Object> inputMatrix = (List<Object>) polarMap.get(key);
            if (twaRows !=  inputMatrix.size()) {
                throw new IllegalArgumentException("STW Rows does not match TWA length expected"+twaRows+" got "+inputMatrix.size());
            }
            if ( twsCols != ((List<Object>)inputMatrix.get(0)).size()) {
                throw new IllegalArgumentException("STW Columns does not match TWS length expected"+twsCols+" got "+((List<Object>)inputMatrix.get(0)).size());
            }
            double stwInput[] = new double[twaRows*twsCols];
            for(int r = 0; r < twaRows; r++) {
                List<Double> inputArray = (List<Double>) inputMatrix.get(r);
                if ( inputArray.size() != twsCols ) {
                    throw new IllegalArgumentException("Polar Map matrix columns was  "+inputArray.size()+" expected "+twsCols);
                }
                for(int c = 0; c < twsCols; c++) {
                    stwInput[r*twsCols+c] = inputArray.get(c).doubleValue() * factor;
                }
            }
            return stwInput;
        } else {
            throw new IllegalArgumentException("Polar Map matrix "+key+" missing from configuration");
        }
    }

    private double[] buildVmgLookup(boolean upwind) {
        double direction = 35.0/CanMessageData.scaleToDegrees;
        if ( !upwind) {
            direction = 150.0/CanMessageData.scaleToDegrees;
        }
        double vmgTarget[] = new double[tws.length*3];
        for(int i = 0; i < tws.length; i++) {
            PolarTarget p = calcMaximumVmg(tws[i], direction);
            vmgTarget[i] = p.twa;
            vmgTarget[tws.length+i] = p.stw;
            vmgTarget[tws.length*2+i] = p.vmg;
            log.debug("Target {} {} ", String.format("%4.2f",tws[i]*CanMessageData.scaleToKnots), p);
        }
        return vmgTarget;
    }
    public PolarTarget calcPolarTarget( double inputTws, double inputTwa, boolean upwind ) {
        return calcPolarTarget(inputTws, inputTwa, (upwind)?upwindVmg:downwindVmg);
    }
    public PolarTarget calcPolarTarget( double inputTws, double inputTwa) {
        return calcPolarTarget(inputTws, inputTwa, (Math.abs(inputTwa)<Math.PI/2)?upwindVmg:downwindVmg);
    }
    private PolarTarget calcPolarTarget( double inputTws, double inputTwa, double source[]) {
        int[] is = findIndexes(this.tws, inputTws, 0, this.tws.length);
        double targetTwa = interpolate(inputTws,
                this.tws[is[0]], this.tws[is[1]],
                source[is[0]], source[is[1]]);
        double targetStw = interpolate(inputTws,
                this.tws[is[0]], this.tws[is[1]],
                source[this.tws.length+is[0]], source[this.tws.length+is[1]]);
        double targetVmg = interpolate(inputTws,
                this.tws[is[0]], this.tws[is[1]],
                source[this.tws.length*2+is[0]], source[this.tws.length*2+is[1]]);
        if ( inputTwa < 0 ) {
            targetTwa = -targetTwa;
        }
        return new PolarTarget(targetTwa, targetStw, targetVmg);
    }


    /**
     *
     * @param tws
     * @param twa
     * @return
     * 1.3ms / call (was 10ms with lookup)
     */
    private PolarTarget calcMaximumVmg( double tws, double twa) {
        if ( CanMessageData.isNa(tws, twa)) {
            return PolarTarget.polarTargetNA;
        }
        if ( twa > Math.PI) {
            twa = twa - 2*Math.PI;
        }
        if ( twa < 0) {
            twa = -twa;
        }
        double twal = 0;
        double twah = Math.PI;
        if ( Math.abs(twa) < Math.PI/2 ) {
            twah = Math.PI/2;
        } else {
            twal = Math.PI/2;
            // downwind scan from 90 - 180
        }
        double targetVmg = 0;
        double targetTwa = 0;
        double targetStw = 0;
        int[] twsi = this.findIndexes(this.tws, tws, 0, this.tws.length);
        if ( debug ) {
            if ( tws >= this.tws[twsi[0]] && tws < this.tws[twsi[1]] ) {
                log.info("Ok TWS col {} {} {} ", tws, this.tws[twsi[0]], this.tws[twsi[1]]);
            } else {
                log.info("FAIL TWS col {} {} {} ", tws, this.tws[twsi[0]], this.tws[twsi[1]]);
            }
            log.info("TWA rough range {} {} ", twal, twah);
        }


        // fine scan between tawl and twlh in 1 deg increments
        for(double ttwa = twal; ttwa <= twah; ttwa += Math.PI/180) {
            int twai[] = this.findIndexes(this.twa, ttwa, 0, this.twa.length);
            double tstw = calcPolarSpeed(tws, ttwa, twsi, twai);
            double tvmg = Math.abs(tstw * Math.cos(ttwa));
            if (tvmg > Math.abs(targetVmg)) {
                if ( debug) {
                    log.info("New MAX VMG was:{} now:{} twa:{} ", targetVmg, tvmg, ttwa);
                }
                targetVmg = tvmg;
                targetTwa = ttwa;
                targetStw = tstw;
            }
        }
        if ( twa < 0 ) {
            targetTwa = -targetTwa;
        }
        return new PolarTarget(targetTwa, targetStw, targetVmg);
    }

    public double calcPolarSpeed(double tws, double twa) {
        return calcPolarSpeed(tws, twa, null, null);
    }

    /**
     *
     * @param tws in m/s
     * @param twa in radians
     * @param twsi lo high index of speed.
     * @return polarSpeed in m/s
     * 26ns/calc
     * lookups took 10x this.
     */
    private double calcPolarSpeed(double tws, double twa, int[] twsi, int[] twai) {
        if ( CanMessageData.isNa(tws, twa)) {
            return CanMessageData.n2kDoubleNA;
        }
        if ( twa > Math.PI) {
            twa = twa - 2.0*Math.PI;
        }
        if (twa < 0){
            twa = -twa;
        }
        // after here in Deg and Kn
        if ( twsi == null ) {
            twsi = this.findIndexes(this.tws, tws, 0, this.tws.length);
        }
        if ( twai == null ) {
            twai = this.findIndexes(this.twa, twa, 0, this.twa.length);
        }
        double stwl = this.interpolate(twa,
                this.twa[twai[0]], this.twa[twai[1]],
                this.stwMatrix[twai[0]*twsCols + twsi[0]], this.stwMatrix[twai[1]*twsCols+twsi[0]]);
        // interpolate a stw high value for a given tws and range
        double stwh = this.interpolate(twa,
                this.twa[twai[0]], this.twa[twai[1]],
                this.stwMatrix[twai[0]*twsCols+twsi[1]], this.stwMatrix[twai[1]*twsCols+twsi[1]]);
        // interpolate a stw final value for a given tws and range using the high an low values for twa.
        return this.interpolate(tws,
                this.tws[twsi[0]], this.tws[twsi[1]],
                stwl, stwh);
    }


    private int[] findIndexes(double[] a, double v, int start, int end) {
        int[] idx = new int[2];
        for (int i = start; i < end; i++) {
            if ( a[i] > v ) {
                if ( debug) {
                    log.info("Find Index {} {} {}",i, v, Arrays.toString(a));
                }
                if ( i == 0 ) {
                    idx[0] = 0;
                    idx[1] = 0;
                    return idx;
                } else {
                    idx[0] = i-1;
                    idx[1] = i;
                    return idx;
                }
            }
        }
        idx[0] = a.length-1;
        idx[1] = a.length-1;
        return idx;
    }

    private double interpolate(double x, double xl, double xh, double yl, double yh) {
        double r;
        if ( x >= xh ) {
            r = yh;
        } else if ( x <= xl ) {
            r =  yl;
        } else if ( (xh - xl) < 1.0E-8 ) {
            r =  yl+(yh-yl)*((x-xl)/1.0E-8);
        } else {
            r = yl+(yh-yl)*((x-xl)/(xh-xl));
        }
        return r;
    }


    public static class PolarTarget {
        public final double twa;
        public final double stw;
        public final double vmg;

        public static PolarTarget polarTargetNA = new PolarTarget(CanMessageData.n2kDoubleNA,
                CanMessageData.n2kDoubleNA,
                CanMessageData.n2kDoubleNA);

        PolarTarget(double twa, double stw, double vmg) {
            this.twa = twa;
            this.stw = stw;
            this.vmg = vmg;
        }

        @Override
        public String toString() {
            return String.format("twa:%4.2f stw:%4.2f vmg:%4.2f",
                    twa*CanMessageData.scaleToDegrees,
                    stw*CanMessageData.scaleToKnots,
                    vmg*CanMessageData.scaleToKnots);
        }

    }

}
