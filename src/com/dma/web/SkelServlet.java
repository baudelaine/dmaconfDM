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
@WebServlet(name = "Skel", urlPatterns = { "/Skel" })
public class SkelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SkelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
						Path file = Paths.get(prj + "/" + item.getName());
						Files.copy(new BufferedInputStream(item.getInputStream()), file, StandardCopyOption.REPLACE_EXISTING);
						file.toFile().setReadable(true, false);
						file.toFile().setWritable(true, false);
						result.put("WRITING", item.getName());
					    
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
					result.put("ERROR", "Input parameters are not valid.");
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