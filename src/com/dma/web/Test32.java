package com.dma.web;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

public class Test32 {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
    	ResultSet rst = null;
    	PreparedStatement stmt = null;
    	Connection csvCon = null;
    	
    	Path prj = Paths.get("/home/fr054721/Downloads/antibia");
    	
		if(Files.exists(Paths.get(prj + "/relationExp.csv"))) {
			Properties props = new java.util.Properties();
			props.put("separator",";");
			Class.forName("org.relique.jdbc.csv.CsvDriver");
			csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
		}

		String table = "Pompabs";
		String type = "Final";
		String alias = "Pompabs";
//		String sql = "SELECT DISTINCT(FK_NAME) FROM relation where FKTABLE_NAME = '" + tableName + "'";
//		String sql = "SELECT FK_NAME FROM relation where FKTABLE_NAME = '" + tableName + "'";
//		String sql = "SELECT DISTINCT(FK_NAME) FROM relation where PKTABLE_NAME = '" + tableName + "'";
//		String sql = "SELECT FK_NAME FROM relation where PKTABLE_NAME = '" + tableName + "'";
		String sql = "SELECT * FROM relationExp where PKTABLE_NAME = '" + table + "'";

//		String sql = "SELECT KEY_SEQ FROM relation where PKTABLE_NAME = '" + tableName + "'";
//		String sql = "SELECT distinct(PKCOLUMN_NAME) FROM relation where PKTABLE_NAME = '" + tableName + "'";
		System.out.println("sql=" + sql);
		stmt = csvCon.prepareStatement(sql);
		rst = stmt.executeQuery();
		
    	while(rst.next()){
//    		String FKName = rst.getString("FK_NAME");
//    		System.out.println("FKName=" + FKName);
//    		JOIN_NAME;FKTABLE_NAME;PKTABLE_NAME;RELATION_EXPRESSION
    		String relExp = rst.getString("RELATION_EXPRESSION");
    		String fkTable = rst.getString("FKTABLE_NAME").trim();
    		String pkTable = rst.getString("PKTABLE_NAME").trim();
    		System.out.println("Before update=" + relExp);
    		
	    	Relation relation = new Relation();

    		try {
    			
	    		Pattern p = Pattern.compile("(\\w+)\\.(\\w+)");
	    		Matcher m = p.matcher(relExp);
        		short matchCount = 1;
        		Seq seq = null;
        		String fldR = null;
        		String fldL = null;
    		    String expR = null;
    		    String expL = null;
	    		while (m.find()) {
	//    		    System.out.println("m.group(1)=" + m.group(1));
	//    		    System.out.println("m.group(2)=" + m.group(2));
	    		    String tableName = m.group(1);
	    		    String field = m.group(2);
	    		    if(matchCount % 2 != 0) {
			        	seq = new Seq();
	    		    }
	    		    if (tableName.contentEquals(table)) {
	    		    	expR = ("[" + type.toUpperCase() + "]." + "[" + alias + "].[" + field + "]");
	    		    	fldR = tableName + "." + field;
			        	seq.setPktable_name(tableName);
			        	seq.setPkcolumn_name(field);
			        	if(matchCount > 1) {
			        		seq.setKey_seq((short) (matchCount -1));
			        	}
			        	else {
			        		seq.setKey_seq(matchCount);
			        	}
	    		    }
	    		    else {
	    		    	expL = ("[" + tableName + "].[" + field + "]");
	    		    	fldL = tableName + "." + field;
			        	seq.setTable_name(tableName);
			        	seq.setColumn_name(field);
	    		    }
	    		    System.out.println("expR=" + expR);
	    		    System.out.println("expL=" + expL);
	    		    if(expL != null && expR != null) {
		    		    relExp = relExp.replaceFirst(fldR, expL);
		    		    relExp = relExp.replaceFirst(fldL, expR);
			        	System.out.println("relExp=" + relExp);
			        	System.out.println(matchCount);
			        	relation.addSeq(seq);
	    		    }
		        	matchCount++;
    		    
	    		}
    			
    			
//	    		relExp = relExp.replaceAll(fkTable, "[" + type.toUpperCase() + "].[" + alias + "]");
//	    		relExp = relExp.replaceAll(pkTable, "[" + pkTable + "]");
//	    		Pattern p = Pattern.compile("([^\\]]+]\\.)(\\w+)");
////	    		Pattern p = Pattern.compile("([^\\]]+]\\.)([^\\=]+)");
//	    		Matcher m = p.matcher(relExp);
//	    		while (m.find()) {
////	    		    System.out.println("m.group(1)=" + m.group(1));
////	    		    System.out.println("m.group(2)=" + m.group(2));
//	    		    String field = m.group(2);
//	    		    relExp = relExp.replaceAll(field, "[" + field + "]");
//	    		}
//	    		relExp = relExp.replaceAll("\\[\\[", "[");
//	    		relExp = relExp.replaceAll("\\]\\]", "]");
	    		
//	    		p = Pattern.compile("\\[([^\\]]+)\\]");
//	    		m = p.matcher(relExp);
//	    	    while (m.find()) {
////	    		    System.out.println("m.group(1)=" + m.group(1));
////	    		    System.out.println("m.group(2)=" + m.group(2));
//	    		    String field = m.group(1);
//	    		    String fixedField = StringUtils.remove(field, "[");
////	    		    System.out.println("fixedField=" + fixedField);
//	    		    relExp = StringUtils.replace(relExp, field, fixedField);
//	    		}
	    		
    		}
    		catch(PatternSyntaxException e) {
    			continue;
    		}
    		System.out.println(relExp);
    		System.out.println(Tools.toJSON(relation.getSeqs()));
    	}
        if(rst != null) {
        	rst.close();
        	rst = null;
        }
		if(stmt != null) {
			stmt.close();
			stmt = null;
		}
		
		if(csvCon != null) {
			csvCon.close();
			csvCon = null;
		}
		
//		String s = "[FINAL].[Pompabs_ALIAS].[CLE]=[Na_abs].[A_MOTIF] AND [FINAL].[Pompabs_ALIAS].[CLE_FILI]=[Na_abs].[A_FILI]";
//		Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
//		Matcher m = p.matcher(s);
//	    System.out.println("s=" + s);
//	    while (m.find()) {
//		    System.out.println("m.group(1)=" + m.group(1));
////		    System.out.println("m.group(2)=" + m.group(2));
//		    String field = m.group(1);
//		    String fixedField = StringUtils.remove(field, "[");
//		    System.out.println("fixedField=" + fixedField);
//		    s = StringUtils.replace(s, field, fixedField);
//		}
//	    System.out.println("s=" + s);
		
		
	}

}
