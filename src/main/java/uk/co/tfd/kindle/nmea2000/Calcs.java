package uk.co.tfd.kindle.nmea2000;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.Data.DataKey;
import uk.co.tfd.kindle.nmea2000.Data.DoubleDataValue;

import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 08/06/2020.
 */
public class Calcs extends StatusUpdates implements Data.Listener<Data.DataValue> {
    private static final Logger log = LoggerFactory.getLogger(Calcs.class);
    /*
    // Now in Config to allow any model to be configured.
    private static double[] POGO_1250_TWS = {0,4,6,8,10,12,14,16,20,25,30,35,40,45,50,55,60};
    private static double[] POGO_1250_TWA = {0,5,10,15,20,25,32,36,40,45,52,60,70,80,90,100,110,120,130,140,150,160,170,180};
    private static double[][] POGO_1250_POLAR = {
            {0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0.4,0.6,0.8,0.9,1,1,1,1.1,1.1,1.1,1.1,0.1,0.1,0.1,0,0},
            {0,0.8,1.2,1.6,1.8,2,2,2.1,2.1,2.2,2.2,2.2,0.5,0.2,0.2,0,0},
            {0,1.2,1.8,2.4,2.7,2.9,3,3.1,3.2,3.3,3.3,3.3,1.2,0.5,0.3,0,0},
            {0,1.4,2.1,2.7,3.1,3.4,3.5,3.6,3.6,3.7,3.8,3.7,1.7,0.7,0.4,0,0},
            {0,1.7,2.5,3.2,3.7,4,4.1,4.3,4.3,4.4,4.5,4.4,2.6,1.1,0.4,0,0},
            {0,2.8,4.2,5.4,6.2,6.7,6.9,7.1,7.2,7.4,7.5,7.4,5.6,2.2,0.7,0,0},
            {0,3.1,4.7,5.9,6.7,7,7.2,7.4,7.6,7.8,7.9,7.9,6.5,2.6,0.8,0,0},
            {0,3.5,5.1,6.3,7,7.3,7.5,7.7,7.9,8.1,8.2,8.3,7.4,2.9,1.2,0,0},
            {0,3.8,5.6,6.7,7.3,7.6,7.8,8,8.2,8.4,8.5,8.6,8.2,3,1.3,0,0},
            {0,4.2,6,7,7.7,8,8.2,8.3,8.6,8.9,9,9.1,8.9,3.2,1.4,0,0},
            {0,4.6,6.3,7.3,8,8.3,8.5,8.7,9,9.3,9.5,9.6,9.6,3.8,1.9,0,0},
            {0,4.8,6.6,7.5,8.2,8.6,8.9,9.1,9.5,9.8,10.1,10.4,10.4,4.2,2.1,0,0},
            {0,5,6.9,7.9,8.3,8.8,9.2,9.4,9.9,10.4,10.9,11.3,11.3,4.5,2.3,0,0},
            {0,5.3,7.1,8.1,8.6,8.9,9.3,9.7,10.4,11.1,11.8,12.5,12.5,5.6,3.1,0.6,0.6},
            {0,5.4,7.1,8.2,8.8,9.2,9.5,9.9,10.9,11.9,12.8,14.1,14.1,7.1,4.2,0.7,0.7},
            {0,5.3,7,8.1,8.8,9.4,9.8,10.3,11.2,12.7,14.3,15,15,8.3,5.3,1.5,1.5},
            {0,5,6.8,7.8,8.6,9.4,10,10.6,11.8,13.2,14.9,15.7,15.7,9.4,6.3,1.6,1.6},
            {0,4.5,6.3,7.4,8.3,9,9.8,10.6,12.3,14.4,15.6,16.6,16.6,10.8,7.5,2.5,2.5},
            {0,3.8,5.6,6.9,7.8,8.5,9.2,10,12.2,15,16.3,17.6,17.6,13.2,9.7,3.5,2.6},
            {0,3.2,4.8,6.1,7.1,7.9,8.6,9.3,10.9,14.4,16.8,18.6,18.6,14.9,11.2,3.7,3.7},
            {0,2.7,4.1,5.3,6.4,7.3,8,8.7,10,12.4,15.4,17.9,17.9,15.2,11.6,4.5,3.6},
            {0,2.4,3.6,4.8,5.9,6.8,7.6,8.2,9.4,11.4,14.3,16.6,16.6,15.8,12.5,5,4.2},
            {0,2.2,3.3,4.4,5.5,6.4,7.2,7.9,9,10.6,12.8,15.4,15.4,15.4,12.3,4.6,3.9}
        };
        */
    private final Store store;

    private Polar polar;
    private DataKey magneticVariationKey;
    private DataKey stwKey;
    private DataKey rollKey;
    private DataKey awsKey;
    private DataKey awaKey;
    private DataKey hdtKey;
    private DataKey hdmKey;
    private DataKey twaKey;
    private DataKey twsKey;
    private DataKey leewayKey;


    public Calcs(Store store) {
        // could load from options.
        polar = new Polar();


        this.store = store;

    }

    public void addConfiguration(Map<String, Object> configuration) {
        if ( configuration.containsKey("calcs")) {
            Map<String, Object> calcConfig = (Map<String, Object>) configuration.get("calcs");
            magneticVariationKey = getDataKey(calcConfig, "magneticVariationInput");
            stwKey = getDataKey(calcConfig, "stwInput");
            rollKey = getDataKey(calcConfig, "rollInput");
            awsKey = getDataKey(calcConfig, "awsInput");
            awaKey = getDataKey(calcConfig, "awaInput");
            hdmKey = getDataKey(calcConfig, "hdmInput");
            // calculated
            hdtKey = getDataKey(calcConfig, "hdtOutput");
            twaKey = getDataKey(calcConfig, "twaOutput");
            twsKey = getDataKey(calcConfig, "twsOutput");
            leewayKey  = getDataKey(calcConfig, "leewayOutput");
            log.info("Calcs Configured ");
        } else {
            throw new IllegalArgumentException("Missing calcs configuration key");
        }
        polar.addConfiguration(configuration);

    }

    private DataKey getDataKey(Map<String, Object> calcConfig, String key) {
        if ( calcConfig.containsKey(key) ) {
            if (DataKey.values.containsKey(calcConfig.get(key))) {
                return DataKey.values.get(calcConfig.get(key));
            } else {
                throw new IllegalArgumentException("Configuration Key "+key+" defined as "+calcConfig.get(key)+" is missing");
            }
        } else {
            throw new IllegalArgumentException("Required Configuration missing at "+key);

        }
    }


    public void start() {

        DoubleDataValue magneticVariation = store.get(magneticVariationKey);
        magneticVariation.addListener(this);
        DoubleDataValue stw = store.get(stwKey);
        stw.addListener(this);
        DoubleDataValue attitude = store.get(rollKey);
        attitude.addListener(this);
        DoubleDataValue aws = store.get(awsKey);
        aws.addListener(this);
        DoubleDataValue awa = store.get(awaKey);
        awa.addListener(this);
        DoubleDataValue hdt = store.get(hdtKey);
        hdt.addListener(this);
        this.updateStatus("Loaded Pogo1250 Polars " + polar.getBuildTime() + "ms");
        this.updateStatus("Performance Calcs ready");
    }

    public void stop() {
    }



    public static double correctBearing(double bearing) {
        if ( bearing > 2*Math.PI) {
            return bearing - 2*Math.PI;
        } else if ( bearing < 0) {
            return bearing + 2*Math.PI;
        }
        return bearing;
    }



    private void calcBearing(Store store) {
        DoubleDataValue magneticVariation = store.get(magneticVariationKey);
        DoubleDataValue magneticBearing =  store.get(hdmKey);
        DoubleDataValue trueBearing =  store.get(hdtKey);
        trueBearing.update(correctBearing(trueBearing.value - magneticVariation.value),magneticBearing.timestamp);
    }

    @Override
    public void onUpdate(Data.DataValue d) {

        enhance(d);
    }



    public void enhance(Data.DataValue d) {
        calcBearing(store);
        DoubleDataValue stw = store.get(stwKey);
        DoubleDataValue roll = store.get(rollKey);


        DoubleDataValue aws = store.get(awsKey);
        DoubleDataValue awa = store.get(awaKey);
        DoubleDataValue twa = store.get(twaKey);
        DoubleDataValue tws = store.get(twsKey);

        DoubleDataValue leeway = store.get(leewayKey);
        if ( Math.abs(awa.value) < Math.PI/2 && aws.value < 30.0/1.943844) {
            if (stw.value > 0.5) {
                // This comes from Pedrick see http://www.sname.org/HigherLogic/System/DownloadDocumentFile.ashx?DocumentFileKey=5d932796-f926-4262-88f4-aaca17789bb0
                // for aws < 30 and awa < 90. UK  =15 for masthead and 5 for fractional
                leeway.update(5 * roll.value / (stw.value * stw.value), roll.timestamp);
            }
        }
        double apparentX = Math.cos(awa.value) * aws.value;
        double apparentY = Math.sin(awa.value) * aws.value;
        twa.update(Math.atan2(apparentY, -stw.value + apparentX),aws.timestamp);
        tws.update(Math.sqrt(Math.pow(apparentY,2) + Math.pow(-stw.value +apparentX,2)), aws.timestamp);
        this.polar.calcPerformance(store);

    }




/*
        if ( tws && twa && stw && hdt ) {
            var performance = this.performance.calcPerformance(tws, twa, stw, hdt, magneticVariation, leeway);
            this.save(state, 'performance.polarSpeed', performance.polarSpeed, timestamp, "m/s", "polar speed at this twa", true);
            this.save(state, 'performance.polarSpeedRatio', performance.polarSpeedRatio, timestamp, "%", "polar speed ratio", true);
            this.save(state, 'performance.oppositeTrackMagnetic', performance.oppositeTrackMagnetic, timestamp, "rad", "opposite track magnetic bearing", true);
            this.save(state, 'performance.oppositeTrackTrue', performance.oppositeTrackTrue, timestamp, "rad", "opposite track true bearing", true);
            this.save(state, 'performance.oppositeHeadingMagnetic', performance.oppositeHeadingMagnetic, timestamp,  "rad", "opposite geading magnetic bearing",true);
            this.save(state, 'performance.oppositeHeadingTrue', performance.oppositeHeadingTrue, timestamp, "rad", "opposite geading true bearing", true);
            this.save(state, 'performance.targetTwa', performance.targetTwa, timestamp, "rad", "target twa on this track for best vmg", true);
            this.save(state, 'performance.targetStw', performance.targetStw, timestamp, "m/s", "target speed on at best vmg and angle", true);
            this.save(state, 'performance.targetVmg', performance.targetVmg, timestamp, "m/s", "target vmg -ve == downwind", true);
            this.save(state, 'performance.vmg', performance.vmg, timestamp, "m/s", "current vmg at polar speed", true);
            this.save(state, 'performance.polarVmg', performance.polarVmg, timestamp, "m/s", "current vmg at best angle", true);
            this.save(state, 'performance.polarVmgRatio', performance.polarVmgRatio, timestamp, "m/s", "ratio between vmg and optimal vmg", true);
            this.save(state, 'environment.wind.windDirectionTrue', performance.windDirectionTrue, timestamp, "rad","True wind direction", true);
            this.save(state, 'environment.wind.windDirectionMagnetic', performance.windDirectionMagnetic, timestamp, "rad","Magnetic wind direction", true);
        }
        this.save(state, 'sys.polarBuild', this.performance.fineBuildTime, timestamp, "ms","Timetaken to build the Polar Table", true);
        this.save(state, 'sys.calcTime', Date.now() - calcStart, timestamp, "ms","Timetaken perform caculations", true);
        if ( window.performance && window.performance.memory  ) {
            this.save(state, 'sys.jsHeapSizeLimit', window.performance.memory.jsHeapSizeLimit , timestamp, "bytes","JS Heap Limit", true);
            this.save(state, 'sys.totalJSHeapSize', window.performance.memory.totalJSHeapSize , timestamp, "bytes","JS Heap Size", true);
            this.save(state, 'sys.usedJSHeapSize', window.performance.memory.usedJSHeapSize , timestamp, "bytes","JS Heap Used", true);
        }
    }
*/

    public static class Polar {
        private static final Logger log = LoggerFactory.getLogger(Polar.class);
        boolean initialised = false;
        String name;
        double[] tws;
        double[] twa;
        double[][] stw;
        double twsstep = 0.1;
        double twastep = 1.0;
        double[] fineTwa;
        double[] fineTws;
        double[] fineStw;
        private long startFineBuild;
        private long endFileBuild;
        private DataKey twsKey;
        private DataKey twaKey;
        private DataKey stwKey;
        private DataKey hdtKey;
        private DataKey magneticVariationKey;
        private DataKey leewayKey;
        private DataKey polarSpeedKey;
        private DataKey polarSpeedRatioKey;
        private DataKey polarVmgKey;
        private DataKey vmgKey;
        private DataKey targetTwaKey;
        private DataKey targetStwKey;
        private DataKey targetVmgKey;
        private DataKey polarVmgRatioKey;
        private DataKey windDirectionTueKey;
        private DataKey windDirectionMagneticKey;
        private DataKey oppositeTrackTrueKey;
        private DataKey oppositeTrackMagneticKey;
        private DataKey oppositeHeadingMagneticKey;

        public Polar() {
        }

        public void addConfiguration(Map<String, Object> configuration) {
            if ( configuration.containsKey("polar")) {
                Map<String, Object> polarConfig = (Map<String, Object>) configuration.get("polar");
                magneticVariationKey = getDataKey(polarConfig, "magneticVariationInput");
                stwKey = getDataKey(polarConfig, "stwInput");
                hdtKey = getDataKey(polarConfig, "hdtInput");
                twaKey = getDataKey(polarConfig, "twaInput");
                twsKey = getDataKey(polarConfig, "twsInput");
                leewayKey  = getDataKey(polarConfig, "leewayInput");

                polarSpeedKey = getDataKey(polarConfig, "polarStwOutput");
                polarSpeedRatioKey  = getDataKey(polarConfig, "polarSpeedRatioOutput");
                polarVmgKey  = getDataKey(polarConfig, "polarVmgOutput");
                vmgKey  = getDataKey(polarConfig, "vmgOutput");
                targetTwaKey  = getDataKey(polarConfig, "targetTwaOutput");
                targetStwKey  = getDataKey(polarConfig, "targetStwOutput");
                targetVmgKey  = getDataKey(polarConfig, "targetVmgOutput");
                polarVmgRatioKey  = getDataKey(polarConfig, "polarVmgRatioOutput");
                windDirectionTueKey  = getDataKey(polarConfig, "windDirectionTrueOutput");
                windDirectionMagneticKey  = getDataKey(polarConfig, "windDirectionMagneticOutput");
                oppositeTrackTrueKey  = getDataKey(polarConfig, "oppositeTrackTrueOutput");
                oppositeTrackMagneticKey  = getDataKey(polarConfig, "oppositeTrackMagneticOutput");
                oppositeHeadingMagneticKey  = getDataKey(polarConfig, "oppositeHeadingMagneticOutput");

                if ( polarConfig.containsKey("polarMap")) {
                    Map<String, Object> polarMap = (Map<String, Object>) polarConfig.get("polarMap");
                    this.name = (String) polarMap.get("name");
                    this.tws = toArray(polarMap, "tws");
                    this.twa = toArray(polarMap, "twa");
                    this.stw = toMatrix(polarMap,"stw");
                    this.checkInput();
                    fineTws = new double[(int)(tws[tws.length-1]/twsstep)];
                    fineTwa = new double[(int)(twa[twa.length-1]/twastep)];
                    fineStw = new double[fineTws.length*fineTwa.length];
                    long buildtime = this.buildFilePolarTable();
                    log.info("Polar Rebuild took {} ms ",buildtime);
                    log.info("Polar Configured with new polar map");
                    initialised = true;
                } else {
                    throw new IllegalArgumentException("No Polar specified in polar configuration, expected polarMap key");
                }

            }
        }

        private double[] toArray(Map<String, Object> polarMap, String key) {
            if ( polarMap.containsKey(key)) {
                List<Double> inputArray = (List<Double>) polarMap.get(key);
                double[] array = new double[inputArray.size()];
                for(int i = 0; i < array.length; i++) {
                    array[i] = inputArray.get(i).doubleValue();
                }
                return array;
            } else {
                throw new IllegalArgumentException("Polar Map array "+key+" missing from configuration");
            }
        }

        private double[][] toMatrix(Map<String, Object> polarMap, String key) {
            if ( polarMap.containsKey(key)) {
                List<Object> inputMatrix = (List<Object>) polarMap.get(key);
                double[][] matrix = new double[inputMatrix.size()][((List<Object>)inputMatrix.get(0)).size()];
                for(int i = 0; i < inputMatrix.size(); i++) {
                    List<Double> inputArray = (List<Double>) inputMatrix.get(i);
                    if ( inputArray.size() != matrix[i].length ) {
                        throw new IllegalArgumentException("Polar Map matrix columns was  "+inputArray.size()+" expected "+matrix[i].length);
                    }
                    for(int j = 0; j < inputArray.size(); j++) {
                        matrix[i][j] = inputArray.get(j).doubleValue();
                    }
                }
                return matrix;
            } else {
                throw new IllegalArgumentException("Polar Map matrix "+key+" missing from configuration");
            }
        }


        private DataKey getDataKey(Map<String, Object> polarConfig, String key) {
            if ( polarConfig.containsKey(key) ) {
                if (DataKey.values.containsKey(polarConfig.get(key))) {
                    return DataKey.values.get(polarConfig.get(key));
                } else {
                    throw new IllegalArgumentException("Configuration Key "+key+" defined as "+polarConfig.get(key)+" is missing");
                }
            } else {
                throw new IllegalArgumentException("Required Configuration missing at "+key);

            }
        }


        public long getBuildTime() {
            return endFileBuild - startFineBuild;
        }

        private void checkInput() {
            if ( twa.length != stw.length) {
                throw new IllegalArgumentException("Polar STW does not have enough rows for the TWA array. Expected:"+twa.length+" Found:"+stw.length);
            }
            for (int i = 0; i < stw.length; i++) {
                if ( tws.length != stw[i].length ) {
                    throw new IllegalArgumentException("Polar STW row "+i+" does not ave enough columns Expected:"+tws.length+" Found:"+stw[i].length);
                }
            }
            for (int i = 1; i < twa.length; i++) {
                if ( twa[i] < twa[i-1] ) {
                    throw new IllegalArgumentException("Polar TWA must be in ascending order and match the columns of stw.");
                }
            }
            for (int i = 1; i < tws.length; i++) {
                if ( tws[i] < tws[i-1] ) {
                    throw new IllegalArgumentException("Polar TWA must be in ascending order and match the rows of stw.");
                }
            }
        }

        private long buildFilePolarTable() {
            startFineBuild = System.currentTimeMillis();
            for (int ia = 0; ia < fineTwa.length; ia++) {
                fineTwa[ia] = ia * twastep * Math.PI / 180; // rad
            }
            for (int is = 0; is < fineTws.length; is++) {
                fineTws[is] = (is * twsstep) / 1.9438444924;  // m/s
            }
            for (int ia = 0; ia < fineTwa.length; ia++) {
                for (int is = 0; is < fineTws.length; is++) {
                    fineStw[ia * fineTws.length + is] = this.calcPolarSpeed(fineTws[is], fineTwa[ia]);
                }
            }
            endFileBuild = System.currentTimeMillis();
            return endFileBuild - startFineBuild;
        }

        private double calcPolarSpeed(double twsv, double twav) {
            // polar Data is in KN and deg
            twsv = twsv*1.9438444924;
            twav = twav*180/Math.PI;
            // after here in Deg and Kn
            int[] twsi = this.findIndexes(tws, twsv);
            int[] twai = this.findIndexes(twa, twav);
            double stwl = this.interpolate(twav, twa[twai[0]], twa[twai[1]], stw[twai[0]][twsi[0]], stw[twai[1]][twsi[0]]);
            // interpolate a stw high value for a given tws and range
            double stwh = this.interpolate(twav, twa[twai[0]], twa[twai[1]], stw[twai[0]][twsi[1]], stw[twai[1]][twsi[1]]);
            // interpolate a stw final value for a given tws and range using the high an low values for twa.
            return this.interpolate(twsv, tws[twsi[0]], tws[twsi[1]], stwl, stwh)/1.9438444924; // in m/s
        }


        private int[] findIndexes(double[] a, double v) {
            int[] idx = new int[2];
            for (int i = 0; i < a.length; i++) {
                if ( a[i] > v ) {
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



        /**
         * Returns polarPerf = {
         vmg : 0,
         polarVmg: 0;
         polarSpeed: 0,
         polarSpeedRatio:  1,
         polarVmgRatio: 1
         }
         Only calcuates polr Vmg ration is targets is defined.
         All inputs outputs are SI
         */

        public void calcPerformance(Store store) {
            if ( !initialised ) {
                return;
            }
            DoubleDataValue tws = store.get(twsKey);
            DoubleDataValue twa = store.get(twaKey);
            DoubleDataValue stw = store.get(stwKey);
            DoubleDataValue hdt = store.get(hdtKey);
            DoubleDataValue magneticVariation = store.get(magneticVariationKey);
            DoubleDataValue leeway = store.get(leewayKey);
            DoubleDataValue polarSpeed = store.get(polarSpeedKey);

            double abs_twa = twa.value;
            if ( twa.value < 0) abs_twa = -twa.value;
            int[] is = this.findIndexes(fineTws, tws.value);
            int[] ia = this.findIndexes(fineTwa, abs_twa);
            polarSpeed.update(fineStw[ia[1] * fineTws.length + is[1]],tws.timestamp);
            DoubleDataValue polarSpeedRatio = store.get(polarSpeedRatioKey);
            if (polarSpeed.value != 0) {
                polarSpeedRatio.update(stw.value/polarSpeed.value, tws.timestamp);
            }

            DoubleDataValue polarVmg = store.get(polarVmgKey);
            polarVmg.update(polarSpeed.value * Math.cos(abs_twa), tws.timestamp);

            DoubleDataValue vmg = store.get(vmgKey);
            vmg.update(stw.value*Math.cos(abs_twa),tws.timestamp);


            // calculate the optimal VMG angles
            double twal = 0;
            double twah = Math.PI;
            if ( abs_twa < Math.PI/2 ) {
                twah = Math.PI/2;
            } else {
                twal = Math.PI/2;
                // downwind scan from 90 - 180
            }
            DoubleDataValue targetVmg = store.get(targetTwaKey);
            DoubleDataValue targetTwa = store.get(targetStwKey);
            DoubleDataValue targetStw = store.get(targetVmgKey);
            for(double ttwa = twal; ttwa <= twah; ttwa += Math.PI/180) {
                ia = this.findIndexes(fineTwa, ttwa);
                double tswt = fineStw[ia[1] * fineTws.length + is[1]];
                double tvmg = tswt*Math.cos(ttwa);
                if ( Math.abs(tvmg) > Math.abs(targetVmg.value) ) {
                    targetVmg.value = tvmg;
                    targetTwa.value = ttwa;
                    targetStw.value = tswt;
                }
            }
            if ( twa.value < 0 ) {
                targetTwa.value = -targetTwa.value;
            }

            targetVmg.update(targetVmg.value, twa.timestamp);
            targetTwa.update(targetTwa.value, twa.timestamp);
            targetStw.update(targetStw.value, twa.timestamp);


            DoubleDataValue polarVmgRatio = store.get(polarVmgRatioKey);

            if (Math.abs(targetVmg.value) > 1.0E-8 ) {
                polarVmgRatio.update(vmg.value/targetVmg.value, twa.timestamp);
            }

            // calculate other track
            DoubleDataValue windDirectionTrue = store.get(windDirectionTueKey);
            DoubleDataValue windDirectionMagnetic = store.get(windDirectionMagneticKey);
            DoubleDataValue oppositeTrackTrue = store.get(oppositeTrackTrueKey);

            windDirectionTrue.update(Calcs.correctBearing(hdt.value + twa.value), twa.timestamp);
            windDirectionMagnetic.update(Calcs.correctBearing(windDirectionTrue.value + magneticVariation.value), twa.timestamp);
            double otherTrackHeadingTrue = Calcs.correctBearing(windDirectionTrue.value + targetTwa.value);

            if ( twa.value > 0 ) {
                oppositeTrackTrue.update(otherTrackHeadingTrue + leeway.value * 2, stw.timestamp);
            } else {
                oppositeTrackTrue.update(otherTrackHeadingTrue-leeway.value*2, stw.timestamp);
            }
            DoubleDataValue oppositeTrackMagnetic = store.get(oppositeTrackMagneticKey);
            oppositeTrackMagnetic.update(oppositeTrackTrue.value + magneticVariation.value, stw.timestamp);

            DoubleDataValue oppositeHeadingMagnetic = store.get(oppositeHeadingMagneticKey);
            oppositeHeadingMagnetic.update(Calcs.correctBearing(otherTrackHeadingTrue + magneticVariation.value), stw.timestamp);

        }

    }

}


