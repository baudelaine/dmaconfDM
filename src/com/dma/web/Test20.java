package com.dma.web;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test20 {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Path path = Paths.get("/home/fr054721/dmaconf/model-decoche.json");
//		String selectedQs = "POFinal";
//		String selectedQs = "POLINEFinal";
		String selectedQs = "WO_LOCATIONSRef";
//		String selectedQs = "PERSONRef";
//		String selectedQs = "RFO_NIV3Ref";
		
		
		
		if(!Files.exists(path)) {
			System.err.println("ERROR: No file found !!!");
			System.exit(1);
		}
		
		List<QuerySubject> qssList = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
		System.out.println(qssList.size());
		
		Map<String, QuerySubject> qss = new HashMap<String, QuerySubject>(); 
		
		for(QuerySubject qs: qssList) {
			qss.put(qs.get_id(), qs);
		}

		for(Entry<String, QuerySubject> qs: qss.entrySet()){
			
			
//			if (qs.getValue().get_id().equalsIgnoreCase(selectedQs)){
				
				System.out.println(qs.getValue().get_id());
				recurse(qs.getValue(), qss, "", "");
//			}
			
		}
	}
	

	public static void recurse(QuerySubject qs, Map<String, QuerySubject> qss, String dir, String selectedQs) {

		String refDir = "";
		
		List<Relation> rels = qs.getRelations();
		for(Relation rel: rels) {
			String pkAlias = rel.getPktable_alias();
			if(rel.isFin()) {
				System.out.println(selectedQs + "." + pkAlias + "Final");
				qs = qss.get(rel.getPktable_alias() + "Final");
			}
			if(rel.isRef()) {
				
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					refDir = dir + "." + pkAlias;
				}
				else{
					refDir = dir + "." + rel.getAbove();
				}					
				

//				if((pkAlias + "Ref").equalsIgnoreCase((selectedQs))) {
					System.out.println(selectedQs + refDir + "Ref");
					qs = qss.get(rel.getPktable_alias() + "Ref");
					recurse(qs, qss, refDir, "");
//				}
				
				refDir = dir + "." + pkAlias;
			}

		}
		
	}
	
}
