package uk.co.tfd.kindle.nmea2000;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NMEA0183ClientTest {

    private NMEA0183Client nmea0183CLient;
    private String lastLine;

    @Before
    public void before() {
        nmea0183CLient = new NMEA0183Client();
        nmea0183CLient.addHandler("RMC",
                new NMEA0183Handler(){
                    @Override
                    public void parseMessage(String line) {
                        lastLine = line;
                    }
                });
    }

    @Test
    public void testParseGoodLine() throws UnsupportedEncodingException {
        nmea0183CLient.processLine("$GPRMC,153235.425,A,5231.373,N,01323.790,E,1776.4,139.1,170824,000.0,W*44");
        Assert.assertNotNull(lastLine);
    }
    @Test
    public void testParseBadLine() throws UnsupportedEncodingException {
        nmea0183CLient.processLine("$GPRMC,153235.425,A,5231.373,N,01323.790,E,1776.4,139.1,170824,000.0,W*14");
        Assert.assertNull(lastLine);
    }

    @Test
    public void testParseNoHandler() throws UnsupportedEncodingException {
        // CS known to be good.
        nmea0183CLient.processLine("$GPGSA,A,3,01,02,03,04,05,06,07,08,09,10,11,12,1.0,1.0,1.0*30");
        Assert.assertNull(lastLine);
    }


}
