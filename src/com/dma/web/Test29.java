package com.dma.web;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test29 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/dmaconf/mod3.json");
		
		if(!Files.exists(path)) {
			System.err.println("ERROR: No file found !!!");
			System.exit(1);
		}

		String schema = "DB2INST1";
		
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		Connection conn = DriverManager.getConnection("jdbc:db2://172.16.186.242:50000/SAMPLE", "db2inst1", "spcspc");
		conn.createStatement().execute("set schema=" + schema);
		DatabaseMetaData metaData = conn.getMetaData();		
		String[] tableTypes = {"TABLE", "VIEW"};
		
		@SuppressWarnings("unchecked")
//		List<String> langs = (List<String>) Tools.fromJSON((String) parms.get("langs"), new TypeReference<List<String>>(){});
		List<String> langs = Arrays.asList("fr");
		
		@SuppressWarnings("unchecked")
		List<QuerySubject> qss = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});

		Map<String, List<Field>> fieldsToRemove = new HashMap<String, List<Field>>();
		Map<String, List<Field>> fieldsToAdd = new HashMap<String, List<Field>>();
		Map<String, List<Field>> fieldsToUpdate = new HashMap<String, List<Field>>();
		Map<String, List<Field>> customFields = new HashMap<String, List<Field>>();

		Map<String, Map<String, Field>> modelMap = new HashMap<String, Map<String, Field>>();
		Map<String, Map<String, Field>> dbMap = new HashMap<String, Map<String, Field>>();
		Map<String, Integer> colCountMap = new HashMap<String, Integer>();
		
		// Start build modelMap and put custom fields in customFields
		for(QuerySubject qs: qss) {
			String table = qs.getTable_name();
			List<Field> fields = qs.getFields();
			Map<String, Field> fMap = new HashMap<String, Field>();
			List<Field> customFList = new ArrayList<Field>();
			for(Field field: fields) {
				String fieldName = field.getField_name();
				if(!field.isCustom()) {
					fMap.put(fieldName, field);
				}
				else {
					customFList.add(field);					
				}
			}
			modelMap.put(table, fMap);
			customFields.put(table, customFList);
		}
		// End build modelMap and put custom fields in customFields
		
		// Start build dbMap and colCountMap
	    ResultSet rstTables = metaData.getTables(conn.getCatalog(), schema, "%", tableTypes);					    
	    while (rstTables.next()) {
	    	String table = rstTables.getString("TABLE_NAME");
	    	if(modelMap.containsKey(table)) {
		    	System.out.println(table);
				ResultSet rstFields = metaData.getColumns(conn.getCatalog(), schema, table, "%");
				ResultSetMetaData rsmd = rstTables.getMetaData();
				colCountMap.put(table, rsmd.getColumnCount());
				Map<String, Field> fMap = new HashMap<String, Field>();
				while(rstFields.next()){
					Field field = new Field();
					String column = rstFields.getString("COLUMN_NAME");
					System.out.println(column);
					field.set_id(rstFields.getString("COLUMN_NAME"));
					field.setField_name(rstFields.getString("COLUMN_NAME"));
					field.setField_type(rstFields.getString("TYPE_NAME"));
					field.setNullable(rstFields.getString("IS_NULLABLE"));
					field.setField_size(rstFields.getInt("COLUMN_SIZE"));
					field.setDescription(rstFields.getString("REMARKS"));
					field.setFieldPos(rstFields.getInt("ORDINAL_POSITION"));
					Map<String, String> langsMap = new HashMap<String, String>();
					for(String lang: langs) {
						langsMap.put(lang, "");
					}
					field.setLabels(langsMap);
					field.setDescriptions(langsMap);
					fMap.put(column, field);
				}
				dbMap.put(table, fMap);
				rstFields.close();
	    	}
	    }
	    rstTables.close();
		// End build dbMap and colCountMap
	    
	    System.out.println(modelMap);
	    System.out.println(dbMap);
	    
	    // Start Update ORDINAL_POSITION (fieldPos) of existing field in model and put fields that does not exists in db in fieldsToRemove 
	    for(Entry<String, Map<String, Field>> model: modelMap.entrySet()) {
	    	String modelTable = model.getKey();
	    	Map<String, Field> modelFieldsMap = model.getValue();
    		List<Field> fieldsToRemoveList = new ArrayList<Field>();
    		List<Field> fieldsToUpdateList = new ArrayList<Field>();
	    	for(Entry<String, Field> field: modelFieldsMap.entrySet()) {
	    		String modelColumn = field.getKey();
	    		Field modelField = field.getValue();
		    	if(dbMap.get(modelTable).get(modelColumn) != null) {
		    		int dbFieldPos = dbMap.get(modelTable).get(modelColumn).getFieldPos();
		    		modelField.setFieldPos(dbFieldPos);
		    		fieldsToUpdateList.add(modelField);
		    		System.out.println(modelColumn + " exists in DB (" + modelField.getFieldPos() + ")");
		    	}
		    	else {
		    		System.out.println(modelColumn + " DOES NOT exists in DB");
		    		fieldsToRemoveList.add(modelField);
		    	}
	    	}
    		fieldsToRemove.put(modelTable, fieldsToRemoveList);
    		fieldsToUpdate.put(modelTable, fieldsToUpdateList);
	    }
	    // End Update ORDINAL_POSITION (fieldPos) of existing field in model and put fields that does not exists in db in fieldsToRemove 
	    
	    // Start Adding field in db that does not exists in model in fieldsToAdd
	    for(Entry<String, Map<String, Field>> db: dbMap.entrySet()) {
	    	String dbTable = db.getKey();
	    	Map<String, Field> dbFieldsMap = db.getValue();
	    	List<Field> fieldsToAddList = new ArrayList<Field>();
	    	for(Entry<String, Field> field: dbFieldsMap.entrySet()) {
	    		String dbColumn = field.getKey();
	    		Field dbField = field.getValue();
	    		if(modelMap.get(dbTable).get(dbColumn) == null) {
	    			fieldsToAddList.add(dbField);
		    		System.out.println(dbColumn + " DOES NOT exists in model (" + dbField.getFieldPos() + ")");
	    		}
	    	}
	    	fieldsToAdd.put(dbTable, fieldsToAddList);
	    }
	    // End Adding field in db that does not exists in model in fieldsToAdd
	    
	    
	    
	    System.out.println(modelMap);
	    System.out.println(dbMap);
	    System.out.println(fieldsToRemove);
	    System.out.println(fieldsToAdd);
	    System.out.println(fieldsToUpdate);
	    System.out.println(customFields);
	    
		for(QuerySubject qs: qss) {
			List<Field> fields = qs.getFields();
			fields.addAll(fieldsToUpdate.get(qs.getTable_name()));
			fields.addAll(fieldsToAdd.get(qs.getTable_name()));
			int fieldPos = colCountMap.get(qs.getTable_name());
			for(Field customField: customFields.get(qs.getTable_name())) {
				customField.setFieldPos(fieldPos++);
				fields.add(customField);
			}
		}

	    System.out.println(qss.size());
	    System.out.println(colCountMap);		
	}

}
