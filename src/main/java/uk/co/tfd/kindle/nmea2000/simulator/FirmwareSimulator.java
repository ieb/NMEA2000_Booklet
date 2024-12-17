package uk.co.tfd.kindle.nmea2000.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.CanMessageData;
import uk.co.tfd.kindle.nmea2000.can.NavMessageHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FirmwareSimulator {
    private final static Logger log = LoggerFactory.getLogger(FirmwareSimulator.class);
    private final List<Sender> senders = new ArrayList();
    private final N2KEngineFirmware n2KEngineFirmware;
    private final BMSFirmware bmsFirmware;
    private final BoatFirmware boatFirmware;

    public FirmwareSimulator() {
        n2KEngineFirmware = new N2KEngineFirmware(this);
        bmsFirmware = new BMSFirmware(this);
        boatFirmware = new BoatFirmware(this);
    }

    public void addSender(Sender s ) {
        senders.add(s);
    }

    public long sendNext(OutputStream out, PGNFilter filter) throws IOException {
        long now = System.currentTimeMillis();
        long wait = 10000;
        int sent = 0;
        int skipped = 0;
        filter.reset();
        for (Sender s : senders) {
            long delay = s.nextSend - now;
            if (delay < 0) {
                delay = s.send(out, filter);
                sent++;
            } else {
                skipped++;
            }
            if (delay < wait) {
                wait = delay;
            }
        }
        //log.info("Sent:{} skip:{} wait: {} {}" , sent, skipped, wait, filter);
        return wait;
    }
}
