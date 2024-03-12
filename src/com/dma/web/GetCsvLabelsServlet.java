package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "GetCsvLabels", urlPatterns = { "/GetCsvLabels" })
public class GetCsvLabelsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCsvLabelsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			
			result.put("CLIENT", request.getRemoteAddr() + ":" + request.getRemotePort());
			result.put("SERVER", request.getLocalAddr() + ":" + request.getLocalPort());
			
			result.put("FROM", this.getServletName());
			
			String user = request.getUserPrincipal().getName();
			result.put("USER", user);

			result.put("JSESSIONID", request.getSession().getId());
			
			Path wks = Paths.get(getServletContext().getRealPath("/datas") + "/" + user);			
			result.put("WKS", wks.toString());
			
			Path prj = Paths.get((String) request.getSession().getAttribute("projectPath"));
			result.put("PRJ", prj.toString());
			
			Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
			
			@SuppressWarnings("unchecked")
			Map<String, DBMDTable> dbmd = (Map<String, DBMDTable>) request.getSession().getAttribute("dbmd");
			
			@SuppressWarnings("unchecked")
			Map<String, QuerySubject> qsFromXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");
			
			if(parms != null && parms.get("aliases") != null) {
				
				String tlCsvFileName = "tableLabel.csv";
				boolean tlIsAlias = false;
				String tdCsvFileName = "tableDescription.csv";
				boolean tdIsAlias = false;
				String clCsvFileName = "columnLabel.csv";
				boolean clIsAlias = false;
				String cdCsvFileName = "columnDescription.csv";
				boolean cdIsAlias = false;
				
				String lang = null;
				
				
				if(parms.get("lang") != null && parms.get("lang").toString().length() ==2) {
					lang = parms.get("lang").toString();
					tlCsvFileName = "tableLabel-" + lang + ".csv";
					tdCsvFileName = "tableDescription-" + lang + ".csv";
					clCsvFileName = "columnLabel-" + lang + ".csv";
					cdCsvFileName = "columnDescription-" + lang + ".csv";
				}
				
				@SuppressWarnings("unchecked")
				Map<String , Map<String, String>> aliasesMap = (Map<String , Map<String, String>>) parms.get("aliases");
				
				if(aliasesMap.size() > 0){
				
					Map<String, String> tlMap = new HashMap<String, String>();
					Map<String, String> tdMap = new HashMap<String, String>();
					Map<String, Map<String, String>> clMap = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> cdMap = new HashMap<String, Map<String, String>>();
					
					Set<String> tables = new HashSet<String>();
					Set<String> aliases = new HashSet<String>();
					
					for(Entry<String, Map<String, String>> alias: aliasesMap.entrySet()) {
						tables.add(alias.getValue().get("table"));
						aliases.add(alias.getValue().get("alias"));
					}
					
					
					String tableInClause = "('" + StringUtils.join(tables.iterator(), "','") + "')";
					String aliasInClause = "('" + StringUtils.join(aliases.iterator(), "','") + "')";
				
					Properties props = new java.util.Properties();
					props.put("separator",";");
					props.put("charset", "UTF-8");
					Connection csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
					Statement csvStmt = csvCon.createStatement();
					ResultSet csvRst = null;
			
					if(Files.exists(Paths.get(prj + "/" + tlCsvFileName))){
						
						String whereClause = "TABLE_NAME";
						String inClause = tableInClause;
						String selectClause = "TABLE_NAME, TABLE_LABEL";

						LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(Paths.get(prj + "/" + tlCsvFileName).toFile())));
						
						if(reader.readLine().contains("TABLE_ALIAS")){
							selectClause = "TABLE_ALIAS, TABLE_LABEL";
							whereClause = "TABLE_ALIAS";
							inClause = aliasInClause;
							tlIsAlias = true;
						}
						reader.close();
						
						String sql = null;
						if(lang != null) {
							sql = "SELECT " + selectClause + " FROM tableLabel-" + lang + " where " + whereClause + " in " + inClause;
						}
						else {
							sql = "SELECT " + selectClause + " FROM tableLabel where " + whereClause + " in " + inClause;
						}
						System.out.println("sql=" + sql);
						csvRst = csvStmt.executeQuery(sql);
						while(csvRst.next()){
							tlMap.put(csvRst.getString("Table_Name"), csvRst.getString("Table_Label"));
						}
						csvRst.close();
					}
					
					if(Files.exists(Paths.get(prj + "/" + tdCsvFileName))){
						
						String selectClause = "TABLE_NAME, TABLE_DESCRIPTION";
						String whereClause = "TABLE_NAME";
						String inClause = tableInClause;

						LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(Paths.get(prj + "/" + tdCsvFileName).toFile())));
						
						if(reader.readLine().contains("TABLE_ALIAS")){
							selectClause = "TABLE_ALIAS, TABLE_DESCRIPTION";
							whereClause = "TABLE_ALIAS";
							inClause = aliasInClause;
							tdIsAlias = true;
						}
						reader.close();
						
						String sql = null;
						if(lang != null) {
							sql = "SELECT " + selectClause + " FROM tableDescription-" + lang + " where " + whereClause + " in " + inClause;
						}
						else {
							sql = "SELECT " + selectClause + " FROM tableDescription where TABLE_NAME in " + inClause;
						}
						csvRst = csvStmt.executeQuery(sql);
						while(csvRst.next()){
							tdMap.put(csvRst.getString("Table_Name"), csvRst.getString("Table_Description"));
						}
						csvRst.close();
					}
					
					if(Files.exists(Paths.get(prj + "/" + clCsvFileName))){
						
						boolean isAlias = false;
						String selectClause = "TABLE_NAME, COLUMN_NAME, COLUMN_LABEL";
						String whereClause = "TABLE_NAME";
						Set<String> elements = tables;
						LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(Paths.get(prj + "/" + clCsvFileName).toFile())));
						
						if(reader.readLine().contains("TABLE_ALIAS")){
							selectClause = "TABLE_ALIAS, COLUMN_NAME, COLUMN_LABEL";
							whereClause = "TABLE_ALIAS";
							elements = aliases;
							isAlias = true;
							clIsAlias = true;
						}
						reader.close();
						
						for(String element: elements) {

							List<String> fields = new ArrayList<String>();
							String tableName = null;
							if(isAlias) {
								tableName = aliasesMap.get(element).get("table");
							}
							else {
								tableName = element;
							}
							
							if(qsFromXML != null) {
								List<Field> fieldsFromXML = qsFromXML.get(tableName).getFields();
								for(Field fieldFromXML: fieldsFromXML) {
									fields.add(fieldFromXML.getField_name());
								}
							}						
							else {
								Connection con = (Connection) request.getSession().getAttribute("con");
								String schema = (String) request.getSession().getAttribute("schema");
								DatabaseMetaData metaData = con.getMetaData();
								ResultSet rst = metaData.getColumns(con.getCatalog(), schema, tableName, "%");
								while(rst.next()){
									fields.add(rst.getString("COLUMN_NAME"));
								}
								if(rst != null) {rst.close();}
							}
				
							String columnInClause = "('" + StringUtils.join(fields.iterator(), "','") + "')";

							Map<String, String> cols = new HashMap<String, String>();
							String sql = null;
							if(lang != null) {
								sql = "SELECT " + selectClause + " FROM columnLabel-" + lang + " where " + whereClause + " = '" + element + "' and COLUMN_NAME in " + columnInClause;
							}
							else {
								sql = "SELECT " + selectClause + " FROM columnLabel where " + whereClause + " = '" + element + "' and COLUMN_NAME in " + columnInClause;
							}
							csvRst = csvStmt.executeQuery(sql);
							while(csvRst.next()){
								cols.put(csvRst.getString("Column_Name"), csvRst.getString("Column_Label"));
							}
							csvRst.close();
							clMap.put(element, cols);
							
						}
						
					}
					
					if(Files.exists(Paths.get(prj + "/" + cdCsvFileName))){
						
						boolean isAlias = false;
						String selectClause = "TABLE_NAME, COLUMN_NAME, COLUMN_DESCRIPTION";
						String whereClause = "TABLE_NAME";
						Set<String> elements = tables;
						LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(Paths.get(prj + "/" + cdCsvFileName).toFile())));
						
						if(reader.readLine().contains("TABLE_ALIAS")){
							selectClause = "TABLE_ALIAS, COLUMN_NAME, COLUMN_DESCRIPTION";
							whereClause = "TABLE_ALIAS";
							elements = aliases;
							isAlias = true;
							cdIsAlias = true;
						}
						reader.close();
						
						for(String element: elements) {
							
							List<String> fields = new ArrayList<String>();
							String tableName = null;
							if(isAlias) {
								tableName = aliasesMap.get(element).get("table");
							}
							else {
								tableName = element;
							}
							
							if(qsFromXML != null) {
								List<Field> fieldsFromXML = qsFromXML.get(tableName).getFields();
								for(Field fieldFromXML: fieldsFromXML) {
									fields.add(fieldFromXML.getField_name());
								}
							}						
							else {
								Connection con = (Connection) request.getSession().getAttribute("con");
								String schema = (String) request.getSession().getAttribute("schema");
								DatabaseMetaData metaData = con.getMetaData();
								ResultSet rst = metaData.getColumns(con.getCatalog(), schema, tableName, "%");
								while(rst.next()){
									fields.add(rst.getString("COLUMN_NAME"));
								}
								if(rst != null) {rst.close();}
							}
				
							String columnInClause = "('" + StringUtils.join(fields.iterator(), "','") + "')";
							
							Map<String, String> cols = new HashMap<String, String>();
							String sql = null;
							if(lang != null) {
								sql = "SELECT " + selectClause + " FROM columnDescription-" + lang + " where " + whereClause + " = '" + element + "' and COLUMN_NAME in " + columnInClause;
							}
							else {
								sql = "SELECT " + selectClause + " FROM columnDescription where " + whereClause + " = '" + element + "' and COLUMN_NAME in " + columnInClause;
							}
							
							csvRst = null;
							try {
								csvRst = csvStmt.executeQuery(sql);
								while(csvRst.next()){
									cols.put(csvRst.getString("Column_Name"), csvRst.getString("Column_Description"));
								}
							}
							catch(SQLException e){
				            	System.out.println("CATCHING SQLEXCEPTION...");
				            	System.out.println(e.getSQLState());
				            	System.out.println(e.getMessage());
				            	
				            }
				            finally {
								if(csvRst != null) {csvRst.close();}
				            }
							
							cdMap.put(element, cols);
							
						}
						
					}
					
					
					if(csvStmt != null) {csvStmt.close();}
					if(csvCon != null){csvCon.close();}
					
					
					for(Entry<String, Map<String, String>> alias: aliasesMap.entrySet()) {
						String tableName = alias.getValue().get("table");
						String aliasName = alias.getValue().get("alias");
						
						DBMDTable dbmdTable = new DBMDTable();
						if(dbmd != null && dbmd.containsKey(tableName)) {
							dbmdTable = dbmd.get(tableName);
						}
						if(dbmd != null && dbmd.containsKey(aliasName)) {
							dbmdTable = dbmd.get(aliasName);
						}
						
						if(tlIsAlias) {
							dbmdTable.setTable_remarks(tlMap.get(aliasName));
						}
						else {
							dbmdTable.setTable_remarks(tlMap.get(tableName));
						}

						if(tdIsAlias) {
							dbmdTable.setTable_description(tdMap.get(aliasName));
						}
						else {
							dbmdTable.setTable_description(tdMap.get(tableName));
						}
						
						Map<String, DBMDColumn> dbmdColumns = new HashMap<String, DBMDColumn>();
						if(dbmd != null && dbmd.containsKey(tableName)) {
							dbmdColumns = dbmdTable.getColumns();
						}
						if(dbmd != null && dbmd.containsKey(aliasName)) {
							dbmdColumns = dbmdTable.getColumns();
						}
						
						Map<String, String> cls = null;
						
						if(clIsAlias) {
							cls = (Map<String, String>) clMap.get(aliasName);
						}
						else {
							cls = (Map<String, String>) clMap.get(tableName);
						}
						
						if(cls != null){
							for(Entry<String, String> cl: cls.entrySet()){
								String column_name = cl.getKey();
								String column_remarks = cl.getValue();	
								if(!dbmdColumns.containsKey(column_name)) {
									dbmdColumns.put(column_name, new DBMDColumn());
									dbmdColumns.get(column_name).setColumn_name(column_name);
								}
								dbmdColumns.get(column_name).setColumn_remarks(column_remarks);
							}
						}
						
						Map<String, String> cds = null;
						
						if(cdIsAlias) {
							cds = (Map<String, String>) cdMap.get(aliasName);
						}
						else {
							cds = (Map<String, String>) cdMap.get(tableName);
						}
						
						if(cds != null){
							for(Entry<String, String> cd: cds.entrySet()){
								String column_name = cd.getKey();
								String column_description = cd.getValue();	
								if(!dbmdColumns.containsKey(column_name)) {
									dbmdColumns.put(column_name, new DBMDColumn());
									dbmdColumns.get(column_name).setColumn_name(column_name);
								}
								dbmdColumns.get(column_name).setColumn_description(column_description);
							}
						}
						
						dbmdTable.setColumns(dbmdColumns);
						if(dbmd == null) {
							dbmd = new HashMap<String, DBMDTable>();
						}
						dbmd.put(alias.getKey(), dbmdTable);
						
					}
					
					result.put("STATUS", "OK");
					result.put("DATAS", dbmd);
					request.getSession().setAttribute("dbmd", dbmd);
					
				}
				else {
					result.put("STATUS", "OK");
					result.put("MESSAGE", "No table given in parms.");
					
				}
				
			}
			else {
				result.put("STATUS", "KO");
				result.put("ERROR", "Input parameters are not valid.");
				throw new Exception();
			}
			
		}
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			result.put("STATUS", "KO");
			result.put("EXCEPTION", e.getClass().getName());
			result.put("MESSAGE", e.getMessage());
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

}