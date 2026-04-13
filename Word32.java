public class Word32 {
    Bit[] bits;
    public Word32() {
        bits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(false);
        }
    }

    public Word32(Bit[] in) {
        bits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(in[i].getValue());
        }
    }

    public void getTopHalf(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.setBitN(i, bits[i]);
        }
    }

    public void getBottomHalf(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.setBitN(i, bits[i + 16]);
        }
    }

    public void copy(Word32 result) {
        for (int i = 0; i < 32; i++) {
            result.bits[i].assign(bits[i]);
        }
    }

    public boolean equals(Word32 other) {
        for (int i = 0; i < 32; i++) {
            if (bits[i].getValue() != other.bits[i].getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Word32 a, Word32 b) {
        return a.equals(b);
    }

    public void getBitN(int n, Bit result) {
        result.assign(bits[n]);
    }

    public void setBitN(int n, Bit source) {
        bits[n].assign(source);
    }

    public void and(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit temp = new Bit(false);
            bits[i].and(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void and(Word32 a, Word32 b, Word32 result) {
        a.and(b, result);
    }

    public void or(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit temp = new Bit(false);
            bits[i].or(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void or(Word32 a, Word32 b, Word32 result) {
        a.or(b, result);
    }

    public void xor(Word32 other, Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit temp = new Bit(false);
            bits[i].xor(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void xor(Word32 a, Word32 b, Word32 result) {
        a.xor(b, result);
    }

    public void not(Word32 result) {
        for (int i = 0; i < 32; i++) {
            Bit temp = new Bit(false);
            bits[i].not(temp);
            result.bits[i].assign(temp);
        }
    }

    public static void not(Word32 a, Word32 result) {
        a.not(result);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(bits[i].getValue() ? "1" : "0");
            if (i < 31) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}