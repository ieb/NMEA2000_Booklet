package uk.co.tfd.kindle.nmea2000.can;

public interface CanMessageHandler {

    CanMessage handleMessage(int pgn, int timeStamp, byte source, byte[] data);

    int[] getPgns();
}
