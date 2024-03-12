package com.dma.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UploadActionLog", urlPatterns = { "/UploadActionLog" })
public class UploadActionLogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadActionLogServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> parms = new HashMap<String, Object>();
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
			
			//Needed if read file line by line and load a List<String> 
//			List<String> lines = new ArrayList<String>();
			
			//Either handle multipart/form-data ajax enctype
			if(ServletFileUpload.isMultipartContent(request)){
				
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						//Item is the file (and not a field)
						//Either read file line by line and load a List<String> for future use
//						LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(item.getInputStream())));
//						String line;
//					    while ((line = reader.readLine()) != null) {
//					    	lines.add(line);
//					    }
//						result.put("READING", "OK");
					    //Or write file in project
						
						Path dir = Paths.get(prj + "/actionLogs");
						if(!Files.exists(dir)) {
							Files.createDirectory(dir);
							dir.toFile().setReadable(true, false);
							dir.toFile().setWritable(true, false);
							dir.toFile().setExecutable(true, false);
							
						}
						
						Path file = Paths.get(prj + "/actionLogs.json");
						Map<String, Object> actionLogMap = new HashMap<String, Object>();
						if(Files.exists(file)) {
							actionLogMap =  (Map<String, Object>) Tools.fromJSON(file.toFile(), new TypeReference<Map<String, Object>>(){});
						}
						
						if(actionLogMap.containsKey(item.getName())) {
							result.put("STATUS", "KO");
							result.put("ERROR", "ActionLog " + item.getName() + " already exists.");
							result.put("TROUBLESHOOTING", "Choose another file or rename it.");
							throw new Exception();
						}
						else {
							actionLogMap.put(item.getName(), null);
							Files.write(file, Tools.toJSON(actionLogMap).getBytes());
							file.toFile().setReadable(true, false);
							file.toFile().setWritable(true, false);
	
							file = Paths.get(dir + "/" + item.getName());
							Files.copy(new BufferedInputStream(item.getInputStream()), file, StandardCopyOption.REPLACE_EXISTING);
							file.toFile().setReadable(true, false);
							file.toFile().setWritable(true, false);
							result.put("FILENAME", item.getName());
						}
						
					}
					else {
						//Item is field (and not a file)
						if (item.isFormField()) {
							item.getFieldName();
				            String value = item.getString();
				            parms = Tools.fromJSON(new ByteArrayInputStream(value.getBytes()));
				            result.put("PARMS", parms);
						}
					}
				}
				result.put("STATUS", "OK");
			}
			//Or handle ajax json dataType
			else {
				parms = Tools.fromJSON(request.getInputStream());
				result.put("PARMS", parms);

				if(parms != null) {
					result.put("DATAS", "Blablabla...");
				}
				else {
					result.put("STATUS", "KO");
					result.put("MESSAGE", "Input parameters are not valid.");
					result.put("TROUBLESHOOTING", "Blablabla...");
					throw new Exception();
				}			
				result.put("STATUS", "OK");
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