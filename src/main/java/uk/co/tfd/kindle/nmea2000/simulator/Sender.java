package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.NMEA0183Client;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public abstract class Sender {
    private final Logger log;
    public long nextSend;
    public int source = 15;

    public Sender() {
        log = LoggerFactory.getLogger(this.getClass());
    }

    public abstract int send(OutputStream out, PGNFilter filter) throws IOException;


    public void send(OutputStream out, CanMessageData cm) throws IOException {
        try {
            int timestamp = (int) (System.currentTimeMillis() & 0x7fffffff);
            StringBuilder dataAsHex = new StringBuilder();
            for (int i = 0; i < cm.message.length; i++) {
                dataAsHex.append(String.format("%02X", cm.message[i]));
            }
            String sentence = NMEA0183Client.addCheckSum(
                    "$PCDIN,"
                            + Long.toHexString(cm.pgn).toUpperCase() + ","
                            + Integer.toHexString(timestamp).toUpperCase() + ","
                            + Integer.toHexString(source).toUpperCase() + ","
                            + dataAsHex.toString());
            log.info(">{}", sentence);
            out.write((sentence+"\r\n").getBytes("ASCII"));
        } catch (UnsupportedEncodingException ex) {
            log.info(ex.getMessage(), ex);
        }

    }


}
