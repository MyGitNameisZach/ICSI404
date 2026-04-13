public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    public void doInstruction() {

        less.assign(new Bit(false));
        equal.assign(new Bit(false));

        Bit temp = new Bit(false);
        int opcode = 0;

        // read first 5 bits of instruction
        for (int i = 0; i < 5; i++) {
            instruction.getBitN(i,temp);
            if(temp.getValue()){
                opcode += (1 << (4-i));
            }
        }

        Word32 tempResult = new Word32();

        switch(opcode){

            case 1: // ADD
                Adder.add(op1,op2,tempResult);
                tempResult.copy(result);
                break;

            case 2: // AND
                op1.and(op2,tempResult);
                tempResult.copy(result);
                break;

            case 3: // MULTIPLY
                Multiplier.multiply(op1,op2,tempResult);
                tempResult.copy(result);
                break;

            case 4: // LEFT SHIFT
                Shifter.LeftShift(op1, TestConverter.toInt(op2), tempResult);
                tempResult.copy(result);
                break;

            case 5: // SUBTRACT
                Adder.subtract(op1,op2,tempResult);
                tempResult.copy(result);
                break;

            case 6: // OR
                op1.or(op2,tempResult);
                tempResult.copy(result);
                break;

            case 7: // RIGHT SHIFT
                Shifter.RightShift(op1, TestConverter.toInt(op2), tempResult);
                tempResult.copy(result);
                break;

            case 11: // COMPARE

                Word32 diff = new Word32();
                Adder.subtract(op1,op2,diff);

                boolean zero = true;

                for(int i=0;i<32;i++){
                    diff.getBitN(i,temp);
                    if(temp.getValue()){
                        zero = false;
                        break;
                    }
                }

                if(zero){
                    equal.assign(new Bit(true));
                }

                diff.getBitN(0,temp); // sign bit

                if(!zero && temp.getValue()){
                    less.assign(new Bit(true));
                }

                break;

            default:
                throw new RuntimeException("Invalid opcode");
        }
    }
}
