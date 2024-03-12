package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "DownloadModel", urlPatterns = { "/DownloadModel" })
public class DownloadModelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadModelServlet() {
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
			
			parms = Tools.fromJSON(request.getInputStream());
//			result.put("PARMS", parms);

			if(parms != null) {
				
				@SuppressWarnings("unchecked")
				List<QuerySubject> qss = (List<QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<List<QuerySubject>>(){});		
				@SuppressWarnings("unchecked")
				List<QuerySubject> views = (List<QuerySubject>) Tools.fromJSON(parms.get("view").toString(), new TypeReference<List<QuerySubject>>(){});		
				
				Map<String, List<QuerySubject>> model = new HashMap<String, List<QuerySubject>>();
				
				model.put("querySubjects", qss);
				model.put("views", views);
				
				Path dlDir = Paths.get(prj + "/downloads");
				
				if(Files.notExists(dlDir)) {
					Files.createDirectory(dlDir);
					dlDir.toFile().setExecutable(true, false);
					dlDir.toFile().setReadable(true, false);
					dlDir.toFile().setWritable(true, false);
				}
				
				Path modelPath = Paths.get(dlDir + "/model.json");
				
				Files.write(modelPath, Tools.toJSON(model).getBytes());
				modelPath.toFile().setReadable(true, false);

				if(Files.exists(modelPath)) {
					result.put("STATUS", "OK");
					result.put("MESSAGE", "Model saved successfully in " + modelPath.getFileName());
				}
				else {
					result.put("STATUS", "KO");
					throw new Exception(modelPath.getFileName() + " not found.");
				}
				
			}
			else {
				result.put("STATUS", "KO");
				throw new Exception("Input parameters are not valid.");
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