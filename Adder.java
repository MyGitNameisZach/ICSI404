public class Adder {
    public static void subtract(Word32 a, Word32 b, Word32 result) {
        Word32 notB = new Word32();
        Word32 one = new Word32();
        Word32 temp = new Word32();

        //notB=b
        b.not(notB);

        //put 1 in LSB index 31!
        Bit oneBit = new Bit(true);
        one.setBitN(31, oneBit);

        //temp = notB + 1
        add(notB, one, temp);

        //result = a + temp
        add(a, temp, result);

    }

    public static void add(Word32 a, Word32 b, Word32 result) {
        Bit carry = new Bit(false);

        for (int i = 31; i >= 0; i--) {

            Bit bitA = new Bit(false);
            Bit bitB = new Bit(false);

            a.getBitN(i, bitA);
            b.getBitN(i, bitB);

            Bit axorb = new Bit(false);
            bitA.xor(bitB, axorb);

            Bit sum = new Bit(false);
            axorb.xor(carry, sum);

            result.setBitN(i, sum);

            Bit aandb = new Bit(false);
            bitA.and(bitB, aandb);

            Bit carryAndAxorb = new Bit(false);
            carry.and(axorb, carryAndAxorb);

            Bit newCarry = new Bit(false);
            aandb.or(carryAndAxorb, newCarry);

            carry.assign(newCarry);
        }
    }
}
