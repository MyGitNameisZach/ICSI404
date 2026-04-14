import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Assembler {

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

    static HashMap<String,Integer> formatMap = new HashMap<>();
    static {
        formatMap.put("halt",0);
        formatMap.put("return",0);
        formatMap.put("syscall",0);
        formatMap.put("call",1);
    }

    // Set of opcodes that take a single immediate argument (branches, syscall)
    private static final Set<String> SINGLE_IMM_OPS = Set.of(
            "ble", "blt", "bge", "bgt", "beq", "bne", "syscall", "call"
    );

    public static String[] assemble(String[] input) {
        String[] output = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            String line = input[i].toLowerCase();
            String[] parts = line.split(" ");
            String opcode = opcodeMap.get(parts[0]);
            int formatBit = formatMap.getOrDefault(parts[0], 0);

            if (parts.length == 1) {
                // No arguments (halt, return)
                output[i] = opcode + formatBit + "00000" + "00000";
            }
            else if (parts.length == 2) {
                // Single argument: immediate or register?
                String arg = parts[1];
                if (SINGLE_IMM_OPS.contains(parts[0])) {
                    // For branches, syscall, call: immediate goes into a single 5‑bit field
                    int imm = Integer.parseInt(arg);
                    // Sign‑extend to 5 bits (for branches) – mask to 5 bits
                    int field1 = imm & 0b11111;
                    int field2 = 0;
                    // For 'call', the processor uses field2, not field1
                    if (parts[0].equals("call")) {
                        field2 = field1;
                        field1 = 0;
                    }
                    output[i] = opcode + formatBit + toBinary(field1) + toBinary(field2);
                }
                else {
                    // Original 10‑bit immediate handling (e.g., "add 10 r0" is 3‑argument, not here)
                    // This case is actually unused for correct two‑argument instructions,
                    // but kept for backward compatibility.
                    int value = Integer.parseInt(arg);
                    int actualFormatBit = (value < 0) ? 1 : 0;
                    int value10 = value & 0x3FF;
                    if (value < 0) value10 = (1 << 10) + value;
                    String high = String.format("%5s", Integer.toBinaryString((value10 >> 5) & 0b11111)).replace(' ', '0');
                    String low  = String.format("%5s", Integer.toBinaryString(value10 & 0b11111)).replace(' ', '0');
                    output[i] = opcode + actualFormatBit + high + low;
                }
            }
            else if (parts.length == 3) {
                // Two arguments (register, register) or (immediate, register)
                String second = parts[1], third = parts[2];
                int actualFormatBit = second.startsWith("r") ? 0 : 1;
                if (actualFormatBit == 0) {
                    output[i] = opcode + actualFormatBit + registerToBinary(second) + registerToBinary(third);
                } else {
                    int imm = Integer.parseInt(second);
                    output[i] = opcode + actualFormatBit + toBinary(imm) + registerToBinary(third);
                }
            }
        }
        return output;
    }

    static String registerToBinary(String reg) {
        return toBinary(Integer.parseInt(reg.substring(1)));
    }

    static String toBinary(int num) {
        num &= 0b11111;
        String bin = Integer.toBinaryString(num);
        while (bin.length() < 5) bin = "0" + bin;
        return bin;
    }

    // Two instructions per 32‑bit word (pad to even length)
    public static String[] finalOutput(String[] input) {
        List<String> list = new ArrayList<>();
        for (String s : input) list.add(s);
        while (list.size() % 2 != 0) list.add("0000000000000000");
        String[] result = new String[list.size() / 2];
        for (int i = 0, j = 0; i < list.size(); i += 2, j++) {
            result[j] = list.get(i) + list.get(i+1);
        }
        return result;
    }
}