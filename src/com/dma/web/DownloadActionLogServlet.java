package com.dma.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "DownloadActionLog", urlPatterns = { "/DownloadActionLog" })
public class DownloadActionLogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadActionLogServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> result = new HashMap<String, Object>();
		int size = 0;

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
			
			Path dlDir = Paths.get(prj + "/downloads");

			Path zip = Paths.get(dlDir + "/actionlogs.zip");
			
			if(Files.exists(zip)) {
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(zip.toFile()));
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				BufferedOutputStream buf = new BufferedOutputStream(bos);
				
				byte[] buffer = new byte[1024];
				int read;
				while ((read = reader.read(buffer)) >= 0) {
				    bos.write(buffer, 0, read);
				}
				
				reader.close();
				size = bos.toByteArray().length;
				
				response.setContentType("application/zip");
				response.addHeader("Content-Disposition", "attachment; filename=actionlogs.zip");
				response.setContentLength(size);
				
				OutputStream out = response.getOutputStream();
				out.write(bos.toByteArray(), 0, size);
	            out.flush();
	            out.close();
	            response.flushBuffer();			
				
				if(bos != null) {bos.close();}
				if(buf != null) {buf.close();}
				
				
				result.put("MESSAGE", "Download started...");
				result.put("STATUS", "OK");
			}
			else {
				result.put("STATUS", "KO");
				result.put("ERROR", "actionlogs.zip not found.");
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