package uk.co.tfd.kindle.nmea2000.can;

import java.io.UnsupportedEncodingException;

public class Utils {

    public static String addCheckSum(String line) throws UnsupportedEncodingException {
        byte cs = 0;
        byte[] bytes = line.getBytes("ASCII");
        for (int i = 1; i < bytes.length; i++) {
            cs = (byte)(cs ^ bytes[i]);
        }
        return String.format("%s*%02X", line, cs);
    }

}
