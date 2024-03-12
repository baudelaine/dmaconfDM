package com.dma.web;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test10 {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/Documents/BDXMET_V2_V0.9.7.5-2023-03-20-12-1-52.json");
		
		if(Files.exists(path)) {
			Map<String, List<QuerySubject>> model = (Map<String, List<QuerySubject>>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
			List<QuerySubject> qss = model.get("querySubjects");
			System.out.println("QSS_COUNT=" + qss.size());
			int fin = 0;
			int ref = 0;
			int dimension = 0;
			int measure = 0;
			int custom = 0;
			for(QuerySubject qs: qss) {
				if(qs.getRelations().size() > 0) {
					for(Relation rel: qs.getRelations()) {
						if(rel.isFin()) {
							fin++;
						}
						if(rel.isRef()) {
							ref++;
						}
					}
				}
				if(! qs.getFields().isEmpty()) {
					for(Field field: qs.getFields()) {
						if(! field.getDimensions().isEmpty()) {
							dimension += field.getDimensions().size();
						}
						if(field.measure != "") {
							measure++;
						}
						if(field.isCustom()) {
							custom++;
						}
					}
				}
			}
			System.out.println("FIN_COUNT=" + fin);
			System.out.println("REF_COUNT=" + ref);
			System.out.println("DIMENSION_COUNT=" + dimension);
			System.out.println("MEASURE_COUNT=" + measure);
			System.out.println("CUSTOM_COUNT=" + custom);
		}
	}

}
