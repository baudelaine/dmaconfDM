package com.dma.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test19 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Path qssPath = Paths.get("/home/dma/dma/cda2/qsFromXML.json");
		Path modelPath = Paths.get("/home/dma/dma/cda2/models/cda-migrated.json");
		Path output = Paths.get("/tmp/output.json");
		Path modelUpdatedPath = Paths.get("/home/dma/dma/cda2/models/cda-updated.json");
		
		List<QuerySubject> model = (List<QuerySubject>) Tools.fromJSON(modelPath.toFile(), new TypeReference<List<QuerySubject>>(){}); 

		Map<String, Map<String, Object>> tMap = new HashMap<String, Map<String, Object>>();
		
		for(QuerySubject qs: model) {
			String table = qs.getTable_name();
			List<Field> fields = qs.getFields();
			Map<String, Object> fMap = new HashMap<String, Object>();
			for(Field field: fields) {
				String fieldName = field.getField_name();
				fMap.put(fieldName, null);
			}
			tMap.put(table, fMap);
		}
		
//		Files.write(output,Tools.toJSON(tMap).getBytes());
//		output.toFile().setReadable(true, false);
//		output.toFile().setWritable(true, false);
		
		
//		if(tMap.get("PO").containsKey("CHANGEBY")) {
//			System.out.println("...");
//		}
		
		
		Map<String, QuerySubject> qss = (Map<String, QuerySubject>) Tools.fromJSON(qssPath.toFile(), new TypeReference<Map<String, QuerySubject>>(){});
		
		Map<String, List<Field>> newFields = new HashMap<String, List<Field>>();
		
		for(Entry<String, QuerySubject> qs: qss.entrySet()) {
			String table = qs.getKey();
			if(tMap.containsKey(table)){
				List<Field> fields = qs.getValue().getFields();
				for(Field field: fields) {
					if(!tMap.get(table).containsKey(field.getField_name())) {

						if(!newFields.containsKey(table)) {
							newFields.put(table, new ArrayList<Field>());
						}
						
						Field newField = new Field();
						newField.set_id(field.getField_name() + field.getField_type());
						newField.setField_name(field.getField_name());
						newField.setField_type(field.getField_type());
						
						newFields.get(table).add(newField);
					}
				}
			}
		}
		
		System.out.println(Tools.toJSON(newFields));
		
		Map<String, List<Field>> result = new HashMap<String, List<Field>>();
		
		for(QuerySubject qs: model) {
			String table = qs.getTable_name();
			List<Field> fields = qs.getFields();
			if(newFields.containsKey(table)) {
				result.put(qs.get_id(), newFields.get(table));
				qs.getFields().addAll(newFields.get(table));
			}
		}
		
		Files.write(output,Tools.toJSON(result).getBytes());
		modelUpdatedPath.toFile().setReadable(true, false);
		modelUpdatedPath.toFile().setWritable(true, false);

		
		
	}

}
