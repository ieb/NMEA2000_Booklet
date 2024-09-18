package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class acts as a message bus informing listeners of messages posted onto the bus.
 * Listeners can be added and can request pgns are allowed to flow. A reference count is
 * maintained in streamPgns of the messages that have been requested by listeners.
 */
public class CanMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(CanMessageProducer.class);
    private CanMessageListener[] listeners = new CanMessageListener[0];
    private Map<Integer, Integer> streamPgns = new HashMap<>();
    private Set<Integer> pgnFilter = Collections.EMPTY_SET;

    public CanMessageProducer() {
    }

    public synchronized  void  addListener(CanMessageListener listener) {
        CanMessageListener[] cml = new CanMessageListener[listeners.length+1];
        for (int i = 0; i < listeners.length; i++) {
            cml[i] = listeners[i];
        }
        cml[listeners.length] = listener;
        listeners = cml;
    }

    public synchronized  void  removeListener(CanMessageListener listener) {
        int newlen = listeners.length;
        for (int i = 0; i < listeners.length; i++) {
            if ( listener == listeners[i] || listeners[i] == null) {
                listeners[i] = null;
                newlen--;
            }
        }
        CanMessageListener[] cml = new CanMessageListener[newlen];
        for (int i = 0, j=0; i < listeners.length; i++) {
            if ( listeners[i] != null) {
                cml[j] = listeners[i];
                j++;
            }
        }
        listeners = cml;
    }

    public void emitDrop(int pgn) {
        log.info("Emit drop pgn:{}", pgn);
        for(CanMessageListener l : listeners) {
            l.onDrop(pgn);
        }
    }
    public void emitMessage(CanMessage message) {
        int pgn = message.getPgn();
        // messages with pgn < 999 are internally generated.
        if (pgn < 999 || pgnFilter.size() == 0 || pgnFilter.contains(pgn)) {
            for(CanMessageListener l : listeners) {
                //log.info("Sendign message to {}  message:{} ", l, message);
                l.onMessage(message);
            }
        } else {
            log.info("Emit drop for filtered pgn:{} {} ", pgn);
            for(CanMessageListener l : listeners) {
                l.onDrop(pgn);
            }
        }
    }

    public void emitNoHandler(int pgn) {
        log.info("Emit no handler pgn:{}", pgn);
        for(CanMessageListener l : listeners) {
            l.onUnhandled(pgn);
        }
    }

    public synchronized  void addPgnsToStream(int[] pgns) {
        for (int pgn : pgns) {
            if (!streamPgns.containsKey(pgn)) {
                streamPgns.put(pgn, 1);
            } else {
                streamPgns.put(pgn, streamPgns.get(pgn) + 1);
            }
        }
        updateFilter();
    }

    public synchronized  void removePgnsFromStream(int[] pgns) {
        for(int pgn: pgns) {
            if ( streamPgns.containsKey(pgn) ) {
                int i = streamPgns.get(pgn);
                if ( i > 1) {
                    streamPgns.put(pgn,streamPgns.get(pgn)-1);
                } else {
                    streamPgns.remove(pgn);
                }
            }
        }
        updateFilter();
    }
    private void updateFilter() {
        Set<Integer> newSet = new HashSet<>();
        for (Map.Entry<Integer, Integer> e: streamPgns.entrySet()) {
            newSet.add(e.getKey());
        }
        pgnFilter = Collections.unmodifiableSet(newSet);
    }

    public Set<Integer> getPgnFilter() {
        return pgnFilter;
    }

}
