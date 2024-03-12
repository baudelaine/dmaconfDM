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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetLabelsFromDBMD")
public class GetLabelsFromDBMDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLabelsFromDBMDServlet() {
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
			
			Connection con = (Connection) request.getSession().getAttribute("con");
			String schema = (String) request.getSession().getAttribute("schema");
			
		    DatabaseMetaData metaData = con.getMetaData();
		    
		    String[] types = {"TABLE"}; 
		    		
		    Project project = (Project) request.getSession().getAttribute("currentProject");
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
		    
		    ResultSet rst0 = metaData.getTables(con.getCatalog(), schema, "%", types);	

			Map<String, DBMDTable> dbmd = new HashMap<String, DBMDTable>();
		    
		    while (rst0.next()) {

		    	String table_name = rst0.getString("TABLE_NAME");
		    	
	            
	            DBMDTable table = new DBMDTable();
	            
		    	String table_type = rst0.getString("TABLE_TYPE");
		    	String table_remarks = rst0.getString("REMARKS");
		    	table.setTable_name(table_name);
		    	table.setTable_type(table_type);
		    	table.setTable_remarks(table_remarks);
		    	
		    	if(table_remarks != null) {
		    		table.setTable_remarks(table_remarks);
		    		table.setTable_description(table_remarks);
		    	}
		    	
			    ResultSet rst1 = metaData.getColumns(con.getCatalog(), schema, table_name, "%");
			    
			    Map<String, DBMDColumn> fields = new HashMap<String, DBMDColumn>();
			    
			    while(rst1.next()){
				    DBMDColumn field = new DBMDColumn();
			    	field.setColumn_name(rst1.getString("COLUMN_NAME"));
			    	field.setColumn_type(rst1.getString("TYPE_NAME"));
			    	String column_remarks = rst1.getString("REMARKS");
			    	if(column_remarks == null) {
			    		field.setColumn_remarks(column_remarks);
				    	field.setColumn_description(column_remarks);
			    	}
			    }
			    if(rst1 != null){rst1.close();}
			    table.setColumns(fields);
			    dbmd.put(table_name, table);
			    
		    }		    
		    
		    if(rst0 != null){rst0.close();}
			    
		    result.put("DATAS", dbmd);
			result.put("STATUS", "OK");
			
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
