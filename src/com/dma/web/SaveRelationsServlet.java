package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "SaveRelations", urlPatterns = { "/SaveRelations" })
public class SaveRelationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveRelationsServlet() {
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
			DatabaseMetaData metaData = con.getMetaData();
			String schema = (String) request.getSession().getAttribute("schema");
			@SuppressWarnings("unchecked")
			Map<String, String> tableAliases = (Map<String, String>) request.getSession().getAttribute("tableAliases");
			
            List<String> tables = new ArrayList<String>();
            
//		    String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
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
		    
		    ResultSet rst = metaData.getTables(con.getCatalog(), schema, "%", types);	
		    
		    while (rst.next()) {
		    	tables.add(rst.getString("TABLE_NAME"));
		    }
			if(rst != null) {rst.close();}
			
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			BufferedOutputStream buf = new BufferedOutputStream(bos);
			
			Path dlDir = Paths.get(prj + "/downloads");
			
			if(Files.notExists(dlDir)) {
				Files.createDirectory(dlDir);
				dlDir.toFile().setExecutable(true, false);
				dlDir.toFile().setReadable(true, false);
				dlDir.toFile().setWritable(true, false);
			}
			
			Path relFile = Paths.get(dlDir + "/relations.csv");
			List<String> lines = new ArrayList<String>();
			
			String header = "FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME";
			
			lines.add(header);
			
			for(String table: tables){
				
		    	Connection csvCon = null;
		    	PreparedStatement stmt = null;

		    	String FKQuery = (String) request.getSession().getAttribute("FKQuery");
				if(Files.exists(Paths.get(prj + "/relation.csv"))) {
					Properties props = new java.util.Properties();
					props.put("separator",";");
					csvCon = DriverManager.getConnection("jdbc:relique:csv:" + prj.toString(), props);
					String sql = "SELECT * FROM relation where FKTABLE_NAME = '" + table + "'";
					stmt = csvCon.prepareStatement(sql);
					rst = stmt.executeQuery();
					result.put("FKS", "CSV");
				}
				else if(FKQuery != null && !FKQuery.isEmpty()) {
					FKQuery = StringUtils.replace(FKQuery, " $TABLE", " '" + table + "'");
					stmt = con.prepareStatement(FKQuery);
//					stmt.setString(1, table);
		    		rst = stmt.executeQuery();
					result.put("FKS", "SQL");
		    	}
				else {
					rst = metaData.getImportedKeys(con.getCatalog(), schema, table);
					result.put("FKS", "DB");
				}
				
				while(rst.next()){
					StringBuffer key = new StringBuffer();
					
					key.append(rst.getString("FK_NAME") + ";");
					key.append(rst.getString("PK_NAME") + ";");
					key.append(rst.getString("FKTABLE_NAME") + ";");
					key.append(rst.getString("PKTABLE_NAME") + ";");
					key.append(rst.getShort("KEY_SEQ") + ";");
					key.append(rst.getString("FKCOLUMN_NAME") + ";");
					key.append(rst.getString("PKCOLUMN_NAME"));

					String pktable_name = rst.getString("PKTABLE_NAME");
					
					boolean isAlias = false;
			        if(tableAliases != null){
				        if(tableAliases.containsKey(pktable_name)){
				        	pktable_name = tableAliases.get(pktable_name);
				        	isAlias = true;
				        }
			        }
			        // Jump to other key if pktable is an alias
			        if(isAlias){continue;}
			        
					lines.add(key.toString());
					
				}
				if(rst != null) {rst.close();}
				if(stmt != null) {stmt.close();}
				if(csvCon != null) {csvCon.close();}
//				buf.flush();
				
			}
			
			Files.write(relFile, lines);
			relFile.toFile().setReadable(true, false);
//			buf.flush();
//			size = bos.toByteArray().length;
//			
//			response.setContentType("text/csv");
//			response.addHeader("Content-Disposition", "attachment; filename=relation.csv");
//			response.setContentLength(size);
//			
//			OutputStream out = response.getOutputStream();
//			out.write(bos.toByteArray(), 0, size);
//            out.flush();
//            out.close();
//            response.flushBuffer();			
//			
//			if(bos != null) {bos.close();}
//			if(buf != null) {buf.close();}
		
			if(Files.exists(relFile)) {
				result.put("STATUS", "OK");
				result.put("MESSAGE", "Relations saved successfully in " + relFile.getFileName());
			}
			else {
				result.put("STATUS", "KO");
				throw new Exception(relFile.getFileName() + " not found.");
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