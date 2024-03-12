package com.dma.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test0 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
//		Path input = Paths.get("/home/fr054721/Documents/coaxis/ERP_CAOXIS_DWH_V1.0.6-2022-12-7-12-8-27.json");	
//		
//		List<QuerySubject> qss = new ArrayList<QuerySubject>();
//		
//		if(Files.exists(input)) {
//			
//			Map<String, List<QuerySubject>> model = (Map<String, List<QuerySubject>>) Tools.fromJSON(input.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
//			
//			qss = model.get("querySubjects");
//			
//		}
		
		
		
		
//		for(QuerySubject qs: qss) {
//			String id = qs.get_id();
//			String table = qs.getTable_name();
//			Map<String, Map<String, Field>> tables = new HashMap<String, Map<String, Field>>();
//			Map<String, Field> columns = new HashMap<String, Field>();
//			for(Field field: qs.getFields()) {
//				columns.put(field.getField_name(), field);
//			}
//			tables.put(table, columns);
//			ids.put(id, tables);
//		}

//		System.out.println(ids.entrySet().iterator().next());
//		System.out.println(ids.entrySet().iterator().next().getKey());
//		System.out.println(ids.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey());
//		System.out.println(ids.entrySet().iterator().next().getValue().entrySet().iterator().next().getValue());
//		System.out.println(ids.entrySet().iterator().next().getValue().entrySet().iterator().next().getValue().entrySet().iterator().next().getValue().getField_name());

		
		Path modelPath = Paths.get("/home/fr054721/Documents/coaxis/ERP_CAOXIS_DWH_V1.0.6-2022-12-7-12-8-27.json");	
		Path idsPath = Paths.get("/home/fr054721/Documents/coaxis/ids.json");		
		Path currentPath = Paths.get("/home/fr054721/Documents/coaxis/currentMap.json");		
		List<QuerySubject> qss = new ArrayList<QuerySubject>();
		Map<String, Map<String, Map<String, Field>>> ids = new HashMap<String, Map<String, Map<String, Field>>>();
		Map<String, Map<String, Field>> currents = new HashMap<String, Map<String, Field>>();
		Map<String, Map<String, Set<String>>> updates = new HashMap<String, Map<String, Set<String>>>();
		List<QuerySubject> newQss = new ArrayList<QuerySubject>();
		Map<String, List<Field>> customFields = new HashMap<String, List<Field>>();
		
		if(Files.exists(currentPath) && Files.exists(idsPath) && Files.exists(modelPath)) {
//			ids = (Map<String, Map<String, Map<String, Field>>>) Tools.fromJSON(idsPath.toFile(), new TypeReference<Map<String, Map<String, Map<String, Field>>>>(){});
			currents = (Map<String, Map<String, Field>>) Tools.fromJSON(currentPath.toFile(), new TypeReference<Map<String, Map<String, Field>>>(){});
			Map<String, List<QuerySubject>> model = (Map<String, List<QuerySubject>>) Tools.fromJSON(modelPath.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
			qss = model.get("querySubjects");			
			System.out.println("On est bien Tintin !!!");
		}
		else {
			System.exit(1);
		}
		
		for(QuerySubject qs: qss) {
			String id = qs.get_id();
			String table = qs.getTable_name();
			Map<String, Map<String, Field>> tables = new HashMap<String, Map<String, Field>>();
			Map<String, Field> columns = new HashMap<String, Field>();
			List<Field> customFList = new ArrayList<Field>();
			for(Field field: qs.getFields()) {
				if(!field.isCustom()) {
					columns.put(field.getField_name(), field);
				}
				else {
					customFList.add(field);					
				}						
			}
			tables.put(table, columns);
			ids.put(id, tables);
			customFields.put(id, customFList);
		}
		
		
		for(Entry<String, Map<String, Map<String, Field>>> id: ids.entrySet()) {
	    	String qs_id = id.getKey();
	    	String qs_table = id.getValue().entrySet().iterator().next().getKey();
	    	Map<String, Field> qs_columns = id.getValue().entrySet().iterator().next().getValue();
	    	Set<String> qs_columnsKeys = new HashSet<String>(qs_columns.keySet());
	    	System.out.println(qs_id + " -> " + qs_table + " -> " + qs_columnsKeys.size() + " - " + qs_columnsKeys);
	    	Map<String, Field> current_columns = currents.get(qs_table);
	    	Set<String> current_columnsKeys = new HashSet<String>(current_columns.keySet());
	    	Set<String> addedColumnsKeys = new HashSet<String>(current_columnsKeys);
	    	addedColumnsKeys.removeAll(qs_columnsKeys);
	    	System.out.println("addedColumnsKeys -> " + addedColumnsKeys.size() + " - " + addedColumnsKeys);
	    	Set<String> removedColumnsKeys = new HashSet<String>(qs_columnsKeys);
	    	removedColumnsKeys.removeAll(current_columnsKeys);
	    	System.out.println("removedColumnsKeys -> " + removedColumnsKeys.size() + " - " + removedColumnsKeys);
	    	Set<String> retainedColumnsKeys = new HashSet<String>(qs_columnsKeys);
	    	retainedColumnsKeys.retainAll(current_columnsKeys);
	    	System.out.println("retainedColumnsKeys -> " + retainedColumnsKeys.size() + " - " + retainedColumnsKeys);
	    	Map<String, Set<String>> update = new HashMap<String, Set<String>>();
	    	update.put("added", addedColumnsKeys);
	    	update.put("removed", removedColumnsKeys);
	    	update.put("retained", retainedColumnsKeys);
	    	updates.put(qs_id, update);
//	    	System.out.println(Tools.toJSON(updates));
		}
		
		for(QuerySubject qs: qss) {
			String id = qs.get_id();
			String table = qs.getTable_name();
			List<Field> fields = qs.getFields();
			List<Field> newFields = new ArrayList<Field>();
			for(Field field: fields) {
				String column = field.getField_name();
				Field newField = null;
				if(updates.get(id).get("retained").contains(column)) {
					newField = field;
					newField.setFieldPos(currents.get(table).get(column).getFieldPos());
					System.out.println("retain " + field.getField_name());
				}
				if(newField != null) {
					newFields.add(newField);
				}
			}
			if(! updates.get(id).get("added").isEmpty()) {
				for(String fieldName: updates.get(id).get("added")) {
					Field newField = currents.get(table).get(fieldName);
					newFields.add(newField);
					System.out.println("added " + newField.getField_name());
				}
			}
			if(! updates.get(id).get("removed").isEmpty()) {
				
			}

			int fieldPos = newFields.size();
			if(customFields.get(qs.get_id()) != null) {
				for(Field customField: customFields.get(qs.get_id())) {
					customField.setFieldPos(++fieldPos);
					newFields.add(customField);
				}
			}
			
			qs.setFields(newFields);
		}
		
		Map<String, List<QuerySubject>> newModel = new HashMap<String, List<QuerySubject>>();
		newModel.put("querySubjects", qss);
		newModel.put("views", new ArrayList<QuerySubject>());
		
//		System.out.println(Tools.toJSON(newModel));
		Path output = Paths.get("/home/fr054721/Documents/coaxis/newModel.json");
		Files.write(output, Tools.toJSON(newModel).getBytes());
		
		Map<String, Set<String>> fieldsToRemove = new HashMap<String, Set<String>>();
		Map<String, Set<String>> fieldsToAdd = new HashMap<String, Set<String>>();
		Map<String, Set<String>> fieldsToUpdate = new HashMap<String, Set<String>>();

		for(Entry<String, Map<String, Set<String>>> update: updates.entrySet()) {
			String qs_id = update.getKey();
			System.out.println("qs_id=" + qs_id);
			for(Entry<String, Set<String>> states: update.getValue().entrySet()) {
				String state = states.getKey();
				System.out.println("state=" + state);
				switch (state) {
					case "added":
						fieldsToAdd.put(qs_id, updates.get(qs_id).get("added"));
						break;
					case "removed":
						fieldsToRemove.put(qs_id, updates.get(qs_id).get("removed"));
						break;
					case "retained":
						fieldsToUpdate.put(qs_id, updates.get(qs_id).get("retained"));
						break;
					}
			}
		}
		
		System.out.println(Tools.toJSON(fieldsToAdd));
		System.out.println(Tools.toJSON(fieldsToRemove));
		System.out.println(Tools.toJSON(fieldsToUpdate));
		
//		String id = "0";
//		String table = "t0";
//		String column = "c0";
//		Field field = new Field();
//		
//		columns.put(column, field);
//		tables.put(table, columns);
//		ids.put(id, tables);
//		
//		System.out.println(ids.get(id).get(table).get(column));
		
		
		
//		String s = "sfdsfvsefv;qsdcdcq";
//    	String qs_id = s.split(";")[0];
//    	String qs_table = s.split(";")[1];
//		
//    	System.out.println("qs_id=" + qs_id);
//    	System.out.println("qs_table=" + qs_table);
//		
//		long recCount = 20;
//		long qs_recCount = 20;
//		
//		double d0 = Double.parseDouble(String.valueOf(recCount));
//		double d1 = Double.parseDouble(String.valueOf(qs_recCount));
//		
//		double num = (d0/d1) * 100;
//		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
//		nf.setMaximumFractionDigits(3);
//		nf.setRoundingMode(RoundingMode.UP);
//	    num = Double.parseDouble(nf.format(num));
		
		
		
	}

}
