public class Memory {
    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram = new Word32[1000];

    public Memory() {
        for(int i = 0; i < 1000; i++) {
            dram[i] = new Word32();
        }
    }

    public int addressAsInt() {

        int result = 0;
        Bit temp = new Bit(false);

        for (int i = 0; i < 32; i++) {

            address.getBitN(i, temp);

            if (temp.getValue()) {
                result += (1 << (31 - i));
            }
        }

        if (result < 0 || result > 999) {
            throw new RuntimeException("Address out of bounds");
        }

        return result;
    }

    public void read() {
        int addr = addressAsInt();
        dram[addr].copy(value);
    }

    public void write() {
        int addr = addressAsInt();
        value.copy(dram[addr]);
    }

    public void load(String[] data) {

        if (data.length > 1000) {
            throw new RuntimeException("Program too large");
        }

        for (int i = 0; i < data.length; i++) {

            if (data[i].length() != 32) {
                throw new RuntimeException("Invalid word length");
            }

            for (int j = 0; j < 32; j++) {

                char c = data[i].charAt(j);

                if (c == '1')
                    dram[i].setBitN(j, new Bit(true));
                else if (c == '0')
                    dram[i].setBitN(j, new Bit(false));
                else
                    throw new RuntimeException("Invalid character");
            }
        }
    }
}
