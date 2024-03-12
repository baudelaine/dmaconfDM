package com.dma.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/BuildTestQuery")
public class BuildTestQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuildTestQueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection con = null;
		Map<String, Object> results = new HashMap<String, Object>();
		String schema = null;
		String dbEngine = null;
		ResultSet rst = null;
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	        Map<String, Object>	parms = new HashMap<String, Object>();
	        parms = mapper.readValue(br, new TypeReference<Map<String, Object>>(){});
		        
			con = (Connection) request.getSession().getAttribute("con");
			schema = (String) request.getSession().getAttribute("schema");
			dbEngine = (String) request.getSession().getAttribute("dbEngine");
			if((dbEngine).equalsIgnoreCase("ORA")){
				con.createStatement().execute("alter session set current_schema=" + schema);
				con.createStatement().execute("ALTER SESSION SET NLS_SORT=BINARY_CI");
				con.createStatement().execute("ALTER SESSION SET NLS_COMP=LINGUISTIC");
			}

			String query = (String) parms.get("query");
			String type = (String) parms.get("type");
			List<String> tables = (List<String>) parms.get("tables");
			String tableInClause = Tools.splitInClause(query, tables, 1000);
			
			switch(type){
			
				case "relation":

					if(!query.isEmpty() && StringUtils.countMatches(query, " = $TABLE") == 1){
						query = StringUtils.replace(query, " = $TABLE", " IN " + tableInClause);
					}
					break;
					
				case "table": 
					
					if(!query.isEmpty() && StringUtils.countMatches(query, "($TABLES)") == 1){
						query = StringUtils.replace(query, "($TABLES)", tableInClause);
					}
					break;
					
				case "column":
					
					if(!query.isEmpty() && StringUtils.countMatches(query, " ($FIELDS)") == 1 && StringUtils.countMatches(query, " = $TABLE ") == 1){
						List<String> fields = new ArrayList<String>();
						
						DatabaseMetaData metaData = con.getMetaData();
						rst = metaData.getColumns(con.getCatalog(), schema, tables.get(0), "%");
						while(rst.next()){
							fields.add(rst.getString("COLUMN_NAME"));
						}
						rst.close();

						String columnInClause = Tools.splitInClause(query, fields, 1000);
						query = StringUtils.replace(query, " = $TABLE", " = '" + tables.get(0) + "'");
						query = StringUtils.replace(query, " ($FIELDS)", " " + columnInClause);

					}
					break;
					
			}
			
			results.put("query", query);
			System.out.println("results=" + results);
			
		    response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(results));
			
		}
		catch (Exception e){
			e.printStackTrace(System.err);
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
