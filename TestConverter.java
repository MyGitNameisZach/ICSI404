public class TestConverter {
    public static void fromInt(int value, Word32 result) {
       Bit t= new Bit(true);
       Bit f= new Bit(false);
       if (value >=0 ) {
           for (int i = 31; i > 0; i--) {
               result.setBitN(i,value % 2 == 0 ? f : t );
               value /=2;
           }
           result.setBitN(0, f);
       }
       else {
          value *= -1;
          fromInt(value, result);
          result.not(result);
          Word32 one = new Word32();
          fromInt(1,one);
          Adder.add(result,one,result);
       }
    }

    public static int toInt(Word32 value) {
        var isNeg = new Bit(false);
        value.getBitN(0,isNeg);
        Word32 positive = new Word32();
        if (isNeg.getValue()) {
            value.not(positive);
            Word32 one = new Word32();
            fromInt(1,one);
            Adder.add(positive,one,positive);
        }
        else {
            value.and(value,positive); // copy
        }
        int val = 1, retVal=0;
        Bit cur = new Bit(true);
        for (int i = 31; i > 0; i--) {
            positive.getBitN(i,cur);
            if (cur.getValue())
                retVal+=val;
            val *=2;
        }
        return retVal * (isNeg.getValue()?-1:1);
    }
}