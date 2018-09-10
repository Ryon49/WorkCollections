.data
n: .word 8

.text
main:
    la t0, n
    lw a0, 0(t0)
    jal ra, factorial
    
    addi a1, a0, 0
    addi a0, x0, 1
    ecall # Print Result
    
    addi a0, x0, 10
    ecall # Exit

factorial:
	addi sp, sp, -4
    sw ra, 0(sp)
    addi a1, x0, 1
    addi a2, x0, 1
    jal ra, loop
    lw ra, 0(sp)
    addi sp, sp, 4
    ret
loop:
	blt a0, a2, exit
    mul a1, a1, a0
    addi a0, a0, -1
    jal x0, loop   
exit:
	add a0, x0, a1
    ret