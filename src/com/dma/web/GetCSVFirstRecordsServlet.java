package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "GetCSVFirstRecords", urlPatterns = { "/GetCSVFirstRecords" })
public class GetCSVFirstRecordsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCSVFirstRecordsServlet() {
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

			List<Path> csvs = new ArrayList<Path>();
			
			Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
			
			if(parms == null) {
				csvs.add(Paths.get(prj + "/tableLabel.csv"));
				csvs.add(Paths.get(prj + "/tableDescription.csv"));
				csvs.add(Paths.get(prj + "/columnLabel.csv"));
				csvs.add(Paths.get(prj + "/columnDescription.csv"));
			}
			if(parms != null && parms.get("lang") != null) {
				if(parms.get("lang").toString().length() == 2) {
					String lang = parms.get("lang").toString();
					csvs.add(Paths.get(prj + "/tableLabel-" + lang + ".csv"));
					csvs.add(Paths.get(prj + "/tableDescription-" + lang + ".csv"));
					csvs.add(Paths.get(prj + "/columnLabel-" + lang + ".csv"));
					csvs.add(Paths.get(prj + "/columnDescription-" + lang + ".csv"));
				}
			}
			csvs.add(Paths.get(prj + "/relation.csv"));
			
			Map<String, List<Map<String, Object>>> datas = (Map<String, List<Map<String, Object>>>) new HashMap<String, List<Map<String, Object>>>();
			
			for(Path csv: csvs) {
				if(Files.exists(csv)) {
					LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(csv.toFile())));
					try{
						String key = "";
						if(csv.getFileName().toString().split("\\.")[0].length() > 0) {
							key = csv.getFileName().toString().split("-")[0];
						}
						else {
							key = csv.getFileName().toString().split("\\.")[0];
						}
						
						if(!datas.containsKey(key)) {
							datas.put(key, new ArrayList<Map<String,Object>>());
						}
						for (String line; ((line = reader.readLine()) != null) && (reader.getLineNumber() <= 2); ) {
							Map<String, Object> record = new HashMap<String, Object>();
							switch(key) {
								case "tableLabel":
									record.put("tableName", line.split(";")[0]);
									record.put("tableLabel", line.split(";")[1]);
									break;
								case "tableDescription":
									record.put("tableName", line.split(";")[0]);
									record.put("tableDescription", line.split(";")[1]);
									break;
								case "columnLabel":
									record.put("tableName", line.split(";")[0]);
									record.put("columnName", line.split(";")[1]);
									record.put("columnLabel", line.split(";")[2]);
									break;
								case "columnDescription":
									record.put("tableName", line.split(";")[0]);
									record.put("columnName", line.split(";")[1]);
									record.put("columnDescription", line.split(";")[2]);
									break;
								case "relation":
									record.put("FK_NAME", line.split(";")[0]);
									record.put("PK_NAME", line.split(";")[1]);
									record.put("FKTABLE_NAME", line.split(";")[2]);
									record.put("PKTABLE_NAME", line.split(";")[3]);
									record.put("KEY_SEQ", line.split(";")[4]);
									record.put("FKCOLUMN_NAME", line.split(";")[5]);
									record.put("PKCOLUMN_NAME", line.split(";")[6]);
									break;
								default:
							}
							if(reader.getLineNumber() > 1) {
								datas.get(key).add(record);
							}
						}
					}
					finally{
						reader.close();
					}			
				}
			}
			
			result.put("STATUS", "OK");
			result.put("DATAS", datas);
			
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