package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UncheckQuerySubject", urlPatterns = { "/UncheckQuerySubject" })
public class UncheckQuerySubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UncheckQuerySubjectServlet() {
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
			
			if(parms != null) {
			
				@SuppressWarnings("unchecked")
				List<QuerySubject> qssList = (List<QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<List<QuerySubject>>(){});		
				
				if(qssList != null) {
				
					Map<String, QuerySubject> qss = new HashMap<String, QuerySubject>(); 
					Set<String> qssRestant = new HashSet<String>();
					Set<String> allQss = new HashSet<String>();
					Set<String> qssToRemove = new HashSet<String>();
					
					for(QuerySubject qs: qssList) {
						qss.put(qs.get_id(), qs);
						allQss.add(qs.get_id());
					}

					for(Entry<String, QuerySubject> qs: qss.entrySet()){
						
						
						if (qs.getValue().getType().equalsIgnoreCase("Final")){
							
							if(qs.getValue().isRoot()) {
								System.out.println(qs.getValue().get_id());
								qssRestant.add(qs.getValue().get_id());
								recurseFinal(qs.getValue(), qssRestant, qss);
							}
							
						}
						
					}
					
					qssToRemove.addAll(allQss);
					qssToRemove.removeAll(qssRestant);
					
					result.put("MESSAGE", "Following Query Subject will be dropped: ");
					result.put("DATAS", qssToRemove);
					result.put("STATUS", "OK");
				}
				else {
					result.put("STATUS", "KO");
					result.put("ERROR", "Input parameter is not a valid Query Subject list.");
					result.put("TROUBLESHOOTING", "Blablabla...");
					throw new Exception();
				}
			}
			else {
				result.put("STATUS", "KO");
				result.put("ERROR", "Input parameters are not valid.");
				result.put("TROUBLESHOOTING", "Blablabla...");
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
	
	private static void recurseFinal(QuerySubject qs, Set<String> qssRestant, Map<String, QuerySubject> qss) {
		for(Relation rel: qs.getRelations()){
			String pkAlias = rel.getPktable_alias();
			if(rel.isFin()) {
				qssRestant.add(pkAlias + "Final");
				recurseFinal(qss.get(pkAlias + "Final"), qssRestant, qss);
			}
			Map<String, Integer> recurseCount = new HashMap<String, Integer>();
			
			for(Entry<String, QuerySubject> rcqs: qss.entrySet()){
	        	recurseCount.put(rcqs.getValue().getTable_alias(), 0);
	        }
			if(rel.isRef()) { 
				recurseRef(pkAlias, "Ref", qss, recurseCount, qssRestant);
			}
			if(rel.isSec()) { 
				recurseRef(pkAlias, "Sec", qss, recurseCount, qssRestant);
			}
			if(rel.isTra()) { 
				recurseRef(pkAlias, "Tra", qss, recurseCount, qssRestant);
			}
		}
	}

	private static void recurseRef(String qsAlias, String qSleftType, Map<String, QuerySubject> qss, Map<String, Integer> recurseCount, Set<String> qssRestant) {
		// TODO Auto-generated method stub
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
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
		qssRestant.add(query_subject.get_id());
		
		for(Relation rel: query_subject.getRelations()){
			String pkAlias = rel.getPktable_alias();
			
			if(rel.isRef()) { 
				qssRestant.add(pkAlias + "Ref");
				recurseRef(pkAlias, "Ref" ,qss, copyRecurseCount, qssRestant);	
			}
			if(rel.isSec()) { 
				qssRestant.add(pkAlias + "Sec");
				recurseRef(pkAlias, "Sec" ,qss, copyRecurseCount, qssRestant);	
			}
			if(rel.isTra()) { 
				qssRestant.add(pkAlias + "Tra");
				recurseRef(pkAlias, "Tra" ,qss, copyRecurseCount, qssRestant);	
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