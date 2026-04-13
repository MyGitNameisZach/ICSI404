import java.util.HashMap;

public class Assembler {

    // 5-bit opcode mapping
    static HashMap<String,String> opcodeMap = new HashMap<>();
    static {
        opcodeMap.put("halt","00000");
        opcodeMap.put("add","00001");
        opcodeMap.put("and","00010");
        opcodeMap.put("multiply","00011");
        opcodeMap.put("leftshift","00100");
        opcodeMap.put("subtract","00101");
        opcodeMap.put("or","00110");
        opcodeMap.put("rightshift","00111");

        opcodeMap.put("syscall","01000");
        opcodeMap.put("call","01001");
        opcodeMap.put("return","01010");
        opcodeMap.put("compare","01011");

        opcodeMap.put("ble","01100");
        opcodeMap.put("blt","01101");
        opcodeMap.put("bge","01110");
        opcodeMap.put("bgt","01111");
        opcodeMap.put("beq","10000");
        opcodeMap.put("bne","10001");

        opcodeMap.put("load","10010");
        opcodeMap.put("store","10011");
        opcodeMap.put("copy","10100");
    }

    // Format bit mapping: 0=2R format, 1=immediate (except special cases)
    static HashMap<String,Integer> formatMap = new HashMap<>();
    static {
        formatMap.put("halt",0);
        formatMap.put("return",0);
        formatMap.put("syscall",0); // special: format 0
        formatMap.put("call",1);
        // All other instructions default to 2R (format=0)
    }

    public static String[] assemble(String[] input) {
        String[] output = new String[input.length];

        for (int i = 0; i < input.length; i++) {
            String line = input[i].toLowerCase();
            String[] parts = line.split(" ");

            String opcode = opcodeMap.get(parts[0]);
            int formatBit = formatMap.getOrDefault(parts[0], 0);

            // No parameters (halt, return)
            if (parts.length == 1) {
                output[i] = opcode + formatBit + "00000" + "00000";
            }
            // One parameter (syscall, call, branches)
            else if (parts.length == 2) {
                int value = Integer.parseInt(parts[1]);

                // For branch instructions, format bit indicates sign (0 for positive, 1 for negative)
                int actualFormatBit;
                if (parts[0].equals("syscall") || parts[0].equals("call") || parts[0].equals("return")) {
                    actualFormatBit = formatMap.getOrDefault(parts[0], 0);
                } else {
                    // For branches, format bit = 0 for positive, 1 for negative
                    actualFormatBit = (value < 0) ? 1 : 0;
                }

                // encode signed 10-bit two's complement
                int value10 = value & 0x3FF;
                if (value < 0) {
                    value10 = (1 << 10) + value;
                }

                String high = String.format("%5s", Integer.toBinaryString((value10 >> 5) & 0b11111)).replace(' ', '0');
                String low  = String.format("%5s", Integer.toBinaryString(value10 & 0b11111)).replace(' ', '0');

                output[i] = opcode + actualFormatBit + high + low;
            }
            // Two parameters (2R or immediate)
            else if (parts.length == 3) {
                String second = parts[1];
                String third = parts[2];

                int actualFormatBit = second.startsWith("r") ? 0 : 1;

                if (actualFormatBit == 0) { // 2R format
                    output[i] = opcode + actualFormatBit + registerToBinary(second) + registerToBinary(third);
                } else { // immediate format
                    int imm = Integer.parseInt(second);
                    output[i] = opcode + actualFormatBit + toBinary(imm) + registerToBinary(third);
                }
            }
        }

        return output;
    }

    // Convert r0-r31 to 5-bit binary
    static String registerToBinary(String reg) {
        int num = Integer.parseInt(reg.substring(1));
        return toBinary(num);
    }

    // Convert int to 5-bit signed binary (two's complement)
    static String toBinary(int num) {
        num &= 0b11111; // keep last 5 bits
        String bin = Integer.toBinaryString(num);
        while (bin.length() < 5)
            bin = "0" + bin;
        return bin;
    }

    public static String[] finalOutput(String[] input) {
        // Always add a halt instruction at the end
        String[] withHalt = new String[input.length + 1];
        System.arraycopy(input, 0, withHalt, 0, input.length);
        withHalt[input.length] = "0000000000000000"; // halt instruction

        // Now make it even length
        if (withHalt.length % 2 != 0) {
            String[] temp = new String[withHalt.length + 1];
            System.arraycopy(withHalt, 0, temp, 0, withHalt.length);
            temp[withHalt.length] = "0000000000000000";
            withHalt = temp;
        }

        String[] result = new String[withHalt.length / 2];
        int index = 0;
        for (int i = 0; i < withHalt.length; i += 2) {
            result[index++] = withHalt[i] + withHalt[i + 1];
        }
        return result;
    }
}