package com.dma.action;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dma.web.Tools;
import com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException;

public class Test8 {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String schema = "MAXIMO";
		String lang = "fr";
		
		try{
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		}
		catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace(System.err);
		}
		
		System.out.println("DB2 driver loaded.");
		Connection conn = DriverManager.getConnection("jdbc:db2://192.168.1.75:50000/ENNOVIA", "db2inst1", "spcspc");
		PreparedStatement stmt = conn.prepareStatement("select current date from sysibm.sysdummy1");
		ResultSet rst = stmt.executeQuery();
		rst.next();
		System.out.println(rst.getString(1));
		rst.close();
		if(stmt != null) {stmt.close();}
		
	    DatabaseMetaData metaData = conn.getMetaData();
	    //String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
	    String[] types = {"TABLE"};
	    rst = metaData.getTables(conn.getCatalog(), schema, "WORKORDER", types);	

		Map<String, Object> recCount = new HashMap<String, Object>();
		
	    while (rst.next()) {

	    	String table_name = (rst.getString("TABLE_NAME"));
	    	System.out.println(table_name);

		    ResultSet rst0 = metaData.getColumns(conn.getCatalog(), schema, table_name, "%");
	    	StringBuffer sb = new StringBuffer("select ");
		    while(rst0.next()){
		    	String column_name =  rst0.getString("COLUMN_NAME");
		    	System.out.println("-- " + column_name);
		    	sb.append("count(" + column_name + ") as " + column_name + ", ");
		    	
		    }
		    if(rst0 != null){rst0.close();}
		    String head = sb.toString();
		    head = head.substring(0, head.lastIndexOf(","));
		    String sql = head + " from " + table_name;
		    System.out.println(sql);
		    PreparedStatement stmt0 = conn.prepareStatement(sql);
			rst0 = stmt0.executeQuery();
			ResultSetMetaData rsmd = rst0.getMetaData();
			int colCount = rsmd.getColumnCount();

			List<String> column_names = new ArrayList<String>();
			
			for(int colid = 1; colid <= colCount; colid++){
				column_names.add(rsmd.getColumnLabel(colid));
			}
			
			while(rst0.next()){
				Map<String, Object> rec = new HashMap<String, Object>();
				for(int colid = 1; colid <= colCount; colid++){
					rec.put(column_names.get(colid -1), rst0.getObject(colid));
				}
				recCount.put(table_name, rec);
			}
			rst0.close();
			stmt0.close();		
			
		
	    }
	    if(rst != null){rst.close();}

	    System.out.println(Tools.toJSON(recCount));
	    
	    if(recCount.containsKey("PROJECT")) {
	    	Map<String, Long> obj = (Map<String, Long>) recCount.get("PROJECT");
	    	if(obj.containsKey("MAJPROJ")) {
	    		System.out.println(obj.get("MAJPROJ"));
	    	}
	    }

	}

}
