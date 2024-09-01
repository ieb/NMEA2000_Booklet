package uk.co.tfd.kindle.nmea2000.can;

public interface CanMessageListener {
    int[] getPgns();

    /**
     * Called when a message is dropped because the handler that was registered didnt accept the message.
     * @param pgn
     */
    void onDrop(int pgn);

    /**
     * Called when the message was not handled because no handler was registered.
     * @param pgn
     */
    void onUnhandled(int pgn);

    /**
     * Called when the message is handled
     * @param message the message that was handled.
     */
    void onMessage(CanMessage message);

}
