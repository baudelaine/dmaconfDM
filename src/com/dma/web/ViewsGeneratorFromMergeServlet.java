package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "ViewsGeneratorFromMerge", urlPatterns = { "/ViewsGeneratorFromMerge" })
public class ViewsGeneratorFromMergeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Map<String, QuerySubject> query_subjects;
	Map<String, Integer> gRefMap;
	Map<String, QuerySubject> query_subjects_views;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewsGeneratorFromMergeServlet() {
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
				
				String qss = (String) parms.get("qss");
				List<QuerySubject> qsList = new ArrayList<QuerySubject>();
				
				try {
					ObjectMapper mapper = new ObjectMapper();
			        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			        qsList = Arrays.asList(mapper.readValue(qss, QuerySubject[].class));
				}
				catch (Exception e) {
					result.put("STATUS", "KO");
					result.put("ERROR", "qss input parameter is not a valid QuerySubject list.");
					throw new Exception();
				}

				String views = (String) parms.get("views");
				List<QuerySubject> viewList = new ArrayList<QuerySubject>();
				
				try {
					ObjectMapper mapper = new ObjectMapper();
			        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			        viewList = Arrays.asList(mapper.readValue(views, QuerySubject[].class));
				}
				catch (Exception e) {
					result.put("STATUS", "KO");
					result.put("ERROR", "views input parameter is not a valid QuerySubject list.");
					throw new Exception();
				}
				
				
		        query_subjects = new HashMap<String, QuerySubject>();
		        Map<String, Integer> recurseCountQs = new HashMap<String, Integer>();
		        
		        for(QuerySubject qs: qsList){
		        	query_subjects.put(qs.get_id(), qs);
		        	recurseCountQs.put(qs.getTable_alias(), 0);
		        }				
				
		        query_subjects_views = new HashMap<String, QuerySubject>();
		        Map<String, Integer> recurseCountVs = new HashMap<String, Integer>();
		        
		        for(QuerySubject qs: viewList){
		        	query_subjects_views.put(qs.getTable_name(), qs);
		        	recurseCountVs.put(qs.getTable_name(), 0);
		        }
		        
		        gRefMap = new HashMap<String, Integer>();
		        
				System.out.println("Start Generate Views");
				
				//scan final views
				for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
					
					if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
						
						//Views Final
						if(!query_subject.getValue().getMerge().equals("")) {
							String viewsTab[] = StringUtils.split(query_subject.getValue().getMerge(), ";");
							
							for (int i=0;i<viewsTab.length;i++) {
								String viewName = viewsTab[i];
								QuerySubject qsView = new QuerySubject();
								qsView.set_id(viewName);
								qsView.setTable_name(viewName);
								qsView.setTable_alias(viewName);
								qsView.setType("Final");
								if (query_subjects_views.get(qsView.getTable_name()) == null) {
									query_subjects_views.put(qsView.getTable_name(), qsView);
								}
							}
						}
						//End Views
					}
				}
				//scan final view
				//scan ref views
				for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
					
					if (query_subject.getValue().getType().equalsIgnoreCase("Ref")){
						
						if(!query_subject.getValue().getMerge().equals("")) {
							String viewsTab[] = StringUtils.split(query_subject.getValue().getMerge(), ";");
							
							for (int i=0;i<viewsTab.length;i++) {
								String viewName = viewsTab[i];
								QuerySubject qsView = new QuerySubject();
								qsView.set_id(viewName);
								qsView.setTable_name(viewName);
								qsView.setTable_alias(viewName);
								qsView.setType("Ref");
								if (query_subjects_views.get(qsView.getTable_name()) == null) {
									query_subjects_views.put(qsView.getTable_name(), qsView);
								}
							}
						}
						//End Views
					}
				}

				//Itération pour chaque vue
				for(Entry<String, QuerySubject> query_subjects_view: query_subjects_views.entrySet()){
					
					if (query_subjects_view.getValue().getType().equalsIgnoreCase("Final")){
						for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
							
							String viewsTab[] = StringUtils.split(query_subject.getValue().getMerge(), ";");
							for (int i=0;i<viewsTab.length;i++) {
								if(query_subjects_view.getValue().getTable_name().equals(viewsTab[i])) {
									
										if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
											
											//ajout filter
											String filterNameSpaceSource = "[FINAL]";
											
											//lancement f1 ref
											for(QuerySubject qs: qsList){
									        	recurseCountQs.put(qs.getTable_alias(), 0);
									        }
												f1(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCountQs, "Final", filterNameSpaceSource, query_subjects_view.getValue().getTable_name(), "");


											
											for(Field field: query_subject.getValue().getFields()) {
												
												//views
												if(!field.isHidden()) {

													String viewName = query_subjects_view.getValue().getTable_name();
													QuerySubject qsView = query_subjects_views.get(viewName);
													Field f = new Field();
													f = field;
													f.set_id(query_subject.getValue().getTable_alias() + "." + field.getField_name());
													f.setField_name(field.getField_name());
													String ex = "[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]";
													f.setExpression(ex);
													f.setRole("Field");
													f.setAlias(query_subject.getValue().getTable_alias());
													Boolean addField = true;
													for(Field existingfield: qsView.getFields()) {
														if (existingfield.get_id().equals(f.get_id())) {
															if(addField) {
																addField = false;
																//Provisoire, le temps d'ajouter le le champ originalTableAlias dans les field. les doublons d'alias, lorsqu'il est remplie, il remplace l'ID du champ
																//Comme le champ ne sera pas vidé par le rapport de création des vues pour antibia, nous n'aurons pas besoin de le reremplir à chaque regénération
																existingfield.setAlias(query_subject.getValue().getTable_alias());
																//Provisoire, le temps d'ajouter le le champ originalTableAlias
															}
														}
													}
													
													if (addField) {
														qsView.addField(f);
													}
												}
												//end views	
											}
										}
									
									
								}
							}
							

						}
					}
				}
						
//				// travail vues Résultats
//				for(Entry<String, QuerySubject> query_subject: query_subjects_views.entrySet()){
//					System.out.println(query_subject.getValue().get_id());
//					for(Field field: query_subject.getValue().getFields()) {
//						System.out.println(query_subject.getValue().get_id() + " *** " + field.get_id() + " * * " + field.getField_name() + " * * * * " + field.getExpression());
//					}
//				}

				/* Ta sortie */
				result.put("DATAS", query_subjects_views);
				result.put("MESSAGE", "Views generated successfully");
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

	protected void f1(String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, String qSleftType, String leftFilterNameSpace, String mergeView, String gDirNameView) {
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		QuerySubject query_subject;
		if (!qSleftType.equals("Final")) {
			
			query_subject = query_subjects.get(qsAlias + qSleftType);
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		
		} else {
			query_subject = query_subjects.get(qsAlias + "Final");
		}

		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()){
			
				String namespaceID = "Ref";
				String namespaceName = "REF";		
								
				String pkAlias = rel.getPktable_alias();
				Integer i = gRefMap.get(pkAlias);
				
				if(i == null){
					gRefMap.put(pkAlias, new Integer(0));
					i = gRefMap.get(pkAlias);
				}
				gRefMap.put(pkAlias, i + 1);

				//seq
				String gFieldName = "";
				String gDirNameCurrent = "";
				String gDirNameCurrentView = "";
				
				if (namespaceID.equals("Ref")) {
					if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
						if (qSleftType.equals("Final")) {
							gFieldName = pkAlias;
							gDirNameCurrent = "." + pkAlias;
							gDirNameCurrentView = "." + pkAlias;
						} else {
							gFieldName = gDirName.substring(1) + "." + pkAlias;
							gDirNameCurrent = gDirName + "." + pkAlias;
							gDirNameCurrentView = gDirNameView + "." + pkAlias;
						}					
					}
					else {
						if (qSleftType.equals("Final")) {
							gFieldName = rel.getAbove();
							gDirNameCurrent = "." + rel.getAbove();
							gDirNameCurrentView = "." + rel.getAbove();
						} else {
							gFieldName = gDirName.substring(1) + "." + rel.getAbove();
							gDirNameCurrent = gDirName + "." + rel.getAbove();
							gDirNameCurrentView = gDirNameView + "." + rel.getAbove();
						}
					}	
				}

				String filterNameSpaceSource = "[" + namespaceName+ "]";
				if (namespaceID.equals("Ref")) {
					
					//Ajout du field(folder) de type RefView dans la vue de type Final
					for(Entry<String, QuerySubject> query_subjects_view: query_subjects_views.entrySet()){
						if (query_subjects_view.getValue().getType().equalsIgnoreCase("Ref")){
							
							String viewsTab[] = StringUtils.split(query_subjects.get(pkAlias + namespaceID).getMerge(), ";");
							for (int j=0;j<viewsTab.length;j++) {
								if(query_subjects_view.getValue().getTable_name().equals(viewsTab[j])) {
									
//									if(query_subjects.get(pkAlias + namespaceID).getMerge().contains(query_subjects_view.getValue().getTable_name())) {
										
										if(!query_subjects_view.getValue().getTable_name().equals(mergeView)) {
//											System.out.println("QS: " + pkAlias + namespaceID + "* * * getMerge() " + query_subjects_view.getValue().getTable_name() + " sera dans " + mergeView);
											
											QuerySubject qsview = query_subjects_views.get(mergeView);
											Field f = new Field();
											f.set_id(qsFinalName + gDirNameCurrent);
											f.setField_name("(" + query_subjects_view.getValue().getTable_name() + ") " + qsFinalName + gDirNameCurrent);
											String ex = "[DATA].[" + qsFinalName + "].[" + gDirNameCurrent.substring(1);
											f.setExpression(ex);
											f.setRole("FolderRefView");
											
											Boolean addField = true;
											for(Field existingfield: qsview.getFields()) {
												if (existingfield.get_id().equals(f.get_id())) {
													if(addField) {
														addField = false;
													}
												}
											}
											if (addField) {
												qsview.addField(f);
											}
											
										//	qsview.addField(f);
											mergeView = query_subjects_view.getValue().get_id();
											gDirNameCurrentView = "*";
										}
									
//									}
								}
							}
								
							//End linked view
						}
						

					
		//end views field													
					}

					
					String viewsTab[] = StringUtils.split(query_subjects.get(pkAlias + namespaceID).getMerge(), ";");
					for (int j=0;j<viewsTab.length;j++) {
						if(mergeView.equals(viewsTab[j])) {
//							if(query_subjects.get(pkAlias + namespaceID).getMerge().contains(mergeView)) {
								
								for(Field field: query_subjects.get(pkAlias + namespaceID).getFields()){
									
									//views field
									if(!field.isHidden()) {
										
										QuerySubject qsView = query_subjects_views.get(mergeView);
										Field f = new Field();
										//Copie des éléments du field afin d'éviter la copie d'objet qui n'est pas adapté ! f = field redonne une référence existante 
										//lorsqu'on repasse par un field ou nous sommes déjà passé. 
										f.setField_type(field.getField_type());
										f.setPk(field.isPk());
										f.setIndexed(field.isIndexed());
										f.setLabel(field.getLabel());
										f.setField_size(field.getField_size());
										f.setNullable(field.getNullable());
										f.setTraduction(field.isTraduction());
										f.setHidden(field.isHidden());
										f.setTimezone(field.isTimezone());
										f.setIcon(field.getIcon());
										f.setDisplayType(field.getDisplayType());
										f.setDescription(field.getDescription());
										f.setLabels(field.getLabels());
										f.setDescriptions(field.getDescriptions());
										f.setMeasure(field.getMeasure());
										f.setCustom(field.isCustom());
										f.setRole("Field");
										f.setAlias(query_subjects.get(pkAlias + namespaceID).getTable_alias());
										
										
										if (qsView.getType().equals("Final")) {
											f.set_id(qsFinalName + gDirNameCurrent + "." + field.getField_name());
											f.setField_name(field.getField_name());
											String ex = "[DATA].[" + qsFinalName + "].[" + gFieldName + "." + field.getField_name() + "]";
											f.setExpression(ex);
										} else {
											f.set_id(gDirNameCurrentView + "." + field.getField_name());
											f.setField_name(field.getField_name());
											String ex = gDirNameCurrentView + "." + field.getField_name() + "]";
											f.setExpression(ex);
										}
										Boolean addField = true;
										for(Field ff: qsView.getFields()) {
											if (ff.get_id().equals(f.get_id())) {
												addField = false;
												//Provisoire, le temps d'ajouter le le champ originalTableAlias dans les field. les doublons d'alias, lorsqu'il est remplie, il remplace l'ID du champ
												//Comme le champ ne sera pas vidé par le rapport de création des vues pour antibia, nous n'aurons pas besoin de le reremplir à chaque regénération
												ff.setAlias(query_subjects.get(pkAlias + namespaceID).getTable_alias());
												//Provisoire, le temps d'ajouter le le champ originalTableAlias
											}
										}
										if (addField) {
											qsView.addField(f);
//											System.out.println("addField: " + f.get_id());
										}											
									}
								}
//							}
						}						
					}

					

							

				}

//				if (!query_subject.getMerge().equals("")) {
					f1(pkAlias, qsFinalName + gDirNameCurrent, gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, namespaceID, filterNameSpaceSource, mergeView, gDirNameCurrentView);
//				}
			}
		}
	}	
}