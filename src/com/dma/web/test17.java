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

public class test17 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/dmaconf/model-decoche.json");
//		String selectedQs = "POFinal";
//		String selectedQs = "POLINEFinal";
		String selectedQs = "ASSETFinal";
		
		
		
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
			
			
			if (qs.getValue().getType().equalsIgnoreCase("Final")){
				
				String qsAlias = qs.getValue().getTable_alias();  // table de gauche, celle ou tu es actuellement
				String gDirName = ""; // prefix qu'on cherche, il vaut cher
				String qsFinalName = qs.getValue().getTable_alias();   //CONSTANTE, nom du QS final auquel l'arbre ref est accroché, le tronc, on peut le connaitre à tout moment de f1
//				String table = qs.getValue().getTable_name();
				String qSleftType = "Final";
				

				if(qs.getValue()._id.equalsIgnoreCase(selectedQs)) {
					System.out.println("[FINAL].[" + qsFinalName + "]");
					for(Field field: qs.getValue().getFields()){
//						System.out.println("[FINAL].[" + qsFinalName + "]." + "[" + field.getField_name() + "]");
					}
				}
				
				Map<String, Integer> recurseCount = new HashMap<String, Integer>();
				
				for(Entry<String, QuerySubject> rcqs: qss.entrySet()){
		        	recurseCount.put(rcqs.getValue().getTable_alias(), 0);
		        }
				
				
				recurse0(qsAlias, gDirName, qsFinalName, qSleftType, qss, recurseCount, selectedQs);
				
				
			}
			
		}
		
	}

	private static void recurse0(String qsAlias, String gDirName, String qsFinalName, String qSleftType,
			Map<String, QuerySubject> qss, Map<String, Integer> recurseCount, String selectedQs) {
		// TODO Auto-generated method stub
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		String gDirNameCurrent = "";
		QuerySubject query_subject;
		
		if (!qSleftType.equals("Final")) {
			
			query_subject = qss.get(qsAlias + qSleftType);
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		}
		
		query_subject = qss.get(qsAlias + qSleftType);
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()) { 
		
				String pkAlias = rel.getPktable_alias();
				
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					gDirNameCurrent = gDirName + "." + pkAlias;
				}
				else{
					gDirNameCurrent = gDirName + "." + rel.getAbove();
				}					

				System.out.println(query_subject.get_id());
				
				if((pkAlias + "Ref").equalsIgnoreCase((selectedQs))) {
					System.out.println("[REF].[" + qsFinalName + gDirNameCurrent + "]");
					for(Field field: qss.get(pkAlias + "Ref").getFields()){
//						System.out.println("[REF].[" + qsFinalName + "].[" + gDirNameCurrent + "." + field.getField_name() +"]");
						
					}
				}

				recurse0(pkAlias, gDirNameCurrent, qsFinalName, "Ref" ,qss, copyRecurseCount, selectedQs);	
				
			}
		}
		
	}

}
