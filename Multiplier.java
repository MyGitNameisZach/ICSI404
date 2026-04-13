public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {
        // clear result
        for (int i = 0; i < 32; i++) {
            result.setBitN(i, new Bit(false));
        }

        Word32 shifted = new Word32();
        Word32 temp = new Word32();

        for (int i = 31; i >= 0; i--) {

            Bit bit = new Bit(false);
            b.getBitN(i, bit);

            if (bit.getValue()) {

                int shiftAmount = 31 - i;

                Shifter.LeftShift(a, shiftAmount, shifted);

                Adder.add(result, shifted, temp);

                temp.copy(result);
            }
        }
    }
}
