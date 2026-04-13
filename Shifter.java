public class Shifter {
    public static void LeftShift(Word32 source, int amount, Word32 result) {

        amount = amount & 31;

        for (int i = 0; i < 32; i++) {

            Bit bit = new Bit(false);

            int sourceIndex = i + amount;

            if (sourceIndex < 32) {
                source.getBitN(sourceIndex, bit);
            }

            result.setBitN(i, bit);
        }
    }

    public static void RightShift(Word32 source, int amount, Word32 result) {
        amount = amount & 31;

        for (int i = 31; i >= 0; i--) {

            Bit bit = new Bit(false);

            int sourceIndex = i - amount;

            if (sourceIndex >= 0) {
                source.getBitN(sourceIndex, bit);
            }

            result.setBitN(i, bit);
        }
    }
}
