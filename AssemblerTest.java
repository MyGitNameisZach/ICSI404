import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssemblerTest {

    private final String[][] instructions = {
            {"add r1 r2","0000100000100010"},
            {"syscall 100","0100000001100100"},
            {"return","0101000000000000"} ,
            {"subtract 10 r4","0010110101000100"} ,
            {"halt","0000000000000000"} ,
            {"and r7 r13","0001000011101101"},
            {"and 7 r13","0001010011101101"},
            {"multiply r31 r0","0001101111100000"},
            {"leftshift 2 r6","0010010001000110"},
            {"or -6 r9","0011011101001001"},
            {"rightshift 0 r2","0011110000000010"},
            {"call 2047","0100111111111111"},
            {"compare r18 r29","0101101001011101"},
            {"ble -100","0110011110011100"},
            {"blt 100","0110100001100100"},
            {"bge -100","0111011110011100"},
            {"bgt 100","0111100001100100"},
            {"beq -100","1000011110011100"},
            {"bne 100","1000100001100100"},
            {"load r31 r0", "1001001111100000"},
            {"load 2 r6",   "1001010001000110"},
            {"store r31 r0","1001101111100000"},
            {"store 2 r6",  "1001110001000110"},
            {"copy r31 r0","1010001111100000"},
            {"copy 2 r6",  "1010010001000110"},
    };

    @Test
    void assemble() {
        var myFirstProgram = new String[] {
                "add r1 r2",
                "syscall 100",
                "return",
                "subtract 10 r4"
        };
        var response = Assembler.assemble(myFirstProgram);
        assertEquals("0000100000100010",response[0]);
        assertEquals("0100000001100100",response[1]);
        assertEquals("0101000000000000",response[2]);
        assertEquals("0010110101000100",response[3]);
    }
    @Test
    void testInstructions() {
        for (var instruction : instructions) {
            var prog = new String[1];
            prog[0] = instruction[0];
            assertEquals(16,instruction[1].length(), "Instruction " + instruction[0] + " correct answer is wrong length");
            var result = Assembler.assemble(prog);
            assertEquals(instruction[1],result[0],"Instruction " + instruction[0] + " failed");
        }
    }
}
