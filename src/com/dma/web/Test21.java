package com.dma.web;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class Test21 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection con = null;
		ResultSet rstTables = null;
		String schema = "MAXIMO";
		String target = "WORKORDER";
		
		try {
		
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			con = DriverManager.getConnection("jdbc:db2://172.16.186.241:50000/CDA3", "db2admin", "spcspc");
			con.createStatement().execute("set schema=" + schema);
			DatabaseMetaData metaData = con.getMetaData();
			
		    String[] types = {"TABLE"};
		    rstTables = metaData.getTables(con.getCatalog(), schema, "%", types);	
		    
		    while (rstTables.next()) {
		    	System.out.println(rstTables.getString("TABLE_NAME"));
		    	if(rstTables.getString("TABLE_NAME").equalsIgnoreCase(target)) {
					ResultSet rstFields = metaData.getColumns(con.getCatalog(), schema, rstTables.getString("TABLE_NAME"), "%");
					while(rstFields.next()){
						System.out.println("*****" + rstFields.getString("COLUMN_NAME") + " - " + rstFields.getString("TYPE_NAME") +
								" - " + rstFields.getInt("COLUMN_SIZE") + " - " + rstFields.getString("IS_NULLABLE") + " - " + rstFields.getString("REMARKS"));
						
						
					}
					rstFields.close();
		    	}

		    }		    
		    
		    rstTables.close();
			
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
		}				
		
	}

}
