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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "DLErrorFile", urlPatterns = { "/DLErrorFile" })
public class DLErrorFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DLErrorFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		try {
			
			Path prj = Paths.get((String) request.getSession().getAttribute("projectPath"));
			Path dlDir = Paths.get(prj + "/downloads");
			Path relFile = Paths.get(dlDir + "/error.json");
			
			if(Files.exists(relFile)) {
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(relFile.toFile()));
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				BufferedOutputStream buf = new BufferedOutputStream(bos);
				
				byte[] buffer = new byte[1024];
				int read;
				while ((read = reader.read(buffer)) >= 0) {
				    bos.write(buffer, 0, read);
				}
				
				reader.close();
				int size = bos.toByteArray().length;
				
				response.setContentType("application/json");
				response.addHeader("Content-Disposition", "attachment; filename=error.json");
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