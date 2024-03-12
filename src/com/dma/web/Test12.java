package com.dma.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test12 {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Path input = Paths.get("/home/fr054721/dmagui/ca/views.csv");
		Path output = Paths.get("/home/fr054721/dmagui/ca/views.json");
		String delim = ";";
		String lang = "fr"; 
		String header = "TABLE_NAME" + delim + "TABLE_TYPE" + delim + "TABLE_LABEL" + delim + "TABLE_DESCRIPTION" + delim +
				"FIELD_ID" + delim  + "FIELD_NAME" + delim + "FIELD_TYPE" + delim + "FIELD_LABEL" + delim + "FIELD_DESCRIPTION" + delim +
				"EXPRESSION" + delim + "HIDDEN" + delim + "ICON" + delim + "ALIAS" + delim + "FOLDER" + delim + "ROLE";				

		List<String> lines = Files.readAllLines(input);
		
		System.out.println(lines.size());
		
		if(lines.get(0).equalsIgnoreCase(header)) {
			System.out.println("Header is OK");
			lines.remove(0);
		}
		
		Map<String, QuerySubject> views = new HashMap<String, QuerySubject>();
		
		QuerySubject view = null;
		
		for(String line: lines) {
			if(! views.containsKey(line.split(delim)[0])) {
				view = new QuerySubject();
				view.setTable_name(line.split(delim)[0]);
				view.setType(line.split(delim)[1]);
				Map<String, String> labels = new HashMap<String, String>();
				labels.put(lang, line.split(delim)[2]);
				view.setLabels(labels);
				Map<String, String> descriptions = new HashMap<String, String>();
				descriptions.put(lang, line.split(delim)[3]);
				view.setDescriptions(descriptions);
				List<Field> fields = new ArrayList<Field>();
				view.addFields(fields);
				views.put(line.split(delim)[0], view);
			}
			Field field = new Field();
			field.set_id(line.split(delim)[4]);
			field.setField_name(line.split(delim)[5]);
			field.setField_type(line.split(delim)[6]);
			Map<String, String> labels = new HashMap<String, String>();
			labels.put(lang, line.split(delim)[7]);
			field.setLabels(labels);
			Map<String, String> descriptions = new HashMap<String, String>();
			descriptions.put(lang, line.split(delim)[8]);
			field.setDescriptions(descriptions);
			field.setExpression(line.split(delim)[9]);
			field.setHidden(Boolean.parseBoolean(line.split(delim)[10].toLowerCase()));
			field.setIcon(line.split(delim)[11]);
			field.setAlias(line.split(delim)[12]);
			field.setFolder(line.split(delim)[13]);
			field.setRole(line.split(delim)[14]);
			views.get(line.split(delim)[0]).addField(field);
			
		}
		
		System.out.println(views.keySet());

		
		System.out.println("TEC_AUTORIZATION_LINE=" + views.get("TEC_AUTORIZATION_LINE").getFields().size());
		System.out.println("TEC_PARTY=" + views.get("TEC_PARTY").getFields().size());
		System.out.println("TEC_CONTRACT=" + views.get("TEC_CONTRACT").getFields().size());
		System.out.println("TEC_AUTORIZATION=" + views.get("TEC_AUTORIZATION").getFields().size());
		
		System.out.println(views.values());
		
		
		Files.deleteIfExists(output);
		
		Files.write(output, Arrays.asList(Tools.toJSON(views.values())), StandardCharsets.UTF_8);			
	}
	

}
