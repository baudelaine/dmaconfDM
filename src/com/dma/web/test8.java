package com.dma.web;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class test8 {

	public static void main(String[] args){
		// TODO Auto-generated method stub

		Map<String, Object> result = new HashMap<String, Object>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rst = null;
		String schema = "DB2INST1";
		
		try {
		
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			con = DriverManager.getConnection("jdbc:db2://localhost:50000/SAMPLE", "db2inst1", "spcspc");
			con.createStatement().execute("set schema=" + schema);
			DatabaseMetaData metaData = con.getMetaData();
			
			
			Set<String> aliases = new HashSet<String>();
			String aliasesQuery = "SELECT base_tabname, tabname FROM syscat.tables WHERE type = 'A' AND owner = '" + schema.toUpperCase() + "'";
			
			stmt = con.getMetaData().getConnection().createStatement();
	        rst = stmt.executeQuery(aliasesQuery);
	        while (rst.next()) {
	            aliases.add(rst.getString(2));
	        }						
			if(rst != null){rst.close();}
			if(stmt != null){stmt.close();}
			
	        result.put("ALIASES", aliases);
	        
	        List<String> tables = new ArrayList<String>();
	//        tables = Arrays.asList(
	//				"PROJECT", 
	//				"DEPARTMENT",
	//				"EMPLOYEE"
	//				);
			
			String[] types = {"TABLE"};
		    rst = metaData.getTables(con.getCatalog(), schema, "%", types);	
		    
		    while (rst.next()) {
		    	tables.add(rst.getString("TABLE_NAME"));
		    }
			if(rst != null){rst.close();}
			
			result.put("TABLES", tables);
			
			Map<String, String> tlMap = new HashMap<String, String>();
			Map<String, String> tdMap = new HashMap<String, String>();
			Map<String, Map<String, String>> clMap = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> cdMap = new HashMap<String, Map<String, String>>();
			
			String tableInClause = "('" + StringUtils.join(tables.iterator(), "','") + "')";
		
			Path prj = Paths.get("/home/dma/dma/p0");
			
			Properties props = new java.util.Properties();
			props.put("separator",";");
			Connection csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
			Statement csvStmt = csvCon.createStatement();
			ResultSet csvRst = null;
	
			if(Files.exists(Paths.get(prj + "/tableLabel.csv"))){
				csvRst = csvStmt.executeQuery("SELECT * FROM tableLabel where TABLE_NAME in " + tableInClause);
				while(csvRst.next()){
					tlMap.put(csvRst.getString("Table_Name").toUpperCase(), csvRst.getString("Table_Label"));
				}
				csvRst.close();
				result.put("TL", tlMap);
			}
			
			if(Files.exists(Paths.get(prj + "/tableDescription.csv"))){
				csvRst = csvStmt.executeQuery("SELECT * FROM tableDescription where TABLE_NAME in " + tableInClause);
				while(csvRst.next()){
					tdMap.put(csvRst.getString("Table_Name").toUpperCase(), csvRst.getString("Table_Description"));
				}
				csvRst.close();
				result.put("TD", tdMap);
			}
			
			for(String table: tables) {
				
				List<String> fields = new ArrayList<String>();
				
				metaData = con.getMetaData();
				rst = metaData.getColumns(con.getCatalog(), schema, table, "%");
				while(rst.next()){
					fields.add(rst.getString("COLUMN_NAME"));
				}
				rst.close();
	
				String columnInClause = "('" + StringUtils.join(fields.iterator(), "','") + "')";
	
				if(Files.exists(Paths.get(prj + "/columnLabel.csv"))){
					Map<String, String> cols = new HashMap<String, String>();
					String sql = "SELECT * FROM columnLabel where TABLE_NAME = '" + table + "' and COLUMN_NAME in " + columnInClause;
					System.out.println(sql);
					csvRst = csvStmt.executeQuery(sql);
					while(csvRst.next()){
						cols.put(csvRst.getString("Column_Name").toUpperCase(), csvRst.getString("Column_Label"));
					}
					csvRst.close();
					clMap.put(table.toUpperCase(), cols);
					result.put("CL", clMap);
				}
				
				if(Files.exists(Paths.get(prj + "/columnDescription.csv"))){
					Map<String, String> cols = new HashMap<String, String>();
					csvRst = csvStmt.executeQuery("SELECT * FROM columnDescription where TABLE_NAME = '" + table + "' and COLUMN_NAME in " + columnInClause);
					while(csvRst.next()){
						cols.put(csvRst.getString("Column_Name").toUpperCase(), csvRst.getString("Column_Description"));
					}
					csvRst.close();
					cdMap.put(table.toUpperCase(), cols);
					result.put("CD", cdMap);
				}
				
			}
			
			csvStmt.close();
			if(csvCon != null){csvCon.close();}
			
			result.put("STATUS", "OK");
			
			System.out.println(Tools.toJSON(result));
			
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
		}
		
	}

}
