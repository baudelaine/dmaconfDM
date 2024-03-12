package com.dma.web;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test25 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Path modelPath = Paths.get("/home/fr054721/Documents/cda-brest/model.json");
		Connection con = null;
		String schema = "MAXIMO";
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		con = DriverManager.getConnection("jdbc:db2://172.16.186.241:50000/CDABREST", "db2admin", "spcspc");
		con.createStatement().execute("set schema=" + schema);
		DatabaseMetaData metaData = con.getMetaData();
		
		
		@SuppressWarnings("unchecked")
		Map<String, List<QuerySubject>> model = (Map<String, List<QuerySubject>>) Tools.fromJSON(modelPath.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
		
		List<QuerySubject> qss = model.get("querySubjects");
		
		System.out.println(qss.size());
		
		Map<String, List<String>> fldMap = new HashMap<String, List<String>>();
		Set<String> tblSet = new HashSet<String>();
		
		for(QuerySubject qs: qss) {
			String table = qs.getTable_name();
			tblSet.add(table);
		}
		
		
		System.out.println(tblSet);
		System.out.println(tblSet.size());
		System.out.println(tblSet.contains("WORKLOG"));

		for(QuerySubject qs: qss) {
			System.out.println(qs._id + " size = " + qs.getFields().size());
		}		
		
		for(String tbl: tblSet) {
			
			ResultSet rstFields = metaData.getColumns(con.getCatalog(), schema, tbl, "%");
			List<String> fldSet = new ArrayList<String>();
			while(rstFields.next()){
				fldSet.add(rstFields.getString("COLUMN_NAME"));
			}
			rstFields.close();
			if(fldSet.size() > 0) {
				fldMap.put(tbl, fldSet);
			}
		}
		
		System.out.println(Tools.toJSON(fldMap));		
		
		System.out.println("************ After update **************");
		
		Map<String, List<String>> toRemoveMap = new HashMap<String, List<String>>();
		
		for(QuerySubject qs: qss) {
			if(!tblSet.contains(qs.getTable_name())) {
				System.out.println(qs.getTable_name() + " does not exists");
				qs.setTableExists(false);
			}
			else {
				List<String> fldSet = fldMap.get(qs.getTable_name());
				List<String> fldToRemove = new ArrayList<String>();
				for(Field fld: qs.getFields()) {
					if(!fldSet.contains(fld.getField_name())) {
//						System.out.println(qs.getTable_name() + "." + fld.getField_name() + " does not exists");
						if(!fld.isCustom()) {
							fldToRemove.add(fld.getField_name());
						}
					}
				}
				toRemoveMap.put(qs.getTable_name(), fldToRemove);
				
			}
		}
		
//		System.out.println(Tools.toJSON(toRemoveMap));
		
		
		for(QuerySubject qs: qss) {
			if(toRemoveMap.containsKey(qs.getTable_name())) {
				System.out.println(qs.getTable_name());
			}
			System.out.println(qs._id + " size = " + qs.getFields().size());
		}		

	}

}
