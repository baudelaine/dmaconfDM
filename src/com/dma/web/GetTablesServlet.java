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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetTables")
public class GetTablesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTablesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection con = null;
		ResultSet rst = null;
		Map<String, Object> result = new HashMap<String, Object>();
		String schema = "";

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
			
			@SuppressWarnings("unchecked")
			Map<String, QuerySubject> qsFromXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");
		    Map<String, String> tables = new HashMap<String, String>();
			
			if(qsFromXML == null) {
				con = (Connection) request.getSession().getAttribute("con");
				schema = (String) request.getSession().getAttribute("schema");
				
			    DatabaseMetaData metaData = con.getMetaData();
			    
//			    String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
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
				    System.out.println("types=" + types);
			    }
			    
			    rst = metaData.getTables(con.getCatalog(), schema, "%", types);	
			    
			    while (rst.next()) {
			    	tables.put(rst.getString("TABLE_NAME"), rst.getString("TABLE_TYPE"));
//			    	System.out.println(rst.getString("TABLE_NAME") + " - " + rst.getString("TABLE_TYPE"));
			    }		    
			    result.put("TABLES", tables);
			    
			    rst.close();
			}
			else {
				for(Entry<String, QuerySubject> qs: qsFromXML.entrySet()) {
					tables.put(qs.getValue().getTable_name(), qs.getValue().getType());
				}
			    result.put("TABLES", tables);
			}
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
