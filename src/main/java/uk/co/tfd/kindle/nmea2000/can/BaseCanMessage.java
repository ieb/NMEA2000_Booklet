package uk.co.tfd.kindle.nmea2000.can;

import java.lang.reflect.Field;

public class BaseCanMessage implements CanMessage {
    public final int pgn;
    public final int src;
    public final int count;
    public final String messageName;
    public final int timestamp;

    BaseCanMessage(int requiredPgn, int pgn, int source, int timestamp, String messageName) {
        if ( pgn != requiredPgn) {
            throw new IllegalArgumentException("Incorrect PGN  got "+pgn+" expected "+requiredPgn);
        }
        this.pgn = pgn;
        this.src = source;
        this.count = 1;
        this.messageName = messageName;
        this.timestamp = timestamp;
    }

    @Override
    public int getPgn() {
        return pgn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Class:").append(this.getClass().getSimpleName());
        for (Field f : this.getClass().getFields()) {
            Object v;
            try {
                v = f.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            sb.append(" ").append(f.getName()).append(":").append(v);
        }
        return sb.toString();
    }
}
