package uk.co.tfd.kindle.nmea2000.can;

import java.util.concurrent.atomic.AtomicInteger;

public class IsoMessageHandler implements CanMessageHandler {
    public static class PGN60928IsoAddressClaim extends BaseCanMessage {

        public final N2KReference.ManufacturerCode manufacturerCode;
        public final int uniqueNumber;
        public final short deviceInstance;
        public final short deviceClass;
        public final N2KReference.Industry industryGroup;
        public final short systemInstance;
        public final short deviceFuncton;

        public final static int PGN = 60928;

        PGN60928IsoAddressClaim(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp, "IsoAddressClaim");
            long codeAndManNumber = CanMessageData.get4ByteUInt(data, 0);
            int industryGroupAndSystemInstance = CanMessageData.get1ByteUInt(data, 7);
            this.manufacturerCode = N2KReference.ManufacturerCode.lookup((int)((codeAndManNumber >> 21) & 0x07ff)); // top 11 bits,
            this.uniqueNumber = (int) ((codeAndManNumber) & 0x1fffff); // lower 21 bits
            this.deviceInstance = (short) CanMessageData.get1ByteUInt(data, 4);
            this.deviceFuncton = (short) CanMessageData.get1ByteUInt(data, 5);
            this.deviceClass = (short) CanMessageData.get1ByteUInt(data, 6);
            this.industryGroup =  N2KReference.Industry.lookup ((int)(industryGroupAndSystemInstance >> 4) & 0x0f);
            this.systemInstance = (short) ((industryGroupAndSystemInstance) & 0x0f);
        }
    }

    public static class PGN126993HeartBeat extends BaseCanMessage {

        public final static int PGN = 126993;

        public PGN126993HeartBeat(int pgn, int timeStamp, byte source, byte[] data) {
            super(PGN, pgn, source, timeStamp, "Heartbeat");
        }

    }

    public static class CanBusStatus extends BaseCanMessage {
        public final static int PGN = -2;
        public final  AtomicInteger dropped = new AtomicInteger();
        public final  AtomicInteger recieved = new AtomicInteger();
        public final  AtomicInteger nohandler = new AtomicInteger();

        public CanBusStatus() {
            super(PGN, PGN, -1, 0, "CanBusStatus");
        }

    }

    @Override
    public CanMessage handleMessage(int pgn, int timeStamp, byte source, byte[] data) {
        switch (pgn) {
            case PGN60928IsoAddressClaim.PGN: return new PGN60928IsoAddressClaim(pgn, timeStamp, source, data);
            case PGN126993HeartBeat.PGN: return new PGN126993HeartBeat(pgn, timeStamp, source, data);
        }
        return null;
    }


    private static final int[] pgns = { PGN60928IsoAddressClaim.PGN, PGN126993HeartBeat.PGN};
    @Override
    public int[] getPgns() {
        return pgns;
    }

}
