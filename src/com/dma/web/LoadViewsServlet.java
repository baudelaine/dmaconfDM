package com.dma.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
@WebServlet(name = "LoadViews", urlPatterns = { "/LoadViews" })
public class LoadViewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadViewsServlet() {
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
			
			String delim = "\";\"";
			String lang = "fr"; 
			String header = "\"" + "TABLE_NAME" + delim + "TABLE_TYPE" + delim + "TABLE_LABEL" + delim + "TABLE_DESCRIPTION" + delim +
					"FIELD_ID" + delim  + "FIELD_NAME" + delim + "FIELD_TYPE" + delim + "FIELD_LABEL" + delim + "FIELD_DESCRIPTION" + delim +
					"EXPRESSION" + delim + "HIDDEN" + delim + "ICON" + delim + "ALIAS" + delim + "FOLDER" + delim + "ROLE" + delim + "ORIGINALTABLEALIAS" + "\"";				
			
			List<String> lines = new ArrayList<String>();
			
			if(ServletFileUpload.isMultipartContent(request)){
				
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// item is the file (and not a field)
						LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(item.getInputStream())));
						String line;
					    while ((line = reader.readLine()) != null) {
					    	lines.add(line);
					    }
					}
					else {
						// item is field (and not a file)
						if (item.isFormField()) {
							item.getFieldName();
				            String value = item.getString();
				            parms = Tools.fromJSON(new ByteArrayInputStream(value.getBytes()));
				            result.put("PARMS", parms);
						}
					}
				}
			}
			else {
				parms = Tools.fromJSON(request.getInputStream());
				result.put("PARMS", parms);
				
			}
			
			if(lines.get(0).equalsIgnoreCase(header) || lines.get(0).equalsIgnoreCase(header + delim)) {
				lines.remove(0);
			}
			else {
				result.put("STATUS", "KO");
				result.put("ERROR", "CSV header is not valid.");
				result.put("TROUBLESHOOTING", "e.g. " + header);
				throw new Exception();
			}
			
			if(parms != null && parms.get("lang") != null) {
				
				Map<String, QuerySubject> views = new HashMap<String, QuerySubject>();
				
				QuerySubject view = null;
				
				for(String line: lines) {
					if(! views.containsKey(line.split(delim)[0])) {
						view = new QuerySubject();
						view.setTable_name(line.split(delim)[0]);
						view.setType(line.split(delim)[1]);
						Map<String, String> labels = new HashMap<String, String>();
						labels.put(lang, line.split(delim)[2]);
						view.setLabels(labels);
						view.setLabel(line.split(delim)[2]);
						Map<String, String> descriptions = new HashMap<String, String>();
						descriptions.put(lang, line.split(delim)[3]);
						view.setDescriptions(descriptions);
						view.setDescription(line.split(delim)[3]);
						List<Field> fields = new ArrayList<Field>();
						view.addFields(fields);
						views.put(line.split(delim)[0], view);
					}
					Field field = new Field();
					field.set_id(line.split(delim)[4]);
					field.setField_name(line.split(delim)[5]);
					field.setField_type(line.split(delim)[6]);
					Map<String, String> labels = new HashMap<String, String>();
					labels.put(lang, line.split(delim)[7]);
					field.setLabels(labels);
					field.setLabel(line.split(delim)[7]);
					Map<String, String> descriptions = new HashMap<String, String>();
					descriptions.put(lang, line.split(delim)[8]);
					field.setDescriptions(descriptions);
					field.setDescription(line.split(delim)[8]);
					field.setExpression(line.split(delim)[9]);
					field.setHidden(Boolean.parseBoolean(line.split(delim)[10].toLowerCase()));
					field.setIcon(line.split(delim)[11]);
					field.setAlias(line.split(delim)[12]);
					field.setFolder(line.split(delim)[13]);
					field.setRole(line.split(delim)[14]);
					field.setOriginalTableAlias(line.split(delim)[15]);
					views.get(line.split(delim)[0]).addField(field);
					
				}
				
				result.put("STATUS", "OK");
				result.put("DATAS", views.values());
				result.put("MESSAGE", views.size() + " views successfully uploaded.");
				
			}
			else {
				result.put("STATUS", "KO");
				result.put("ERROR", "Input parameters are not valid.");
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