package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/GetQuerySubjects")
public class GetQuerySubjectsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	Connection con = null;
	DatabaseMetaData metaData = null;
	String schema = "";
	String table = "";
	String alias = "";
	String type = "";
	String qs_id = "";
	String r_id = "";
	String linker_id = "";
	String language = "";
	boolean withRecCount = false;
	boolean relationCount = false;
	boolean importLabel = false;
	long qs_recCount = 0L;
	Map<String, DBMDTable> dbmd = null;
	Map<String, String> tableAliases = null;
	String FKQuery = "";
	Path prj = null;
	String relationMode = "DB";
	Map<String, QuerySubject> qsFromXML = new HashMap<String, QuerySubject>();
	Project project = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetQuerySubjectsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("CLIENT", request.getRemoteAddr() + ":" + request.getRemotePort());
		result.put("SERVER", request.getLocalAddr() + ":" + request.getLocalPort());
		
		result.put("FROM", this.getServletName());
		
		String user = request.getUserPrincipal().getName();
		result.put("USER", user);

		result.put("JSESSIONID", request.getSession().getId());
		
		Path wks = Paths.get(getServletContext().getRealPath("/datas") + "/" + user);			
		result.put("WKS", wks.toString());
		
		prj = Paths.get((String) request.getSession().getAttribute("projectPath"));
		result.put("PRJ", prj.toString());
		
		table = request.getParameter("table");
		alias = request.getParameter("alias");
		type = request.getParameter("type");
		linker_id = request.getParameter("linker_id");
		importLabel = Boolean.parseBoolean(request.getParameter("importLabel"));
		
		try{
			
			project = (Project) request.getSession().getAttribute("currentProject");
			if(project != null) {
				language = project.languages.get(0);
				qsFromXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");
				relationCount = false;
				dbmd = (Map<String, DBMDTable>) request.getSession().getAttribute("dbmd");
//				if(!project.getResource().getJndiName().equalsIgnoreCase("XML")) {
				if(qsFromXML == null) {
					withRecCount = (Boolean) request.getServletContext().getAttribute("withRecCount");
					relationCount = project.isRelationCount();
					con = (Connection) request.getSession().getAttribute("con");
					schema = (String) request.getSession().getAttribute("schema");
					tableAliases = (Map<String, String>) request.getSession().getAttribute("tableAliases");
					metaData = con.getMetaData();
				}
			}
			
			QuerySubject querySubject = getQuerySubjects();
			
			System.out.println("On passe...");
				
			querySubject.setFields(getFields());
			
			System.out.println("On passe getFields...");

			FKQuery = (String) request.getSession().getAttribute("FKQuery");
			
			querySubject.addRelations(getForeignKeys());

			System.out.println("On passe getForeignKeys...");

			result.put("MODE", relationMode);
			result.put("DATAS", querySubject);
			result.put("STATUS", "OK");
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			result.put("STATUS", "KO");
			result.put("EXCEPTION", e.getClass().getName());
			result.put("MESSAGE", e.getMessage());
			result.put("TROUBLESHOOTING", "Check relations settings are matching a connected datasource or if a relation.csv have been upload in the project.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			result.put("STACKTRACE", sw.toString());
			e.printStackTrace(System.err);
		}

		finally{
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(result));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected QuerySubject getQuerySubjects() throws SQLException{
		


		String label = "";
		String desc = "";
		ResultSet rst = null;

		QuerySubject result = new QuerySubject();
		
		if(qsFromXML != null) {
			result.setTable_name(qsFromXML.get(table).getTable_name());
			result.set_id(alias + result.getType());
		}
		else {
			result.setTable_name(table);
		}

		result.set_id(alias + type);
		result.setType(type);
		result.setTable_alias(alias);
		result.setLabel(label);
		result.setDescription(desc);
		if(!language.isEmpty()) {
			result.getLabels().put(language, label);
			result.getDescriptions().put(language, desc);
		}
		result.addLinker_id(linker_id);
		
		if(withRecCount && qsFromXML == null){
            long recCount = 0;
            
//            if(dbmd != null){
//            	System.out.println("Get recCount from DBMD...");
//				DBMDTable dbmdTable = dbmd.get(table);
//				if(dbmdTable != null){
//					result.setRecCount(dbmdTable.getTable_recCount());
//					qs_recCount = dbmdTable.getTable_recCount();
//				}
//			}            
//            else {
            	System.out.println("Compute recCount...");
	    		Statement stmt = null;
	    		ResultSet rs = null;
	            try{
		    		String query = "SELECT COUNT(*) FROM ";
		    		if(!schema.isEmpty()){
		    			query += schema + ".";
		    		}
		    		query += table;
				System.out.println("query=" + query);
		    		
		    		stmt = con.createStatement();
		            rs = stmt.executeQuery(query);
		            while (rs.next()) {
		            	recCount = rs.getLong(1);
		            }
		            result.setRecCount(recCount);
		            qs_recCount = recCount;
	            }
	            catch(SQLException e){
	            	System.out.println("CATCHING SQLEXEPTION...");
	            	System.out.println(e.getSQLState());
	            	System.out.println(e.getMessage());
	            	
	            }
	            finally {
		            if (stmt != null) { stmt.close();}
		            if(rst != null){rst.close();}
					
				}
//            }
			
		}
		
		if(importLabel) {
			if(dbmd != null){
				DBMDTable dbmdTable = dbmd.get(table);
				if(dbmdTable != null){
					label = dbmdTable.getTable_remarks();
					result.setLabel(label);
					desc = dbmdTable.getTable_description();
					result.setDescription(desc);
					if(!language.isEmpty()) {
						result.getLabels().put(language, label);
						result.getDescriptions().put(language, desc);
					}
				}
			}
		}
        
        return result;
        
	}
	
	protected List<Field> getFields() throws SQLException{

		if(qsFromXML != null) {
			List<Field> fields = new ArrayList<Field>();
	        Map<String, DBMDColumn> dbmdColumns = null;
	        if(importLabel) {
		        if(dbmd != null){
		        	DBMDTable dbmdTable = dbmd.get(table);
					if(dbmdTable != null){
						dbmdColumns = dbmdTable.getColumns();
					}
		        }
	        	if(dbmdColumns != null){
	        		for(Field field: qsFromXML.get(table).getFields()) {
		    			DBMDColumn dbmdColumn = dbmdColumns.get(field.getField_name());
		    			if(dbmdColumn != null){
		    				String label = (String) dbmdColumn.getColumn_remarks();
			    			field.setLabel(label);
			    			String desc = (String) dbmdColumn.getColumn_description();
			    			field.setDescription(desc);
			           		if(!language.isEmpty()) {
			        			field.getLabels().put(language, label);
			        			field.getDescriptions().put(language, desc);
			        		}	    			
		    			}
		    			fields.add(field);
	        		}
	    		}
	        	if(fields.isEmpty()) {
	    			return qsFromXML.get(table).getFields();
	        	}
		        return fields;
	        }
//			else {
//				return qsFromXML.get(table).getFields();
//			}
		}

	    boolean tableIsView = false;
	    
	    if(project != null) {
		    String tableTypes = project.getResource().getTableTypes();
		    switch(tableTypes.toUpperCase()) {
		    	case "TABLE":
		    		tableIsView = false;
		    		break;
		    	case "VIEW":
		    		tableIsView = true;
		    		break;
		    	case "BOTH":
		    		tableIsView = false;
		    		break;
		    }
	    }
		
		ResultSet rst = metaData.getPrimaryKeys(con.getCatalog(), schema, table);
	    Set<String> pks = new HashSet<String>();
		
	    if(!tableIsView) {
		    while (rst.next()) {
		    	pks.add(rst.getString("COLUMN_NAME"));
		    }
		    
	        if(rst != null){rst.close();}
	    }

		System.out.println("pks=" + pks);
        
        rst = metaData.getIndexInfo(con.getCatalog(), schema, table, false, true);
	    Set<String> indexes = new HashSet<String>();
	    
	    if(!tableIsView) {
		    while (rst.next()) {
		    	indexes.add(rst.getString("COLUMN_NAME"));
		    }
	
	        if(rst != null){rst.close();}
	    }

		System.out.println("indexes=" + indexes);
	    
		Set<String> emptyColumns = new HashSet<String>();
	/*	
        rst = metaData.getColumns(con.getCatalog(), schema, table, "%");
	    Statement stmt = con.createStatement();
		
	    if(!tableIsView) {
		    while (rst.next()) {
	
		    	String colName = (rst.getString("COLUMN_NAME"));
		    	
		    	String query = "select * from " + table + " where " + colName + " is not null";
	
			System.out.println("query=" + query);

			System.out.println("emptyColumns=" + emptyColumns);
			    
	    		ResultSet rst1 = null;
	            try{
		            rst1 = stmt.executeQuery(query);
		            if (!rst1.next()) {    
		                emptyColumns.add(colName);
		            } 		            
	            }
	            catch(SQLException e){
	            	System.out.println("CATCHING SQLEXCEPTION...");
	            	System.out.println(e.getSQLState());
	            	System.out.println(e.getMessage());
	            	
	            }
	            finally {
					if(rst1 != null) {rst1.close();}
	            }
			
		    }
		    if(rst != null){rst.close();}
		    if(stmt != null) {stmt.close();}
	    }
	  */  
	    System.out.println("emptyColumns=" + emptyColumns);


		List<Field> result = new ArrayList<Field>();
		
        rst = metaData.getColumns(con.getCatalog(), schema, table, "%");
        
        Map<String, DBMDColumn> dbmdColumns = null;
        if(importLabel) {
	        if(dbmd != null){
	        	DBMDTable dbmdTable = dbmd.get(table);
				if(dbmdTable != null){
					dbmdColumns = dbmdTable.getColumns();
				}
	        }
        }
		
//        int fieldPos = 0;
        
        while (rst.next()) {
        	String field_name = rst.getString("COLUMN_NAME");
        	String field_type = rst.getString("TYPE_NAME");
//        	System.out.println(field_name + "," + field_type);
        	Field field = new Field();
        	field.setField_name(field_name);
        	field.setField_type(field_type);
        	field.setLabel("");
        	field.setDescription("");
        	field.setFieldPos(rst.getInt("ORDINAL_POSITION"));
        	
        	if(importLabel) {
	        	field.setLabel(rst.getString("REMARKS"));
	        	
		    	String column_remarks = rst.getString("REMARKS");
		    	if(column_remarks == null) {
		    		field.setLabel("");
		    		field.setDescription("");
		    		if(!language.isEmpty()) {
	        			field.getLabels().put(language, "");
	        			field.getDescriptions().put(language, "");
		    		}
		    	}
		    	else {
		    		field.setLabel(column_remarks);
			    	field.setDescription(column_remarks);
		    		if(!language.isEmpty()) {
	        			field.getLabels().put(language, column_remarks);
	        			field.getDescriptions().put(language, column_remarks);
		    		}
		    	}
        	}
        	
        	field.setField_size(rst.getInt("COLUMN_SIZE"));
        	field.setNullable(rst.getString("IS_NULLABLE"));
        	field.set_id(field_name);
        	if(pks.contains(rst.getString("COLUMN_NAME"))){
    			field.setPk(true);
    			field.setIcon("Identifier");
    		}
        	if(indexes.contains(rst.getString("COLUMN_NAME"))){
    			field.setIndexed(true);
    		}

        	if(dbmdColumns != null){
    			DBMDColumn dbmdColumn = dbmdColumns.get(field_name);
    			if(dbmdColumn != null){
    				String label = (String) dbmdColumn.getColumn_remarks();
	    			field.setLabel(label);
	    			String desc = (String) dbmdColumn.getColumn_description();
	    			field.setDescription(desc);
	           		if(!language.isEmpty()) {
	        			field.getLabels().put(language, label);
	        			field.getDescriptions().put(language, desc);
	        		}	    			
    			}
    		}
        	
    		if(StringUtils.containsAny(field.getField_type().toUpperCase(), "DATE", "DATETIME", "TIMESTAMP")) {
    			field.setTimeDimension(true);
    		}
    		
    	    if(emptyColumns.contains(field_name)) {
    	    	field.setHidden(true);
    	    }
    		
    		
        	result.add(field);
        }

        if(rst != null){rst.close();}
        
		return result;
		
	}
	
	protected List<Relation> getForeignKeys() throws SQLException{
		
		Map<String, Relation> map = new HashMap<String, Relation>();
		
		Connection csvCon = null;
		PreparedStatement stmt = null;
		ResultSet rst = null;
		
		if(Files.exists(Paths.get(prj + "/relationExp.csv"))) {
			Properties props = new java.util.Properties();
			props.put("separator",";");
			csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
			String sql = "SELECT * FROM relationExp where FKTABLE_NAME = '" + table + "'";
			stmt = csvCon.prepareStatement(sql);
			rst = stmt.executeQuery();
			relationMode = "CSVEXP";
			
    		List<Relation> result = new ArrayList<Relation>();
    		// JOIN_NAME;FKTABLE_NAME;PKTABLE_NAME;RELATION_EXPRESSION
    		// Jointure789;Pomphistservsec;Pomphistburosec;Pomphistservsec.CLEENREG=Pomphistburosec.CLEENREG and Pomphistservsec.CLEPERS=Pomphistburosec.CLEPERS
    		
    	    while (rst.next()) {

		    	String fkName = "FK_" + rst.getString("JOIN_NAME").trim(); 
		    	String pkName = "PK_" + rst.getString("JOIN_NAME").trim(); 
		    	String fkTableName = rst.getString("FKTABLE_NAME").trim();
		    	String pkTableName = rst.getString("PKTABLE_NAME").trim();
//		    	String fkColumnName = "notAvailable";
//		    	String pkColumnName = "notAvailable";
		    	String relExp = rst.getString("RELATION_EXPRESSION");
//		    	short keySeq = 1;
	    		
		    	Relation relation = new Relation();
	        	relation.set_id(fkName + "F");
	        	relation.setKey_name(fkName);
	        	relation.setFk_name(fkName);
	        	relation.setPk_name(pkName);
	        	relation.setTable_name(fkTableName);
	        	relation.setTable_alias(alias);
	        	relation.setPktable_name(pkTableName);
	        	relation.setPktable_alias(pkTableName);
	//        	relation.setRelashionship("[" + type.toUpperCase() + "].[" + alias + "].[" + fkcolumn_name + "] = [" + pktable_name + "].[" + pkcolumn_name + "]");
	    		try {

	    			Pattern p = Pattern.compile("(\\w+)\\.(\\w+)");
	        		Matcher m = p.matcher(relExp);
	        		short matchCount = 1;
	        		Seq seq = null;
		    		while (m.find()) {
		//    		    System.out.println("m.group(1)=" + m.group(1));
		//    		    System.out.println("m.group(2)=" + m.group(2));
		    		    String tableName = m.group(1);
		    		    String field = m.group(2);
		    		    String exp = null;
		    		    if(matchCount == 1) {
		    		    }
		    		    if(matchCount % 2 != 0) {
				        	seq = new Seq();
		    		    }
		    		    if (tableName.contentEquals(table)) {
		    		    	relation.setAbove(field);
		    		    	exp = ("[" + type.toUpperCase() + "]." + "[" + alias + "].[" + field + "]");
				        	seq.setTable_name(tableName);
				        	seq.setColumn_name(field);
				        	if(matchCount > 1) {
				        		seq.setKey_seq((short) (matchCount -1));
				        	}
				        	else {
				        		seq.setKey_seq(matchCount);
				        	}
		    		    }
		    		    else {
		    		    	exp = ("[" + tableName + "].[" + field + "]");
				        	seq.setPktable_name(tableName);
				        	seq.setPkcolumn_name(field);
				        	relation.addSeq(seq);
		    		    }
		    		    relExp = relExp.replaceFirst(tableName + "." + field, exp);
			        	System.out.println("relExp=" + relExp);
			        	System.out.println(matchCount);
			        	matchCount++;
		    		}
	    		}
	    		catch(PatternSyntaxException e) {
	    			continue;
	    		}
	        	
	        	relation.setRelashionship(relExp);
	//        	relation.setWhere(fktable_name + "." + fkcolumn_name + " = " + pktable_name + "." + pkcolumn_name);
	        	relation.setKey_type("F");
	        	relation.setType(type.toUpperCase());
	        	relation.set_id("FK_" + relation.getPktable_alias() + "_" + alias + "_" + type.toUpperCase());
//	        	relation.setAbove(fkColumnName);
	        	result.add(relation);
    	    }
    	    if(rst != null) {rst.close();}
    	    if(stmt != null) {stmt.close();}
    	    if(csvCon != null) {csvCon.close();}
    		
	    	return result;
			
		}
		if(Files.exists(Paths.get(prj + "/relation.csv"))) {
			System.out.println("on passe dans relation.csv...");
			Properties props = new java.util.Properties();
			props.put("separator",";");
			csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
			String sql = "SELECT * FROM relation where FKTABLE_NAME = '" + table + "'";
			stmt = csvCon.prepareStatement(sql);
			rst = stmt.executeQuery();
			relationMode = "CSV";
		}
		else if(FKQuery != null && !FKQuery.isEmpty()) {
			System.out.println("on passe dans FKQuery...");
//			System.out.println("FKQuery=" + FKQuery);
			FKQuery = StringUtils.replace(FKQuery, " $TABLE", " '" + table + "'");
//			System.out.println("FKQuery=" + FKQuery);
			stmt = con.prepareStatement(FKQuery);
//			stmt.setString(1, table);
    		rst = stmt.executeQuery();
			relationMode = "SQL";
    	}
		else if(metaData != null) {
			rst = metaData.getImportedKeys(con.getCatalog(), schema, table);
			relationMode = "DB";
		}
	    
		if(rst != null) {
		    while (rst.next()) {
		    	
		    	if(relationMode.equalsIgnoreCase("CSVEXP")) {
			    	
		    	}
		    	else {
			    	String key_name = rst.getString("FK_NAME");
			    	String fk_name = rst.getString("FK_NAME");
			    	String pk_name = rst.getString("PK_NAME");
			    	String key_seq = rst.getString("KEY_SEQ");
			    	String fkcolumn_name = rst.getString("FKCOLUMN_NAME");
			    	String pkcolumn_name = rst.getString("PKCOLUMN_NAME");
			        String fktable_name = rst.getString("FKTABLE_NAME");
			        String pktable_name = rst.getString("PKTABLE_NAME");
			        String _id = key_name + "F";
			        boolean isAlias = false;
			        
			        if(tableAliases != null){
				        if(tableAliases.containsKey(fktable_name)){
				        	fktable_name = tableAliases.get(fktable_name);
				        }
			
				        if(tableAliases.containsKey(pktable_name)){
				        	pktable_name = tableAliases.get(pktable_name);
				        	isAlias = true;
				        }
			        }
		
			        // Jump to other key if pktable is an alias
			        if(isAlias){continue;}
		
			        if(!map.containsKey(_id)){
			        	
			        	Relation relation = new Relation();
			        	
			        	relation.set_id(_id);
			        	relation.setKey_name(key_name);
			        	relation.setFk_name(fk_name);
			        	relation.setPk_name(pk_name);
			        	relation.setTable_name(fktable_name);
			        	relation.setTable_alias(alias);
			        	relation.setPktable_name(pktable_name);
			        	relation.setPktable_alias(pktable_name);
			        	relation.setRelashionship("[" + type.toUpperCase() + "].[" + alias + "].[" + fkcolumn_name + "] = [" + pktable_name + "].[" + pkcolumn_name + "]");
			        	relation.setWhere(fktable_name + "." + fkcolumn_name + " = " + pktable_name + "." + pkcolumn_name);
			        	relation.setKey_type("F");
			        	relation.setType(type.toUpperCase());
			        	relation.set_id("FK_" + relation.getPktable_alias() + "_" + alias + "_" + type.toUpperCase());
			        	relation.setAbove(fkcolumn_name);
			        	
			        	if(importLabel && qsFromXML == null) {
			        		
		//				    String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
						    String[] types = {"TABLE"}; 
						    		
						    if(project != null) {
							    String tableTypes = project.getResource().getTableTypes();
							    List<String> typesList = new ArrayList<String>();
							    switch(tableTypes.toUpperCase()) {
							    	case "TABLE":
							    		typesList.add("TABLE");
							    		break;
							    	case "VIEW":
							    		typesList.add("VIEW");
							    		break;
							    	case "BOTH":
							    		typesList.add("TABLE");
							    		typesList.add("VIEW");
							    		break;
							    }
							    types = typesList.stream().toArray(String[]::new);
						    }
						    
				    		ResultSet rst0 = metaData.getTables(con.getCatalog(), schema, pktable_name, types);
				    		while (rst0.next()) {
				    			String label = rst0.getString("REMARKS");
				    	    	relation.setLabel(label);
				    	    	
				    	    	if(label == null) {
				    	    		relation.setLabel("");
				    	    		relation.setDescription("");
						    		if(!language.isEmpty()) {
						    			relation.getLabels().put(language, "");
						    			relation.getDescriptions().put(language, "");
						    		}
				    	    	}
				    	    	else {
			    			    	relation.setDescription(label);
			    		    		relation.setLabel(label);
						    		if(!language.isEmpty()) {
						    			relation.getLabels().put(language, label);
						    			relation.getDescriptions().put(language, label);
						    		}
				    	    	}
				    	    	
				    	    }
				    		if(rst0 != null){rst0.close();}
			        	}
			    		
			    		if(relation.getLabel() == null) {relation.setLabel("");}
			    		if(relation.getDescription() == null) {relation.setDescription("");}
			        	
			    		if(importLabel && qsFromXML == null) {
				    		if(dbmd != null){
				    			DBMDTable dbmdTable = dbmd.get(pktable_name);
				    			if(dbmdTable != null){
				    				String label = dbmdTable.getTable_remarks();
					    			relation.setLabel(label);
					    			String desc = dbmdTable.getTable_description();
					    			relation.setDescription(desc);
					           		if(!language.isEmpty()) {
					           			relation.getLabels().put(language, label);
					           			relation.getDescriptions().put(language, desc);
					        		}	    			
				    			}
				    		}
			    		}
			        	
			        	Seq seq = new Seq();
			        	seq.setTable_name(fktable_name);
			        	seq.setPktable_name(pktable_name);
			        	seq.setColumn_name(fkcolumn_name);
			        	seq.setPkcolumn_name(pkcolumn_name);
			        	seq.setKey_seq(Short.parseShort(key_seq));
			        	relation.addSeq(seq);
			        	
			        	map.put(_id, relation);
		
			        }
			        else{
			        	
			        	Relation relation = map.get(_id);
			        	if(!relation.getSeqs().isEmpty()){
			        		Seq seq = new Seq();
				        	seq.setTable_name(fktable_name);
				        	seq.setPktable_name(pktable_name);
				        	seq.setColumn_name(fkcolumn_name);
				        	seq.setPkcolumn_name(pkcolumn_name);
				        	seq.setKey_seq(Short.parseShort(key_seq));
				        	
				        	relation.addSeq(seq);
				        	
				        	StringBuffer sb = new StringBuffer((String) relation.getRelationship());
				        	sb.append(" AND [" + type.toUpperCase() + "].[" + alias + "].[" + fkcolumn_name + "] = [" + pktable_name + "].[" + pkcolumn_name + "]");
				        	relation.setRelashionship(sb.toString());
				        	
				        	sb = new StringBuffer((String) relation.getWhere());
				        	sb.append(" AND " + fktable_name + "." + fkcolumn_name + " = " + pktable_name + "." + pkcolumn_name);
				        	relation.setWhere(sb.toString());
				        	
			        	}
			        }
		    	}
		        	
		    }
		}
	    if(rst != null) {rst.close();}
	    if(stmt != null) {stmt.close();}
	    if(csvCon != null) {csvCon.close();}
	    
	    if(relationCount){
	    	for(Entry<String, Relation> relation: map.entrySet()){
	    		Relation rel = relation.getValue();
	    		
	    		Set<String> tableSet = new HashSet<String>();
	    		for(Seq seq: rel.getSeqs()){
	    			if(!schema.isEmpty()){
		    			tableSet.add(schema + "." + seq.pktable_name);
		    			tableSet.add(schema + "." + seq.table_name);
	    			}
	    			else{
		    			tableSet.add(schema + seq.pktable_name);
		    			tableSet.add(schema + seq.table_name);
	    			}
	    		}
	    		
	    		System.out.println("tableSet=" + tableSet);
	    		
	    		StringBuffer sb = new StringBuffer();;
	    		
	    		for(String table: tableSet){
	    			sb.append(", " + table);
	    		}
	    		String tables = sb.toString().substring(1);
	    		
	            long recCount = 0;
	    		Statement stm = null;
	    		ResultSet rs = null;
	            try{
		    		String query = "SELECT COUNT(*) FROM " + tables + " WHERE " + rel.where;
		    		System.out.println(query);
		    		stm = con.createStatement();
		            rs = stm.executeQuery(query);
		            while (rs.next()) {
		            	recCount = rs.getLong(1);
		            }
		            rel.setRecCount(recCount);
//		    		long result = (Math.round(((double)recCount / qs_recCount) * 100));
		    		
		    		double d0 = Double.parseDouble(String.valueOf(recCount));
		    		double d1 = Double.parseDouble(String.valueOf(qs_recCount));
		    		
//		    		System.out.println("recCount=" + recCount);
//		    		System.out.println("qs_recCount=" + qs_recCount);
		    		
		    		double num = (d0/d1) * 100;
		    		System.out.println("num=" + num);
		    		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		    		nf.setMaximumFractionDigits(3);
//		    		nf.setMinimumFractionDigits(5);	    
		    		nf.setRoundingMode(RoundingMode.UP);
		    	    num = Double.parseDouble(nf.format(num));
		            rel.setRecCountPercent(num);
	            }
	            catch(NumberFormatException nfe) {
	            	// Avoid divide 0/0 failure
	            	System.out.println("CATCHING NUMBER FORMAT EXCEPTION...");
	            	System.out.println(nfe.getMessage());
	            }
	            catch(SQLException e){
	            	System.out.println("CATCHING SQLEXEPTION...");
	            	System.out.println(e.getSQLState());
	            	System.out.println(e.getMessage());
	            	
	            }
	            finally {
		            if (stmt != null) { stmt.close();}
		            if(rst != null){rst.close();}
					
				}
	    		
	    	}
	    }
	    
	    return new ArrayList<Relation>(map.values());
		
	}

}

