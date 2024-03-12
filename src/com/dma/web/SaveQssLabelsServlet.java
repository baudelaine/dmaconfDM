package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
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

import org.zeroturnaround.zip.ZipUtil;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "SaveQssLabels", urlPatterns = { "/SaveQssLabels" })
public class SaveQssLabelsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveQssLabelsServlet() {
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
			
			Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
			
			if(parms.containsKey("qss") && parms.containsKey("delim") && parms.containsKey("lang")) {

				String delim = (String) parms.get("delim");
				String lang = (String) parms.get("lang");
				
				@SuppressWarnings("unchecked")
				List<QuerySubject> qss = (List<QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<List<QuerySubject>>(){});				
				
				String tlHeader = "TABLE_NAME" + delim + "TABLE_LABEL";				
				List<String> tlLines = new ArrayList<String>();
				tlLines.add(tlHeader);

				String tdHeader = "TABLE_NAME" + delim + "TABLE_DESCRIPTION";				
				List<String> tdLines = new ArrayList<String>();
				tdLines.add(tdHeader);

				String clHeader = "TABLE_NAME" + delim + "COLUMN_NAME" + delim + "COLUMN_LABEL";				
				List<String> clLines = new ArrayList<String>();
				clLines.add(clHeader);

				String cdHeader = "TABLE_NAME" + delim + "COLUMN_NAME" + delim + "COLUMN_DESCRIPTION";				
				List<String> cdLines = new ArrayList<String>();
				cdLines.add(cdHeader);
				
				for(QuerySubject qs: qss) {
					StringBuffer tlBuf = new StringBuffer();
					StringBuffer tdBuf = new StringBuffer();
					tlBuf.append(qs.getTable_name());
					tdBuf.append(qs.getTable_name());
					String label = "";
					if(qs.getDescriptions().containsKey(lang)) {
						label = qs.getLabels().get(lang);
					}
					String description = "";
					if(qs.getDescriptions().containsKey(lang)) {
						description = qs.getDescriptions().get(lang);
					}
					tlBuf.append(delim + label);
					tdBuf.append(delim + description);
					tlLines.add(tlBuf.toString());
					tdLines.add(tdBuf.toString());

					for(Field field: qs.getFields()) {
						StringBuffer clBuf = new StringBuffer();
						StringBuffer cdBuf = new StringBuffer();
						clBuf.append(qs.getTable_name() + delim + field.getField_name());
						cdBuf.append(qs.getTable_name() + delim + field.getField_name());
						String cLabel = "";
//						String cLabel = "";
						if(field.getLabels().containsKey(lang)) {
							cLabel = field.getLabels().get(lang);
						}
						String cDescription = "";
						if(field.getLabels().containsKey(lang)) {
							cDescription = field.getDescriptions().get(lang);
						}
						clBuf.append(delim + cLabel);
						cdBuf.append(delim + cDescription);
						clLines.add(clBuf.toString());
						cdLines.add(cdBuf.toString());
					}
				}

				Path dlDir = Paths.get(prj + "/labels");
				
				if(Files.notExists(dlDir)) {
					Files.createDirectory(dlDir);
					dlDir.toFile().setExecutable(true, false);
					dlDir.toFile().setReadable(true, false);
					dlDir.toFile().setWritable(true, false);
				}
				
				Path tlPath = Paths.get(dlDir + "/table-labels-" + lang + ".csv");
				Path tdPath = Paths.get(dlDir + "/table-descriptions-" + lang + ".csv");
				Path clPath = Paths.get(dlDir + "/column-labels-" + lang + ".csv");
				Path cdPath = Paths.get(dlDir + "/column-descriptions-" + lang + ".csv");
				
				Files.write(tlPath, tlLines, Charset.defaultCharset());
				tlPath.toFile().setReadable(true, false);
				
				Files.write(tdPath, tdLines, Charset.defaultCharset());
				tdPath.toFile().setReadable(true, false);

				Files.write(clPath, clLines, Charset.defaultCharset());
				tlPath.toFile().setReadable(true, false);
				
				Files.write(cdPath, cdLines, Charset.defaultCharset());
				tdPath.toFile().setReadable(true, false);
				
				Path zip = Paths.get(prj + "/labels.zip");
				
				if(Files.exists(dlDir)){
					ZipUtil.pack(dlDir.toFile(), zip.toFile());		
				}
				
				if(Files.exists(zip)) {				
					result.put("STATUS", "OK");
					result.put("MESSAGE", "Labels for lang " + lang + " saved successfully. Download started...");
				}
			}
			else {
				result.put("STATUS", "KO");
				result.put("MESSAGE", "Input parameters are not valid.");
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