package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "GetQSFullPath", urlPatterns = { "/GetQSFullPath" })
public class GetQSFullPathServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetQSFullPathServlet() {
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
			
			@SuppressWarnings("unchecked")
			Map<String, QuerySubject> qss = (Map<String, QuerySubject>) Tools.fromJSON(request.getInputStream(), new TypeReference<Map<String, QuerySubject>>(){});
			
			if(qss != null) {
				
				Map<String, String> datas = new HashMap<String, String>();
				
				for(Entry<String, QuerySubject> qs: qss.entrySet()){
					
					if (qs.getValue().getType().equalsIgnoreCase("Final")){
						
						String qsAlias = qs.getValue().getTable_alias();  // table de gauche, celle ou tu es actuellement
						String gDirName = ""; // prefix qu'on cherche, il vaut cher
						String qsFinalName = qs.getValue().getTable_alias();   //CONSTANTE, nom du QS final auquel l'arbre ref est accroché, le tronc, on peut le connaitre à tout moment de f1
//						String table = qs.getValue().getTable_name();
						String qSleftType = "Final";
						
						Map<String, Integer> recurseCount = new HashMap<String, Integer>();
						
						
						for(Entry<String, QuerySubject> rcqs: qss.entrySet()){
				        	recurseCount.put(rcqs.getValue().getTable_alias(), 0);
				        }
						
						
						datas.put("[FINAL].[" + qsFinalName + "]", qs.getKey());
						
						recurse0(qsAlias, gDirName, qsFinalName, qSleftType, qss, recurseCount, datas);
						
						
					}
					
				}
				
				
				result.put("DATAS", datas);
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
	
	protected void recurse0(String qsAlias, String gDirName, String qsFinalName, String qSleftType,
			Map<String, QuerySubject> qss, Map<String, Integer> recurseCount, Map<String, String> datas) {
		// TODO Auto-generated method stub
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		String gDirNameCurrent = "";
		QuerySubject query_subject;
		
		if (!qSleftType.equals("Final")) {
			
			query_subject = qss.get(qsAlias + qSleftType);
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		}
		
		query_subject = qss.get(qsAlias + qSleftType);
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()) { 
		
				String pkAlias = rel.getPktable_alias();
//				System.out.println(pkAlias);
				
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					gDirNameCurrent = gDirName + "." + pkAlias;
				}
				else{
					gDirNameCurrent = gDirName + "." + rel.getAbove();
				}					
				
				datas.put("[REF].[" + qsFinalName + gDirNameCurrent + "]", pkAlias + "Ref");
				
				recurse0(pkAlias, gDirNameCurrent, qsFinalName, "Ref" ,qss, copyRecurseCount, datas);	
			}
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