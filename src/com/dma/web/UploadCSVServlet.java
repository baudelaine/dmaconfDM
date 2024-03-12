package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UploadCSV", urlPatterns = { "/UploadCSV" })
public class UploadCSVServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadCSVServlet() {
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

			Path csv = null;
			
			if(ServletFileUpload.isMultipartContent(request)){
				
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// item is the file (and not a field)
						csv = Paths.get(prj + "/" + item.getName());
//						Files.copy(new BufferedInputStream(item.getInputStream()), csv, StandardCopyOption.REPLACE_EXISTING);
						csv.toFile().setWritable(true);

						String fileName = csv.getFileName().toString();
						
						LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(item.getInputStream(), StandardCharsets.UTF_8)));

						List<String> lines = new ArrayList<String>();
						String line;
					    while ((line = reader.readLine()) != null) {
					    	
					    	if(fileName.startsWith("tableLabel") && line.split(";").length >= 2) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    	if(fileName.startsWith("tableDescription") && line.split(";").length >= 2) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    	if(fileName.startsWith("columnLabel") && line.split(";").length >= 3) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    	if(fileName.startsWith("columnDescription") && line.split(";").length >= 3) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    	if(fileName.equalsIgnoreCase("relation.csv") && line.split(";").length >= 7) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    	if(fileName.equalsIgnoreCase("relationExp.csv") && line.split(";").length >= 4) {
					    		lines.add(line.replaceAll("\"", ""));
					    	}
					    }
						
					    Files.write(csv, lines, StandardCharsets.UTF_8);
						csv.toFile().setReadable(true);
					}
				}
			}

			
			List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
			
			LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(csv.toFile())));
			try{
				for (String line; ((line = reader.readLine()) != null) && reader.getLineNumber() <= 2; ) {
					Map<String, Object> data = new HashMap<String, Object>();
					String fileName = csv.getFileName().toString();
					
					if(fileName.startsWith("tableLabel")) {
						if(line.split(";").length != 2) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
						if(reader.getLineNumber() == 1 && ( !line.equalsIgnoreCase("TABLE_NAME;TABLE_LABEL") && !line.equalsIgnoreCase("TABLE_ALIAS;TABLE_LABEL"))) {
							Files.deleteIfExists(csv);
							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
							throw new Exception("First row have to match column headers.");
						}
						data.put("tableName", line.split(";")[0]);
						data.put("tableLabel", line.split(";")[1]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
					}
					else if(fileName.startsWith("tableDescription")) {
						if(line.split(";").length != 2) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
						if(reader.getLineNumber() == 1 && (!line.equalsIgnoreCase("TABLE_NAME;TABLE_DESCRIPTION") && !line.equalsIgnoreCase("TABLE_ALIAS;TABLE_DESCRIPTION"))) {
							Files.deleteIfExists(csv);
							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
							throw new Exception("First row have to match column headers.");
						}
						data.put("tableName", line.split(";")[0]);
						data.put("tableDescription", line.split(";")[1]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
						
					}
					else if(fileName.startsWith("columnLabel")) {
						if(line.split(";").length != 3) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
						if(reader.getLineNumber() == 1 && (!line.equalsIgnoreCase("TABLE_NAME;COLUMN_NAME;COLUMN_LABEL") && !line.equalsIgnoreCase("TABLE_ALIAS;COLUMN_NAME;COLUMN_LABEL"))) {
							Files.deleteIfExists(csv);
							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
							throw new Exception("First row have to match column headers.");
						}
						data.put("tableName", line.split(";")[0]);
						data.put("columnName", line.split(";")[1]);
						data.put("columnLabel", line.split(";")[2]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
					}
					else if(fileName.startsWith("columnDescription")) {
						if(line.split(";").length != 3) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
						if(reader.getLineNumber() == 1 && (!line.equalsIgnoreCase("TABLE_NAME;COLUMN_NAME;COLUMN_DESCRIPTION") && !line.equalsIgnoreCase("TABLE_ALIAS;COLUMN_NAME;COLUMN_DESCRIPTION"))) {
							Files.deleteIfExists(csv);
							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
							throw new Exception("First row have to match column headers.");
						}
						data.put("tableName", line.split(";")[0]);
						data.put("columnName", line.split(";")[1]);
						data.put("columnDescription", line.split(";")[2]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
					}
					else if(fileName.equalsIgnoreCase("relation.csv")) {
						if(line.split(";").length != 7) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
						if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME")) {
							Files.deleteIfExists(csv);
							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
							throw new Exception("First row have to match column headers.");
						}
						data.put("FK_NAME", line.split(";")[0]);
						data.put("PK_NAME", line.split(";")[1]);
						data.put("FKTABLE_NAME", line.split(";")[2]);
						data.put("PKTABLE_NAME", line.split(";")[3]);
						data.put("KEY_SEQ", line.split(";")[4]);
						data.put("FKCOLUMN_NAME", line.split(";")[5]);
						data.put("PKCOLUMN_NAME", line.split(";")[6]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
					}
					else if(fileName.equalsIgnoreCase("relationExp.csv")) {
						if(line.split(";").length != 4) {
							Files.deleteIfExists(csv);
							throw new ArrayIndexOutOfBoundsException();
						}
//						if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME")) {
//							Files.deleteIfExists(csv);
//							result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//							throw new Exception("First row have to match column headers.");
//						}
						data.put("JOIN_NAME", line.split(";")[0]);
						data.put("FKTABLE_NAME", line.split(";")[1]);
						data.put("PKTABLE_NAME", line.split(";")[2]);
						data.put("RELATION_EXPRESSION", line.split(";")[3]);
						result.put("STATUS", "OK");
						result.put("MESSAGE", "CSV successfully saved in " + csv.toString());
					}
					
//					switch(csv.getFileName().toString()) {
//						case "tableLabel.csv":
//							if(line.split(";").length != 2) {
//								Files.deleteIfExists(csv);
//								throw new ArrayIndexOutOfBoundsException();
//							}
//							if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("TABLE_NAME;TABLE_LABEL")) {
//								Files.deleteIfExists(csv);
//								result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//								throw new Exception("First row have to match column headers.");
//							}
//							data.put("tableName", line.split(";")[0]);
//							data.put("tableLabel", line.split(";")[1]);
//							break;
//						case "tableDescription.csv":
//							if(line.split(";").length != 2) {
//								Files.deleteIfExists(csv);
//								throw new ArrayIndexOutOfBoundsException();
//							}
//							if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("TABLE_NAME;TABLE_DESCRIPTION")) {
//								Files.deleteIfExists(csv);
//								result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//								throw new Exception("First row have to match column headers.");
//							}
//							data.put("tableName", line.split(";")[0]);
//							data.put("tableDescription", line.split(";")[1]);
//							break;
//						case "columnLabel.csv":
//							if(line.split(";").length != 3) {
//								Files.deleteIfExists(csv);
//								throw new ArrayIndexOutOfBoundsException();
//							}
//							if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("TABLE_NAME;COLUMN_NAME;COLUMN_LABEL")) {
//								Files.deleteIfExists(csv);
//								result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//								throw new Exception("First row have to match column headers.");
//							}
//							data.put("tableName", line.split(";")[0]);
//							data.put("columnName", line.split(";")[1]);
//							data.put("columnLabel", line.split(";")[2]);
//							break;
//						case "columnDescription.csv":
//							if(line.split(";").length != 3) {
//								Files.deleteIfExists(csv);
//								throw new ArrayIndexOutOfBoundsException();
//							}
//							if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("TABLE_NAME;COLUMN_NAME;COLUMN_DESCRIPTION")) {
//								Files.deleteIfExists(csv);
//								result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//								throw new Exception("First row have to match column headers.");
//							}
//							data.put("tableName", line.split(";")[0]);
//							data.put("columnName", line.split(";")[1]);
//							data.put("columnDescription", line.split(";")[2]);
//							break;
//						case "relation.csv":
//							if(line.split(";").length != 7) {
//								Files.deleteIfExists(csv);
//								throw new ArrayIndexOutOfBoundsException();
//							}
//							if(reader.getLineNumber() == 1 && !line.equalsIgnoreCase("FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME")) {
//								Files.deleteIfExists(csv);
//								result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
//								throw new Exception("First row have to match column headers.");
//							}
//							data.put("FK_NAME", line.split(";")[0]);
//							data.put("PK_NAME", line.split(";")[1]);
//							data.put("FKTABLE_NAME", line.split(";")[2]);
//							data.put("PKTABLE_NAME", line.split(";")[3]);
//							data.put("KEY_SEQ", line.split(";")[4]);
//							data.put("FKCOLUMN_NAME", line.split(";")[5]);
//							data.put("PKCOLUMN_NAME", line.split(";")[6]);
//							break;
//						default:
//					}
					
					if(reader.getLineNumber() > 1) {
						datas.add(data);
					}

				}
			}
			finally{
				reader.close();
			}			
			
			result.put("DATAS", datas);
			
		}
		
		catch(ArrayIndexOutOfBoundsException e) {
			result.put("STATUS", "KO");
			result.put("EXCEPTION", e.getClass().getName());
			result.put("MESSAGE", "Incorrect CSV format.");
			result.put("TROUBLESHOOTING", "Have a look at CSV format and e.g.");
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