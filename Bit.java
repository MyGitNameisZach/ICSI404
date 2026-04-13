public class Bit {

    boolean bitValue;
    public enum boolValues { FALSE, TRUE }

    public Bit(boolean value) {
        bitValue = value;
    }

    public boolean getValue() {
        return bitValue;
    }

    public void assign(Bit bool) {
        this.bitValue = bool.bitValue;
    }

    public void and(Bit b2, Bit result) {
        and(this, b2, result);
    }

    public static void and(Bit b1, Bit b2, Bit result) {
        if (b1.bitValue){
            if(b2.bitValue){
                result.bitValue = true;
            }
        }else{
            result.bitValue = false;
        }
    }

    public void or(Bit b2, Bit result) {
        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) {
        if (b1.bitValue) {
            result.bitValue = true;
        }else if (b2.bitValue){
            result.bitValue = true;
        }else{
            result.bitValue = false;
        }
    }

    public void xor(Bit b2, Bit result) {
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        if(b1.bitValue){
            if(!b2.bitValue){
                result.bitValue = true;
                return;
            }
        }
        if(!b1.bitValue){
            if(b2.bitValue){
                result.bitValue = true;
                return;
            }
        }
        result.bitValue = false;

    }

    public static void not(Bit b2, Bit result) {
        if(b2.bitValue){
            result.bitValue = false;
            return;
        }
        result.bitValue = true;
    }

    public void not(Bit result) {
        not(this, result);
    }

    public String toString() {
        if(bitValue){
            return "1";
        }
        return "0";
    }
}
