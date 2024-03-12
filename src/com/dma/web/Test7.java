package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Test7 {

	public static void main(String[] args){
		// TODO Auto-generated method stub

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rst = null;
		String schema = "DB2INST1";
		
		try {
//			Class.forName("com.ibm.db2.jcc.DB2Driver");
//			con = DriverManager.getConnection("jdbc:db2://localhost:50000/SAMPLE", "db2inst1", "spcspc");
//			con.createStatement().execute("set schema=" + schema);
//			DatabaseMetaData metaData = con.getMetaData();
//			
//			Set<String> aliases = new HashSet<String>();
//			String aliasesQuery = "SELECT base_tabname, tabname FROM syscat.tables WHERE type = 'A' AND owner = '" + schema.toUpperCase() + "'";
//			
//			stmt = con.getMetaData().getConnection().prepareStatement(aliasesQuery);
//            rst = stmt.executeQuery();
//            while (rst.next()) {
//                String alias = rst.getString(2);
//                aliases.add(alias);
//            }						
//			
//            System.out.println(Tools.toJSON(aliases));
//			
//            List<String> tables = new ArrayList<String>();
//            tables = Arrays.asList(
//					"PROJECT", 
//					"DEPARTMENT",
//					"EMPLOYEE"
//					);
			
//			String[] types = {"TABLE"};
//		    rst = metaData.getTables(con.getCatalog(), schema, "%", types);	
//		    
//		    while (rst.next()) {
//		    	tables.add(rst.getString("TABLE_NAME"));
//		    }
//			if(rst != null) {rst.close();}
//			
//			List<String> keys = new ArrayList<String>();
//			Path path = Paths.get("/opt/wks/dmagui/relations.csv");
//			FileWriter fw = new FileWriter(path.toFile());
//			String newLine = System.getProperty("line.separator");
//			String header = "FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME";
//			fw.write(header + newLine);
//
//			for(String table: tables){
//				rst = metaData.getImportedKeys(con.getCatalog(), schema, table);
//				while(rst.next()){
//					StringBuffer key = new StringBuffer();
//					
//					key.append(rst.getString("FK_NAME") + ";");
//					key.append(rst.getString("PK_NAME") + ";");
//					key.append(rst.getString("FKTABLE_NAME") + ";");
//					key.append(rst.getString("PKTABLE_NAME") + ";");
//					key.append(rst.getShort("KEY_SEQ") + ";");
//					key.append(rst.getString("FKCOLUMN_NAME") + ";");
//					key.append(rst.getString("PKCOLUMN_NAME") + newLine);
//					
//					String pktable_name = rst.getString("PKTABLE_NAME");
//					if(!aliases.contains(pktable_name)) {
//						keys.add(key.toString());
//						fw.write(key.toString());
//					}
//				}
//				rst.close();
//			}
//			if(rst != null){
//				rst.close();
//			}
//			if(fw != null) {
//				fw.close();
//			}
//			if(stmt != null){
//				stmt.close();
//			}
//			if(con != null){
//				con.close();
//			}
//			
//			System.out.println(Tools.toJSON(keys));

			Path path = Paths.get("/tmp/relation.csv");
			
			// Load the driver.
			Class.forName("org.relique.jdbc.csv.CsvDriver");

			// Create a connection using the directory containing the file(s)
			Properties props = new java.util.Properties();
			props.put("separator",";");
//			props.put("suppressHeaders","true"); 
			Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + path.getParent().toString(), props);

			// Create a Statement object to execute the query with.
			String sql = "SELECT * FROM relation where FKTABLE_NAME = 'S_SAMPLE'";
			PreparedStatement stm = conn.prepareStatement(sql);

			// Query the table. The name of the table is the name of the file without ".csv"
			ResultSet results = stm.executeQuery();
			while(results.next()) {
				System.out.println(results.getString("FK_NAME"));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
		}		
		
	}

}
