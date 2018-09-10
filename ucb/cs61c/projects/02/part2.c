#include <stdio.h> // for stderr
#include <stdlib.h> // for exit()
#include "types.h"
#include "utils.h"
#include "riscv.h"

void execute_rtype(Instruction, Processor *);
void execute_itype_except_load(Instruction, Processor *);
void execute_branch(Instruction, Processor *);
void execute_jal(Instruction, Processor *);
void execute_load(Instruction, Processor *, Byte *);
void execute_store(Instruction, Processor *, Byte *);
void execute_ecall(Processor *, Byte *);
void execute_lui(Instruction, Processor *);

void execute_instruction(uint32_t instruction_bits, Processor *processor,Byte *memory) {    
    Instruction instruction = parse_instruction(instruction_bits);
    switch(instruction.opcode) {
        case 0x33:
            execute_rtype(instruction, processor);
            processor->PC += 4;
            break;
        case 0x13:
            execute_itype_except_load(instruction, processor);
            processor->PC += 4;
            break;
        case 0x73:
            execute_ecall(processor, memory);
            processor->PC += 4;
            break;
        case 0x63:
            execute_branch(instruction, processor);
            break;
        case 0x6F:
            execute_jal(instruction, processor);
            break;
        case 0x23:
            execute_store(instruction, processor, memory);
            processor->PC += 4; 
            break;
        case 0x03:
            execute_load(instruction, processor, memory);
            processor->PC += 4;
            break;
        case 0x37:
            execute_lui(instruction, processor);
            processor->PC += 4;
            break;
        default: // undefined opcode
            handle_invalid_instruction(instruction);
            processor->PC += 4;
            exit(-1);
            break;
    }
}

void execute_rtype(Instruction instruction, Processor *processor) {
    unsigned int funct3 = instruction.rtype.funct3;
    unsigned int funct7 = instruction.rtype.funct7;

    unsigned int rd = instruction.rtype.rd;
    // unsigned int rs1 = instruction.rtype.rs1;
    // unsigned int rs2 = instruction.rtype.rs2;

    int rs1 = sign_extend_number(processor->R[instruction.rtype.rs1], 32);
    int rs2 = sign_extend_number(processor->R[instruction.rtype.rs2], 32);

    switch (funct3){
        case 0x0:
            switch (funct7) {
                case 0x0:
                    processor->R[rd] = rs1 + rs2;
                    break;
                case 0x1:
                    // Mul 
                    processor->R[rd] = rs1 * rs2;
                    break;
                case 0x20:
                    // Sub                    
                    processor->R[rd] = rs1 - rs2;
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    exit(-1);
                    break;
            }
            break;
        case 0x1:
            switch (funct7) {
                sDouble param1, param2;
                case 0x0:
                    // SLL
                    processor->R[rd] = rs1 << rs2;
                    break;
                case 0x1:
                    // MULH
                    param1 = (Double) rs1;
                    param2 = (Double) rs2;

                    param1 *= param2;
                    processor->R[rd] = (int) (param1 >> 32);
                    break;
            }
            break;
        case 0x2:
            // SLT
            processor->R[rd] = (rs1 < rs2) ? 1 : 0;
            break;
        case 0x4:
            switch (funct7) {
                case 0x0:
                    // XOR
                    processor->R[rd] = rs1 ^ rs2;
                    break;
                case 0x1:
                    // DIV
                    processor->R[rd] = rs1 / rs2;
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    exit(-1);
                    break;
            }
            break;
        case 0x5:
            switch (funct7) {
                case 0x0:
                    // SRL
                    processor->R[rd] = processor->R[instruction.rtype.rs1] >> processor->R[instruction.rtype.rs2]; 
                    break;
                case 0x20:
                    // SRA
                    processor->R[rd] = rs1 >> rs2;
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    exit(-1);
                break;
            }
            break;
        case 0x6:
            switch (funct7) {
                case 0x0:
                    // OR
                    processor->R[rd] = rs1 | rs2;
                    break;
                case 0x1:
                    // REM
                    processor->R[rd] = rs1 % rs2;
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    exit(-1);
                    break;
            }
            break;
        case 0x7:
            // AND
            processor->R[rd] = rs1 & rs2;
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_itype_except_load(Instruction instruction, Processor *processor) {
    unsigned int funct3 = instruction.itype.funct3;
    unsigned int rd = instruction.itype.rd;

    int rs1 = sign_extend_number(processor->R[instruction.itype.rs1], 32);
    int imm = sign_extend_number(instruction.itype.imm, 12);
    
    switch (funct3) {
        int shiftOp;
        
        case 0x0:
            // ADDI
            processor->R[rd] = rs1 + imm;
            break;
        case 0x1:
            // SLLI
            shiftOp = (instruction.itype.imm >> 5) & 0b1111111;
            switch(shiftOp) {
                case 0x00:
                case 0x20:
                    processor->R[rd] = processor->R[instruction.itype.rs1] << (imm& 0x1F);
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    break;
            }
            break;
        case 0x2:
            // STLI
            processor->R[rd] = (rs1 < imm) ? 1 : 0;
            break;
        case 0x4:
            // XORI
            processor->R[rd] = rs1 ^ imm;
            break;
        case 0x5:
            // Shift Right (You must handle both logical and arithmetic)
            shiftOp = instruction.itype.imm >> 10;
            switch(shiftOp) {
                case 0x0:
                    processor->R[rd] = processor->R[instruction.itype.rs1] >> (imm & 0x1F);
                    break;
                case 0x1:
                    processor->R[rd] = ((signed int) processor->R[instruction.itype.rs1]) >> (imm & 0x1F);
                    break;
                default:
                    handle_invalid_instruction(instruction);
                    break;
            }
            break;
        case 0x6:
            // ORI
            processor->R[rd] = rs1 | imm;
            break;
        case 0x7:
            // ANDI
            processor->R[rd] = rs1 & imm;
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void execute_ecall(Processor *p, Byte *memory) {
    Register i;
    
    // syscall number is given by a0 (x10)
    // argument is given by a1
    switch(p->R[10]) {
        case 1: // print an integer
            printf("%d",p->R[11]);
            break;
        case 4: // print a string
            for(i=p->R[11];i<MEMORY_SPACE && load(memory,i,LENGTH_BYTE);i++) {
                printf("%c",load(memory,i,LENGTH_BYTE));
            }
            break;
        case 10: // exit
            printf("exiting the simulator\n");
            exit(0);
            break;
        case 11: // print a character
            printf("%c",p->R[11]);
            break;
        default: // undefined ecall
            printf("Illegal ecall number %d\n", p->R[10]);
            exit(-1);
            break;
    }
}

void execute_branch(Instruction instruction, Processor *processor) {
    Word offset = (Word) get_branch_offset(instruction);
    unsigned int funct3 = instruction.sbtype.funct3;

    unsigned int rs1 = instruction.sbtype.rs1;
    unsigned int rs2 = instruction.sbtype.rs2;

    unsigned int jumpTo = (Word) processor->PC + offset;
    unsigned int next = (Word) processor->PC + (Word) 4;

    if (processor->PC > 0x0fffffff) {
        jumpTo += 0xffffc800;
        next += 0xffffc800;
    }
    switch (funct3) {
        case 0x0:
            // BEQ
            processor->PC = (processor->R[rs1] == processor->R[rs2]) ? jumpTo : next;
            break;
        case 0x1:
            // BNE
            processor->PC = (processor->R[rs1] != processor->R[rs2]) ? jumpTo : next;
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_load(Instruction instruction, Processor *processor, Byte *memory) {
    unsigned int funct3 = instruction.itype.funct3;
    unsigned int rd = instruction.itype.rd;
    unsigned int imm = instruction.itype.imm;

    int rs1 = sign_extend_number(processor->R[instruction.itype.rs1], 32);
    switch (funct3) {
        case 0x0:
            // LB
            processor->R[rd] = sign_extend_number(load(memory, rs1 + imm, LENGTH_BYTE), 8); 
            break;
        case 0x1:
            // LH
            processor->R[rd] = sign_extend_number(load(memory, rs1 + imm, LENGTH_HALF_WORD), 16);
            break;
        case 0x2:
            // LW
            processor->R[rd] = sign_extend_number(load(memory, rs1 + imm, LENGTH_WORD), 32);
            break;
        default:
            handle_invalid_instruction(instruction);
            break;
    }
}

void execute_store(Instruction instruction, Processor *processor, Byte *memory) {
    unsigned int funct3 = instruction.stype.funct3;
    
    int rs1 = sign_extend_number(processor->R[instruction.stype.rs1], 32);
    int rs2 = sign_extend_number(processor->R[instruction.stype.rs2], 32);
    int offset = get_store_offset(instruction);
    
    switch (funct3) {
        case 0x0:
            // SB
            store(memory, rs1 + offset, LENGTH_BYTE, rs2);
            break;
        case 0x1:
            // SH
            store(memory, rs1 + offset, LENGTH_HALF_WORD, rs2);
            break;
        case 0x2:
            // SW
            store(memory, rs1 + offset, LENGTH_WORD, rs2);
            break;
        default:
            handle_invalid_instruction(instruction);
            exit(-1);
            break;
    }
}

void execute_jal(Instruction instruction, Processor *processor) {
    /* YOUR CODE HERE */
    int offset = get_jump_offset(instruction);
    unsigned int rd = instruction.ujtype.rd;
    processor->R[rd] = processor->PC + 4;
    processor->PC += offset;
}

void execute_lui(Instruction instruction, Processor *processor) {
    int imm = instruction.utype.imm;
    unsigned int rd = instruction.utype.rd;
    processor->R[rd] = imm << 12;
}

void store(Byte *memory, Address address, Alignment alignment, Word value) {
    if (address >= MEMORY_SPACE || address + alignment > MEMORY_SPACE) {
        handle_invalid_write(address);
    }
    int i = 0;
    while (i < alignment) {
        memory[address + i] = (value >> (8 * i));
        i += 1;
    }
    return;
}

Word load(Byte *memory, Address address, Alignment alignment) {
    if(address >= MEMORY_SPACE || address + alignment > MEMORY_SPACE) {
        handle_invalid_read(address);
    }
    Word data = 0;
    int i = 0;
    while (i < alignment) {
        Word temp = memory[address + i];
        temp <<= (8 * i);
        data += temp;
        i += 1;
    }
    return data;
}