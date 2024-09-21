package uk.co.tfd.kindle.nmea2000.can;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class SimulatorTest {

    private static final Logger log = LoggerFactory.getLogger(SimulatorTest.class);
    private Polar polar;
    private CanMessageProducer canMessageProducer = new CanMessageProducer();

    @Before
    public void before() throws IOException, ParseException {
        InputStream in = getClass().getResourceAsStream("/config.json");
        JSONParser jsonParser = new JSONParser();
        InputStreamReader reader = new InputStreamReader(in);
        Map<String, Object> configuration = (Map<String, Object>) jsonParser.parse(reader);
        reader.close();
        polar = new Polar(configuration);
    }

    //@Test
    public void testUpdate() throws InterruptedException {
        Simulator s = new Simulator(canMessageProducer, polar);
        for (int i = 0; i < 100; i++) {
            s.updateModel();
            log.info("State {} ", s.toString());
            Thread.sleep(500);
        }
    }
}
