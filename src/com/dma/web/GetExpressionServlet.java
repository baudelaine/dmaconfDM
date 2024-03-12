package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/GetExpression")
public class GetExpressionServlet extends HttpServlet {	
	
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetExpressionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("SESSIONID", request.getSession().getId());		
		result.put("CLIENT", request.getRemoteAddr() + ":" + request.getRemotePort());
		result.put("SERVER", request.getLocalAddr() + ":" + request.getLocalPort());
		result.put("FROM", this.getServletName());
		
		try{

			Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
			
			if(parms != null && parms.get("dimensions") != null && parms.get("qss") != null && parms.get("selectedQs") != null) {
			
				Map<String, QuerySubject> query_subjects = 
						(Map<String, QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<Map<String, QuerySubject>>(){});
				
				
				List<String> dims = (List<String>) Tools.fromJSON(parms.get("dimensions").toString(), new TypeReference<List<String>>(){});
				
				String selectedQs = parms.get("selectedQs").toString();
				
				Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
				for(String dim: dims) {
					Dimension dimension = new Dimension();
					dimension.setName(dim);
					
					for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
						
						if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
							
							String qsAlias = query_subject.getValue().getTable_alias();  // table de gauche, celle ou tu es actuellement
							String gDirName = ""; // prefix qu'on cherche, il vaut cher
							String qsFinalName = query_subject.getValue().getTable_alias();   //CONSTANTE, nom du QS final auquel l'arbre ref est accroché, le tronc, on peut le connaitre à tout moment de f1
							String table = query_subject.getValue().getTable_name();
							String qSleftType = "Final";
							
					    	List<Map<String, Object>> orders = dimension.getOrders();
					    	List<Map<String, Object>> bks = dimension.getBks();							
							
							for(Field field: query_subjects.get(qsFinalName + qSleftType).getFields()){
								
								for(Map<String, String> map: field.getDimensions()) {
									if(map.get("dimension").contentEquals(dimension.getName())) {
								    	Map<String, Object> order = new HashMap<String, Object>();
								    	order.put("qsFinalName", qsFinalName);
								    	order.put("order", field.getField_name());
								    	order.put("alias" , qsFinalName);
								    	order.put("table" , table);
								    	orders.add(order);
									}
								}
								
							    Map<String, Object> bk = new HashMap<String, Object>();
							    if (query_subject.getValue().get_id().equals(selectedQs)) {
							    	bk.put("qsFinalName", qsFinalName);
							    	bk.put("bk", field.getField_name());
							    	bk.put("alias" , qsFinalName);
							    	bk.put("table" , table);
							    	bks.add(bk);
							    }
							}
							
							Map<String, Integer> recurseCount = new HashMap<String, Integer>();
							
							
							for(Entry<String, QuerySubject> qs: query_subjects.entrySet()){
					        	recurseCount.put(qs.getValue().getTable_alias(), 0);
					        }
							
							recurse0(qsAlias, gDirName, qsFinalName, qSleftType, dimension, query_subjects, selectedQs, recurseCount);
							
							dimensions.put(dim, dimension);
							
						}
						
					}				
					
					
				}


				Map<String, DBMDTable> dbmd = (Map<String, DBMDTable>) request.getSession().getAttribute("dbmd");
				
		        if(dbmd != null){

			        DBMDTable dbmdTable = new DBMDTable();
			        Map<String, DBMDColumn> dbmdColumns = new HashMap<String, DBMDColumn>();
	
					for(Entry<String, Dimension> dimension: dimensions.entrySet()) {
					
						List<Map<String, Object>> orders = dimension.getValue().getOrders();
						for(Map<String, Object> order: orders) {
						
							String table = (String) order.get("table");
							
							dbmdTable = dbmd.get(table);
							dbmdColumns = dbmdTable.getColumns();
					        
							for (Entry<String, DBMDColumn> dbmdColumn: dbmdColumns.entrySet()) {
								String colName = dbmdColumn.getValue().getColumn_name();
								String fieldName = (String) order.get("order");
								int i = StringUtils.split(fieldName, ".").length;
								if(i > 0) {
									fieldName = StringUtils.split(fieldName, ".")[i -1];
								}
								if(colName.equalsIgnoreCase(fieldName)) {
							    	String label = dbmdColumn.getValue().getColumn_remarks();
									if(label == null) {
						    			order.put("label", "");
									}
									else {
								    	if(label.length() > 50) {
							    			order.put("label", label.substring(0, 50));
								    	}
								    	else {
							    			order.put("label", label);
								    	}
									}
									boolean isPK = dbmdColumn.getValue().isColumn_isPrimaryKey();;
									
					    			order.put("isPK", isPK);
									boolean isIdx = dbmdColumn.getValue().isColumn_isIndexed();
					    			order.put("isIdx", isIdx);
								}
								
						    }
							
						}
						
						List<Map<String, Object>> bks = dimension.getValue().getBks();
						for(Map<String, Object> bk: bks) {
						
							String table = (String) bk.get("table");
							
							dbmdTable = dbmd.get(table);
							dbmdColumns = dbmdTable.getColumns();
					        
							for (Entry<String, DBMDColumn> dbmdColumn: dbmdColumns.entrySet()) {
								String colName = dbmdColumn.getValue().getColumn_name();
								String fieldName = (String) bk.get("bk");
								int i = StringUtils.split(fieldName, ".").length;
								if(i > 0) {
									fieldName = StringUtils.split(fieldName, ".")[i -1];
								}
								System.out.println(colName + " -> " + fieldName);
								if(colName.equalsIgnoreCase(fieldName)) {
									String label = dbmdColumn.getValue().getColumn_remarks();
									if(label == null) {
										bk.put("label", "");
									}
									else {
								    	if(label.length() > 50) {
								    		bk.put("label", label.substring(0, 50));
								    	}
								    	else {
								    		bk.put("label", label);
								    	}
									}
									
									boolean isPK = dbmdColumn.getValue().isColumn_isPrimaryKey();
					    			bk.put("isPK", isPK);
									boolean isIdx = dbmdColumn.getValue().isColumn_isIndexed();
					    			bk.put("isIdx", isIdx);
									
								}
								
						    }
							
						}
						
						
					}
		        }
				
				result.put("STATUS", "OK");
				result.put("DBMD", dbmd);
				result.put("DATA", dimensions);
			
			}
			else {
			
				result.put("STATUS", "KO");
				result.put("ERROR", "Input parameters are not valid.");
				throw new Exception();
			}
			
		}
		catch(Exception e){
			result.put("STATUS", "KO");
            result.put("EXCEPTION", e.getClass().getName());
            result.put("MESSAGE", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            result.put("STACKTRACE", sw.toString());
            e.printStackTrace(System.err);
		}			
		
		finally {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(result));
		}

	}
	
	protected void recurse0(String qsAlias, String gDirName, String qsFinalName, String qSleftType, Dimension dimension, Map<String, QuerySubject> query_subjects, String selectedQs, Map<String, Integer> recurseCount) {
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		String gDirNameCurrent = "";
		QuerySubject query_subject;
		
		if (!qSleftType.equals("Final")) {
			
			query_subject = query_subjects.get(qsAlias + qSleftType);
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		}
		
		query_subject = query_subjects.get(qsAlias + qSleftType);

    	List<Map<String, Object>> orders = dimension.getOrders();
    	List<Map<String, Object>> bks = dimension.getBks();							
    	
			
		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()) { 
				if((rel.getUsedForDimensions().equals(dimension.getName()) && qSleftType.equalsIgnoreCase("Final")) 
						|| (rel.getUsedForDimensions().equalsIgnoreCase("true") && qSleftType.equalsIgnoreCase("Ref"))) {
				
								
					String pkAlias = rel.getPktable_alias();
					String table = rel.getPktable_name();
	
					if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
							gDirNameCurrent = gDirName + "." + pkAlias;
					}
					else{
							gDirNameCurrent = gDirName + "." + rel.getAbove();
					}					
					
					for(Field field: query_subjects.get(pkAlias + "Ref").getFields()){
						
						for(Map<String, String> map: field.getDimensions()) {
//					    	// afficher dans la colonne order [DATA].[qsFinalName].[gDirNameCurrent + "." + field.getName()]
//					    	// afficher dans la liste order gDirNameCurrent + "." + field.getName() -- qsFinalName //grisé
							if(map.get("dimension").contentEquals(dimension.getName())) {
						    	Map<String, Object> order = new HashMap<String, Object>();
						    	order.put("qsFinalName", qsFinalName);
						    	order.put("order", gDirNameCurrent.substring(1) + "." + field.getField_name());
						    	order.put("alias" , pkAlias);
						    	order.put("table" , table);
						    	orders.add(order);
							}
						}
						
					    Map<String, Object> bk = new HashMap<String, Object>();
					    if (query_subjects.get(pkAlias + "Ref").get_id().equals(selectedQs)) {
					    	bk.put("qsFinalName", qsFinalName);
					    	bk.put("bk", gDirNameCurrent.substring(1) + "." + field.getField_name());
					    	bk.put("alias" , pkAlias);
					    	bk.put("table" , table);
					    	bks.add(bk);
					    }
					}
					
					recurse0(pkAlias, gDirNameCurrent, qsFinalName, "Ref" ,dimension, query_subjects, selectedQs, copyRecurseCount);	
				}
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

