package pass1;

import java.io.*;
import java.util.*;

class OPVALUES {
	public String getStatement_class() {
		return statement_class;
	}

	public String getMachine_code() {
		return machine_code;
	}

	String statement_class;
	String machine_code;

	public OPVALUES createTuple(String statement_class, String machine_code) {
		OPVALUES opvalues = new OPVALUES();
		opvalues.statement_class = statement_class;
		opvalues.machine_code = machine_code;
		return opvalues;
	}

	public Map<String, OPVALUES> getOptable() {
		Map<String, OPVALUES> op_table = new HashMap<>();
		op_table.put("STOP", createTuple("IS", "0"));
		op_table.put("ADD", createTuple("IS", "1"));
		op_table.put("SUB", createTuple("IS", "2"));
		op_table.put("MULT", createTuple("IS", "3"));
		op_table.put("MOVER", createTuple("IS", "4"));
		op_table.put("MOVEM", createTuple("IS", "5"));
		op_table.put("COMP", createTuple("IS", "6"));
		op_table.put("BC", createTuple("IS", "7"));
		op_table.put("DIV", createTuple("IS", "8"));
		op_table.put("READ", createTuple("IS", "9"));
		op_table.put("PRINT", createTuple("IS", "10"));
		op_table.put("DC", createTuple("DL", "1"));
		op_table.put("DS", createTuple("DL", "2"));
		op_table.put("START", createTuple("AD", "1"));
		op_table.put("END", createTuple("AD", "2"));
		op_table.put("ORIGIN", createTuple("AD", "3"));
		op_table.put("EQU", createTuple("AD", "4"));
		op_table.put("LTORG", createTuple("AD", "5"));
		op_table.put("AREG", createTuple("RG", "1"));
		op_table.put("BREG", createTuple("RG", "2"));
		op_table.put("CREG", createTuple("RG", "3"));
		op_table.put("EQ", createTuple("CC", "1"));
		op_table.put("LT", createTuple("CC", "2"));
		op_table.put("GT", createTuple("CC", "3"));
		op_table.put("LE", createTuple("CC", "4"));
		op_table.put("GE", createTuple("CC", "5"));
		op_table.put("NE", createTuple("CC", "6"));
		return op_table;
	}
}

class TabValueHolder {
	int address;
	int index;

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}

public class PassOne {

	public static String removeComma(String token) {
		return token.replaceAll(",", "");
	}

	public static boolean isOpCode(String token) {
		if ((new OPVALUES()).getOptable().containsKey(token))
			return true;
		return false;
	}

	public static String stringOfTuple(OPVALUES tuple) {
		return "(" + tuple.getStatement_class() + "," + tuple.getMachine_code() + ") ";
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void printTab(BufferedWriter bw, LinkedHashMap<String, TabValueHolder> SYMTAB) throws IOException {
		for (String key : SYMTAB.keySet()) {
			bw.write(key + "\t" + SYMTAB.get(key).getAddress() + "\t" + SYMTAB.get(key).getIndex() + "\n");
		}
	}
	
	public static int setLITTAB(int address, LinkedHashMap<String, TabValueHolder> LITTAB) {
		for(String key : LITTAB.keySet()) {
			TabValueHolder tabValueHolder = LITTAB.get(key);
			if(tabValueHolder.getAddress() == 0) {
				tabValueHolder.setAddress(address);
				address++;
			}
		}
		return address;
	}

	public static void main(String[] args) throws IOException {
		Map<String, OPVALUES> OPTAB = (new OPVALUES()).getOptable(); // get Optable
		LinkedHashMap<String, TabValueHolder> SYMTAB = new LinkedHashMap<>();
		LinkedHashMap<String, TabValueHolder> LITTAB = new LinkedHashMap<>();
		List<Integer> POOLTAB = new ArrayList<>();
		int symtabPointer = 1;
		int littabPointer = 1;
		int addressPointer = 0;
		ArrayList<String> intermediateCode = new ArrayList<>();
		File file = new File("C:\\Users\\admin\\eclipse-workspace\\Assembler\\src\\assemble_code.txt"); // read ALP
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String intermediateLine = "";
			boolean end_flag = false;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				int tokenCounter = 0;
				if (addressPointer != 0)
					intermediateLine += String.valueOf(addressPointer) + ") ";
				for (int i = 0; i < tokens.length; i++) {
					OPVALUES optuple = new OPVALUES();
					String token = removeComma(tokens[i]);
					if (isOpCode(token)) {
						optuple = OPTAB.get(token);
						if (token.equals("END")) {
							intermediateLine = "";
							intermediateLine += stringOfTuple(OPTAB.get(token));
							addressPointer = setLITTAB(addressPointer, LITTAB);
							POOLTAB.add(littabPointer);
							break;
						}	
						if(token.equals("LTORG")) {
							intermediateLine = "";
							addressPointer = setLITTAB(addressPointer, LITTAB);
							POOLTAB.add(littabPointer);
						}
						if (optuple.getStatement_class().equals("RG") || optuple.getStatement_class().equals("CC"))
							intermediateLine += "(" + optuple.getMachine_code() + ") ";
						else
							intermediateLine += stringOfTuple(optuple);
						if (token.equals("START")) {
							addressPointer = Integer.parseInt(tokens[i + 1]) - 1;
							intermediateLine += "(C," + Integer.parseInt(tokens[i + 1]) + ") ";
							i++;
						} else if (token.equals("ORIGIN")) {
							TabValueHolder symtabHolder = SYMTAB.get(tokens[i + 1]);
							addressPointer = symtabHolder.getAddress();
							i++;
						}

					} else if (SYMTAB.containsKey(token)) {
						intermediateLine += "(S," + SYMTAB.get(token).getIndex() + ") ";
					} else if (tokenCounter == 0) { // if Label
						TabValueHolder symtabHolder = new TabValueHolder();
						symtabHolder.setAddress(addressPointer);
						symtabHolder.setIndex(symtabPointer);
						SYMTAB.put(token, symtabHolder);
						// intermediateLine += "(S," + symtabPointer + ") ";
						symtabPointer++;
					} else if (isNumeric(token)) {
						if (tokens[i - 1].equals("DS")) {
							TabValueHolder symtabHolder = new TabValueHolder();
							symtabHolder.setAddress(addressPointer);
							symtabHolder.setIndex(SYMTAB.get(tokens[i - 2]).getIndex());
							SYMTAB.put(tokens[i - 2], symtabHolder);
						} else if (tokens[i - 1].equals("DC")) {
							TabValueHolder symtabHolder = new TabValueHolder();
							symtabHolder.setAddress(addressPointer);
							symtabHolder.setIndex(SYMTAB.get(tokens[i - 2]).getIndex());
							SYMTAB.put(tokens[i - 2], symtabHolder);
						}
						intermediateLine += "(C," + token + ") ";
					} else {
						if (token.startsWith("=")) {
							TabValueHolder littabHolder = new TabValueHolder();
							littabHolder.setIndex(littabPointer);
							intermediateLine += "(L," + littabPointer + ") "; 
							littabPointer++;
							LITTAB.put(token, littabHolder);
						} else {
							TabValueHolder symtabHolder = new TabValueHolder();
							symtabHolder.setAddress(-1);
							symtabHolder.setIndex(symtabPointer);
							SYMTAB.put(token, symtabHolder);
							intermediateLine += "(S," + String.valueOf(symtabPointer) + ") ";
							symtabPointer++;
						}

					}
					tokenCounter++;
				}
				addressPointer++;
				tokenCounter = 0;
				intermediateCode.add(intermediateLine);
				intermediateLine = "";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File fout = new File("C:\\Users\\admin\\eclipse-workspace\\Assembler\\src\\pass1.txt");
		FileOutputStream fos = new FileOutputStream(fout); 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for (String line : intermediateCode) {
			bw.write(line + "\n");
		}
		bw.write("\nSYMBOL TABLE: \n");
		printTab(bw, SYMTAB);
		bw.write("\nLITERAL TABLE: \n");
		printTab(bw, LITTAB);
		bw.write("\nPOOLTAB:\n");
		for(int i : POOLTAB) {
			bw.write(String.valueOf(i) + "\n");
		}
		bw.close();
	}
}
