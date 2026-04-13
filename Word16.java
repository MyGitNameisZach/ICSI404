public class Word16 {
    Bit[] bits;
    public Word16() {
        bits = new Bit[16];
        for(int i =0; i < 16; i++){
            bits[i] = new Bit(false);
        }
    }

    public Word16(Bit[] in) {
        bits = new Bit[16];
        for(int i = 0; i <16; i++){
            bits[i] = new Bit(in[i].getValue());
        }
    }

    public void copy(Word16 result) {
        for (int i = 0; i < 16; i++) {
            result.bits[i].assign(this.bits[i]);
        }
    }

    public void setBitN(int n, Bit source) {
        bits[n].assign(source);
    }

    public void getBitN(int n, Bit result) {
        result.assign(bits[n]);
    }

    public boolean equals(Word16 other) {
        for (int i = 0; i < 16; i++) {
            if (this.bits[i].getValue() != other.bits[i].getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Word16 a, Word16 b) {
        return a.equals(b);
    }

    public void and(Word16 other, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit temp = new Bit(false);
            this.bits[i].and(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        a.and(b, result);
    }

    public void or(Word16 other, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit temp = new Bit(false);
            this.bits[i].or(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        a.or(b, result);
    }

    public void xor(Word16 other, Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit temp = new Bit(false);
            this.bits[i].xor(other.bits[i], temp);
            result.bits[i].assign(temp);
        }
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        a.xor(b, result);
    }

    public void not(Word16 result) {
        for (int i = 0; i < 16; i++) {
            Bit temp = new Bit(false);
            this.bits[i].not(temp);
            result.bits[i].assign(temp);
        }
    }

    public static void not(Word16 a, Word16 result) {
        a.not(result);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(bits[i].getValue() ? "1" : "0");
            if (i < 15) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}