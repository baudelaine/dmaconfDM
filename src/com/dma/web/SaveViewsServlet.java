package com.dma.web;

import java.io.IOException;
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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "SaveViews", urlPatterns = { "/SaveViews" })
public class SaveViewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveViewsServlet() {
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
			
			if(parms.containsKey("views") && parms.containsKey("delim") && parms.containsKey("lang")) {
				
//				String delim = (String) parms.get("delim");
				String delim = "\";\"";
				String lang = (String) parms.get("lang");
				
				@SuppressWarnings("unchecked")
				List<QuerySubject> views = (List<QuerySubject>) Tools.fromJSON(parms.get("views").toString(), new TypeReference<List<QuerySubject>>(){});				
				
				String header = "\"" + "TABLE_NAME" + delim + "TABLE_TYPE" + delim + "TABLE_LABEL" + delim + "TABLE_DESCRIPTION" + delim +
						"FIELD_ID" + delim  + "FIELD_NAME" + delim + "FIELD_TYPE" + delim + "FIELD_LABEL" + delim + "FIELD_DESCRIPTION" + delim +
						"EXPRESSION" + delim + "HIDDEN" + delim + "ICON" + delim + "ALIAS" + delim + "FOLDER" + delim + "ROLE" + delim + "ORIGINALTABLEALIAS" + "\"";				
				
				List<String> lines = new ArrayList<String>();
				lines.add(header);
				
				for(QuerySubject view: views) {
					StringBuffer tblBuf = new StringBuffer();
					tblBuf.append("\"" + view.getTable_name() + delim + view.getType());
					String label = "";
					if(view.getDescriptions().containsKey(lang)) {
						label = view.getLabels().get(lang);
					}
					String description = "";
					if(view.getDescriptions().containsKey(lang)) {
						description = view.getDescriptions().get(lang);
					}
					tblBuf.append(delim + label + delim + description.replaceAll(delim, " "));
					String tbl = tblBuf.toString();
					for(Field field: view.getFields()) {
						StringBuffer fldBuf = new StringBuffer();
						fldBuf.append(tbl + delim + field.get_id() + delim + field.getField_name() + delim + field.getField_type());
						String flabel = "";
						if(field.getLabels().containsKey(lang)) {
							flabel = field.getLabels().get(lang);
						}
						String fdescription = null;
						if(field.getLabels().containsKey(lang)) {
							fdescription = field.getDescriptions().get(lang);
						}
						if(fdescription == null) {
							fdescription = "";
						}
						fldBuf.append(delim + flabel + delim + fdescription + delim + field.getExpression() + delim +
								String.valueOf(field.isHidden()) + delim + field.getIcon() + delim + 
								field.getAlias() + delim + field.getFolder() + delim + field.getRole() + delim + field.getOriginalTableAlias() + "\"");
						lines.add(fldBuf.toString());
						
					}
				}
				
				
				request.getSession().setAttribute("views", lines);
				
				result.put("MESSAGE", "Views saved successfully");
				result.put("STATUS", "OK");
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