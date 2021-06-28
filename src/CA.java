import java.io.*;
import java.util.*;

public class CA { 

	Instruction[] instructionMemory = new Instruction[1024];
	byte[] dataMemory = new byte[2048];
	byte[] registerFile = new byte[64];
	boolean[] statusRegister = new boolean[8];
	short pc;

	public void printStatusRegister(){
		System.out.println("Zero flag = " + statusRegister[0] + "\n" +
							"Sign flag = " + statusRegister[1] + "\n" +
							"Negative flag = " + statusRegister[2] + "\n" +
							"Overflow flase = " + statusRegister[3]+ "\n" +
							"Carry flag = " + statusRegister[4]);
	}

	public Instruction fetch() {
		return instructionMemory[pc++];
	}

	public Object[] decode(Instruction instruction) throws Exception{
		String  opCode = instruction.getOpCode();
		String r1 = instruction.getR1();
		int destination = Integer.parseInt(r1, 2);
		int r2ImmediateValue = instruction.getR2Value();
		System.out.println(instruction);
		Object[] res = new Object[4];
		switch (opCode) {
			case "0000", "0001", "0010", "0101", "0110", "0111" -> {
				res[0] = opCode;
				res[1] = destination;
				res[2] = registerFile[Integer.parseInt(r1, 2)];
				res[3] = registerFile[r2ImmediateValue];
				return res;
			}
			case "0011", "0100", "1000", "1001", "1010", "1011" -> {
				res[0] = opCode;
				res[1] = destination;
				res[2] = registerFile[Integer.parseInt(r1, 2)];
				res[3] = r2ImmediateValue;
				return res;
			}
			default -> System.out.println("The opcode is invalid");
		}
		return null;
	}

	public boolean execute(String opCode, int destination, byte r1, int r2Immediate) throws Exception {

		switch (opCode) {
		case "0000":
			add(destination, r1, r2Immediate);
			break;
		case "0001":
			subtract(destination, r1, r2Immediate);
			break;
		case "0010":
			multiply(destination, r1, r2Immediate);
			break;
		case "0011":
			registerFile[destination] = (byte) r2Immediate;
			break;
		case "0100":
			if(r1 == 0){
				pc += r2Immediate - 1;
				return true;
			}
			break;
		case "0101":
			and(destination, r1, r2Immediate);
			break;
		case "0110":
			or(destination, r1, r2Immediate);
			break;
		case "0111":
			pc = concatenate(r1, (byte)r2Immediate);
			return true;
		case "1000":
			slc(destination, r1, r2Immediate);
			break;

		case "1001":
			src(destination, r1, r2Immediate);
			break;

		case "1010":
			if(r2Immediate<0)
				throw new Exception ("Immedediate value is negative");
			registerFile[destination] = dataMemory[r2Immediate];
			break;

		case "1011":
			if(r2Immediate<0)
				throw new Exception ("Immedediate value is negative");
			dataMemory[r2Immediate] = r1;
			break;
		}
		return false;
	}

	public void add(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 + r2Immediate);

		// C
		statusRegister[4] = r1 + r2Immediate > Byte.MAX_VALUE;

		// V
		statusRegister[3] = (byte) (r1 + r2Immediate) != (r1 + r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 + r2Immediate) < 0;

		// S
		statusRegister[1] = xor(statusRegister[2], statusRegister[3]);

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;
	}

	public void subtract(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 - r2Immediate);

		// C
		statusRegister[4] = r1 - r2Immediate > Byte.MAX_VALUE;

		// V
		statusRegister[3] = (byte) (r1 - r2Immediate) != (r1 - r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 - r2Immediate) < 0;

		// S
		statusRegister[1] = xor(statusRegister[2], statusRegister[3]);

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;
	}

	public void multiply(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 * r2Immediate);

		// C
		statusRegister[4] = r1 * r2Immediate > Byte.MAX_VALUE;

		// N
		statusRegister[2] = (byte) (r1 - r2Immediate) < 0;

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;

	}

	public void and(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 & r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 & r2Immediate) < 0;

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;

	}

	public void or(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 | r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 | r2Immediate) < 0;

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;

	}

	public void slc(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 << r2Immediate | r1 >> 8 - r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 - r2Immediate) < 0;

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;
	}

	public void src(int destination, byte r1, int r2Immediate) {

		registerFile[destination] = (byte) (r1 >> r2Immediate | r1 << 8 - r2Immediate);

		// N
		statusRegister[2] = (byte) (r1 - r2Immediate) < 0;

		// Z
		statusRegister[0] = registerFile[destination] == 0;

		statusRegister[5] = false;

		statusRegister[6] = false;

		statusRegister[7] = false;
	}

	public static boolean xor(boolean x, boolean y) {
		return x != y;
	}

	public void run() throws Exception {
		Queue<Instruction> decode = new LinkedList<>();
		Queue<Object[]> execute = new LinkedList<>();
		boolean branch = false;
		for (int clockCycle = 1; clockCycle < (instructionMemory.length + 2); clockCycle++) {
			Instruction instruction = null;
			if (!execute.isEmpty()) {
				Object[] arr = execute.poll();
				try {
					branch = execute((String) arr[0], (int) arr[1], (byte) arr[2], (int) arr[3]);
				} catch (ClassCastException c) {
					branch = execute((String) arr[0], (int) arr[1], (byte) arr[2], (byte) arr[3]);

				}
			}
			if (!decode.isEmpty()) {
				if(branch){
					decode.clear();
					branch = false;
				}
				else{
					Object[]arr = decode(decode.poll());
					execute.add(arr);
				}
			}

			System.out.println("clock cycle: " + clockCycle);
			printRegister();
			printStatusRegister();
			System.out.println("***************************************");
			if (pc < instructionMemory.length)
				instruction = fetch();
			else
				return;
			if(instruction != null)
				decode.add(instruction);
			if(instruction == null && decode.isEmpty() && execute.isEmpty())
				return;
		}

	}

	public void printRegister() {
		for (int i = 0; i < registerFile.length; i++)
			System.out.println("R" + i + " -> " + registerFile[i]);
	}

	public void printRegister(int registerNumber) {
		System.out.println("R" + registerNumber + ": " + registerFile[registerNumber]);
	}

	public void interpreter(String filePath) throws Exception {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		int index = 0;
		while (br.ready()){
			String instruction = br.readLine();
			instruction = instruction.toLowerCase();
			StringTokenizer st = new StringTokenizer(instruction);
			ArrayList<String> tokens = new ArrayList<String>();
			while(st.hasMoreTokens()) 
				tokens.add(st.nextToken());
			
			if(tokens.isEmpty() || tokens.get(0).charAt(0) == '#')
				continue;
			
			if(tokens.size() != 3)
				throw new Exception("Syntax Error!");
			
			String res = switch (tokens.get(0)) {
				case "add" -> "0000" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "sub" -> "0001" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "mul" -> "0010" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "ldi" -> "0011" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				case "beqz" -> "0100" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				case "and" -> "0101" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "or" -> "0110" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "jr" -> "0111" + verifyRegister(tokens.get(1)) + verifyRegister(tokens.get(2));
				case "slc" -> "1000" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				case "src" -> "1001" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				case "lb" -> "1010" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				case "sb" -> "1011" + verifyRegister(tokens.get(1)) + verifyNumber(tokens.get(2));
				default -> throw new Exception("Syntax Error");
			};
			Instruction i = new Instruction(res);

			instructionMemory[index++] = i;
		}
		br.close();
		run();
	}

	public static String flipBits(String bits) {

		String res = "";
		for (int i = 0; i < bits.length(); i++)
			if (bits.charAt(i) == '1')
				res += "0";
			else if (bits.charAt(i) == '0')
				res += "1";
			else
				throw new NumberFormatException("The string is not binary");

		return res;
	}

	public static short binaryToDecimal(String number) {
		try {
			return Short.parseShort(number, 2);
		} catch (NumberFormatException e) {
			number = flipBits(number);
			short res = Short.parseShort(number, 2);
			return (short) (-(res + 1));
		}
	}

	public static String verifyRegister(String register) throws Exception {
		int num;
		try {
			num = Integer.parseInt(register.substring(1));
		} catch (Exception e) {
			throw new Exception("Syntax Error!");
		}

		if (register.charAt(0) != 'r' || (num < 0 || num > 63))
			throw new Exception("Syntax Error!");
		
		String res = Integer.toBinaryString(num);
		for(int i = res.length() ; i < 6 ; i++)
			res = "0" + res;
		return res;
	}

	public static String verifyNumber(String num)throws Exception {
		int number;
		try {
			number = Integer.parseInt(num);
		} catch (Exception e) {
			throw new Exception("Syntax Error!");
		}
			String res = Integer.toBinaryString(number);
			for(int i = res.length() ; i < 6 ; i++)
				res = "0" + res;
			return res.substring(res.length()-6);
	}

	public static short concatenate(byte x, byte y){
		String b1 = Integer.toBinaryString(x);
		String b2 = Integer.toBinaryString(y);
		String res = b1 + b2;
		return binaryToDecimal(res);
	}

	public static void main(String[] args) throws Exception{
		CA ca = new CA();
		ca.interpreter("program.txt");
	}

}
