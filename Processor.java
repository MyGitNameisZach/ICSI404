import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {

    private Memory mem;
    private ALU alu = new ALU();
    public List<String> output = new LinkedList<>();

    private Word32[] registers = new Word32[32];
    private Word32 pc = new Word32();          // word address (two 16‑bit instructions per word)

    private Word32 fetchedWord = new Word32();
    private Word16 instruction = new Word16();

    private boolean useTopHalf = true;
    private boolean halted = false;

    private int opcode, format, field1, field2;
    private Stack<Word32> stack = new Stack<>();

    public Processor(Memory m) {
        mem = m;
        for (int i = 0; i < 32; i++) registers[i] = new Word32();
    }

    public void run() {
        int ic = 0;
        while (!halted && ic++ < 5000) {
            fetch();
            decode();
            execute();
            store();
        }
    }


    private void fetch() {
        pc.copy(mem.address);
        mem.read();
        mem.value.copy(fetchedWord);

        if (useTopHalf) {
            fetchedWord.getTopHalf(instruction);
        } else {
            fetchedWord.getBottomHalf(instruction);
            incrementPC();                  // advance to next word after bottom half
        }
        useTopHalf = !useTopHalf;
    }

    private void incrementPC() {
        Word32 one = new Word32();
        TestConverter.fromInt(1, one);
        Adder.add(pc, one, pc);
    }


    private void decode() {
        opcode = getBits(0, 5);
        format = getBits(5, 1);
        field1 = getBits(6, 5);
        field2 = getBits(11, 5);

        instruction.copy(alu.instruction);
        alu.op1 = new Word32();
        alu.op2 = new Word32();

        switch (opcode) {
            case 1: case 2: case 3: case 4: case 6: case 7:
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
            case 5: // SUBTRACT – swap for immediate
                if (format == 1) {
                    Word32 imm = new Word32();
                    TestConverter.fromInt(signExtend5(field1), imm);
                    registers[field2].copy(alu.op1);
                    imm.copy(alu.op2);
                } else {
                    registers[field1].copy(alu.op1);
                    registers[field2].copy(alu.op2);
                }
                break;
            case 18: case 19: break;
            case 20:
                if (format == 1) {
                    Word32 imm = new Word32();
                    TestConverter.fromInt(signExtend5(field1), imm);
                    imm.copy(registers[field2]);
                } else {
                    registers[field1].copy(registers[field2]);
                }
                break;
            case 8: // SYSCALL
                field1 = getBits(6, 5); // Ensure field1 is decoded so store() knows what to do
                field2 = getBits(11, 5);
                break;
        }
    }


    private void execute() {
        switch (opcode) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 11:
                alu.doInstruction();
                break;
            case 18: {
                Word32 off = new Word32(); TestConverter.fromInt(signExtend5(field1), off);
                Word32 addr = new Word32(); registers[field2].copy(addr); Adder.add(addr, off, addr);
                mem.address.copy(addr); mem.read(); mem.value.copy(registers[field2]);
                break;
            }
            case 19: { // store
                Word32 off = new Word32();
                TestConverter.fromInt(signExtend5(field1), off);
                Word32 addr = new Word32();
                registers[field2].copy(addr);
                Adder.add(addr, off, addr);
                mem.address.copy(addr);
                if (format == 1) {
                    // store immediate value (sign‑extended field1) to memory
                    Word32 imm = new Word32();
                    TestConverter.fromInt(signExtend5(field1), imm);
                    imm.copy(mem.value);
                } else {
                    // store register
                    registers[field1].copy(mem.value);
                }
                mem.write();
                break;
            }
            case 20: break;
        }
    }


    private void store() {
        switch (opcode) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                if (format == 1) alu.result.copy(registers[field2]);
                else alu.result.copy(registers[field2]);
                break;
            case 11: // compare
                // compare only updates flags in ALU
                // nothing stored to registers
                break;

            case 20:
                break;

            case 17: // bne
                if (!alu.equal.getValue()) {
                    int offset = signExtend5(field1);
                    branchRelative(offset);
                }
                break;

            case 9: // call
                int callOffset = signExtend5(field2);
                Word32 ret = new Word32();
                pc.copy(ret);
                stack.push(ret);

                branchRelative(callOffset);
                break;

            case 10: // return
                if (!stack.isEmpty()) {
                    Word32 raddr = stack.pop();
                    raddr.copy(pc);
                    useTopHalf = true;
                }
                break;

            case 8: // syscall
                if (field1 == 0) printReg(); else printMem();
                break;

            case 0: halted = true; break;

        }
    }

    private void branchRelative(int wordOffset) {
        // The branch instruction is at the current word if useTopHalf == false,
        // or at the previous word if useTopHalf == true (since pc wasn't incremented yet).
        Word32 branchAddr = new Word32();
        pc.copy(branchAddr);
        if (useTopHalf) {
            // we haven't incremented pc after fetching top half, so branch is at (pc - 1)
            Word32 minusOne = new Word32();
            TestConverter.fromInt(-1, minusOne);
            Adder.add(branchAddr, minusOne, branchAddr);
        }
        Word32 offsetWord = new Word32();
        TestConverter.fromInt(wordOffset, offsetWord);
        Adder.add(branchAddr, offsetWord, pc);
        useTopHalf = true;
    }

    private int getBits(int start, int len) {
        int v = 0; Bit t = new Bit(false);
        for (int i = 0; i < len; i++) {
            instruction.getBitN(start + i, t);
            if (t.getValue()) v |= (1 << (len - 1 - i));
        }
        return v;
    }

    private int signExtend5(int val) {
        return (val & 0x10) != 0 ? val - 32 : val;
    }

    private void printReg() {
        for (int i = 0; i < 32; i++)
            output.add("r" + i + ":" + registers[i] + ",");
    }

    private void printMem() {
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32(); TestConverter.fromInt(i, addr);
            mem.address.copy(addr); mem.read();
            Word32 val = new Word32(); mem.value.copy(val);
            output.add(i + ":" + val + "(" + TestConverter.toInt(val) + ")");
        }
    }
}