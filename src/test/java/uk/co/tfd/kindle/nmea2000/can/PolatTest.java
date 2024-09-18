package uk.co.tfd.kindle.nmea2000.can;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class PolatTest {

    private Map<String, Object> configuration;
    private static final Logger log = LoggerFactory.getLogger(PolatTest.class);

    @Before
    public void before() throws IOException, ParseException {
        InputStream in = getClass().getResourceAsStream("/config.json");
        JSONParser jsonParser = new JSONParser();
        InputStreamReader reader = new InputStreamReader(in);
        configuration = (Map<String, Object>) jsonParser.parse(reader);
        reader.close();
    }

    @Test
    public void testCreate() {
        log.info("Testing Polar Map");
        Polar p = new Polar(configuration);
        p.setDebug(true);
        double polarSpeed = p.calcPolarSpeed(10.0/CanMessageData.scaleToKnots, 56.0/CanMessageData.scaleToDegrees);
        Assert.assertEquals(7.86, polarSpeed*CanMessageData.scaleToKnots, 0.1);
    }

    @Test
    public void testCheckStw() {
        log.info("Testing Polar Map");
        Polar p = new Polar(configuration);
        //p.setDebug(true);
        double tws[] = { 5.8, 7.3, 10.5, 13.4, 15.4, 18, 20.2, 24, 26.5, 32, 43.2};
        double twa[] = { 56,  56,  56,   56,   56,   56, 56,   56, 56,   56,  56};
        double stw[] = { 5.975,  6.8,  7.925,  8.29,   8.45,   8.65, 8.812,   9.04, 9.14,  9.29,  5.56};
        for (int i = 0; i < tws.length; i++) {
            double polarSpeed = p.calcPolarSpeed(tws[i]/CanMessageData.scaleToKnots, twa[i]/CanMessageData.scaleToDegrees);
            Assert.assertEquals("twa="+twa[i]+" tws="+tws[i], stw[i], polarSpeed*CanMessageData.scaleToKnots, 0.1);
        }
    }

    @Test
    public void testCheckPolar() throws IOException {
        Polar p = new Polar(configuration);
        FileWriter fw = new FileWriter("polartest.csv");
        fw.append("twa,2,4,6,8,10,12,14,18,20,24,30,35,40,45,50\n");
        double tws[] = { 2,4,6,8,10,12,14,18,20,24,30,35,40,45,50 };
        for (double twa = 0; twa < 180; twa+= 5) {
            fw.append(Double.toString(twa));
            for (int i = 0; i < tws.length; i++) {
                fw.append(",");
                double stw = CanMessageData.scaleToKnots * p.calcPolarSpeed(tws[i]/CanMessageData.scaleToKnots, twa/CanMessageData.scaleToDegrees);
                fw.append(Double.toString(stw));
            }
            fw.append("\n");
        }
        for (double twa = -180; twa < 0; twa+= 5) {
            fw.append(Double.toString(twa));
            for (int i = 0; i < tws.length; i++) {
                fw.append(",");
                double stw = CanMessageData.scaleToKnots * p.calcPolarSpeed(tws[i]/CanMessageData.scaleToKnots, twa/CanMessageData.scaleToDegrees);
                fw.append(Double.toString(stw));
            }
            fw.append("\n");
        }
        fw.close();
    }

    @Test
    public  void testCheckVmg1() throws IOException {
        Polar p = new Polar(configuration);
        p.setDebug(true);
        Polar.PolarTarget t = p.calcPolarTarget(10 / CanMessageData.scaleToKnots, 45 / CanMessageData.scaleToDegrees, 45 / CanMessageData.scaleToDegrees);
        log.info("Polar Target {} ", t);
    }

        @Test
    public  void testCheckVmg() throws IOException {
        Polar p = new Polar(configuration);
        FileWriter fw = new FileWriter("vmgtest.csv");
        fw.append("Upwind\n");
        fw.append("tws,twa,stw,vmg\n");
        double tws[] = {2, 4, 6, 8, 10, 12, 14, 18, 20, 24, 30, 35, 40, 45, 50};
        for (int i = 0; i < tws.length; i++) {
            fw.append(Double.toString(tws[i]));
            fw.append(", ");
            Polar.PolarTarget t  = p.calcPolarTarget(tws[i] / CanMessageData.scaleToKnots, 50 / CanMessageData.scaleToDegrees);
            fw.append(Double.toString(t.twa*CanMessageData.scaleToDegrees));
            fw.append(", ");
            fw.append(Double.toString(t.stw * CanMessageData.scaleToKnots));
            fw.append(", ");
            fw.append(Double.toString(t.vmg * CanMessageData.scaleToKnots));
            fw.append("\n");
            }
        fw.append("\n");
        fw.append("Downwind\n");
        fw.append("tws,twa,stw,vmg\n");
        for (int i = 0; i < tws.length; i++) {
            fw.append(Double.toString(tws[i]));
            fw.append(", ");
            Polar.PolarTarget t  = p.calcPolarTarget(tws[i] / CanMessageData.scaleToKnots, 150 / CanMessageData.scaleToDegrees);
            fw.append(Double.toString(t.twa*CanMessageData.scaleToDegrees));
            fw.append(", ");
            fw.append(Double.toString(t.stw*CanMessageData.scaleToKnots));
            fw.append(", ");
            fw.append(Double.toString(t.vmg*CanMessageData.scaleToKnots));
            fw.append("\n");
        }
        fw.append("\n");
        fw.close();
    }


    @Test
    public void testCheckInput() throws IOException {
        Map<String, Object> config = (Map<String, Object>) configuration.get("polar");
        Map<String, Object> polarMap = (Map<String, Object>) config.get("polarMap");
        List<Double> tws = (List<Double>) polarMap.get("tws");
        List<Double> twa = (List<Double>) polarMap.get("twa");
        List<List<Double>> stw = (List<List<Double>>) polarMap.get("stw");
        FileWriter fw = new FileWriter("polarinput.csv");
        fw.append("twa");
        for (int is = 0; is < tws.size(); is++) {
            fw.append(",");
            fw.append(tws.get(is).toString());
        }
        fw.append("\n");
        for (int ia = 0; ia < twa.size(); ia++) {
            fw.append(twa.get(ia).toString());
            for(Double s: stw.get(ia)) {
                fw.append(",");
                fw.append(s.toString());
            }
            fw.append("\n");
        }
        for (int ia = twa.size()-2; ia > 0; ia--) {
            fw.append(Double.toString(-1.0*twa.get(ia)));
            for(Double s: stw.get(ia)) {
                fw.append(",");
                fw.append(s.toString());
            }
            fw.append("\n");
        }
        fw.close();
    }

    @Test
    public void testCalc() {
        Polar p = new Polar(configuration);
        for(int i = 0; i < 50; i++) {
            int n = 0;
            long start = System.nanoTime();
            for (double tws = 0; tws < 60; tws += 0.1) {
                for (double twa = 0; twa < 180; twa += 1.0) {
                    n++;
                    double polarSpeed = p.calcPolarSpeed(tws / CanMessageData.scaleToKnots, twa / CanMessageData.scaleToDegrees);
                }
            }
            long t = System.nanoTime() - start;
            log.info("Calculations {} in {}ns {}ns/cals", n, t, t/n);
        }
    }

    @Test
    public void testCalcTarget() {
        Polar p = new Polar(configuration);
        for(int i = 0; i < 10; i++) {
            int n = 0;
            long start = System.nanoTime();
            for (double tws = 0; tws < 60; tws += 0.1) {
                for (double twa = 0; twa < 180; twa += 1.0) {
                    n++;
                    Polar.PolarTarget target = p.calcPolarTarget(tws / CanMessageData.scaleToKnots, twa);
                }
            }
            long t = System.nanoTime() - start;
            log.info("Calc Target {} in {}ns {}ns/cals", n, t, t/n);
        }
    }




}
