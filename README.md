# ISA
 instruction set architecture based on von neumann architecture

 There are 12 instructions in this ISA and there are 64 registers named r0 till r63.  

## Instructions syntax:
 add r1 r2 -> r1 = r1 + r2  
 sub r1 r2 -> r1 = r1 - r2
 mul r1 r2 -> r1 = r1 * r2
 ldi r1 num -> r1 = num
 beqz r1 num -> if r1 = 0 then skip (num) instructions
 and r1 r2 -> r1 = r1 & r2
 or r1 r2 -> r1 = r1 | r2
 jr r1 r2 -> pc = concat(r1, r2)
 slc r1 num -> shift left circular to r1 by (num)
 src r1 num -> shift right circular to r1 by (num)
 lb r1 address -> r1 = Memory[address]
 sw r1 address -> Memory[address] = r1
 

