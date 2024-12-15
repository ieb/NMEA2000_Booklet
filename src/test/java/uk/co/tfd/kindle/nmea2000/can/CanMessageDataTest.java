package uk.co.tfd.kindle.nmea2000.can;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class CanMessageDataTest {

    private static final Logger log = LoggerFactory.getLogger(CanMessageDataTest.class);

    @Test
    public void testAsByteArray() {
        Assert.assertArrayEquals(new byte[] {0x01, (byte)0xff, (byte)0xfd}, CanMessageData.asByteArray("01,FF,FD".split(","), 0));
    }
    @Test
    public void dumpNAValues() {

        Assert.assertEquals(new BigInteger("18446744073709551615"), CanMessageData.n2kUInt64NA);
        Assert.assertEquals(new BigInteger("9223372036854775807"), CanMessageData.n2kInt64NA);
        Assert.assertEquals(4294967295L, CanMessageData.n2kUInt32NA);
        Assert.assertEquals(2147483647, CanMessageData.n2kInt32NA);
        Assert.assertEquals(16777215, CanMessageData.n2kUInt24NA);
        Assert.assertEquals(8388607, CanMessageData.n2kInt24NA);
        Assert.assertEquals(65535, CanMessageData.n2kUInt16NA);
        Assert.assertEquals(32767, CanMessageData.n2kInt16NA);
        Assert.assertEquals(255, CanMessageData.n2kUInt8NA);
        Assert.assertEquals(127, CanMessageData.n2kInt8NA);
        Assert.assertEquals(-1E9, CanMessageData.n2kDoubleNA, 0);
    }

    @Test
    public void testNA() {
        Assert.assertEquals(CanMessageData.n2kUInt64NA,
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt64NA,
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFF7F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt32NA,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt32NA,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("00FFFFFF7F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt24NA,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt24NA,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00FFFF7F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt16NA,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt16NA,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("00FF7F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt16NA,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt16NA,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("00FF7F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt8NA,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("00FF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt8NA,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("007F010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get8ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get8ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFF7F010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get4ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get4ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFFFF7F010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get3ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get3ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFF7F010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get2ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1, 1.0f), 0.0);
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get2ByteDouble(
                        CanMessageData.asPackedByteArray("00FF7F010F",0, -1),1, 1.0f), 0.0);
    }

    @Test
    public void test1ByteInt() {
        Assert.assertEquals(-1,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("00FF010F",0, -1),1));
        Assert.assertEquals(-127,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("0081010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("0001010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("0010010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("00100110",0, -1),3));
        Assert.assertEquals(CanMessageData.n2kInt8NA,
                CanMessageData.get1ByteInt(
                        CanMessageData.asPackedByteArray("001001",0, -1),3));
        for (int i = -127; i < 127 ; i++) {
            String s = String.format("%02X", i);
            if ( s.length() == 8) {
                s = s.substring(6);
            }
            Assert.assertEquals(i,
                    CanMessageData.get1ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }

        byte[] message = new byte[5];
        for (int i = -127; i < 127 ; i++) {
            CanMessageData.set1ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get1ByteInt(message,1));
        }

    }
    @Test
    public void test1ByteUint() {
        Assert.assertEquals(254,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("00FE010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("0001010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("0010010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("0010",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt8NA,
                CanMessageData.get1ByteUInt(
                        CanMessageData.asPackedByteArray("00",0, -1),1));
        for (int i = 0; i < 255 ; i++) {
            String s = String.format("%02X", i);
            if ( s.length() > 2) {
                s = s.substring(s.length()-2, s.length());
            }
            String inputString = "00,01,"+s;
            //log.info("InputString {} ", inputString);
            byte[] inputValue = CanMessageData.asByteArray(inputString.split(","),0);
            //log.info("Bytes {} ", Arrays.toString(inputValue));
            Assert.assertArrayEquals("asByteArray Failed to parse", new byte[] {0x00, 0x01, (byte)(i&0xff)}, inputValue);
            int testValue = CanMessageData.get1ByteUInt(inputValue, 2);

            //log.info("Input {} {} {}", Integer.toString(i,2), Integer.toString(testValue, 2), s);
            Assert.assertEquals(i, testValue);
        }
        byte[] message = new byte[5];
        for (int i = 0; i < 255 ; i++) {
            CanMessageData.set1ByteUInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get1ByteUInt(message,1));
        }

    }

    @Test
    public void test2ByteInt() {
        Assert.assertEquals(-1,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1));
        Assert.assertEquals(-2,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("00FEFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("000100010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("001000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("001000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt16NA,
                CanMessageData.get2ByteInt(
                        CanMessageData.asPackedByteArray("0010",0, -1),1));
        for (int i = -1024; i < 1024 ; i++) {
            String s = String.format("%04X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get2ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[5];
        for (int i = -1024; i < 1024 ; i++) {
            CanMessageData.set2ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get2ByteInt(message,1));
        }

    }
    @Test
    public void test2ByteUInt() {
        Assert.assertEquals(CanMessageData.n2kUInt16NA,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1));
        Assert.assertEquals(65534,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("00FEFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("000100010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("001000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("001000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt16NA,
                CanMessageData.get2ByteUInt(
                        CanMessageData.asPackedByteArray("0010",0, -1),1));
        for (int i = 0; i < 2024 ; i++) {
            String s = String.format("%04X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get2ByteUInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[5];
        for (int i = 0; i < 2024 ; i++) {
            CanMessageData.set2ByteUInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get2ByteUInt(message,1));
        }

    }

    @Test
    public void test3ByteInt() {
        Assert.assertEquals(-1,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1));
        Assert.assertEquals(-2,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00FEFFFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00010000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00100000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("00100000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt24NA,
                CanMessageData.get3ByteInt(
                        CanMessageData.asPackedByteArray("001000",0, -1),1));
        for (int i = -8388608; i < -8388608+10024 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2)+s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }

        byte[] message = new byte[5];
        for (int i = -8388608; i < -8388608+10024 ; i++) {
            CanMessageData.set3ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteInt(message,1));
        }
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            CanMessageData.set3ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteInt(message,1));
        }

    }
    @Test
    public void test3ByteUInt() {
        Assert.assertEquals(CanMessageData.n2kUInt24NA,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt24NA-1,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00FEFFFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00010000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00100000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("00100000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt24NA,
                CanMessageData.get3ByteUInt(
                        CanMessageData.asPackedByteArray("001000",0, -1),1));
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteUInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[5];
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            CanMessageData.set3ByteUInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get3ByteUInt(message,1));
        }

    }

    @Test
    public void test4ByteInt() {
        Assert.assertEquals(-1,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1));
        Assert.assertEquals(-2,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("00FEFFFFFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("0001000000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("0010000000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("0010000000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt32NA,
                CanMessageData.get4ByteInt(
                        CanMessageData.asPackedByteArray("00100000",0, -1),1));
        for (int i = -2147483648; i < -2147483648+10024 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[5];
        for (int i = -2147483648; i < -2147483648+10024 ; i++) {
            CanMessageData.set4ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteInt(message,1));
        }
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            CanMessageData.set4ByteInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteInt(message,1));
        }

    }
    @Test
    public void test4ByteUInt() {
        Assert.assertEquals(CanMessageData.n2kUInt32NA,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt32NA-1,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("00FEFFFFFF010F",0, -1),1));
        Assert.assertEquals(1,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("0001000000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("0010000000010F",0, -1),1));
        Assert.assertEquals(16,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("0010000000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt32NA,
                CanMessageData.get4ByteUInt(
                        CanMessageData.asPackedByteArray("00100000",0, -1),1));
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteUInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[5];
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            CanMessageData.set4ByteUInt(message,1,i);
            Assert.assertEquals(i,
                    CanMessageData.get4ByteUInt(message,1));
        }

    }

    @Test
    public void test8ByteInt() {
        Assert.assertEquals(BigInteger.valueOf(-1),
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(-2),
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("00FEFFFFFFFFFFFFFF010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(1),
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("000100000000000000010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(16),
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("001000000000000000010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(16),
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("001000000000000000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kInt64NA,
                CanMessageData.get8ByteInt(
                        CanMessageData.asPackedByteArray("0010000000000000",0, -1),1));
        for (long i = -2147483648; i < -2147483648+10024 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }

        byte[] message = new byte[9];
        for (long i = -2147483648; i < -2147483648+10024 ; i++) {
            CanMessageData.set8ByteInt(message,1,BigInteger.valueOf(i));
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteInt(message,1));
        }
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            CanMessageData.set8ByteInt(message,1,BigInteger.valueOf(i));
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteInt(message,1));
        }

    }
    @Test
    public void test8ByteUInt() {
        Assert.assertEquals(CanMessageData.n2kUInt64NA,
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt64NA.subtract(BigInteger.ONE),
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("00FEFFFFFFFFFFFFFF010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(1),
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("000100000000000000010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(16),
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("001000000000000000010F",0, -1),1));
        Assert.assertEquals(BigInteger.valueOf(16),
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("001000000000000000",0, -1),1));
        Assert.assertEquals(CanMessageData.n2kUInt64NA,
                CanMessageData.get8ByteUInt(
                        CanMessageData.asPackedByteArray("0010000000000000",0, -1),1));
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteUInt(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1));
        }
        byte[] message = new byte[9];
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            CanMessageData.set8ByteUInt(message,1,BigInteger.valueOf(i));
            Assert.assertEquals(BigInteger.valueOf(i),
                    CanMessageData.get8ByteUInt(message,1));
        }

    }

    @Test
    public void test1ByteDouble() {
        Assert.assertEquals(-0.1,
                CanMessageData.get1ByteDouble(
                        CanMessageData.asPackedByteArray("00FF010F",0, -1),1, 0.1f), 0.0001);
        Assert.assertEquals(-12.7,
                CanMessageData.get1ByteDouble(
                        CanMessageData.asPackedByteArray("0081010F",0, -1),1, 0.1f), 0.0001);
        Assert.assertEquals(0.1,
                CanMessageData.get1ByteDouble(
                        CanMessageData.asPackedByteArray("0001010F",0, -1),1, 0.1f), 0.0001);
        Assert.assertEquals(1.6,
                CanMessageData.get1ByteDouble(
                        CanMessageData.asPackedByteArray("0010010F",0, -1),1, 0.1f), 0.0001);
        for (int i = -127; i < 127 ; i++) {
            String s = String.format("%02X", i);
            if ( s.length() == 8) {
                s = s.substring(6);
            }
            Assert.assertEquals(0.2*i,
                    CanMessageData.get1ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.2f), 0.0001);
        }
        byte[] message = new byte[9];
        for (int i = -127; i < 127 ; i++) {
            double v = 0.2*i;
            CanMessageData.set1ByteDouble(message,0,v, 0.2);
            Assert.assertEquals(v,
                    CanMessageData.get1ByteDouble(message,0, 0.2), 0.01);
        }

    }

    @Test
    public void test1ByteUDouble() {
        Assert.assertEquals(2.54,
                CanMessageData.get1ByteUDouble(
                        CanMessageData.asPackedByteArray("00FE010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get1ByteUDouble(
                        CanMessageData.asPackedByteArray("0001010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get1ByteUDouble(
                        CanMessageData.asPackedByteArray("0010010F",0, -1),1, 0.01), 0.00001);
        for (int i = 0; i < 255 ; i++) {
            String s = String.format("%02X", i);
            if ( s.length() > 2) {
                s = s.substring(s.length()-2, s.length());
            }
            Assert.assertEquals(0.01*i,
                    CanMessageData.get1ByteUDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = 0; i < 255 ; i++) {
            double v = 0.01*i;
            CanMessageData.set1ByteUDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get1ByteUDouble(message,0, 0.01), 0.0001);
        }

    }

    @Test
    public void test2ByteDouble() {
        Assert.assertEquals(-0.01,
                CanMessageData.get2ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(-0.02,
                CanMessageData.get2ByteDouble(
                        CanMessageData.asPackedByteArray("00FEFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get2ByteDouble(
                        CanMessageData.asPackedByteArray("000100010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get2ByteDouble(
                        CanMessageData.asPackedByteArray("001000010F",0, -1),1, 0.01), 0.00001);
        for (int i = -1024; i < 1024 ; i++) {
            String s = String.format("%04X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get2ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = -1024; i < 1024 ; i++) {
            double v = 0.01*i;
            CanMessageData.set2ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get2ByteDouble(message,0, 0.01), 0.0001);
        }

    }
    @Test
    public void test2ByteUDouble() {
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get2ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(655.34,
                CanMessageData.get2ByteUDouble(
                        CanMessageData.asPackedByteArray("00FEFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get2ByteUDouble(
                        CanMessageData.asPackedByteArray("000100010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get2ByteUDouble(
                        CanMessageData.asPackedByteArray("001000010F",0, -1),1, 0.01), 0.00001);
        for (int i = 0; i < 2024 ; i++) {
            String s = String.format("%04X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get2ByteUDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = 0; i < 2024 ; i++) {
            double v = 0.01*i;
            CanMessageData.set2ByteUDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get2ByteUDouble(message,0, 0.01), 0.0001);
        }
    }

    @Test
    public void test3ByteDouble() {
        Assert.assertEquals(-0.01,
                CanMessageData.get3ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(-0.02,
                CanMessageData.get3ByteDouble(
                        CanMessageData.asPackedByteArray("00FEFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get3ByteDouble(
                        CanMessageData.asPackedByteArray("00010000010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get3ByteDouble(
                        CanMessageData.asPackedByteArray("00100000010F",0, -1),1, 0.01), 0.00001);
        for (int i = -8388608; i < -8388608+10024 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())+s.substring(s.length()-4,s.length()-2)+s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get3ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.0001);
        }
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get3ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = -8388608; i < -8388608+10024 ; i++) {
            double v = 0.01*i;
            CanMessageData.set3ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get3ByteDouble(message,0, 0.01), 0.0001);
        }
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            double v = 0.01*i;
            CanMessageData.set3ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get3ByteDouble(message,0, 0.01), 0.0001);
        }

    }
    @Test
    public void test3ByteUDouble() {
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get3ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01*(CanMessageData.n2kUInt24NA-1),
                CanMessageData.get3ByteUDouble(
                        CanMessageData.asPackedByteArray("00FEFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get3ByteUDouble(
                        CanMessageData.asPackedByteArray("00010000010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get3ByteUDouble(
                        CanMessageData.asPackedByteArray("00100000010F",0, -1),1, 0.01), 0.00001);
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            String s = String.format("%06X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get3ByteUDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = 8388607-10024; i < 8388607 ; i++) {
            double v = 0.01*i;
            CanMessageData.set3ByteUDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get3ByteUDouble(message,0, 0.01), 0.0001);
        }
    }

    @Test
    public void test4ByteDouble() {
        Assert.assertEquals(-0.01,
                CanMessageData.get4ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(-0.02,
                CanMessageData.get4ByteDouble(
                        CanMessageData.asPackedByteArray("00FEFFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get4ByteDouble(
                        CanMessageData.asPackedByteArray("0001000000010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get4ByteDouble(
                        CanMessageData.asPackedByteArray("0010000000010F",0, -1),1, 0.01), 0.00001);
        for (int i = -2147483648; i < -2147483648+10024 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get4ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1,0.01), 0.00001);
        }
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get4ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01),0.00001);
        }
        byte[] message = new byte[9];
        for (int i = -2147483648; i < -2147483648+10024 ; i++) {
            double v = 0.01*i;
            CanMessageData.set4ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get4ByteDouble(message,0, 0.01), 0.0001);
        }
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            double v = 0.01*i;
            CanMessageData.set4ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get4ByteDouble(message,0, 0.01), 0.0001);
        }
    }
    @Test
    public void test4ByteUDouble() {
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get4ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFF010F",0, -1),1, 0.01), 0);
        Assert.assertEquals(0.01*(CanMessageData.n2kUInt32NA-1),
                CanMessageData.get4ByteUDouble(
                        CanMessageData.asPackedByteArray("00FEFFFFFF010F",0, -1),1, 0.01),0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get4ByteUDouble(
                        CanMessageData.asPackedByteArray("0001000000010F",0, -1),1,0.01),0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get4ByteUDouble(
                        CanMessageData.asPackedByteArray("0010000000010F",0, -1),1,0.01), 0.00001);
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%08X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get4ByteUDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (int i = 2147483647-10024; i < 2147483647 ; i++) {
            double v = 0.01*i;
            CanMessageData.set4ByteUDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get4ByteUDouble(message,0, 0.01), 0.0001);
        }
    }

    @Test
    public void test8ByteDouble() {
        Assert.assertEquals(-0.01,
                CanMessageData.get8ByteDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(-0.02,
                CanMessageData.get8ByteDouble(
                        CanMessageData.asPackedByteArray("00FEFFFFFFFFFFFFFF010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get8ByteDouble(
                        CanMessageData.asPackedByteArray("000100000000000000010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get8ByteDouble(
                        CanMessageData.asPackedByteArray("001000000000000000010F",0, -1),1, 0.01), 0.00001);
        for (long i = -2147483648; i < -2147483648+10024 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(0.01*i,
                    CanMessageData.get8ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(BigInteger.valueOf(i).doubleValue() * 0.01,
                    CanMessageData.get8ByteDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), 0.00001);
        }
        byte[] message = new byte[9];
        for (long i = -2147483648; i < -2147483648+10024 ; i++) {
            double v = 0.01*i;
            CanMessageData.set8ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get8ByteDouble(message,0, 0.01), 0.0001);
        }
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            double v = 0.01*i;
            CanMessageData.set8ByteDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get8ByteDouble(message,0, 0.01), v*0.0001);
        }
    }
    @Test
    public void test8ByteUDouble() {
        Assert.assertEquals(CanMessageData.n2kDoubleNA,
                CanMessageData.get8ByteUDouble(
                        CanMessageData.asPackedByteArray("00FFFFFFFFFFFFFFFF010F",0, -1),1, 0.01), 0);
        Assert.assertEquals(CanMessageData.n2kUInt64NA.subtract(BigInteger.ONE).doubleValue()*0.01,
                CanMessageData.get8ByteUDouble(
                        CanMessageData.asPackedByteArray("00FEFFFFFFFFFFFFFF010F",0, -1),1, 0.01), CanMessageData.n2kUInt64NA.doubleValue()*0.00001);
        Assert.assertEquals(0.01,
                CanMessageData.get8ByteUDouble(
                        CanMessageData.asPackedByteArray("000100000000000000010F",0, -1),1, 0.01), 0.00001);
        Assert.assertEquals(0.16,
                CanMessageData.get8ByteUDouble(
                        CanMessageData.asPackedByteArray("001000000000000000010F",0, -1),1, 0.01), 0.00001);
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            String s = String.format("%016X", i);
            s = s.substring(s.length()-2,s.length())
                    +s.substring(s.length()-4,s.length()-2)
                    +s.substring(s.length()-6,s.length()-4)
                    +s.substring(s.length()-8,s.length()-6)
                    +s.substring(s.length()-10,s.length()-8)
                    +s.substring(s.length()-12,s.length()-10)
                    +s.substring(s.length()-14,s.length()-12)
                    +s.substring(s.length()-16,s.length()-14);
            //log.info("{} {} ", i, s);
            Assert.assertEquals(BigInteger.valueOf(i).doubleValue()*0.01,
                    CanMessageData.get8ByteUDouble(
                            CanMessageData.asPackedByteArray("00"+s+"010F",0, -1),1, 0.01), BigInteger.valueOf(i).doubleValue()*0.000001);
        }
        byte[] message = new byte[9];
        for (long i = 2147483647-10024; i < 2147483647 ; i++) {
            double v = 0.01*i;
            CanMessageData.set8ByteUDouble(message,0,v, 0.01);
            Assert.assertEquals(v,
                    CanMessageData.get8ByteUDouble(message,0, 0.01), v*0.0001);
        }
    }
}
