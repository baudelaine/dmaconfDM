package com.dma.nicomains;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;


import com.dma.svc.CognosSVC;
import com.dma.svc.FactorySVC;
import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Relation;
import com.dma.web.RelationShip;
import com.dma.web.Tools;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTravail_dmaxml_POC_views_algo {

	static Map<String, Integer> gRefMap;
	static List<RelationShip> rsList;
	static Map<String, QuerySubject> query_subjects;
	static Map<String, QuerySubject> query_subjects_views;
	static Map<String, Map<String, String>> labelMap;
	static Map<String, Map<String, String>> qsScreenTipMap;
	static Map<String, Map<String, String>> qiScreenTipMap;
	static Map<String, Map<String, String>> qifScreenTipMap;
	static Map<String, String> filterMap;
	static Map<String, String> filterMapApply;
	static Map<String, Boolean> folerMap;
	static List<QuerySubject> qsList = null;
	static CognosSVC csvc;
	static FactorySVC fsvc;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String json = null;
		String model = "/opt/wks/v1/dmaNC/WebContent/models/POC10_views.json";
		Path path = Paths.get(model);
		Charset charset = StandardCharsets.UTF_8;

		Map<String, Integer> recurseCount = null;

		if(Files.exists(path)){
			try {
				json = new String(Files.readAllBytes(path), charset);
//				System.out.println("json =" + json);

		        ObjectMapper mapper = new ObjectMapper();
		        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

/* Ton entrée */
//		        QuerySubject[] qsArray = mapper.readValue(br, QuerySubject[].class);
		        qsList = Arrays.asList(mapper.readValue(json, QuerySubject[].class));
		        
		        query_subjects = new HashMap<String, QuerySubject>();
		        query_subjects_views = new HashMap<String, QuerySubject>();
		        recurseCount = new HashMap<String, Integer>();
		        
		        for(QuerySubject qs: qsList){
		        	query_subjects.put(qs.get_id(), qs);
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
		       
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		gRefMap = new HashMap<String, Integer>();
		
		rsList = new ArrayList<RelationShip>();

		labelMap = new HashMap<String, Map<String, String>>();
		qsScreenTipMap = new HashMap<String, Map<String, String>>();
		qiScreenTipMap = new HashMap<String, Map<String, String>>();
		qifScreenTipMap = new HashMap<String, Map<String, String>>();
		filterMap = new HashMap<String, String>();
		folerMap = new HashMap<String, Boolean>();
		filterMapApply = new HashMap<String, String>();
//		viewsMap = new HashMap<String, String>();
		
		System.out.println("Start");
		//scan final views
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			
			if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
				
				//Views
				if(!query_subject.getValue().getMerge().equals("")) {
					String viewsTab[] = StringUtils.split(query_subject.getValue().getMerge(), ";");
					
					for (int i=0;i<viewsTab.length;i++) {
						String viewName = viewsTab[i];
						QuerySubject qsView = new QuerySubject();
						qsView.set_id(viewName);
						qsView.setTable_name(viewName);
						qsView.setTable_alias(viewName);
						qsView.setType("Final");
						if (query_subjects_views.get(qsView.getTable_alias()) == null) {
							query_subjects_views.put(qsView.getTable_alias(), qsView);
						}		
					}
				}
				//End Views
			}
		}
		//scan final view
						
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			
			if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
				//ajout filter
				String filterNameSpaceSource = "[FINAL]";
				
				//lancement f1 ref
				for(QuerySubject qs: qsList){
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
				
				f1(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCount, "Final", filterNameSpaceSource, query_subject.getValue().getMerge(), "");
				//end f1		

				for(Field field: query_subject.getValue().getFields()) {
					
					//views
					if(!query_subject.getValue().getMerge().equals("") && !field.isHidden()) {
						String viewsTab[] = StringUtils.split(query_subject.getValue().getMerge(), ";");
						
						for (int i=0;i<viewsTab.length;i++) {
							String viewName = viewsTab[i];
							QuerySubject qsView = query_subjects_views.get(viewName);
							Field f = new Field();
							f = field;
							f.set_id(query_subject.getValue().getTable_alias() + "." + field.getField_name());
							f.setField_name(field.getField_name());
							String ex = "[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]";
							f.setExpression(ex);
//							f.setRole("Field");
							qsView.addField(f);
						}
					}
					//end views
					
				}
				// end label
			}
			
		}
				
		// Pour visualiser dans la console
/*
		for(Entry<String, QuerySubject> query_subject: query_subjects_views.entrySet()){
			for(Field field: query_subject.getValue().getFields()) {
				System.out.println(query_subject.getValue().get_id() + " *** " + field.get_id() + " * * " + field.getField_name() + " * * * * " + field.getExpression() + " *  *  * " + field.isHidden());
			}
		}
*/
		Tools.toJSON(query_subjects_views);
		/* Ta sortie */
	}
	
	protected static void f1(String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, String qSleftType, String leftFilterNameSpace, String mergeView, String gDirNameView) {
		
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
			if(rel.isRef() || rel.isSec() || rel.isTra()){
				
				String namespaceID = "";
				String namespaceName = "";
				if(rel.isRef()) {
					namespaceID = "Ref";
					namespaceName = "REF";
				} else if (rel.isSec()) {
					namespaceID = "Sec";
					namespaceName = "SEC";
				} else {
					namespaceID = "Tra";
					namespaceName = "TRA";
				}
								
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
				
				String filterNameSpaceSource = "[" + namespaceName+ "]";
				if (namespaceID.equals("Ref")) {
					
					//Ajout du field de type RefView dans la vue de type Final
					if(!query_subjects.get(pkAlias + namespaceID).getMerge().equals("") && !query_subjects.get(pkAlias + namespaceID).getMerge().contains(mergeView)) {
						QuerySubject qsview = query_subjects_views.get(mergeView);
						Field f = new Field();
						f.set_id(qsFinalName + gDirNameCurrent);
						f.setField_name("(" + query_subjects.get(pkAlias + namespaceID).getMerge() + ") " + qsFinalName + gDirNameCurrent);
						String ex = qsFinalName + gDirNameCurrent;
						f.setExpression(ex);
//						f.setRole("FolderRefView");
						qsview.addField(f);
						mergeView = query_subjects.get(pkAlias + namespaceID).getMerge();
					}
					//End linked view
					
					//QsViews create Ref view
					if(!query_subjects.get(pkAlias + namespaceID).getMerge().equals("")) {
						String viewsTab[] = StringUtils.split(query_subjects.get(pkAlias + namespaceID).getMerge(), ";");
						
						for (int j=0;j<viewsTab.length;j++) {
														
							String viewName = viewsTab[j];
							QuerySubject qsView = new QuerySubject();
							qsView.set_id(viewName);
							qsView.setTable_name(viewName);
							qsView.setTable_alias(viewName);
							qsView.setType("Ref");
							if (query_subjects_views.get(qsView.getTable_alias()) == null) {
								query_subjects_views.put(qsView.getTable_alias(), qsView);
								String gDirNameCurrentTab[] = StringUtils.split(gDirNameCurrent,".");
								gDirNameCurrentView = gDirNameCurrentTab[gDirNameCurrentTab.length-1];
								mergeView = query_subjects.get(pkAlias + namespaceID).getMerge();
							}
						}
					}
					//end QsViews
					String viewsTab[] = StringUtils.split(query_subjects.get(pkAlias + namespaceID).getMerge(), ";");
					
					for(Field field: query_subjects.get(pkAlias + namespaceID).getFields()){
						
						if (rel.isRef()) {
		
							//views field
							if(!query_subjects.get(pkAlias + namespaceID).getMerge().equals("") && !field.isHidden()) {
								
								for (int j=0;j<viewsTab.length;j++) {
									String viewName = viewsTab[j];
									if (viewName.equals(mergeView)) {
										QuerySubject qsView = query_subjects_views.get(viewName);
										Field f = new Field();
										//Copie des éléments du field afin d'éviter la copie d'objet qui n'est pas adapté ! f = field redonne une référence existante 
										//lorsqu'on repasser par un field ou nous sommes déjà passé. 
										f.setField_type(field.getField_type());
										f.setPk(field.isPk());
										f.setIndex(field.isIndex());
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
//										f.setRole("Field");
										
										if (qsView.getType().equals("Final")) {
											f.set_id(qsFinalName + gDirNameCurrent + "." + field.getField_name());
											f.setField_name(field.getField_name());
											String ex = "[DATA].[" + qsFinalName + "].[" + gFieldName + "." + field.getField_name() + "]";
											f.setExpression(ex);
										} else {
											f.set_id(gDirNameCurrentView + "." + field.getField_name());
											f.setField_name(field.getField_name());
											String ex = "*." + gDirNameCurrentView + "." + field.getField_name() + "]";
											f.setExpression(ex);
										}
										qsView.addField(f);
									}
								}
							}
							//end views field							
						}
					}
				}
				//end only for Ref

								
				f1(pkAlias, qsFinalName + gDirNameCurrent, gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, namespaceID, filterNameSpaceSource, mergeView, gDirNameCurrentView);	
			}
		}
	}	
}
