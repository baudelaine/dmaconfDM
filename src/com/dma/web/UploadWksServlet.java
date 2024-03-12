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
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UploadWks", urlPatterns = { "/UploadWks" })
public class UploadWksServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadWksServlet() {
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
			
			Path prj = null;
			
			try {
				prj = Paths.get((String) request.getSession().getAttribute("projectPath"));
				result.put("PRJ", prj.toString());
			}
			catch(NullPointerException npe) {
				// Catch in case no project is already loaded
			}
			
			if(ServletFileUpload.isMultipartContent(request)){
				
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						Path zip = Paths.get("/tmp" + "/" + user + "-" + Tools.getCurrentTimestamp() + ".zip");
						Files.copy(new BufferedInputStream(item.getInputStream()), zip, StandardCopyOption.REPLACE_EXISTING);
						
						if(!ZipUtil.containsEntry(zip.toFile(), "projects.json")) {
							result.put("STATUS", "KO");
							throw new Exception("Archive is not valid. No projects.json found in it.");
						}
						
						byte[] json = ZipUtil.unpackEntry(zip.toFile(), "projects.json");
						
						
						try {
							Tools.fromJSON(new ByteArrayInputStream(json), new TypeReference<Map<String, Project>>(){});
						}
						catch(Exception e) {
							result.put("STATUS", "KO");
							throw new Exception("Archive is not valid. projects.json is not a valid Project Map object.");
						}
						
						
						FileUtils.deleteDirectory(wks.toFile());
						Files.createDirectory(wks);
						wks.toFile().setExecutable(true, false);
						wks.toFile().setReadable(true, false);
						wks.toFile().setWritable(true, false);
						
						ZipUtil.unpack(zip.toFile(), wks.toFile());
						
						Files.deleteIfExists(zip);
					    
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
				result.put("MESSAGE", "Workspace was upload successfully");
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