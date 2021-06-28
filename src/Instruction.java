public class Instruction {
    private String opCode;
    private String r1;
    private String r2Immediate;

    public Instruction(String instruction) throws Exception{
        if(instruction.length() != 16)
            throw new Exception("Instruction length should be 16");
        this.opCode = instruction.substring(0, 4);
        this.r1 = instruction.substring(4, 10);
        this.r2Immediate = instruction.substring(10);
    }

    public String getOpCode(){
        return opCode;
    }

    public String getR1() {
        return r1;
    }

    public String getR2Immediate() {
        return r2Immediate;
    }

    public int getR2Value(){
        if(r2Immediate.charAt(0) == '1'){
            r2Immediate = CA.flipBits(r2Immediate);
            return  -(Integer.parseInt(r2Immediate, 2) + 1);
        }
        else
            return Integer.parseInt(r2Immediate, 2);
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opCode='" + opCode + '\'' +
                ", r1='" + r1 + '\'' +
                ", r2Immediate='" + r2Immediate + '\'' +
                '}';
    }

    public static void main(String[] args)throws Exception {
       Instruction i = new Instruction("0011000000111111");
        System.out.println(i.opCode);
        System.out.println(i.r1);
        System.out.println(i.r2Immediate);


    }




}
