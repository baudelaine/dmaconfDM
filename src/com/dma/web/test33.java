package com.dma.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class test33 {

	private static String fkColumnFullName;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Path csvExp = Paths.get("/home/fr054721/Downloads/antibia/relationExp.csv"); 
		Path csv = Paths.get("/home/fr054721/Downloads/antibia/relation.csv");
		LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(csvExp.toFile())));
		
		// JOIN_NAME;FKTABLE_NAME;PKTABLE_NAME;RELATION_EXPRESSION
		// Jointure789;Pomphistservsec;Pomphistburosec;Pomphistservsec.CLEENREG=Pomphistburosec.CLEENREG and Pomphistservsec.CLEPERS=Pomphistburosec.CLEPERS

		// FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME
		// FK_ACTCIRELATION_ACTCI_65;PK_ACTCI_ACTCIRELATION_65;ACTCIRELATION;ACTCI;1;ANCESTORCI;ACTCINUM

//		data.put("FK_NAME", line.split(";")[0]);
//		data.put("PK_NAME", line.split(";")[1]);
//		data.put("FKTABLE_NAME", line.split(";")[2]);
//		data.put("PKTABLE_NAME", line.split(";")[3]);
//		data.put("KEY_SEQ", line.split(";")[4]);
//		data.put("FKCOLUMN_NAME", line.split(";")[5]);
//		data.put("PKCOLUMN_NAME", line.split(";")[6]);
		
		
		List<String> lines = new ArrayList<String>();
		
		
		String line;
		String sep = ";";
		String header = "FK_NAME" + sep + "PK_NAME" + sep + "FKTABLE_NAME" + sep + "PKTABLE_NAME" + sep + 
				"KEY_SEQ" + sep + "FKCOLUMN_NAME" + sep + "PKCOLUMN_NAME" + sep + "RELATION_EXPRESSION";
		lines.add(header);
		int counter = 0;
	    while ((line = reader.readLine()) != null) {
//	    	line = line.toUpperCase();
	    	String fkName = "FK_" + line.split(sep)[1] + "_" + line.split(sep)[2] + "_" + counter; 
	    	String pkName = "PK_" + line.split(sep)[2] + "_" + line.split(sep)[1] + "_" + counter; 
	    	String fkTableName = line.split(sep)[1];
	    	String pkTableName = line.split(sep)[2];
	    	String fkColumnName = "notAvailable";
	    	String pkColumnName = "notAvailable";
	    	String relExp = line.split(sep)[3];
	    	short keySeq = 1;
//	    	if(relExp.contains("=") && !relExp.contains("<") && !relExp.contains(">") && !relExp.contains("(") && !relExp.contains(")")) {
//	    		String fkColumnFullName = relExp.split("=")[0];
//	    		fkColumnName = fkColumnFullName.split("\\.")[1].replaceAll("\"|<|>|(|)|=", ""); 
//	    		String pkColumnFullName = relExp.split("=")[1];
//	    		pkColumnName = pkColumnFullName.split("\\.")[1].replaceAll("\"|<|>|(|)|=", ""); 
//	    	}
	    	counter++;
    		lines.add(fkName + sep + pkName + sep + fkTableName + sep + pkTableName + sep + String.valueOf(keySeq) + sep + fkColumnName + sep + pkColumnName + sep + relExp);
	    }
		
		Path output = Paths.get("/home/fr054721/Downloads/antibia/relationExpression.csv"); 

	    if(Files.notExists(output)) {
	    	Files.createFile(output);
	    }
	    lines.remove(1);
		Files.write(output, lines);
		output.toFile().setReadable(true, false);
	    
	}

}
