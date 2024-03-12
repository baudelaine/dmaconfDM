package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetFields")
public class GetFieldsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFieldsServlet() {
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
		Connection con = null;
		ResultSet rst = null;
		List<Field> fields = new ArrayList<Field>();
		String schema = "";
		Map<String, DBMDTable> dbmd = null;
		String language = "";

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
			
			if(parms != null && parms.get("table") != null) {
				String table = (String) parms.get("table");

				Map<String, QuerySubject> qsFromXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");				
				if(qsFromXML != null) {
					fields = qsFromXML.get(table).getFields();
				}
				else {
					con = (Connection) request.getSession().getAttribute("con");
					schema = (String) request.getSession().getAttribute("schema");
					dbmd = (Map<String, DBMDTable>) request.getSession().getAttribute("dbmd");
					Project project = (Project) request.getSession().getAttribute("currentProject");
					language = project.languages.get(0);
					
				    DatabaseMetaData metaData = con.getMetaData();
				    
				    rst = metaData.getPrimaryKeys(con.getCatalog(), schema, table);
				    Set<String> pks = new HashSet<String>();
				    
				    while (rst.next()) {
				    	pks.add(rst.getString("COLUMN_NAME"));
				    }
	
			        if(rst != null){rst.close();}
	
				    rst = metaData.getIndexInfo(con.getCatalog(), schema, table, false, true);
				    Set<String> indexes = new HashSet<String>();
				    
				    while (rst.next()) {
				    	indexes.add(rst.getString("COLUMN_NAME"));
				    }
	
			        if(rst != null){rst.close();}
			        
			        rst = metaData.getColumns(con.getCatalog(), schema, table, "%");
			        
			        Map<String, DBMDColumn> dbmdColumns = null;
			        if(dbmd != null){
						DBMDTable dbmdTable = dbmd.get(table);
						dbmdColumns = dbmdTable.getColumns();
			        }
			        
			        while (rst.next()) {
			        	String field_name = rst.getString("COLUMN_NAME");
			        	String field_type = rst.getString("TYPE_NAME");
			        	String field_remarks = rst.getString("REMARKS");
			        	String field_desc = "";
			        	
			    		if(field_remarks == null) {
			    			field_remarks = "";
			    		}
			    		else {
		    		    	field_desc = field_remarks;
		    	    		field_remarks = field_remarks;
			    		}
			        	
			        	Field field = new Field();
			        	field.setField_name(field_name);
			        	field.setField_type(field_type);
			        	field.setLabel(field_remarks);
			        	field.setDescription(field_desc);
		        		if(!language.isEmpty()) {
		        			field.getLabels().put(language, field_remarks);
		        			field.getDescriptions().put(language, field_desc);
		        		}
			        	field.set_id(field_name);
			        	if(pks.contains(rst.getString("COLUMN_NAME"))){
			    			field.setPk(true);
			    		}
			        	if(indexes.contains(rst.getString("COLUMN_NAME"))){
			        		field.setIndexed(true);
			        	}
			        	if(dbmdColumns != null){
			    			DBMDColumn dbmdColumn = dbmdColumns.get(field_name);
			    			if(dbmdColumns != null){
			    				String label = "";
			    				try {
				    				if(dbmdColumn.getColumn_remarks() != null) {
				    					label = dbmdColumn.getColumn_remarks();
				    				}
			    				}
			    				catch (NullPointerException e) {
			    				}
			    				
				    			field.setLabel(label);
				    			String desc = "";
				    			try {
					    			if(dbmdColumn.getColumn_description() != null) {
					    				desc = dbmdColumn.getColumn_description();
					    			}
			    				}
			    				catch (NullPointerException e) {
			    				}
				    			field.setDescription(desc);
				           		if(!language.isEmpty()) {
				        			field.getLabels().put(language, label);
				        			field.getDescriptions().put(language, desc);
				        		}	    			
			    			}
			    		}
			        	
			        	fields.add(field);
			        }
				    
			        if(rst != null){rst.close();}				
				}
		        
		        result.put("DATAS", fields);
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
