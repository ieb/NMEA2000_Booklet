package uk.co.tfd.kindle.nmea2000;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by ieb on 28/03/2022.
 */
public class CSVParser {

    private Map<String, RecordReader> readers = new HashMap<>();

    public CSVParser () {
        new EngineReader();
        new BatteryReader();
        new FluidLevelReader();
        new HeadingReader();
        new SpeedReader();
        new WaterDepthReader();
        new RudderReader();
        new AttitudeReader();
        new WindReader();
        new CogSogReader();
        new GnssReader();
        new OutsideEnvironmentReader();
        new HumidityReader();
        new PressureReader();
        new TemperatureReader();
        new TemperaturesReader();
        new Bmp280Reader();
        new VoltagesReader();
        new XteReader();
        new VariationReader();
        new PositionReader();
        new LeewayReader();
        new TimeReader();
        new LogReader();

    }
    

    // parses a body and returns a strucutre ready for update
    public Map<String, Object>  parseCSV(BufferedReader in) throws IOException {
        Map<String, Object> root = new HashMap<>();
        String line = in.readLine();
        while(line != null) {
            String[] elements = line.split(",");
            if ( readers.containsKey(elements[0])) {
                RecordReader rr = readers.get(elements[0]);
                rr.read(root, elements);
            }
            line = in.readLine();
        }
        return root;
    }


    public abstract class RecordReader {

        String[] elements;
        private int index;
        private String key;

        public RecordReader(String key) {
            this.key = key;
            readers.put(key, this);
        }

        public final void read(Map<String, Object> root, String[] elements) {
            this.elements = elements;
            this.index = 0;
            this.doRead(root);
        }

        protected abstract void doRead(Map<String, Object> root);

        Map<String, Object> getInstance(Map<String, Object> root) {
            return getInstance(root, true);
        }

        Map<String, Object> getInstance(Map<String, Object> root, boolean withId) {
            index = 0;
            String rootKey = nextString();
            if (!root.containsKey(rootKey) ) {
                root.put(rootKey, new HashMap<>());
            }
            Map<String, Object> instances = (Map<String, Object>) root.get(rootKey);
            Map<String, Object>  instance = instances;
            if ( withId ) {
                String instanceId = nextString();
                if (!instances.containsKey(instanceId)) {
                    instances.put(instanceId, new HashMap<>());
                }
                instance = (Map<String, Object>) instances.get(instanceId);
            }

            instance.put("lastModified",nextLong());
            return instance;
        }
        String nextString() {
            return elements[index++];
        }

        long nextLong() {
            return nextLong(-1000000000);
        }

        long nextLong(long defaultValue) {
            if ( index < elements.length && elements[index] != null && !elements[index].isEmpty()) {
                return Long.parseLong(elements[index++]);
            }
            return defaultValue;
        }
        double nextDouble() {
            if ( index < elements.length && elements[index] != null && !elements[index].isEmpty()) {
                return Double.parseDouble(elements[index++]);
            }
            return -1000000000.0;
        }
    }

    public class TimeReader extends RecordReader {
        /*
            # engine,id,t,lastModified,speed,coolantTemp,altenatorVoltage,status1,status2,hours
            engine,0,83652827,83652736,4350,355.25,14.21,10,0,7695180

         */
        public TimeReader() {
            super("t");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            root.put("t",Long.parseLong(elements[1]));
        }
    }

    public class EngineReader extends  RecordReader {
        /*
            # engine,id,lastModified,speed,coolantTemp,altenatorVoltage,status1,status2,hours
            engine,0,83652827,83652736,4350,355.25,14.21,10,0,7695180

         */
        public EngineReader() {
            super("engine");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("speed",nextLong());
            instance.put("coolantTemp", nextDouble());
            instance.put("altenatorVoltage", nextDouble());
            instance.put("status1",nextLong());
            instance.put("status2",nextLong());
            instance.put("hours",nextLong());
        }
    }
    public class BatteryReader extends  RecordReader {
        /*
            # battery,id,t,lastModified,voltage,current,temperature
            battery,0,83652827,83651743,12.72,,,
         */
        public BatteryReader() {
            super("battery");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("voltage",nextDouble());
            instance.put("current", nextDouble());
            instance.put("temperature", nextDouble());
        }
    }
    public class FluidLevelReader extends  RecordReader {
        /*
            # fluid,id,t,lastModified,level,capacity,type
            fluidLevel,1,83652827,0,60,0
         */
        public FluidLevelReader() {
            super("fluidLevel");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("level", nextDouble());
            instance.put("capacity", nextDouble());
            instance.put("type",nextLong());
        }
    }

    public class HeadingReader extends  RecordReader {
        /*
            #heading,id,t,lastmodified,heading,deviation,variation,type (0=true,1=magnetic)
            heading,1,83663680,83663614,3.97,-0.05,0.1,1
         */
        public HeadingReader() {
            super("heading");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            double heading = nextDouble();
            instance.put("deviation", nextDouble());
            instance.put("variation", nextDouble());
            long type = nextLong();
            if ( type == 0 ) {
                instance.put("hdt",heading);
            } else {
                instance.put("hdm",heading);
            }
        }
    }

    public class SpeedReader extends  RecordReader {
        /*
            #speed,id,t,lastmodified,stw,sog,type (0=water)
            speed,0,83663680,83663614,6.28,,0
         */
        public SpeedReader() {
            super("speed");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("stw", nextDouble());
            instance.put("sog", nextDouble());
            instance.put("type",nextLong());
        }
    }
    public class WaterDepthReader extends  RecordReader {
        /*
            #waterDeptch,id,t,lastmodified,depthBelowTransducer,offset
         */
        public WaterDepthReader() {
            super("waterDepth");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("depthBelowTransducer", nextDouble());
            instance.put("offset", nextDouble());
        }
    }
    public class RudderReader extends  RecordReader {
        /*
            #rudder,id,t,lastmodified,directionOrder,position,angleOder
         */
        public RudderReader() {
            super("rudder");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("directionOrder",nextLong());
            instance.put("position", nextDouble());
            instance.put("angleOder", nextDouble());
        }
    }
    public class AttitudeReader extends  RecordReader {
        /*
            #attitude,id,t,lastmodified,pitch,roll,yaw
         */
        public AttitudeReader() {
            super("attitude");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("pitch", nextDouble());
            instance.put("roll", nextDouble());
            instance.put("yaw", nextDouble());
        }
    }
    public class WindReader extends  RecordReader {
        /*
            #wind,id,t,lastmodified,angle,speed,type  (0=true,  1=apparent)
         */
        public WindReader() {
            super("wind");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            double angle = nextDouble();
            double speed = nextDouble();
            long type = nextLong();
            if ( type == 0 ) {
                instance.put("twa", angle);
                instance.put("tws", speed);
            } else {
                instance.put("awa", angle);
                instance.put("aws", speed);
            }
        }
    }

    public class CogSogReader extends  RecordReader {
        /*
            #cogSog,id,t,lastmodified,cog,sog,reference
            cogSog,0,83663680,83663614,2.02,0.1,0
         */
        public CogSogReader() {
            super("cogSog");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("cog", nextDouble());
            instance.put("sog", nextDouble());
            instance.put("reference",nextLong());
        }
    }
    public class GnssReader extends  RecordReader {
        /*
            #gnss,id,t,lastmodified,daysSince1970,secondsSinceMidnight,latitude,longitude,altitude,type,method,nSatellites,HDOP,PDOP,geoidalSeparation,nReferenceStations,referenceStationType,ageOfCorrection
            gnss,0,83663680,83675791,18973,77160,60.44,22.24,10.5,0,1,12,0.8,0.5,15,1,0,2
         */
        public GnssReader() {
            super("gnss");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("daysSince1970", nextLong());
            instance.put("secondsSinceMidnight", nextLong());
            instance.put("latitude", nextDouble());
            instance.put("longitude", nextDouble());
            instance.put("altitude", nextDouble());
            instance.put("type", nextLong());
            instance.put("method", nextLong());
            instance.put("nSatellites", nextLong());
            instance.put("HDOP", nextDouble());
            instance.put("PDOP", nextDouble());
            instance.put("geoidalSeparation", nextLong());
            instance.put("nReferenceStations", nextLong());
            instance.put("referenceStationType", nextLong());
            instance.put("ageOfCorrection", nextLong());
        }
    }

    public class OutsideEnvironmentReader extends  RecordReader {
        /*
            #outsideEnvironment,id,t,lastmodified,waterTemperature,outsideAmbientAirTemperature,atmosphericPressure
            outsideEnvironment,0,83663680,83663614,288.65,298.45,101400
         */
        public OutsideEnvironmentReader() {
            super("outsideEnvironment");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("waterTemperature", nextDouble());
            instance.put("outsideAmbientAirTemperature", nextDouble());
            instance.put("atmosphericPressure", nextDouble());
        }
    }

    public class HumidityReader extends  RecordReader {
        /*
            #humidity,id,t,lastmodified,actual,set
            humidity,0,83663680,83675791,41.8,
         */
        public HumidityReader() {
            super("humidity");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("actual", nextDouble());
            instance.put("set", nextDouble());
        }
    }

    public class PressureReader extends  RecordReader {
        /*
            #pressure,id,t,lastmodified,actual
            pressure,0,83663680,83675791,102400
         */
        public PressureReader() {
            super("pressure");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("actual", nextDouble());
        }
    }

    public class TemperatureReader extends  RecordReader {
        /*
            #temperature,id,t,lastmodified,actual,set
            #temperatureExt,id,t,lastmodified,actual,set
            temperature,0,83663680,83663614,273.15,
            temperatureExt,0,83663680,83663614,273.15,
         */
        public TemperatureReader() {
            super("temperature");
            readers.put("temperatureExt", this);
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("actual", nextDouble());
            instance.put("set", nextDouble());
        }
    }

    public class TemperaturesReader extends  RecordReader {
        /*
            #temperatures,t,lastmodified,temperatures...
            temperatures,83663680,83663614,273.15,
         */
        public TemperaturesReader() {
            super("temperatures");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root, false);
            List<Map<String, Object>> temperatures = new ArrayList<>();
            instance.put("temperatures", temperatures);
            for ( int i = 2; i  < elements.length; i++) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("channel",i-2);
                temp.put("temp", nextDouble());
                temperatures.add(temp);
            }
        }
    }

    public class Bmp280Reader extends  RecordReader {
        /*
            #bmp280,t,lastmodified,temp,pressure,humidity,historyInterval,history...
            bmp280,83663680,83663614,27.48,1010.19,36.28,675000,998.31,998.42,998.4,998.21,998.2
         */
        public Bmp280Reader() {
            super("bmp280");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root,false);
            instance.put("temp", nextDouble());
            instance.put("pressure", nextDouble());
            instance.put("humidity", nextDouble());
            instance.put("historyInterval", nextLong());
            List<Double> history = new ArrayList<>();
            instance.put("history", history);
            for ( int i = 6; i  < elements.length; i++) {
                history.add(nextDouble());
            }
        }
    }



    public class VoltagesReader extends  RecordReader {
        /*
            #voltages,id,t,lastmodified,voltages...
            voltages,83663680,83663614,0.023184,0.031135,-0.06515
         */
        public VoltagesReader() {
            super("voltages");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root, false);
            List<Map<String, Object>> temperatures = new ArrayList<>();
            instance.put("voltages", temperatures);
            for ( int i = 2; i  < elements.length; i++) {
                Map<String, Object> voltage = new HashMap<>();
                voltage.put("channel",i-2);
                voltage.put("v", nextDouble());
                temperatures.add(voltage);
            }
        }
    }

    public class XteReader extends  RecordReader {
        /*
            #xte,id,t,lastmodified,xte
            xte,0,83663680,83663614,10.20
         */
        public XteReader() {
            super("xte");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("xte", nextDouble());
        }
    }
    public class VariationReader extends  RecordReader {
        /*
            #variation,id,t,lastmodified,daysSince1970,variation
            variation,0,83663680,83663614,18973,-0.02
         */
        public VariationReader() {
            super("variation");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("daysSince1970",nextLong());
            instance.put("variation", nextDouble());
        }
    }

    public class PositionReader extends  RecordReader {
        /*
            #position,id,t,lastmodified,latitude,longitude
            position,0,83663680,83663614,0.00,0.00
         */
        public PositionReader() {
            super("position");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("latitude", nextDouble());
            instance.put("longitude", nextDouble());
        }
    }
    public class LeewayReader extends  RecordReader {
        /*
            #leeway,id,t,lastmodified,leeway
            leeway,0,83663680,83663614,0.00
         */
        public LeewayReader() {
            super("leeway");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("leeway", nextDouble());
        }
    }

    public class LogReader extends  RecordReader {
        /*
            #log,id,lastmodified,daysSince1970,secondsSinceMidnight,log,tripLog
            t,83652827

            log,0,83663614,18973,77353.00,23878151,23876299
            */
        public LogReader() {
            super("log");
        }

        @Override
        public void doRead(Map<String, Object> root) {
            Map<String, Object> instance = getInstance(root);
            instance.put("daysSince1970", nextLong());
            instance.put("secondsSinceMidnight", nextDouble());
            instance.put("log", nextDouble());
            instance.put("trip", nextDouble());
        }
    }




}
