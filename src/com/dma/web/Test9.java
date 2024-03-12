package com.dma.web;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test9 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rst = null;
		String schema = "DB2INST1";
		
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			con = DriverManager.getConnection("jdbc:db2://localhost:50000/SAMPLE", "db2inst1", "spcspc");
			con.createStatement().execute("set schema=" + schema);
			DatabaseMetaData metaData = con.getMetaData();
			
			Set<String> aliases = new HashSet<String>();
			String aliasesQuery = "SELECT base_tabname, tabname FROM syscat.tables WHERE type = 'A' AND owner = '" + schema.toUpperCase() + "'";
			
			stmt = con.getMetaData().getConnection().prepareStatement(aliasesQuery);
            rst = stmt.executeQuery();
            while (rst.next()) {
                String alias = rst.getString(2);
                aliases.add(alias);
            }						
			
            System.out.println(Tools.toJSON(aliases));
			
            List<String> tables = new ArrayList<String>();
//            tables = Arrays.asList(
//					"PROJECT", 
//					"DEPARTMENT",
//					"EMPLOYEE"
//					);
			
			String[] types = {"TABLE"};
		    rst = metaData.getTables(con.getCatalog(), schema, "%", types);	
		    
		    while (rst.next()) {
		    	tables.add(rst.getString("TABLE_NAME"));
		    }
			if(rst != null) {rst.close();}
			
			List<String> keys = new ArrayList<String>();
			Path path = Paths.get("/opt/wks/dmagui/tmp");
			FileWriter fw = new FileWriter(path.toFile());
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BufferedOutputStream buf = new BufferedOutputStream(bos);
			
			
			String newLine = System.getProperty("line.separator");
			String header = "FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME";
			
			bos.write((header + newLine).getBytes());
			
//			fw.write(header + newLine);

			for(String table: tables){
				rst = metaData.getImportedKeys(con.getCatalog(), schema, table);
				while(rst.next()){
					StringBuffer key = new StringBuffer();
					
					key.append(rst.getString("FK_NAME") + ";");
					key.append(rst.getString("PK_NAME") + ";");
					key.append(rst.getString("FKTABLE_NAME") + ";");
					key.append(rst.getString("PKTABLE_NAME") + ";");
					key.append(rst.getShort("KEY_SEQ") + ";");
					key.append(rst.getString("FKCOLUMN_NAME") + ";");
					key.append(rst.getString("PKCOLUMN_NAME") + newLine);
					
					String pktable_name = rst.getString("PKTABLE_NAME");
					if(!aliases.contains(pktable_name)) {
						keys.add(key.toString());
//						fw.write(key.toString());
						bos.write(key.toString().getBytes());
					}
				}
				rst.close();
				buf.flush();
				
			}
			buf.flush();
			int size = bos.toByteArray().length;
			Files.write(path, bos.toByteArray());
			
			if(rst != null){
				rst.close();
			}
			if(fw != null) {
				fw.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
			
			System.out.println(Tools.toJSON(keys));
			
			

		} catch (ClassNotFoundException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
		}				

	}

}
