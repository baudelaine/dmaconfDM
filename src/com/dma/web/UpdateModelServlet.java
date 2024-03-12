package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UpdateModel", urlPatterns = { "/UpdateModel" })
public class UpdateModelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateModelServlet() {
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

			
			if(parms != null) {

				
				@SuppressWarnings("unchecked")
				List<String> langs = (List<String>) Tools.fromJSON((String) parms.get("langs"), new TypeReference<List<String>>(){});
				
				@SuppressWarnings("unchecked")
				List<QuerySubject> qss = (List<QuerySubject>) Tools.fromJSON((String) parms.get("model"), new TypeReference<List<QuerySubject>>(){});

				Map<String, Set<String>> fieldsToRemove = new HashMap<String, Set<String>>();
				Map<String, Set<String>> fieldsToAdd = new HashMap<String, Set<String>>();
				Map<String, Set<String>> fieldsToUpdate = new HashMap<String, Set<String>>();

				Map<String, List<Field>> customFields = new HashMap<String, List<Field>>();
				Map<String, Map<String, Field>> modelFields = new HashMap<String, Map<String, Field>>();

				Map<String, Map<String, Field>> dbMap = new HashMap<String, Map<String, Field>>();
				Map<String, Map<String, Field>> xmlMap = new HashMap<String, Map<String, Field>>();
				Map<String, Map<String, Field>> currents = new HashMap<String, Map<String, Field>>();

				Map<String, Map<String, Set<String>>> updates = new HashMap<String, Map<String, Set<String>>>();
				List<QuerySubject> newQss = new ArrayList<QuerySubject>();
				
				
				Map<String, Map<String, Map<String, Field>>> ids = new HashMap<String, Map<String, Map<String, Field>>>();
				Set<String> tablesInModel = new HashSet<String>();

				// Start build ids and put custom fields in customFields
				for(QuerySubject qs: qss) {
					String id = qs.get_id();
					String table = qs.getTable_name();
					tablesInModel.add(table);
					Map<String, Map<String, Field>> tables = new HashMap<String, Map<String, Field>>();
					Map<String, Field> columns = new HashMap<String, Field>();
					List<Field> customFList = new ArrayList<Field>();
					for(Field field: qs.getFields()) {
						if(!field.isCustom()) {
							columns.put(field.getField_name(), field);
						}
						else {
							customFList.add(field);					
						}						
					}
					tables.put(table, columns);
					ids.put(id, tables);
					customFields.put(id, customFList);
				}
				// End build ids and put custom fields in customFields

				boolean isXML = false;
				Project project = (Project) request.getSession().getAttribute("currentProject");
				if(project != null) {
					Resource resource = project.getResource();
					if(resource.getJndiName().equalsIgnoreCase("XML")) {
						isXML = true;
					}
				}
				
				if(isXML) {
					@SuppressWarnings("unchecked")
					Map<String, QuerySubject> qssXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");
					
					for(Entry<String, QuerySubject> qs: qssXML.entrySet()) {
						String table = qs.getKey();
						if(tablesInModel.contains(table)){
					    	System.out.println("XML " + table);
					    	Map<String, Field> fMap = new HashMap<String, Field>();
							List<Field> fields = qs.getValue().getFields();
							for(Field field: fields) {
								String column = field.getField_name();
								fMap.put(column, field);
							}
							xmlMap.put(table, fMap);
						}
					}
					currents = xmlMap;
				}
				else {

					// Start build dbMap
					Connection conn = (Connection) request.getSession().getAttribute("con");
					String schema = (String) request.getSession().getAttribute("schema");
					
				    DatabaseMetaData metaData = conn.getMetaData();
				    
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
				    
				    ResultSet rstTables = metaData.getTables(conn.getCatalog(), schema, "%", types);					    
				    while (rstTables.next()) {
				    	String table = rstTables.getString("TABLE_NAME");
				    	if(tablesInModel.contains(table)) {
							ResultSet rstFields = metaData.getColumns(conn.getCatalog(), schema, table, "%");
							Map<String, Field> fMap = new HashMap<String, Field>();
							while(rstFields.next()){
								Field field = new Field();
								String column = rstFields.getString("COLUMN_NAME");
								field.set_id(rstFields.getString("COLUMN_NAME"));
								field.setField_name(rstFields.getString("COLUMN_NAME"));
								field.setField_type(rstFields.getString("TYPE_NAME"));
								field.setNullable(rstFields.getString("IS_NULLABLE"));
								field.setField_size(rstFields.getInt("COLUMN_SIZE"));
								field.setDescription(rstFields.getString("REMARKS"));
								field.setFieldPos(rstFields.getInt("ORDINAL_POSITION"));
								Map<String, String> langsMap = new HashMap<String, String>();
								for(String lang: langs) {
									langsMap.put(lang, "");
								}
								field.setLabels(langsMap);
								field.setDescriptions(langsMap);
								fMap.put(column, field);
							}
							dbMap.put(table, fMap);
							rstFields.close();
				    	}
				    }
				    rstTables.close();
					// End build dbMap
				    currents = dbMap;
				}

				
				for(Entry<String, Map<String, Map<String, Field>>> id: ids.entrySet()) {
			    	String qs_id = id.getKey();
			    	String qs_table = id.getValue().entrySet().iterator().next().getKey();
			    	Map<String, Field> qs_columns = id.getValue().entrySet().iterator().next().getValue();
			    	Set<String> qs_columnsKeys = new HashSet<String>(qs_columns.keySet());
			    	System.out.println(qs_id + " -> " + qs_table + " -> " + qs_columnsKeys.size() + " - " + qs_columnsKeys);
			    	Map<String, Field> current_columns = currents.get(qs_table);
			    	if(current_columns != null) {
				    	Set<String> current_columnsKeys = new HashSet<String>(current_columns.keySet());
				    	Set<String> addedColumnsKeys = new HashSet<String>(current_columnsKeys);
				    	addedColumnsKeys.removeAll(qs_columnsKeys);
				    	System.out.println("addedColumnsKeys -> " + addedColumnsKeys.size() + " - " + addedColumnsKeys);
				    	Set<String> removedColumnsKeys = new HashSet<String>(qs_columnsKeys);
				    	removedColumnsKeys.removeAll(current_columnsKeys);
				    	System.out.println("removedColumnsKeys -> " + removedColumnsKeys.size() + " - " + removedColumnsKeys);
				    	Set<String> retainedColumnsKeys = new HashSet<String>(qs_columnsKeys);
				    	retainedColumnsKeys.retainAll(current_columnsKeys);
				    	System.out.println("retainedColumnsKeys -> " + retainedColumnsKeys.size() + " - " + retainedColumnsKeys);
				    	Map<String, Set<String>> update = new HashMap<String, Set<String>>();
				    	update.put("added", addedColumnsKeys);
				    	update.put("removed", removedColumnsKeys);
				    	update.put("retained", retainedColumnsKeys);
				    	updates.put(qs_id, update);
			    	}
//			    	System.out.println(Tools.toJSON(updates));
				}
				
				for(QuerySubject qs: qss) {
					String id = qs.get_id();
					String table = qs.getTable_name();
					List<Field> fields = qs.getFields();
					List<Field> newFields = new ArrayList<Field>();
					for(Field field: fields) {
						String column = field.getField_name();
						Field newField = null;
						if(updates.get(id) != null) {
							if(updates.get(id).get("retained").contains(column)) {
								newField = field;
								newField.setFieldPos(currents.get(table).get(column).getFieldPos());
								System.out.println("retain " + field.getField_name());
							}
						}
						if(newField != null) {
							newFields.add(newField);
						}
					}
					if(updates.get(id) != null) {
						if(! updates.get(id).get("added").isEmpty()) {
							for(String fieldName: updates.get(id).get("added")) {
								Field newField = currents.get(table).get(fieldName);
								newFields.add(newField);
								System.out.println("added " + newField.getField_name());
							}
						}
						if(! updates.get(id).get("removed").isEmpty()) {
							
						}
					}

					int fieldPos = newFields.size();
					if(customFields.get(qs.get_id()) != null) {
						for(Field customField: customFields.get(qs.get_id())) {
							customField.setFieldPos(++fieldPos);
							newFields.add(customField);
						}
					}
					
					qs.setFields(newFields);
				}
				
				for(Entry<String, Map<String, Set<String>>> update: updates.entrySet()) {
					String qs_id = update.getKey();
					System.out.println("qs_id=" + qs_id);
					for(Entry<String, Set<String>> states: update.getValue().entrySet()) {
						String state = states.getKey();
						System.out.println("state=" + state);
						switch (state) {
							case "added":
								fieldsToAdd.put(qs_id, updates.get(qs_id).get("added"));
								break;
							case "removed":
								fieldsToRemove.put(qs_id, updates.get(qs_id).get("removed"));
								break;
							case "retained":
								fieldsToUpdate.put(qs_id, updates.get(qs_id).get("retained"));
								break;
							}
					}
				}
				
				
				result.put("MODEL", qss);
				result.put("UPDATED", fieldsToUpdate);
				result.put("REMOVED", fieldsToRemove);
				result.put("ADDED", fieldsToAdd);
				result.put("CUSTOMFIELDS", customFields);
				result.put("STATUS", "OK");
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