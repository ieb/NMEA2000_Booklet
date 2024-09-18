package uk.co.tfd.kindle.nmea2000;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 10/04/2022.
 */
public class TestConfig {

    private static Logger log = LoggerFactory.getLogger(TestConfig.class);
    private static Store store;
    private static Calcs calcs;
    private static NMEA2000HttpClient nmea2000HttpClient;


    @BeforeClass
    public static void start() throws ParseException, IOException, NoSuchMethodException {
        store = new Store();
        calcs = new Calcs(store);
        nmea2000HttpClient =  new NMEA2000HttpClient(store);

        File f = new File("src/test/resources/config.json");
        JSONParser jsonParser = new JSONParser();
        log.info("Loading config file {} ", f.getAbsolutePath());
        FileReader config = new FileReader(f);
        Map<String, Object> configuration = (Map<String, Object>) jsonParser.parse(config);
        store.addConfiguration(configuration);
        calcs.addConfiguration(configuration);
    }


    private void inject(String testData) {
        Assert.assertTrue(nmea2000HttpClient.fetch("data:" + testData, ""));
    }

    @Test
    public void test2() {
        String testData = "t,83652827\n" +
                "heading,0,83663614,3.97,-0.05,0.1,1\n" + // magnetic
                "speed,0,83663614,6.28,,0\n" + // paddlewheel
                "waterDepth,0,83662795,5.1,0.1\n" +
                "rudder,0,83663613,1,0.09,-0.09\n" +
                "attitude,0,83662733,-0.14,0.04,-0.05\n" +
                "wind,0,83662733,-0.24,0.254,1\n"; // aparent
        inject(testData);
        check("candiag.heading.0.hdm", 3.97);
        check("candiag.heading.0.deviation", -0.05);
        check("candiag.heading.0.variation", 0.1);
        check("candiag.rudder.0.position", 0.09);
        check("candiag.waterDepth.0.depthBelowTransducer",5.1);
        check("candiag.waterDepth.0.offset",0.1);
        check("candiag.speed.0.stw",6.28);
        check("candiag.attitude.0.roll",0.04);
        check("candiag.attitude.0.pitch",-0.14);
        check("candiag.attitude.0.yaw",-0.05);
        check("candiag.wind.0.awa",-0.24);
        check("candiag.wind.0.aws",0.254);

    }

    @Test
    public void test3() {
        String testData = "cogSog,0,83663614,2.02,0.1,0\n" +
                "gnss,0,83675791,18973,77160,60.44,22.24,10.5,0,1,12,0.8,0.5,15,1,0,2\n";
        //                       |     |     |     |     |    | | |  |   |   |  | | ageOfCorrection
        //                       |     |     |     |     |    | | |  |   |   |  | referenceStationType,
        //                       |     |     |     |     |    | | |  |   |   |  nReferenceStations,
        //                       |     |     |     |     |    | | |  |   |   geoidalSeparation,
        //                       |     |     |     |     |    | | |  |   PDOP,
        //                       |     |     |     |     |    | | |  HDOP,
        //                       |     |     |     |     |    | | nSatellites,
        //                       |     |     |     |     |    | method,
        //                       |     |     |     |     |    type,
        //                       |     |     |     |     altitude,
        //                       |     |     |     longitude,
        //                       |     |     latitude,
        //                       |     secondsSinceMidnight,
        //                       daysSince1970,
        //      #gnss,id,lastmodified,
        inject(testData);
        Data.DataKey k = new Data.DataKey("candiag.gnss.0", Data.Unit.MAP, Data.DataType.NONE,"candiag.gnss.0");
        Data.NMEA2KGnss gnss = store.get(k);
        //Assert.assertEquals(83675791,gnss.getLastModified());
        Assert.assertEquals(18973,gnss.getDaysSince1970());
        Assert.assertEquals(77160.0,gnss.getSecondsSinceMidnight(),0.1);
        Assert.assertEquals(60.44,gnss.getLatitude(),60.44/1000);
        Assert.assertEquals(22.24,gnss.getLongitude(),22.24/1000);
        Assert.assertEquals(10.5,gnss.getAltitude(),10.5/1000);
        Assert.assertEquals("0",gnss.getFixType());
        Assert.assertEquals(1,gnss.getMethod());
        Assert.assertEquals(0.8,gnss.getHorizontalDilution(),0.8/100);
        Assert.assertEquals(0.5,gnss.getPDOP(),0.5/100);
        Assert.assertEquals(15.0,gnss.getGeoidalSeparation(),15.0/100);
        Assert.assertEquals(1,gnss.getNReferenceStations());
        Assert.assertEquals(0,gnss.getReferenceStationType());
        Assert.assertEquals(2,gnss.getAgeOfCorrection(),0.1);
        check("candiag.cogSog.0.cog", 2.02);
        check("candiag.cogSog.0.sog", 0.1);

    }

    @Test
    public void test4() {
        String testData = "t,83652827\n" +
                "\n" +
                "outsideEnvironment,0,83663614,288.65,298.45,101400\n" +
//                                             |      |      atmosphericPressure
//                                             |      outsideAmbientAirTemperature,
//                                             waterTemperature,
//        #outsideEnvironment,id,lastmodified,
                "humidity,0,83675791,41.8,\n" +
            // #humidity,id,lastmodified,actual,set
                "pressure,0,83675791,102600"; // aparent
          //   #pressure,id,lastmodified,actual
        inject(testData);
        check("candiag.humidity.0.actual", 41.8);
        check("candiag.outsideEnvironment.0.waterTemperature", 288.65);
        check("candiag.outsideEnvironment.0.outsideAmbientAirTemperature", 298.45);
        check("candiag.outsideEnvironment.0.atmosphericPressure", 101400.0);
        check("candiag.pressure.0.actual", 102600.0);

    }

    @Test
    public void test5() {
        String testData = "#temperature,id,lastmodified,actual,set\n" +
                "#temperatureExt,id,lastmodified,actual,set\n" +
                "t,83652827\n" +
                "temperature,0,83663614,273.15,\n" +
                //                             set
                //                      actual
                // #temperature,id,lastmodified,actual,set
                "temperatureExt,0,83663614,273.15,\n";
                //                                set
                //                         actual
                // #temperatureExt,id,lastmodified,actual,set
        inject(testData);
        check("candiag.temperature.0.actual", 273.15);
        check("candiag.temperatureExt.0.actual", 273.15);

    }

    @Test
    public void test6() {
        String testData = "#temperatures,id,lastmodified,temperatures...\n" +
                "t,83652827\n" +
                "\n" +
                "temperatures,83663614,273.15,278.15,271.15,272.15";
        inject(testData);
        Data.DataKey k = new Data.DataKey("candiag.temperatures", Data.Unit.MAP, Data.DataType.NONE,"candiag.temperature");
        Data.Temperatures temperatures = store.get(k);
        Map<Integer, Data.OneWireTemperature> onewireTemp = temperatures.getTemperatures();

        Assert.assertEquals(273.15,onewireTemp.get(0).temp,0.01);
        Assert.assertEquals(278.15, onewireTemp.get(1).temp, 0.01);
        Assert.assertEquals(271.15, onewireTemp.get(2).temp, 0.01);
        Assert.assertEquals(272.15, onewireTemp.get(3).temp, 0.01);

    }

    @Test
    public void test7() {
        String testData = "#bmp280,id,lastmodified,temp,pressure,humidity,historyInterval,history...\n" +
                "t,83652827\n" +
                "\n" +
                "bmp280,83663614,27.48,1010.19,36.28,675000,998.31,998.42,998.4,998.21,998.2\n" +
                "\n" +
                "\n";
        inject(testData);
        Data.DataKey k = new Data.DataKey("candiag.bmp280", Data.Unit.MAP, Data.DataType.NONE,"candiag.bmp280");
        Data.BMP280 bmp280 = store.get(k);
        Assert.assertEquals((27.48+273.15),bmp280.getTemp(),0.01);
        Assert.assertEquals(101019, bmp280.getPressure(), 0.01);
        Assert.assertEquals(36.28,bmp280.getHumidity(),0.01);
        Assert.assertEquals(675000,bmp280.getHistoryInterval());
        List<Double> history = bmp280.getPressureHistory();
        Double[] expected = new Double[]{998.31,998.42,998.4,998.21,998.2};

        Assert.assertArrayEquals(expected, history.toArray(new Double[history.size()]));

    }

    @Test
    public void test9() {
        String testData = "#type,id,lastmodified,...\n" +
                "#xte,id,lastmodified,xte\n" +
                "t,83652827\n" +
                "\n" +
                "xte,0,83663614,10.20\n";
        inject(testData);
        check("candiag.xte.0.xte", 10.20);
    }
    @Test
    public void test10() {
        String testData = "#variation,id,lastmodified,daysSince1970,variation\n" +
                "t,83652827\n" +
                "\n" +
                "variation,0,83663614,18973,-0.02\n" +
                "\n";
        inject(testData);
        check("candiag.variation.0.variation", -0.02);
    }

    @Test
    public void test11() {
        String testData = "#log,id,lastmodified,daysSince1970,secondsSinceMidnight,log,tripLog\n" +
                "t,83652827\n" +
                "\n" +
                "log,0,83663614,18973,77353.00,23878151,23876299\n" +
                "";
        inject(testData);
        Data.DataKey k = new Data.DataKey("candiag.log.0", Data.Unit.MAP, Data.DataType.NONE,"candiag.log.0");
        Data.NMEA2KLog n2kLog = store.get(k);
        Assert.assertEquals(23878151.0,n2kLog.getLog(),0.1);
        Assert.assertEquals(23876299.0,n2kLog.getTrip(),0.1);
        Assert.assertEquals(18973, n2kLog.getDaysSince1970());
        Assert.assertEquals(77353.0, n2kLog.getSecondsSinceMidnight(), 0.1);
    }

    @Test
    public void test12() {
        String testData = "#position,id,lastmodified,latitude,longitude\n" +
                "t,83652827\n" +
                "\n" +
                "position,0,83663614,1.01,2.02\n" +
                "\n";
        inject(testData);
        Data.DataKey k = new Data.DataKey("candiag.position.0", Data.Unit.MAP, Data.DataType.NONE,"candiag.log.0");
        Data.NMEA2KPosition n2kPosition = store.get(k);
        Assert.assertEquals(1.01,n2kPosition.getLatitude(),0.1);
        Assert.assertEquals(2.02,n2kPosition.getLongitude(),0.1);
    }


    private void check(String key, double v) {
        Data.DataKey k = new Data.DataKey(key, Data.Unit.MAP, Data.DataType.NONE,"");
        Data.DoubleDataValue dv = store.get(k);
        Assert.assertNotNull(dv);
        Assert.assertEquals(v, dv.getValue(), v / 100);
    }

}
