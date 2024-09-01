package uk.co.tfd.kindle.nmea2000;

public interface NMEA0183Handler {
    void parseMessage(String line);
}
