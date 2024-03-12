package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zeroturnaround.zip.ByteSource;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipEntrySource;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "ZipPRJ", urlPatterns = { "/ZipPRJ" })
public class ZipPRJServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZipPRJServlet() {
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

			Project project = (Project) request.getSession().getAttribute("currentProject");
			
			String prjName = project.getName();
			
			Path dlDir = Paths.get(wks + "/downloads");
			
			if(Files.notExists(dlDir)) {
				Files.createDirectory(dlDir);
				dlDir.toFile().setExecutable(true, false);
				dlDir.toFile().setReadable(true, false);
				dlDir.toFile().setWritable(true, false);
			}
			

			Path temp = Paths.get(dlDir + "/temp.zip");
			Path zip = Paths.get(dlDir + "/project.zip");
			
			if(Files.exists(prj)){
				ZipUtil.pack(prj.toFile(), temp.toFile(), new NameMapper() {
					public String map(String name) {
						return prjName + "/" + name;
					}
				});
				
				ZipEntrySource[] entries = new ZipEntrySource[] {
					new ByteSource("project.json", Tools.toJSON(project).getBytes())
				};
				ZipUtil.addEntries(temp.toFile(), entries, zip.toFile());			
				
				Files.deleteIfExists(temp);
				
			}
			
			

			if(Files.exists(zip)) {
				zip.toFile().setReadable(true, false);
				zip.toFile().setWritable(true, false);
				zip.toFile().setExecutable(true, false);
				result.put("MESSAGE", "Project saved successfully in " + zip.getFileName());
				result.put("STATUS", "OK");
			}
			else {
				result.put("STATUS", "KO");
				throw new Exception(zip.getFileName() + " not found.");
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