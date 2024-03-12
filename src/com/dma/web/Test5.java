package com.dma.web;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test5 {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		String a = "SELECT * FROM $TABLE WHERE $TABLE.$FIELD IS NOT NULL AND $TABLE.$FIELD != '0' AND $TABLE.STX_AT = 0";
		System.out.println(a.replaceAll("\\$TABLE", "WORKORDER"));
		
		Path path = Paths.get("/home/dma/dma/p0/dbmd.json");
		
		System.out.println(path.getFileName().toString());
		
		if(Files.exists(path)){
			@SuppressWarnings("unchecked")
			Map<String, DBMDTable> dbmd = (Map<String, DBMDTable>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, DBMDTable>>(){});
			System.out.println(Tools.toJSON(dbmd));
			
			for(Entry<String, DBMDTable> e: dbmd.entrySet()) {
				DBMDTable table = e.getValue();
				table.setTable_description("");
				table.setTable_remarks("");
				Map<String, DBMDColumn> columns = table.getColumns();
				for(Entry<String, DBMDColumn> f: columns.entrySet()){
					DBMDColumn column = f.getValue();
					column.setColumn_description("");
					column.setColumn_remarks("");
				}
			}

			System.out.println(Tools.toJSON(dbmd));
			
		}
		
		path = Paths.get("/home/dma/dma/p0/relation.json");
    	if(Files.exists(path)) {
			@SuppressWarnings("unchecked")
			Map<String, String> queries = (Map<String, String>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, String>>(){});
			if(queries != null) {
				System.out.println("FKQuery=" + queries.get("FKQuery"));
			}
    	}		
		
	}

}
