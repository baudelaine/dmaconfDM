package com.dma.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test11 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Path input = Paths.get("/home/fr054721/dmagui/views.json");
		String delim = ";";
		String lang = "fr"; 
		String header = "TABLE_NAME" + delim + "TABLE_TYPE" + delim + "TABLE_LABEL" + delim + "TABLE_DESCRIPTION" + delim +
				"FIELD_NAME" + delim + "FIELD_TYPE" + delim + "FIELD_LABEL" + delim + "FIELD_DESCRIPTION" + delim +
				"EXPRESSION" + delim + "HIDDEN" + delim + "ICON" + delim + "ALIAS" + delim + "FOLDER" + delim + "ROLE";
		
		if(Files.exists(input)) {
			
			@SuppressWarnings("unchecked")
			List<QuerySubject> views = (List<QuerySubject>) Tools.fromJSON(input.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			System.out.println(views.size());
			
			List<String> lines = new ArrayList<String>();
			lines.add(header);
			
			for(QuerySubject view: views) {
				StringBuffer tblBuf = new StringBuffer();
				tblBuf.append(view.getTable_name() + delim + view.getType());
				String label = "";
				if(view.getDescriptions().containsKey(lang)) {
					label = view.getLabels().get(lang);
				}
				String description = "";
				if(view.getDescriptions().containsKey(lang)) {
					description = view.getDescriptions().get(lang);
				}
				tblBuf.append(delim + label + delim + description);
				String tbl = tblBuf.toString();
				for(Field field: view.getFields()) {
					StringBuffer fldBuf = new StringBuffer();
					fldBuf.append(tbl + delim + field.getField_name() + delim + field.getField_type());
					String flabel = "";
					if(field.getLabels().containsKey(lang)) {
						flabel = field.getLabels().get(lang);
					}
					String fdescription = "";
					if(field.getLabels().containsKey(lang)) {
						fdescription = field.getDescriptions().get(lang);
					}
					fldBuf.append(delim + flabel + delim + fdescription + delim + field.getExpression() + delim +
							String.valueOf(field.isHidden()) + delim + field.getIcon() + delim + 
							field.getAlias() + delim + field.getFolder() + delim + field.getRole());
					lines.add(fldBuf.toString());
					
				}
			}
			
			Path output = Paths.get("/home/fr054721/dmagui/views.csv");
			
			Files.deleteIfExists(output);
			
			Files.write(output, lines, StandardCharsets.UTF_8);
		}
		
	}

}
