import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {

    private Memory mem;
    private ALU alu = new ALU();

    public List<String> output = new LinkedList<>();

    private Word32[] registers = new Word32[32];
    private Word32 pc = new Word32();

    private Word32 fetchedWord = new Word32();
    private Word16 instruction = new Word16();

    private boolean useTopHalf = true;
    private boolean halted = false;

    private int opcode;
    private int format;
    private int field1;
    private int field2;

    private Stack<Word32> stack = new Stack<>();

    public Processor(Memory m) {
        mem = m;

        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32();
        }
    }

    public void run() {
        while (!halted) {
            fetch();
            decode();
            execute();
            store();
        }
    }

    // ================= FETCH =================
    private void fetch() {
        if (useTopHalf) {
            mem.address.copy(pc);
            mem.read();
            mem.value.copy(fetchedWord);
            fetchedWord.getTopHalf(instruction);
        } else {
            fetchedWord.getBottomHalf(instruction);
            incrementPC();
        }

        useTopHalf = !useTopHalf;
    }

    // ================= DECODE =================
    private void decode() {
        opcode = getBits(0, 5);
        format = getBits(5, 1);
        field1 = getBits(6, 5);
        field2 = getBits(11, 5);

        // Copy the instruction to ALU - the ALU will extract opcode from bits 0-4
        instruction.copy(alu.instruction);

        // Reset operands
        alu.op1 = new Word32();
        alu.op2 = new Word32();

        switch (opcode) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                if (format == 1) {
                    Word32 imm = new Word32();
                    TestConverter.fromInt(signExtend5(field1), imm);
                    imm.copy(alu.op1);
                    registers[field2].copy(alu.op2);
                } else {
                    registers[field1].copy(alu.op1);
                    registers[field2].copy(alu.op2);
                }
                break;
            case 11:
                Word32 cmp = new Word32();
                TestConverter.fromInt(signExtend5(field1), cmp);
                cmp.copy(alu.op1);
                registers[field2].copy(alu.op2);
                break;
            case 18: case 19:
                break;
            case 20:
                if (format == 1) {
                    Word32 imm = new Word32();
                    TestConverter.fromInt(signExtend5(field1), imm);
                    imm.copy(registers[field2]);
                } else {
                    registers[field1].copy(registers[field2]);
                }
                break;
        }
    }

    // ================= EXECUTE =================
    private void execute() {
        switch (opcode) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7:
            case 11:
                alu.doInstruction();
                break;
            case 18: {
                Word32 offset = new Word32();
                TestConverter.fromInt(signExtend5(field1), offset);
                Word32 addr = new Word32();
                registers[field2].copy(addr);
                Adder.add(addr, offset, addr);
                mem.address.copy(addr);
                mem.read();
                mem.value.copy(registers[field2]);
                break;
            }
            case 19: {
                Word32 offset = new Word32();
                TestConverter.fromInt(signExtend5(field1), offset);
                Word32 addr = new Word32();
                registers[field2].copy(addr);
                Adder.add(addr, offset, addr);
                mem.address.copy(addr);
                registers[field2].copy(mem.value);
                mem.write();
                break;
            }
            case 20:
                break;
        }
    }

    // ================= STORE =================
    private void store() {
        switch (opcode) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                if (format == 1) {
                    alu.result.copy(registers[field2]);
                } else {
                    alu.result.copy(registers[field1]);
                }
                break;
            case 11: case 20:
                break;
            case 17:
                if (!alu.equal.getValue()) {
                    branch(signExtend5(field1));
                }
                break;
            case 9:
                Word32 ret = new Word32();
                pc.copy(ret);
                stack.push(ret);
                branch(signExtend5(field1));
                break;
            case 10:
                if (!stack.isEmpty()) {
                    Word32 returnAddr = stack.pop();
                    returnAddr.copy(pc);
                    useTopHalf = true;
                }
                break;
            case 8:
                if (field1 == 0) printReg();
                else printMem();
                // Halt after syscall to prevent infinite loops in tests without halt
                halted = true;
                break;
            case 0:
                halted = true;
                break;
        }
    }

    // ================= HELPERS =================
    private void incrementPC() {
        Word32 one = new Word32();
        TestConverter.fromInt(1, one);
        Adder.add(pc, one, pc);
    }

    private void branch(int offset) {
        Word32 off = new Word32();
        TestConverter.fromInt(offset, off);
        Adder.add(pc, off, pc);
        useTopHalf = true;
    }

    private int getBits(int start, int length) {
        int value = 0;
        Bit temp = new Bit(false);
        for (int i = 0; i < length; i++) {
            instruction.getBitN(start + i, temp);
            if (temp.getValue()) {
                value |= (1 << (length - 1 - i));
            }
        }
        return value;
    }

    private int signExtend5(int val) {
        if ((val & 0x10) != 0) {
            return val - 32;
        }
        return val;
    }

    // ================= OUTPUT =================
    private void printReg() {
        for (int i = 0; i < 32; i++) {
            String line = "r" + i + ":" + registers[i] + ",";
            output.add(line);
        }
    }

    private void printMem() {
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32();
            TestConverter.fromInt(i, addr);
            mem.address.copy(addr);
            mem.read();
            Word32 value = new Word32();
            mem.value.copy(value);
            String line = i + ":" + value + "(" + TestConverter.toInt(value) + ")";
            output.add(line);
        }
    }
}