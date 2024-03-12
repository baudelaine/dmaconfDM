package com.dma.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test28 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/Documents/antibia/bug-semi-column/Antibia_POC_V0.2.9-2023-06-27-9-54-41.json");
		Path output = Paths.get("/home/fr054721/Documents/antibia/bug-semi-column/views-semi-column.csv");
//		String selectedQs = "POFinal";
//		String selectedQs = "POLINEFinal";
//		String selectedQs = "ASSETFinal";
		
		
		
		if(!Files.exists(path)) {
			System.err.println("ERROR: No file found !!!");
			System.exit(1);
		}
		
		Map<String, List<QuerySubject>> model = (Map<String, List<QuerySubject>>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
		
		List<QuerySubject> views = (List<QuerySubject>) model.get("views");
		
		System.out.println(views.size());
		
		String delim = "\";\"";
		String lang = "fr";
		
		String header = "\"" + "TABLE_NAME" + delim + "TABLE_TYPE" + delim + "TABLE_LABEL" + delim + "TABLE_DESCRIPTION" + delim +
				"FIELD_ID" + delim  + "FIELD_NAME" + delim + "FIELD_TYPE" + delim + "FIELD_LABEL" + delim + "FIELD_DESCRIPTION" + delim +
				"EXPRESSION" + delim + "HIDDEN" + delim + "ICON" + delim + "ALIAS" + delim + "FOLDER" + delim + "ROLE" + "\"";				
		
		List<String> lines = new ArrayList<String>();
		lines.add(header);
		
		for(QuerySubject view: views) {
			StringBuffer tblBuf = new StringBuffer();
			tblBuf.append("\"" + view.getTable_name() + delim + view.getType());
			String label = "";
			if(view.getDescriptions().containsKey(lang)) {
				label = view.getLabels().get(lang);
			}
			String description = "";
			if(view.getDescriptions().containsKey(lang)) {
				description = view.getDescriptions().get(lang);
			}
			tblBuf.append(delim + label + delim + description.replaceAll(delim, " "));
			String tbl = tblBuf.toString();
			for(Field field: view.getFields()) {
				StringBuffer fldBuf = new StringBuffer();
				fldBuf.append(tbl + delim + field.get_id() + delim + field.getField_name() + delim + field.getField_type());
				String flabel = "";
				if(field.getLabels().containsKey(lang)) {
					flabel = field.getLabels().get(lang);
				}
				String fdescription = null;
				if(field.getLabels().containsKey(lang)) {
					fdescription = field.getDescriptions().get(lang);
				}
				if(fdescription == null) {
					fdescription = "";
				}
				fldBuf.append(delim + flabel + delim + fdescription.replaceAll(delim, " ") + delim + field.getExpression() + delim +
						String.valueOf(field.isHidden()) + delim + field.getIcon() + delim + 
						field.getAlias() + delim + field.getFolder() + delim + field.getRole() + "\"");
				lines.add(fldBuf.toString());
				
			}
		}
		
		Files.deleteIfExists(output);
		
		Files.write(output, lines, StandardCharsets.UTF_8);
		
	}
	

}
