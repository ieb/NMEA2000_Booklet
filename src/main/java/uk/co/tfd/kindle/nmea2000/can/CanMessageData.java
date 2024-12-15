package uk.co.tfd.kindle.nmea2000.can;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class CanMessageData {
    private static final Logger log = LoggerFactory.getLogger(CanMessageData.class);
    public static double n2kDoubleNA = -1000000000.0;
    public static BigInteger n2kInt64NA = new BigInteger("9223372036854775807");
    public static BigInteger n2kUInt64NA = new BigInteger("18446744073709551615");
    public static int n2kInt32NA = 2147483647;
    public static long n2kUInt32NA = 4294967295L;
    public static int n2kInt24NA = 8388607;
    public static int n2kUInt24NA = 16777215;
    public static int n2kInt16NA = 32767;
    public static int n2kUInt16NA = 65535;
    public static int n2kInt8NA = 127;
    public static int n2kUInt8NA = 255;
    private static final BigInteger max64BitValue = new BigInteger("9223372036854775807");


    public final static double scaleToKnots = 1.94384;
    public final static double scaleToDegrees = 57.2958; // 180/PI
    public final static double scaleSecondsToHours = 0.00027777777; // (1.0/3600.0);
    public final static double offsetCelcius = -273.15;
    public final static double scaleToNm = 0.0005399568; //1.0/1852.0


    public static boolean isNa(double ... va) {
        for(double v : va) {
            if ( v == CanMessageData.n2kDoubleNA) {
                return true;
            }
        }
        return false;
    }

    public static byte[] asByteArray(String[] parts, int startAt) {
        byte[] a = new byte[parts.length-startAt];
        for ( int i = 0; i < a.length; i++) {
            a[i] = (byte)Integer.parseInt(parts[i+startAt], 16);
        }
        return a;
    }
    public static byte[] asPackedByteArray(String packed, int startAt, int endAt) {
        if ( endAt == -1) {
            endAt = packed.length();
        }
        byte[] a = new byte[(packed.length()-startAt)/2];
        for (int i = 0; i <a.length ; i++) {
            a[i] = (byte)Integer.parseInt(packed.substring(startAt+i*2,startAt+i*2+2), 16);
        }
        return a;
    }

    public static double get8ByteUDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 8) {
            return n2kDoubleNA;
        }
        BigInteger v = get8ByteUInt(message, byteOffset);
        if ( v == n2kUInt64NA) {
            return n2kDoubleNA;
        }
        return (float)(factor * v.doubleValue());
    }

    public static void set8ByteUDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 8) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set8ByteUInt(message, byteOffset, n2kUInt64NA);
        } else {
            set8ByteUInt(message, byteOffset, BigDecimal.valueOf(value / factor).round(MathContext.DECIMAL64).toBigInteger());
        }
    }

    public static double get8ByteDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 8) {
            return n2kDoubleNA;
        }
        BigInteger v = get8ByteInt(message, byteOffset);
        if ( v == n2kInt64NA) {
            return n2kDoubleNA;
        }
        return  (factor * v.doubleValue());
    }

    public static void set8ByteDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 8) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set8ByteInt(message, byteOffset, n2kInt64NA);
        } else {
            set8ByteInt(message, byteOffset, BigDecimal.valueOf(value / factor).round(MathContext.DECIMAL64).toBigInteger());
        }
    }


    public static BigInteger get8ByteUInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 8) {
            return n2kUInt64NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0xff
                && (message[byteOffset + 3] & 0xff) == 0xff
                && (message[byteOffset + 4] & 0xff) == 0xff
                && (message[byteOffset + 5] & 0xff) == 0xff
                && (message[byteOffset + 6] & 0xff) == 0xff
                && (message[byteOffset + 7] & 0xff) == 0xff) {
            return n2kUInt64NA;
        }

        // this is necessary as the BigInteger constructor assumes signed ints.
        BigInteger v = BigInteger.valueOf(message[byteOffset + 7] & 0xff );
        for (int i = 6; i >= 0; i--) {
            v = v.shiftLeft(8).or(BigInteger.valueOf(message[byteOffset + i] & 0xff));
        }
        return v;
    }

    public static void set8ByteUInt(byte[] message, int byteOffset, BigInteger value) {
        if (message.length < byteOffset + 8) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kUInt64NA) {
            message[byteOffset] = (byte)0xff;
            message[byteOffset+1] = (byte)0xff;
            message[byteOffset+2] = (byte)0xff;
            message[byteOffset+3] = (byte)0xff;
            message[byteOffset+4] = (byte)0xff;
            message[byteOffset+5] = (byte)0xff;
            message[byteOffset+6] = (byte)0xff;
            message[byteOffset+7] = (byte)0xff;
        } else {
            for(int i = 0; i < 8; i++) {
                message[byteOffset+i] = value.byteValue();
                value = value.shiftRight(8);
            }
        }
    }

    public static BigInteger get8ByteInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 8) {
            return n2kInt64NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0xff
                && (message[byteOffset + 3] & 0xff) == 0xff
                && (message[byteOffset + 4] & 0xff) == 0xff
                && (message[byteOffset + 5] & 0xff) == 0xff
                && (message[byteOffset + 6] & 0xff) == 0xff
                && (message[byteOffset + 7] & 0xff) == 0x7f) {
            return n2kInt64NA;
        }
        BigInteger v = BigInteger.valueOf(message[byteOffset + 7] & 0x7f );
        for (int i = 6; i >= 0; i--) {
            v = v.shiftLeft(8).or(BigInteger.valueOf(message[byteOffset + i] & 0xff));
        }
        if ((message[byteOffset + 7] & 0x80) == 0x80) {
            v = v.subtract(max64BitValue).subtract(BigInteger.ONE);
        }
        return v;
    }

    public static void set8ByteInt(byte[] message, int byteOffset, BigInteger value) {
        if (message.length < byteOffset + 8) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kInt64NA) {
            message[byteOffset] = (byte)0xff;
            message[byteOffset+1] = (byte)0xff;
            message[byteOffset+2] = (byte)0xff;
            message[byteOffset+3] = (byte)0xff;
            message[byteOffset+4] = (byte)0xff;
            message[byteOffset+5] = (byte)0xff;
            message[byteOffset+6] = (byte)0xff;
            message[byteOffset+7] = (byte)0x7f;
        } else {
            boolean negative = (value.compareTo(BigInteger.ZERO) < 0);
            for(int i = 0; i < 7; i++) {
                message[byteOffset+i] = value.byteValue();
                value = value.shiftRight(8);
            }
            message[byteOffset+7] = (byte)(value.byteValue() & 0x7f);
            if ( negative ) {
                message[byteOffset+7] = (byte)(message[byteOffset+7] | 0x80);
            }
        }
    }


    public static double get4ByteUDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 4) {
            return n2kDoubleNA;
        }
        long v = get4ByteUInt(message, byteOffset);
        if ( v == n2kUInt32NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }

    public static void set4ByteUDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set4ByteUInt(message, byteOffset, n2kUInt32NA);
        } else {
            set4ByteUInt(message, byteOffset, (Math.round(value/factor)));
        }
    }
    public static double get4ByteDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 4) {
            return n2kDoubleNA;
        }
        long v = get4ByteInt(message, byteOffset);
        if ( v == n2kInt32NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }
    public static void set4ByteDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set4ByteInt(message, byteOffset, n2kInt32NA);
        } else {
            set4ByteInt(message, byteOffset, (Math.round(value/factor)));
        }
    }

    public static long get4ByteUInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 4) {
            return n2kUInt32NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0xff
                && (message[byteOffset + 3] & 0xff) == 0xff) {
            return n2kUInt32NA;
        }
        int v = message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0xff ) << 8
                | (message[byteOffset + 2] & 0xff ) << 16;
        // have to use a long here since ints are 32 bit signed and cant hold a
        // uint32. This makes the conversion 4x slower compared to using an int.
        // however, still relatively fast.
        return (long)v | (long)(message[byteOffset + 3] & 0xff ) << 24;
    }
    public static void set4ByteUInt(byte[] message, int byteOffset, long value) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kUInt32NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0xff;
            message[byteOffset + 2] = (byte) 0xff;
            message[byteOffset + 3] = (byte) 0xff;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0xff);
            message[byteOffset + 2] = (byte) ((value >> 16) & 0xff);
            message[byteOffset + 3] = (byte) ((value >> 24) & 0xff);
        }
    }


    public static int get4ByteInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 4) {
            return n2kInt32NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0xff
                && (message[byteOffset + 3] & 0xff) == 0x7f) {
            return n2kInt32NA;
        }
        int v = message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0xff ) << 8
                | (message[byteOffset + 2] & 0xff ) << 16
                | (message[byteOffset + 3] & 0x7f ) << 24;
        if ((message[byteOffset + 3] & 0x80) == 0x80) {
            v = v-2147483647-1;
        }
        return v;
    }

    public static void set4ByteInt(byte[] message, int byteOffset, long value) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kInt32NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0xff;
            message[byteOffset + 2] = (byte) 0xff;
            message[byteOffset + 3] = (byte) 0x7f;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0xff);
            message[byteOffset + 2] = (byte) ((value >> 16) & 0xff);
            message[byteOffset + 3] = (byte) ((value >> 24) & 0x7f);
            if ( value < 0) {
                message[byteOffset + 3] = (byte) (message[byteOffset + 3] | 0x80);
            }
        }
    }

    public static double get3ByteUDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 3) {
            return n2kDoubleNA;
        }

        int v = get3ByteUInt(message, byteOffset);
        if ( v == n2kUInt24NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }
    public static void set3ByteUDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set3ByteUInt(message, byteOffset, n2kUInt24NA);
        } else {
            set3ByteUInt(message, byteOffset,(int)(Math.round(value/factor)));
        }
    }

    public static double get3ByteDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 2) {
            return n2kDoubleNA;
        }
        int v = get3ByteInt(message, byteOffset);
        if ( v == n2kInt24NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }
    public static void set3ByteDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set3ByteInt(message, byteOffset, n2kInt24NA);
        } else {
            set3ByteInt(message, byteOffset,  (int)(Math.round(value/factor)));
        }
    }

    public static int get3ByteUInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 3) {
            return n2kUInt24NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0xff) {
            return n2kUInt24NA;
        }
        return message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0xff) << 8
                | (message[byteOffset + 2] & 0xff) << 16;
    }

    public static void set3ByteUInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kUInt24NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0xff;
            message[byteOffset + 2] = (byte) 0xff;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0xff);
            message[byteOffset + 2] = (byte) ((value >> 16) & 0xff);
        }
    }


    public static int get3ByteInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 3) {
            return n2kInt24NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff
                && (message[byteOffset + 2] & 0xff) == 0x7f) {
            return n2kInt24NA;
        }
        int v = message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0xff ) << 8
                | (message[byteOffset + 2] & 0x7f ) << 16;
        if ((message[byteOffset + 2] & 0x80) == 0x80) {
            v = v-8388608;
        }
        return v;
    }

    public static void set3ByteInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kInt24NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0xff;
            message[byteOffset + 2] = (byte) 0x7f;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0xff);
            message[byteOffset + 2] = (byte) ((value >> 16) & 0x7f);
            if ( value < 0) {
                message[byteOffset + 2] = (byte) (message[byteOffset + 2] | 0x80);
            }
        }
    }


    public static double get2ByteUDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 2) {
            return n2kDoubleNA;
        }
        int v = get2ByteUInt(message, byteOffset);
        if ( v == n2kUInt16NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }
    public static void set2ByteUDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set2ByteUInt(message, byteOffset, n2kUInt16NA);
        } else {
            set2ByteUInt(message, byteOffset,  (int)(Math.round(value/factor)));
        }
    }

    public static double get2ByteDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 2) {
            return n2kDoubleNA;
        }
        int v = get2ByteInt(message, byteOffset);
        if ( v == n2kInt16NA) {
            return n2kDoubleNA;
        }
        return factor * v;
    }
    public static void set2ByteDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set2ByteInt(message, byteOffset, n2kInt16NA);
        } else {
            set2ByteInt(message, byteOffset, (int)(Math.round(value/factor)));
        }
    }

    public static int get2ByteUInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 2) {
            return n2kUInt16NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0xff) {
            return n2kUInt16NA;
        }
        return message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0xff) << 8;
    }

    public static void set2ByteUInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 2) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kUInt16NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0xff;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0xff);
        }
    }


    public static int get2ByteInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 2) {
            return n2kInt16NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff
                && (message[byteOffset + 1] & 0xff) == 0x7f) {
            return n2kInt16NA;
        }
        int v = message[byteOffset] & 0xff
                | (message[byteOffset + 1] & 0x7f) << 8;
        if ((message[byteOffset + 1] & 0x80) == 0x80) {
            v = v-32768;
        }
        return v;
    }

    public static void set2ByteInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 4) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kInt16NA) {
            message[byteOffset] = (byte) 0xff;
            message[byteOffset + 1] = (byte) 0x7f;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
            message[byteOffset + 1] = (byte) ((value >> 8) & 0x7f);
            if ( value < 0) {
                message[byteOffset + 1] = (byte) (message[byteOffset + 1] | 0x80);
            }
        }
    }


    public static double get1ByteUDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 1) {
            return n2kDoubleNA;
        }
        if ((message[byteOffset] & 0xff) == 0xff) {
            return n2kDoubleNA;
        }
        return factor * get1ByteUInt(message, byteOffset);
    }
    public static void set1ByteUDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 3) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set1ByteUInt(message, byteOffset, n2kUInt8NA);
        } else {
            set1ByteUInt(message, byteOffset,  (int)(Math.round(value/factor)));
        }
    }

    public static double get1ByteDouble(byte[] message, int byteOffset, double factor) {
        if (message.length < byteOffset + 1) {
            return n2kDoubleNA;
        }
        if ((message[byteOffset] & 0xff) == 0x7f ) {
            return n2kDoubleNA;
        }
        return factor * get1ByteInt(message, byteOffset);
    }
    public static void set1ByteDouble(byte[] message, int byteOffset, double value, double factor) {
        if (message.length < byteOffset + 1) {
            throw new IllegalArgumentException("message overflow");
        }
        if ( value == n2kDoubleNA) {
            set1ByteInt(message, byteOffset, n2kInt8NA);
        } else {
            set1ByteInt(message, byteOffset,  (int)(Math.round(value/factor)));
        }
    }


    public static int get1ByteUInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 1) {
            return n2kUInt8NA;
        }
        if ((message[byteOffset] & 0xff) == 0xff ) {
            return n2kUInt8NA;
        }
        return message[byteOffset] & 0xff;
    }

    public static void set1ByteUInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 1) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kUInt8NA) {
            message[byteOffset] = (byte) 0xff;
        } else {
            message[byteOffset] = (byte) (value & 0xff);
        }
    }

    public static int get1ByteInt(byte[] message, int byteOffset) {
        if (message.length < byteOffset + 1) {
            return n2kInt8NA;
        }
        if ((message[byteOffset] & 0xff) == 0x7f ) {
            return n2kInt8NA;
        }
        int v = message[byteOffset] & 0x7f;
        if ((message[byteOffset] & 0x80) == 0x80) {
            v = v-128;
        }
        return v;
    }
    public static void set1ByteInt(byte[] message, int byteOffset, int value) {
        if (message.length < byteOffset + 1) {
            throw new IllegalArgumentException("message overflow");
        }
        if (value == n2kInt8NA) {
            message[byteOffset] = (byte) 0x7f;
        } else {
            message[byteOffset] = (byte) (value & 0x7f);
            if ( value < 0) {
                message[byteOffset] = (byte) (message[byteOffset] | 0x80);
            }
        }
    }


    public static String dumpMessage(byte[] message) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            output.append(Integer.toHexString(message[i]&0xff));
            output.append(" ");
        }
        return output.toString();
    }

}
