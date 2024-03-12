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
@WebServlet("/GetNewRelation")
public class GetNewRelationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewRelationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Relation relation = null;
		Connection con = null;
		DatabaseMetaData metaData = null;
		String schema = "";
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
			
			if(parms != null && parms.get("table") != null) {

				String table = (String) parms.get("table");
				Project project = (Project) request.getSession().getAttribute("currentProject");
				String language = project.languages.get(0);			
				relation = new Relation();
				
				@SuppressWarnings("unchecked")
				Map<String, QuerySubject> qsFromXML = (Map<String, QuerySubject>) request.getSession().getAttribute("QSFromXML");				
				if(qsFromXML != null) {
    	    		relation.setDescription("");
    				if(!language.isEmpty()) {
    					relation.getLabels().put(language, "");
    					relation.getDescriptions().put(language, "");
    				}					
				}
				else {
				
					@SuppressWarnings("unchecked")
					Map<String, DBMDTable> dbmd = (Map<String, DBMDTable>) request.getSession().getAttribute("dbmd");
					
					con = (Connection) request.getSession().getAttribute("con");
					schema = (String) request.getSession().getAttribute("schema");
		        	metaData = con.getMetaData();
		        	
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
		        	
		    		ResultSet rst0 = metaData.getTables(con.getCatalog(), schema, table, types);
		    		String label = "";
		    		String desc = "";
		    		while (rst0.next()) {
		    			label = rst0.getString("REMARKS");
		    			relation.setLabel(label);
		    	    	
		    	    	if(label == null) {
		    	    		label = "";
		    	    		relation.setLabel(label);
		    	    		relation.setDescription(desc);
		    				if(!language.isEmpty()) {
		    					relation.getLabels().put(language, label);
		    					relation.getDescriptions().put(language, desc);
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
			        
					if(dbmd != null){
						DBMDTable dbmdTable = dbmd.get(table);
						if(dbmdTable != null){
							label = dbmdTable.getTable_remarks(); 
							desc = dbmdTable.getTable_description();
							relation.setLabel(label);
							relation.setDescription(desc);
							
							if(!language.isEmpty()) {
								relation.getLabels().put(language, label);
								relation.getDescriptions().put(language, desc);
							}
						}
					}
				}
				
				result.put("DATAS", relation);
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
