package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ieb on 13/03/2022.
 */
public class DisplayConversion {

    public static DisplayUnits createSIDisplayUnits() {
        DisplayUnits displayUnits = new DisplayUnits();
        displayUnits.add(new DepthInM());
        displayUnits.add(new DistanceInNm());
        displayUnits.add(new SpeedInKn());
        displayUnits.add(new BearingInDeg());
        displayUnits.add(new RelativeAngleInDeg());
        displayUnits.add(new LatitudeDisplay());
        displayUnits.add(new LongitudeDisplay());
        displayUnits.add(new TemperatureDisplay());
        displayUnits.add(new PercentageDisplay());
        displayUnits.add(new HumidityDisplay());
        displayUnits.add(new AtmosphericPressureDisplay());
        displayUnits.add(new RpmDisplay());
        displayUnits.add(new FrequencyDisplay());
        displayUnits.add(new VoltageDisplay());
        displayUnits.add(new CurrentDisplay());
        displayUnits.add(new HoursDisplay());
        displayUnits.add(new NoConversion());
        return displayUnits;
    }

    /**
     * Created by ieb on 13/03/2022.
     */
    public interface Conversion {

        String convert(double value, DecimalFormat format);

        Data.DataType getDataType();
    }

    public static class LongitudeDisplay implements Conversion {

        DecimalFormat formatDeg = new DecimalFormat("000");
        DecimalFormat formatMin = new DecimalFormat("00.000");

        @Override
        public String convert(double longitude, DecimalFormat format) {
            String EW = "E";
            if (longitude < 0) {
                longitude = -longitude;
                EW = "W";
            }
            double d = Math.floor(longitude);
            double m = (60.0*(longitude-d));
            return formatDeg.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + EW;
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.LONGITUDE;
        }
    }

    public static class HoursDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.HOURS;
        }
    }


    public static class TemperatureDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value-273.15);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.TEMPERATURE;
        }
    }

    public static class PercentageDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*100.0);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.PERCENTAGE;
        }
    }

    public static class HumidityDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.HUMIDITY;
        }
    }

    public static class AtmosphericPressureDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*0.01);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.ATMOSPHERICPRESSURE;
        }
    }

    public static class PressureDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value/100000.0);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.PRESSURE;
        }
    }

    public static class RpmDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value*60.0);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.RPM;
        }
    }


    public static class FrequencyDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.FREQUENCY;
        }
    }

    public static class VoltageDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.VOLTAGE;
        }
    }

    public static class CurrentDisplay implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.CURRENT;
        }
    }

    public static class DepthInM implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.DEPTH;
        }
    }

    public static class DistanceInNm implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value * 0.000539957);  // m -> Nm
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.DISTANCE;
        }
    }

    public static class SpeedInKn implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value * 1.94384);  // m/s -> Kn
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.SPEED;
        }
    }

    public static class BearingInDeg implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            // 0 - 360
            return format.format(Calcs.correctBearing(value)*180/Math.PI);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.BEARING;
        }
    }

    public static class RelativeAngleInDeg implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            // - PI to +PI
            if ( value < Math.PI ) {
                value = value+Math.PI*2;
            }
            if ( value > Math.PI ) {
                value = value-Math.PI*2;
            }
            return format.format(value*180/Math.PI);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.RELATIVEANGLE;
        }
    }

    public static class NoConversion implements Conversion {

        @Override
        public String convert(double value, DecimalFormat format) {
            return format.format(value);
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.NONE;
        }
    }

    public static class LatitudeDisplay implements Conversion {

        DecimalFormat formatDeg = new DecimalFormat("00");
        DecimalFormat formatMin = new DecimalFormat("00.000");

        @Override
        public String convert(double latitude, DecimalFormat format) {
            String NS = "N";
            if ( latitude < 0) {
                latitude = -latitude;
                NS = "S";
            }
            double d = Math.floor(latitude);
            double m = (60.0*(latitude-d));
            return formatDeg.format(d) + "\u00B0" + formatMin.format(m) + "\u2032" + NS;
        }

        @Override
        public Data.DataType getDataType() {
            return Data.DataType.LATITUDE;
        }
    }

    public static class DisplayUnits {
        private static final Logger log = LoggerFactory.getLogger(DisplayUnits.class);

        private Map<Data.DataType,Conversion> conversions = new HashMap<Data.DataType, Conversion>();

        public void add(Conversion conversion) {
            conversions.put(conversion.getDataType(), conversion);
        }

        public String toDispay(double value, DecimalFormat format, Data.DataType dataType) {
            if ( conversions.containsKey(dataType) ) {
                return conversions.get(dataType).convert(value, format);
            }
            log.warn("Conversion Not found for data type {} {} ", dataType, value);
            Exception e = new Exception("Traceback");
            log.warn("Traceback",e);
            return format.format(value);
        }
    }
}
