package com.dma.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
@WebServlet(name = "DLPrj", urlPatterns = { "/DLPrj" })
public class DLPrjServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DLPrjServlet() {
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
			
			Path dlDir = Paths.get(wks + "/downloads");
			
			Path zip = Paths.get(dlDir + "/project.zip");
			
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
				response.addHeader("Content-Disposition", "attachment; filename=project.zip");
				response.setContentLength(size);
				
				OutputStream out = response.getOutputStream();
				out.write(bos.toByteArray(), 0, size);
	            out.flush();
	            out.close();
	            response.flushBuffer();			
				
				if(bos != null) {bos.close();}
				if(buf != null) {buf.close();}
				
			}
		}
		
		catch (Exception e) {
			// TODO Auto-generated catch block
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