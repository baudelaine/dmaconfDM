package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
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
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/SaveModel")
public class SaveModelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveModelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
		
		String modelName = (String) parms.get("modelName");

		Map<String, Object> results = new HashMap<String, Object>();
		
		try {
			
			@SuppressWarnings("unchecked")
			List<QuerySubject> querySubjects = (List<QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<List<QuerySubject>>(){});		
			@SuppressWarnings("unchecked")
			List<QuerySubject> views = (List<QuerySubject>) Tools.fromJSON(parms.get("view").toString(), new TypeReference<List<QuerySubject>>(){});		

			Calendar c = Calendar.getInstance();
	
			java.text.SimpleDateFormat mois = new java.text.SimpleDateFormat("MM");
			
			String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
			date = c.get(Calendar.YEAR) + "-" + mois.format(c.getTime()) + "-" + c.get(Calendar.DAY_OF_MONTH);
			String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
	
			Path path = Paths.get((String) request.getSession().getAttribute("projectPath"));
			
			String fileName = path + "/models/" + modelName + "-" + date + "-" + time + ".json";
			Path output = Paths.get(fileName);
			
			Map<String, List<QuerySubject>> content = new HashMap<String, List<QuerySubject>>();
			content.put("querySubjects", querySubjects);
			content.put("views", views);
			
			Files.write(output, Arrays.asList(Tools.toJSON(content)), StandardCharsets.UTF_8);
			
			results.put("STATUS", "OK");
			results.put("FILENAME", fileName);
		   
		}
		
		catch (Exception e){
			results.put("STATUS", "KO");
            results.put("EXCEPTION", e.getClass().getName());
            results.put("MESSAGE", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            results.put("STACKTRACE", sw.toString());
            e.printStackTrace(System.err);		}
		
		finally {
		    response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(results));
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

