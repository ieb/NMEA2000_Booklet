package uk.co.tfd.kindle.nmea2000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.nmea2000.can.*;

import java.util.*;

public class SeaSmartHandler extends StatusUpdates implements NMEA0183Handler {
    private static final Logger log = LoggerFactory.getLogger(SeaSmartHandler.class);
    private final CanMessageProducer canMessageProducer;

    private Map<Integer, CanMessageHandler> handlers = new HashMap<Integer, CanMessageHandler>();
    private Set<Integer> ignoreSet = new HashSet<>();
    private String commandMessage = "";
    private IsoMessageHandler.CanBusStatus status = new IsoMessageHandler.CanBusStatus();

    public SeaSmartHandler(CanMessageProducer canMessageProducer) {
        this.canMessageProducer = canMessageProducer;
    }

    public void addHandler(CanMessageHandler handler) {
        for(int pgn: handler.getPgns()) {
            handlers.put(pgn, handler);
        }
    }


    /**
     * The NMEA0183 line, which has already had the checksum checked ok.
     * @param line
     */
    public void parseMessage(String line) {
      if (line.startsWith("$PCDIN,")) {
          String[] parts = line.substring(0,line.lastIndexOf('*')).split(",");
          int pgn = toUint(parts[1]);
          if ( ignoreSet.contains(pgn)) {
              return;
          }
          CanMessageHandler handler = handlers.get(pgn);
          if ( handler == null ) {
              handler = handlers.get(-1);
          }
          if ( handler != null) {
              CanMessage message = null;
              if (parts.length == 5) {
                  // packed form
                  message = handler.handleMessage(pgn,
                      toUint(parts[2]),
                      (byte) toUint(parts[3]),
                      CanMessageData.asPackedByteArray(parts[4],0, parts[4].length()));
              } else {
                  // comma seperated form.
                  message = handler.handleMessage(pgn,
                      toUint(parts[2]),
                      (byte) toUint(parts[3]),
                      CanMessageData.asByteArray(parts, 4));
              }
              // only create the CanM(essage if it can be handled.
              if  (message == null) {
                  status.dropped.incrementAndGet();
              } else {
                  status.recieved.incrementAndGet();
                  canMessageProducer.emitMessage(message);
              }
              // do something with the message ?
          } else {
              log.info("Uhandled pgn:{}", pgn);
              status.nohandler.incrementAndGet();
              canMessageProducer.emitNoHandler(pgn);
          }
      };
    }

    public void emitStatus() {
        canMessageProducer.emitMessage(status);
    }




    private int toUint(String field) {
        return Integer.parseInt(field, 16);
    }


    public void addIgnore(int[] array) {
    }

    public void addIgnore(int pgn) {
        ignoreSet.add(pgn);
    }


    /**
     * Generate a command message to send to the server containing the PGNs that
     * the client needs based on the active components listening to the canMessageProducer.
     * This is typically the messages required to feed the widgets that are currently showing.
     * Doing this avoids the can device emitting more messages than are necessary.
     * @return
     */
    public String getCommandMessage() {
        List<String> pgns = new ArrayList<>();
        for (Integer pgn: canMessageProducer.getPgnFilter()) {
            if (pgn > 100) {
                pgns.add(String.valueOf(pgn));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("$PCDCM,1,");
        sb.append(pgns.size());
        sb.append(",");
        sb.append(String.join(",", pgns));
        return sb.toString();
    }


}
