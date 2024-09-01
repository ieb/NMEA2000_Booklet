package uk.co.tfd.kindle.nmea2000;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by ieb on 10/06/2020.
 */
public class Data {

    /** Units of the raw reading, this is not the internal unit, which will be SI units.
     * Internal units are RAD, MS, RATIO, M, K, PA, RH, HZ, V, A, C, S */
    public enum Unit {
        RAD,
        DEGREES(Math.PI/180.0,0), // convert to radians
        MS,
        RATIO,
        PERCENTAGE(0.01,0.0),
        M,
        MAP,
        K,
        TEXT,
        PA,
        MBAR(100.0,0.0),
        RH,
        HZ,
        V,
        A,
        C(1.0, 273.15), // convert to K
        S,
        RPM(1.0/60.0,0.0), // convert to Hz
        V100ASHUNT(100.0/0.075,0) // 75mV == 100A
        ;
        private final double scale;
        private final double offset;
        Unit() {
            this.scale = 1.0;
            this.offset = 0.0;
        }

        Unit(double scale, double offset) {
            this.scale = scale;
            this.offset = offset;
        }

        double convertToInternal(double v) {
            return (v*scale) + offset;
        }
    }

    /** Type of units */
    public enum DataType {
        SPEED, BEARING, DISTANCE, NONE, RELATIVEANGLE, LATITUDE, LONGITUDE,
        TEMPERATURE, PERCENTAGE, DEPTH, ATMOSPHERICPRESSURE, PRESSURE,
        HUMIDITY, FREQUENCY, RPM, VOLTAGE, AMPS, CURRENT, HOURS

    }

    public static class DataKey {
        public static Map<String, DataKey> values = new HashMap<>();


        public final String id;
        public final Unit units;
        public final String description;
        public final DataType type;

        public DataKey(String id, Unit units, DataType type, String description) {
            this.id = id;
            this.units = units;
            this.description = description;
            this.type = type;
            values.put(id, this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DataKey) {
                return id.equals(((DataKey) obj).id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return id;
        }

        public String getInstanceKey(int i) {
            if ( id.endsWith(".")) {
                return id+i;
            }
            return id;
        }

    }


    public interface Listener<T extends Observable> {

        void onUpdate(T d);
    }

    public static class Observable extends StatusUpdates {
        private Listener[] listeners = new Listener[0];
        private Set<Listener> listenerSet = new HashSet<Listener>();
        private long debounce = 500;
        private long nextUpdate = 0;

        public void addListener(Listener l) {
            listenerSet.add(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }

        public void removeListener(Listener l) {
            listenerSet.remove(l);
            listeners = listenerSet.toArray(new Listener[listenerSet.size()]);
        }

        protected void fireUpdate() {

            if (nextUpdate < System.currentTimeMillis()) {
                nextUpdate = System.currentTimeMillis() + debounce;
                for (Listener listener : listeners) {
                    listener.onUpdate(this);
                }
            }
        }

    }


    public static class DataValue extends Observable {
        private static final Logger log = LoggerFactory.getLogger(DataValue.class);
        protected final DataKey key;
        protected final String valuePath;
        protected final String sourcePath;
        protected final String dataPath;
        String source;
        String text;

        protected long timestamp;
        private int lastModified;


        public DataValue(DataKey key, String dataPath, String sourcePath) {
            this.sourcePath = sourcePath;
            this.dataPath = dataPath;
            if ( sourcePath.length() < dataPath.length() ) {
                this.valuePath = dataPath.substring(sourcePath.length());
            } else {
                this.valuePath = "value";
            }
            this.key = key;
            this.source = "empty";
        }


        public void update(Object input,  long timeOffset) {
            if (input instanceof Map) {
                update((Map<String, Object>) input, timeOffset);
            } else if (input instanceof String) {
                this.text = (String) input;
                this.source = "input";
            } else {
                throw new IllegalArgumentException("Unable to update " + key + " from " + input);
            }
        }




        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            String newValue = String.valueOf(Util.resolve(input, valuePath, this.text));
            if ("".equals(newValue)) {
                log.warn("update() No text value found");
            }
            this.source = "input";
            if (!newValue.equals(this.text)) {
                this.text = newValue;
                this.fireUpdate();
            }
        }

        public void update(String v, long ts) {
            this.text = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.fireUpdate();
        }

        public void calcStats() {
        }

        public boolean isInput() {
            return "input".equals(this.source);
        }

        public String getText() {
            return text;
        }

        public double getValue() {
            return 0;
        }

        public double getMax() {
            return 0;
        }

        public double getMin() {
            return 0;
        }

        public double getStdev() {
            return 0;
        }

        public double getMean() {
            return 0;
        }

        public DataKey getKey() {
            return this.key;
        }

        public boolean isType(DataType t) {
            return this.key.type.equals(t);
        }

        public DataType getType() {
            return this.key.type;
        }

        public int getLastModified() {
            return lastModified;
        }
    }


    public static class DoubleDataValue extends DataValue {
        private static final Logger log = LoggerFactory.getLogger(DoubleDataValue.class);
        double value = 0.0;
        protected double[] values = new double[100];
        protected int ilast = 0;
        protected int ifirst = 0;
        protected double mean;
        protected double stdev;
        protected double min;
        protected double max;
        private long change = 0;
        private long nochange = 0;

        public double getValue() {
            return value;
        }

        public double getMax() {
            return max;
        }

        public double getMin() {
            return min;
        }

        public double getStdev() {
            return stdev;
        }

        public double getMean() {
            return mean;
        }

        public DoubleDataValue(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
            source = "empty";
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            Object o = Util.resolve(input, valuePath, null);
            double newValue = 0;
            if (o instanceof Long) {
                newValue = 1.0 * (long) o;
            } else if (o instanceof Double) {
                newValue = (double) o ;
            } else {
                log.warn("Value not recognised in datavalue update {} {} {} ", sourcePath, valuePath, o);
                newValue = 0;
            }
            if ( newValue == 0.0 ) {
                newValue = 1E-4;
            }
            //newValue = newValue + newValue*((Math.random()-0.5)/10);
            double internalValue = key.units.convertToInternal(newValue);
            //log.info("Update {} with {} internal {} ", dataPath, newValue, internalValue);


            if (internalValue != this.value) {
                this.value = internalValue;
                this.fireUpdate();
                change++;
            } else {
                nochange++;
            }
        }

        public void update(double v, long ts) {
            this.value = v;
            this.timestamp = ts;
            this.source = "calculated";
            this.fireUpdate();
        }

        @Override
        public void calcStats() {
            double pmin = this.min;
            double pmax = this.max;
            double pstdev = this.stdev;
            this.values[ilast] = this.value;
            ilast = (ilast + 1) % 100;
            if (ifirst == ilast) {
                ifirst = (ifirst + 1) % 100;
            }


            double s = 0.0;
            double n = 0.0;
            if (ifirst < ilast) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double) (i + 1) / 2.0;
                    s += this.values[i] * w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double) ((i - ifirst) + 1) / 2.0;
                    s += this.values[i] * w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double) ((100 - ifirst + i) + 1) / 2.0;
                    s += this.values[i] * w;
                    n += w;
                }
            }

            this.mean = s / n;
            s = 0.0;
            n = 0.0;
            if (ifirst == 0) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double) (i + 1) / 2.0;
                    s += (this.values[i] - this.mean) * (this.values[i] - this.mean) * w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double) ((i - ifirst) + 1) / 2.0;
                    s += (this.values[i] - this.mean) * (this.values[i] - this.mean) * w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double) ((100 - ifirst + i) + 1) / 2.0;
                    s += (this.values[i] - this.mean) * (this.values[i] - this.mean) * w;
                    n += w;
                }
            }
            this.stdev = Math.sqrt(s / n);

            this.min = this.mean;
            this.max = this.mean;
            if (ifirst == 0) {
                for (int i = 0; i < ilast; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
                for (int i = ilast; i < ifirst; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            }
            if ( pmax != this.max || pmin != this.min || pstdev != this.stdev ) {
                this.fireUpdate();
            }

        }
    }


    public static class CircularDataValue extends DoubleDataValue {

        private final double[] sinvalues = new double[100];
        private final double[] cosvalues = new double[100];

        public CircularDataValue(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void calcStats() {
            double pmin = this.min;
            double pmax = this.max;
            double pstdev = this.stdev;

            this.values[ilast] = this.value;
            this.sinvalues[ilast] = Math.sin(this.value);
            this.cosvalues[ilast] = Math.cos(this.value);
            ilast = (ilast + 1) % 100;
            if (ifirst == ilast) {
                ifirst = (ifirst + 1) % 100;
            }


            double s = 0.0;
            double c = 0.0;
            double n = 0.0;
            if (ifirst < ilast) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double) (i + 1) / 2.0;
                    s += this.sinvalues[i] * w;
                    c += this.cosvalues[i] * w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double) ((i - ifirst) + 1) / 2.0;
                    s += this.sinvalues[i] * w;
                    c += this.cosvalues[i] * w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double) ((100 - ifirst + i) + 1) / 2.0;
                    s += this.sinvalues[i] * w;
                    c += this.cosvalues[i] * w;
                    n += w;
                }
            }
            this.mean = Math.atan2(s / n, c / n);

            s = 0.0;
            n = 0.0;
            if (ifirst == 0) {
                for (int i = 0; i < ilast; i++) {
                    double w = (double) (i + 1) / 2.0;
                    double a = this.values[i] - this.mean;
                    // find the smallest sweep from the mean.
                    if (a > Math.PI) {
                        a = a - 2 * Math.PI;
                    } else if (a < -Math.PI) {
                        a = a + 2 * Math.PI;
                    }
                    s += a * a * w;
                    n += w;
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    double w = (double) ((i - ifirst) + 1) / 2.0;
                    double a = this.values[i] - this.mean;
                    // find the smallest sweep from the mean.
                    if (a > Math.PI) {
                        a = a - 2 * Math.PI;
                    } else if (a < -Math.PI) {
                        a = a + 2 * Math.PI;
                    }
                    s += a * a * w;
                    n += w;
                }
                for (int i = ilast; i < ifirst; i++) {
                    double w = (double) ((100 - ifirst + i) + 1) / 2.0;
                    double a = this.values[i] - this.mean;
                    // find the smallest sweep from the mean.
                    if (a > Math.PI) {
                        a = a - 2 * Math.PI;
                    } else if (a < -Math.PI) {
                        a = a + 2 * Math.PI;
                    }
                    s += a * a * w;
                    n += w;
                }
            }
            this.stdev = Math.sqrt(s / n);

            this.min = this.mean;
            this.max = this.mean;
            if (ifirst == 0) {
                for (int i = 0; i < ilast; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            } else {
                for (int i = ifirst; i < 100; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
                for (int i = ilast; i < ifirst; i++) {
                    this.min = Math.min(this.values[i], this.min);
                    this.max = Math.max(this.values[i], this.max);
                }
            }
            if ( pmax != this.max || pmin != this.min || pstdev != this.stdev ) {
                this.fireUpdate();
            }

        }

    }


    /*
     * Aggregate Data Values. These are stored as aggregates of many values
     * They may be watched by other datavalues that extract and convert.
     */
    


    public static class NMEA2KAttitude extends Data.DataValue {

        /**
         "id": 0,
         "lastModified": 0,
         "roll": 0.00,
         "pitch": 0.00,
         "yaw": 0.00


         */

        private int id;
        private int lastModified;
        private double roll;
        private double pitch;
        private double yaw;

        public NMEA2KAttitude(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"), 0);
            double newRoll = Util.doubleValue(input.get("roll"), 0);
            double newPitch = Util.doubleValue(input.get("pitch"),0);
            double newYaw = Util.doubleValue(input.get("yaw"),0);
            if ( lm != lastModified ||
                    newRoll != roll ||
                    newPitch != pitch ||
                    newYaw != yaw) {
                roll = newRoll;
                pitch = newPitch;
                yaw = newYaw;
                lastModified = lm;
                this.fireUpdate();
            }
        }

        public double getRoll() {
            return roll;
        }

        public double getPitch() {
            return pitch;
        }
    }

    public static class NMEA2KCurrent extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "cog": 0.00,
         "sog": 0.00,
         "reference": 0

         */

        private int id;
        private int reference;
        private int lastModified;
        private double set;
        private double drift;

        public NMEA2KCurrent(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            reference = Util.intValue(input.get("reference"), 0);
            double newSet = Util.doubleValue(input.get("set"), 0);
            double newDrift = Util.doubleValue(input.get("drift"),0);
            if ( lm != lastModified ||
                    newSet != set ||
                    newDrift != drift) {
                set = newSet;
                drift = newDrift;
                lastModified = lm;
                this.fireUpdate();
            }
        }

        public double getDrift() {
            return drift;
        }

        public double getSet() {
            return set;
        }
    }



    public static class NMEA2KLog extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "log": 0.00,
         "trip": 0.00,
         "reference": 0

         */

        private int id;
        private int reference;
        private int lastModified;
        private double log;
        private double trip;
        private int daysSince1970;
        private double secondsSinceMidnight;

        public NMEA2KLog(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"), 0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            double newLog = Util.doubleValue(input.get("log"), 0);
            double newTrip = Util.doubleValue(input.get("trip"),0);
            int newDaysSince1970 = Util.intValue(input.get("daysSince1970"), 0);
            double newsecondsSinceMidnight = Util.doubleValue(input.get("secondsSinceMidnight"), 0);
            if ( lm != lastModified ||
                    newLog != log || 
                    newTrip != trip || 
                    newDaysSince1970 != daysSince1970 ||
                    newsecondsSinceMidnight != secondsSinceMidnight) {
                log = newLog;
                trip = newTrip;
                daysSince1970 = newDaysSince1970;
                secondsSinceMidnight = newsecondsSinceMidnight;
                lastModified = lm;
                this.fireUpdate();
            }
        }

        public double getLog() {
            return log;
        }

        public double getTrip() {
            return trip;
        }

        public int getDaysSince1970() {
            return daysSince1970;
        }

        public double getSecondsSinceMidnight() {
            return secondsSinceMidnight;
        }
    }

    public static class NMEA2KGnss extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "daysSince1970": 0,
         "secondsSinceMidnight": 0.00,
         "latitude": 0.00,
         "longitude": 0.00,
         "altitude": 0.00,
         "type": 0,
         "method": 0,
         "nSatellites": 0,
         "HDOP": 0.00,
         "PDOP": 0.00,
         "geoidalSeparation": 0.00,
         "nReferenceStations": 0,
         "referenceStationType": 0,
         "ageOfCorrection": 0.00

         */

        private int id;
        private int lastModified;
        private int daysSince1970;
        private int type;
        private int method;
        private int nSatellites;
        private int nReferenceStations;
        private int referenceStationType;
        private double secondsSinceMidnight;
        private double latitude;
        private double longitude;
        private double altitude;
        private double hdop;
        private double pdop;
        private double geoidalSeparation;
        private double ageOfCorrection;


        public NMEA2KGnss(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            int newDaysSince1970 = Util.intValue(input.get("daysSince1970"),0);
            int newType = Util.intValue(input.get("type"),0);
            int newMethod = Util.intValue(input.get("method"),0);
            int newNSatellites = Util.intValue(input.get("nSatellites"),0);
            int newNReferenceStations = Util.intValue(input.get("nReferenceStations"),0);
            int newReferenceStationType = Util.intValue(input.get("referenceStationType"),0);
            double newSecondsSinceMidnight = Util.doubleValue(input.get("secondsSinceMidnight"), 0);
            double newLatitude = Util.doubleValue(input.get("latitude"),0);
            double newLongitude = Util.doubleValue(input.get("longitude"),0);
            double newAltitude = Util.doubleValue(input.get("altitude"),0);
            double newHdop = Util.doubleValue(input.get("HDOP"),0);
            double newPdop = Util.doubleValue(input.get("PDOP"),0);
            double newGeoidalSeparation = Util.doubleValue(input.get("geoidalSeparation"),0);
            double newAgeOfCorrection = Util.doubleValue(input.get("ageOfCorrection"),0);
            if ( lm != lastModified ||
                newDaysSince1970 != daysSince1970 ||
                newType != type ||
                newMethod != method ||
                newNSatellites != nSatellites ||
                newNReferenceStations != nReferenceStations ||
                newReferenceStationType != referenceStationType ||
                newSecondsSinceMidnight != secondsSinceMidnight ||
                newLatitude != latitude ||
                newLongitude != longitude ||
                newAltitude != altitude ||
                newHdop != hdop ||
                newPdop != pdop ||
                newGeoidalSeparation != geoidalSeparation ||
                newAgeOfCorrection != ageOfCorrection
                    ) {
                lastModified = lm;
                daysSince1970 = newDaysSince1970;
                type = newType;
                method = newMethod;
                nSatellites = newNSatellites;
                nReferenceStations = newNReferenceStations;
                referenceStationType = newReferenceStationType;
                secondsSinceMidnight = newSecondsSinceMidnight;
                latitude = newLatitude;
                longitude = newLongitude;
                altitude = newAltitude;
                hdop = newHdop;
                pdop = newPdop;
                geoidalSeparation = newGeoidalSeparation;
                ageOfCorrection = newAgeOfCorrection;
                this.fireUpdate();
            }
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getFixDate() {
            LocalDate day = LocalDate.ofEpochDay(daysSince1970);
            LocalDateTime time = day.atTime(LocalTime.ofNanoOfDay((long)(secondsSinceMidnight*10E9)));
            return time.format(DateTimeFormatter.ISO_INSTANT);
        }

        public String getMethodQuality() {
            return String.valueOf(method);
        }

        public double getHorizontalDilution() {
            return hdop;
        }

        public int getSatellites() {
            return nSatellites;
        }

        public String getIntegrity() {
            return "1";
        }

        public String getFixType() {
            return String.valueOf(type);
        }

        public int getDaysSince1970() {
            return daysSince1970;
        }

        public double getSecondsSinceMidnight() {
            return secondsSinceMidnight;
        }

        public double getAltitude() {
            return altitude;
        }

        public int getMethod() {
            return method;
        }

        public double getPDOP() {

            return pdop;
        }

        public double getGeoidalSeparation() {
            return geoidalSeparation;
        }

        public int getNReferenceStations() {
            return nReferenceStations;
        }

        public int getReferenceStationType() {
            return referenceStationType;
        }

        public double getAgeOfCorrection() {
            return ageOfCorrection;
        }
    }

    public static class NMEA2KOutsideEnvironment extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "waterTemperature": 0.00,
         "outsideAmbientAirTemperature": 0.00,
         "atmosphericPressure": 0.00
         }
         */

        private int id;
        private int lastModified;
        private double waterTemperature;
        private double outsideAmbientAirTemperature;
        private double atmosphericPressure;

        public NMEA2KOutsideEnvironment(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"), 0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            double newWaterTemperature = Util.doubleValue(input.get("waterTemperature"), 0);
            double newOutsideAmbientAirTemperature = Util.doubleValue(input.get("outsideAmbientAirTemperature"),0);
            double newAtmosphericPressure = Util.doubleValue(input.get("atmosphericPressure"),0);
            if ( lm != lastModified ||
                    newWaterTemperature != waterTemperature ||
                    newOutsideAmbientAirTemperature != outsideAmbientAirTemperature ||
                    newAtmosphericPressure != atmosphericPressure ) {
                lastModified = lm;
                waterTemperature = newWaterTemperature;
                outsideAmbientAirTemperature = newOutsideAmbientAirTemperature;
                atmosphericPressure = newAtmosphericPressure;
                this.fireUpdate();
            }
        }
    }

    public static class NMEA2KHumidity extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "instance": 0,
         "source": 0,
         "actual": 0.00,
         "set": 0.00
         }         */

        private int id;
        private int lastModified;
        private int instance;
        private int source;
        private double actual;
        private double set;

        public NMEA2KHumidity(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            instance = Util.intValue(input.get("instance"),0);
            source = Util.intValue(input.get("source"), 0);
            double newActual = Util.doubleValue(input.get("actual"), 0);
            double newSet = Util.doubleValue(input.get("set"),0);
            if ( lm != lastModified ||
                    newActual != actual ||
                    newSet != set) {
                lastModified = lm;
                set = newSet;
                actual = newActual;
                this.fireUpdate();
            }
        }
    }

    public static class NMEA2KPressure extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 0,
         "instance": 0,
         "source": 0,
         "actual": 0.00
         }        */

        private int id;
        private int lastModified;
        private int instance;
        private int source;
        private double actual;

        public NMEA2KPressure(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            instance = Util.intValue(input.get("instance"),0);
            source = Util.intValue(input.get("source"), 0);
            double newActual = Util.doubleValue(input.get("actual"), 0);
            if ( lm != lastModified ||
                    newActual != actual) {
                lastModified = lm;
                actual = newActual;
                this.fireUpdate();
            }
        }
    }

    public static class NMEA2KTemperature extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 62114,
         "instance": 0,
         "source": 14,
         "actual": 273.15,
         "set": -1000000000.00
         }        */

        private int id;
        private int lastModified;
        private int instance;
        private int source;
        private double actual;
        private double set;

        public NMEA2KTemperature(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            instance = Util.intValue(input.get("instance"),0);
            source = Util.intValue(input.get("source"), 0);
            double  newActual = Util.doubleValue(input.get("actual"), 0);
            double newSet = Util.doubleValue(input.get("set"),0);
            if ( lm != lastModified ||
                    newActual != actual ||
                    newSet != set) {
                lastModified = lm;
                set = newSet;
                actual = newActual;
                this.fireUpdate();
            }
        }
    }

    public static class NMEA2KPosition extends Data.DataValue {

        /**
         {
         "id": 0,
         "lastModified": 62114,
         "instance": 0,
         "source": 14,
         "actual": 273.15,
         "set": -1000000000.00
         }        */

        private int id;
        private int lastModified;
        private int instance;
        private int source;
        private double longitude;
        private double latitude;

        public NMEA2KPosition(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            source = Util.intValue(input.get("source"), 0);
            double newLatitude = Util.doubleValue(input.get("latitude"), 0);
            double newLongitude = Util.doubleValue(input.get("longitude"),0);
            if ( lm != lastModified ||
                    newLatitude != latitude ||
                    newLongitude != longitude) {
                lastModified = lm;
                latitude = newLatitude;
                longitude = newLongitude;
                this.fireUpdate();
            }
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getFixDate() {
            return new Date(timestamp).toString();
        }
    }

    public static class Temperatures extends Data.DataValue {

        /**
         "temperatures": []
}        */

        private int id;
        private int lastModified;
        private Map<Integer,OneWireTemperature> oneWireTemp = new HashMap<Integer, OneWireTemperature>();

        public Temperatures(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);;
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            List<Map<String, Object>> temperatures = (List<Map<String, Object>>) input.get("temperatures");
            boolean update = false;
            for (Map<String, Object> t : temperatures) {
                int ch = (Integer) t.get("channel");
                OneWireTemperature a = oneWireTemp.get(ch);
                if ( a == null) {
                    a = new OneWireTemperature(ch);
                    oneWireTemp.put(ch, a);
                }
                double newTempt = (Double) t.get("temp");
                if ( newTempt != a.temp ) {
                    a.temp = newTempt;
                    update = true;
                }
            }
            if ( lm != lastModified || update) {
                this.fireUpdate();
            }
        }

        public Map<Integer,OneWireTemperature> getTemperatures() {
            return oneWireTemp;
        }
    }
    
    public static class OneWireTemperature {
        private int ch;
        public double temp;

        public OneWireTemperature(int ch) {

            this.ch = ch;
        }
    }

    public static class BMP280 extends Data.DataValue {

        /**
         {
         "t": 83728915,
         "bmp280": {
         "lastModified": 83722011,
         "temp": 27.48,
         "pressure": 1010.19,
         "humidity": 36.28,
         "historyInterval": 675000,
         "history": [
         998.31,
         998.42,
         998.4,
         998.21,
         998.2,
         998.08,
         997.91,
         998.01,
         998.22,
         998.53,
         998.82,
         998.97,
         998.86,
         999.18,
         999.41,
         999.56,
         999.83,
         1000.2,
         1000.39,
         1000.13,
         1000.64,
         1000.92,
         1001.25,
         1001.4,
         1001.72,
         1002.02,
         1002.14,
         1002.2,
         1002.29,
         1002.49,
         1002.66,
         1003.03,
         1003.21,
         1003.35,
         1003.46,
         1003.63,
         1003.91,
         1003.9,
         1003.96,
         1004.06,
         1004.35,
         1004.74,
         1004.8,
         1004.82,
         1004.9,
         1005.06,
         1005.14,
         1005.41,
         1005.54,
         1005.48,
         1005.68,
         1005.97,
         1005.95,
         1005.98,
         1006.04,
         1006.02,
         1006.12,
         1006.15,
         1006.2,
         1006.26,
         1006.3,
         1006.22,
         1006.28,
         1006.33,
         1006.23,
         1006.34,
         1006.18,
         1006.15,
         1006.18,
         1006.23,
         1006.43,
         1006.43,
         1006.31,
         1006.51,
         1006.7,
         1006.52,
         1006.5,
         1006.56,
         1006.6,
         1006.78,
         1006.86,
         1006.82,
         1006.97,
         1006.97,
         1007.06,
         1007.05,
         1007.08,
         1007.07,
         1007.16,
         1007.13,
         1007.26,
         1007.16,
         1007.19,
         1007.14,
         1007.09,
         1007.25,
         1007.51,
         1007.68,
         1007.69,
         1007.66,
         1007.78,
         1007.89,
         1007.82,
         1007.94,
         1008.05,
         1008.12,
         1008.26,
         1008.65,
         1008.95,
         1009.09,
         1009.25,
         1009.3,
         1009.4,
         1009.54,
         1009.41,
         1009.47,
         1009.5,
         1009.71,
         1009.87,
         1010.02,
         1010.12,
         1010.08,
         1010.19,
         1010.25,
         1010.23
         ]
         }
         }         */

        private int id;
        private int lastModified;
        private double temp;
        private double pressure;
        private double humidity;
        private int historyInterval;
        private List<Double> history;

        public BMP280(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"), 0);
            double newtemp = Data.Unit.C.convertToInternal(Util.doubleValue(input.get("temp"), 0));
            double newpressure = Data.Unit.MBAR.convertToInternal(Util.doubleValue(input.get("pressure"), 0));
            double newhumidity = Util.doubleValue(input.get("humidity"), 0);
            int newhistoryInterval = Util.intValue(input.get("historyInterval"), 0);
            ArrayList newHistory = new ArrayList<Double>();
            newHistory.addAll((List<Double>) input.get("history"));
            if ( lm != lastModified ||
                    newtemp != temp ||
                    newpressure != pressure ||
                    newhumidity != humidity ||
                    newhistoryInterval != historyInterval ||
                    !listEquals(newHistory, history)) {
                lastModified = lm;
                temp = newtemp;
                pressure = newpressure;
                humidity = newhumidity;
                historyInterval = newhistoryInterval;
                history = newHistory;
                this.fireUpdate();
            }
        }

        private boolean listEquals(List<Double> l1, List<Double> l2) {
            if ( l1.size() != l2.size()) {
                return false;
            }
            for(int i = 0; i < l1.size(); i++) {
                if ( l1.get(i) != l2.get(i)) {
                    return false;
                }
            }
            return true;
        }

        public double getPressure() {
            return pressure;
        }

        public double getTemp() {
            return temp;
        }

        public double getHumidity() {
            return humidity;
        }

        public List<Double> getPressureHistory() {
            return history;
        }

        public int getHistoryInterval() {
            return historyInterval;
        }
    }

    public static class Voltages extends Data.DataValue {

        /**
         {
         "t": 62770,
         "lastModified": 60014,
         "voltages": [{
         "channel": 0,
         "adcr": 14,
         "adcv": 0.001750,
         "v": 0.009836
         }         */

        private int id;
        private int lastModified;
        private Map<Integer,ADCVoltage> adcReadings = new HashMap<Integer, ADCVoltage>();

        public Voltages(DataKey k, String dataPath, String sourcePath) {
            super(k, dataPath, sourcePath);
        }

        @Override
        public void update(Map<String, Object> input, long timeOffset) {
            if ( input.containsKey("lastModified") ) {
                timestamp = timeOffset + (long) input.get("lastModified");
            } else {
                timestamp = System.currentTimeMillis();
            }
            id = Util.intValue(input.get("id"),0);
            int lm = lastModified;
            lastModified = Util.intValue(input.get("lastModified"),0);
            List<Map<String, Object>> voltages = (List<Map<String, Object>>) input.get("voltages");
            boolean update = false;
            for (Map<String, Object> v : voltages) {
                int ch = (Integer) v.get("channel");
                ADCVoltage a = adcReadings.get(ch);
                if ( a == null) {
                    a = new ADCVoltage(ch);
                    adcReadings.put(ch, a);
                }
                double adcr = (Double) v.get("adcr");
                double adcv = (Double) v.get("adcv");
                double vr = (Double) v.get("v");
                if ( vr != a.v || a.adcv != adcv || a.adcr != adcr) {
                    a.v = vr;
                    a.adcv = adcv;
                    a.adcr = adcr;
                    update = true;

                }
            }
            if ( lm != lastModified || update) {
                lastModified = lm;
                this.fireUpdate();
            }
        }

        public Map<Integer,ADCVoltage> getVoltages() {
            return adcReadings;
        }
    }

    public static class ADCVoltage {
        private final int ch;
        public double adcr;
        public double adcv;
        public double v;

        public ADCVoltage(int ch) {
            this.ch = ch;
        }
    }




}
