package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
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

import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "RunRelationsQuery", urlPatterns = { "/RunRelationsQuery" })
public class BuildRelationsQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuildRelationsQueryServlet() {
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
			
			if(parms != null && parms.get("query") != null) {

				String query = (String) parms.get("query");

				if(StringUtils.countMatches(query, " ? ") == 1) {
				
					Connection con = (Connection) request.getSession().getAttribute("con");
					DatabaseMetaData metaData = con.getMetaData();
					String schema = (String) request.getSession().getAttribute("schema");

//				    String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
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
					
					while (rst0.next()) {

				    	String table_name = rst0.getString("TABLE_NAME");
					
					
				    	PreparedStatement stmt = con.prepareStatement(query);
					
					}
					if(rst0 != null) {rst0.close();}
					
					result.put("STATUS", "OK");
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