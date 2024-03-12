package com.dma.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/GetResources")
public class GetResourcesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetResourcesServlet() {
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

		
		result = (Map<String, Object>) request.getServletContext().getAttribute("resources");
		
		if(result != null) {
		
			Resource xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("ORA");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("ORAXML", xml);
			
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("DB2");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("DB2XML", xml);
	
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("DB2400");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("DB2400XML", xml);
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("SQLSRV");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("SQLSRVXML", xml);
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("MYSQL");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("MYSQLXML", xml);
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("PGSQL");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("PGSQLXML", xml);
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("IFX");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("IFXXML", xml);
			
			xml = new Resource();
			xml.setJndiName("XML");
			xml.setDbEngine("TD");
			xml.setDescription("Cognos model with PHYSICAL namespace");
			result.put("TDXML", xml);
		
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(Tools.toJSON(result));			

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

