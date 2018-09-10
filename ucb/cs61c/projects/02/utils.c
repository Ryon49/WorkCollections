#include "utils.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int power(int, int);

/* Sign extends the given field to a 32-bit integer where field is
 * interpreted an n-bit integer. */ 
int sign_extend_number( unsigned int field, unsigned int n) {
    int max = power(2, n - 1) - 1; 
    unsigned toShift = 32 - n;
    int shifted = (field << toShift) >> toShift;
    if (shifted > max) {
        shifted -= power(2, n); // bias offset is 2^n
    }
    return shifted;
}

int power(int base, int to) {
    if (to < 0) {
        return 0;
    }

    int ret = 1;
    while (to-- > 0) {
        ret = ret * base;
    }
    return ret;
}

/* Unpacks the 32-bit machine code instruction given into the correct
 * type within the instruction struct */ 
Instruction parse_instruction(uint32_t instruction_bits) {
    /* YOUR CODE HERE */
    Instruction instruction;
    int opcode = instruction_bits & (0b1111111);
    instruction.opcode = opcode;

    switch(opcode) {
        case 0x33:  // R-type
            instruction.rtype.opcode  = opcode;
            instruction.rtype.rd      = (instruction_bits >> 7) & (0b11111);
            instruction.rtype.funct3  = (instruction_bits >> 12) & (0b111);
            instruction.rtype.rs1     = (instruction_bits >> 15) & (0b11111);
            instruction.rtype.rs2     = (instruction_bits >> 20) & (0b11111);
            instruction.rtype.funct7  = instruction_bits >> 25;
            break;
        case 0x13:  // I-type
        case 0x3:
        case 0x73:
            instruction.itype.opcode  = opcode;
            instruction.itype.rd      = (instruction_bits >> 7) & (0b11111);
            instruction.itype.funct3  = (instruction_bits >> 12) & (0b111);
            instruction.itype.rs1     = (instruction_bits >> 15) & (0b11111);
            instruction.itype.imm     = instruction_bits >> 20;
            break;
        case 0x23:  // S-type
            instruction.stype.opcode  = opcode;
            instruction.stype.imm5    = (instruction_bits >> 7) & (0b11111);
            instruction.stype.funct3  = (instruction_bits >> 12) & (0b111);
            instruction.stype.rs1     = (instruction_bits >> 15) & (0b11111);
            instruction.stype.rs2     = (instruction_bits >> 20) & (0b11111);
            instruction.stype.imm7    = instruction_bits >> 25;
            break;
        case 0x63:  // SB-type
            instruction.sbtype.opcode  = opcode;
            instruction.sbtype.imm5    = (instruction_bits >> 7) & (0b11111);
            instruction.sbtype.funct3  = (instruction_bits >> 12) & (0b111);
            instruction.sbtype.rs1     = (instruction_bits >> 15) & (0b11111);
            instruction.sbtype.rs2     = (instruction_bits >> 20) & (0b11111);
            instruction.sbtype.imm7    = instruction_bits >> 25;
            break;
        case 0x37:  // U-type
            instruction.utype.opcode  = opcode;
            instruction.utype.rd      = (instruction_bits >> 7) & (0b11111);
            instruction.utype.imm     = instruction_bits >> 12;
            break;
        case 0x6F:  // UJ-type
            instruction.ujtype.opcode = opcode;
            instruction.ujtype.rd     = (instruction_bits >> 7) & (0b11111);
            instruction.utype.imm     = instruction_bits >> 12;
            break;
        default: // undefined opcode
            break;
    }
    return instruction;
}

/* Return the number of bytes (from the current PC) to the branch label using the given
 * branch instruction */
int get_branch_offset(Instruction instruction) {
    int dumbImm5 = instruction.sbtype.imm5;
    int dumbImm7 = instruction.sbtype.imm7;

    int imm11 = dumbImm5 & 0b1;
    int imm1 = dumbImm5 >> 1;
    int imm5 = dumbImm7 & 0b111111;
    int imm12 = dumbImm7 >> 6;

    int offset = (imm12 << 12) + (imm11 << 11) + (imm5 << 4) + imm1;
    offset = sign_extend_number(offset, 20) << 1;
    return offset; 
}

/* Returns the number of bytes (from the current PC) to the jump label using the given
 * jump instruction */
int get_jump_offset(Instruction instruction) {
    unsigned int dumbImm = instruction.ujtype.imm;

    unsigned int imm1 = (dumbImm >> 9) & 0b1111111111;
    unsigned int imm11 = (dumbImm >> 8) & 0b1;
    unsigned int imm12 = dumbImm & 0b11111111;
    unsigned int imm20 = dumbImm >> 19;
    int offset = (imm20 << 19) + (imm12 << 11) + (imm11 << 10) + imm1;
    offset = sign_extend_number(offset, 20) << 1;
    return offset;
}

int get_store_offset(Instruction instruction) {
    unsigned int imm5 = instruction.stype.imm5;
    unsigned int imm7 = instruction.stype.imm7;
    unsigned int offset = (imm7 << 5) + imm5;
    offset = sign_extend_number(offset, 12);
    return offset;
}

void handle_invalid_instruction(Instruction instruction) {
    printf("Invalid Instruction: 0x%08x\n", instruction.bits); 
}

void handle_invalid_read(Address address) {
    printf("Bad Read. Address: 0x%08x\n", address);
    exit(-1);
}

void handle_invalid_write(Address address) {
    printf("Bad Write. Address: 0x%08x\n", address);
    exit(-1);
}
