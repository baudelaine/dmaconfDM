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
import java.rmi.RemoteException;
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
import com.dma.web.Filter;
import com.dma.web.QuerySubject;
import com.dma.web.Relation;
import com.dma.web.RelationShip;
import com.dma.web.Tools;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTravail_221117_BDXMET_dmaconf2_OK {

	static Map<String, Integer> gRefMap;
	static List<RelationShip> rsList;
	static Map<String, QuerySubject> query_subjects;
	static Map<String, QuerySubject> query_subjects_views;
	static Map<String, Map<String, String>> labelMapRefAliasLabel;
	static Map<String, Map<String, String>> labelMap;
	static Map<String, Map<String, String>> qsScreenTipMap;
	static Map<String, Map<String, String>> qiScreenTipMap;
	static Map<String, Map<String, String>> qifScreenTipMap;
	static Map<String, Map<String, String>> viewsLabelMap;
	static Map<String, String> filterMap;
	static Map<String, String> filterMapApply;
	static Map<String, String> viewsFolders;
	static Map<String, Boolean> folderMap;
	static List<QuerySubject> qsList = null;
	static List<QuerySubject> viewList = null;
	static CognosSVC csvc;
	static FactorySVC fsvc;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String json = null;
		String model = "/opt/wks/v1/dmaNC/WebContent/models/BDXMET_V2_dmaconf2.json";
		Path path = Paths.get(model);
		Charset charset = StandardCharsets.UTF_8;

		Map<String, Integer> recurseCount = null;

		if(Files.exists(path)){
			try {
				json = new String(Files.readAllBytes(path), charset);
//				System.out.println("json =" + json);
				@SuppressWarnings("unchecked")
				Map<String, List<QuerySubject>> parms = (Map<String, List<QuerySubject>>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});

		        qsList = parms.get("querySubjects");
		        viewList = parms.get("views");
		        
		        query_subjects = new HashMap<String, QuerySubject>();
		        recurseCount = new HashMap<String, Integer>();
		        
		        for(QuerySubject qs: qsList){
		        	query_subjects.put(qs.get_id(), qs);
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
		       
		        query_subjects_views = new HashMap<String, QuerySubject>();
		        for(QuerySubject qs: viewList){
		        	query_subjects_views.put(qs.getTable_name(), qs);
		        	recurseCount.put(qs.getTable_name(), 0);
		        }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// On est bien Tintin...
		
//        TaskerSVC.start();
		
		String cognosDataSource = "CDA4";
		String cognosCatalog = "";
		String cognosSchema = "MAXIMO";
		String cognosDefaultLocale = "en-gb";
		String cognosLocales = "";
		
		String cognosDispatcher = "http://172.16.186.246:9300/p2pd/servlet/dispatch";
		String pathToXML = "/opt/wks/v1/dmaNC/WebContent/res/templates";
		String cognosLogin = "admin";
		String cognosPassword = "Freestyle05$";
		String cognosNamespace = "CognosEx";
		String projectName = "BDXMET_Physical_V2.1_test7";
		String cognosFolder = "C:/models/user0";
		String cognosModelsPath = "/mnt/models/user0";
		String dBEngine = "ORA";
		boolean isXML = true;
		
		Path projectPath = Paths.get(cognosModelsPath + "/" + projectName);
		
		
		csvc = new CognosSVC(cognosDispatcher);
		csvc.setPathToXML(pathToXML);
		fsvc = new FactorySVC(csvc);
		try {
			csvc.logon(cognosLogin, cognosPassword, cognosNamespace);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String modelName = projectName;
		try {
			csvc.openModel(modelName, cognosFolder);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fsvc.setLocale(cognosDefaultLocale);
		

// end start		

		if (!isXML) {
			fsvc.createNamespace("PHYSICAL", "Model");
		}
		fsvc.createNamespace("PHYSICALUSED", "Model");
		fsvc.createNamespace("AUTOGENERATION", "Model");
		fsvc.createNamespace("FINAL", "AUTOGENERATION");
		fsvc.createNamespace("REF", "AUTOGENERATION");
		fsvc.createNamespace("SEC", "AUTOGENERATION");
		fsvc.createNamespace("TRA", "AUTOGENERATION");
		fsvc.createNamespace("FILTER_FINAL", "AUTOGENERATION");
		fsvc.createNamespace("FILTER_REF", "AUTOGENERATION");
		fsvc.createNamespace("DATA", "Model");
		fsvc.createNamespace("DIMENSIONAL", "Model");
		fsvc.createNamespace("VIEWS", "Model");

		if (!isXML) {
			fsvc.DBImport("PHYSICAL", cognosDataSource, cognosCatalog ,cognosSchema, dBEngine);
		}
		
		gRefMap = new HashMap<String, Integer>();
		
		rsList = new ArrayList<RelationShip>();

		labelMap = new HashMap<String, Map<String, String>>();
		labelMapRefAliasLabel = new HashMap<String, Map<String, String>>();
		qsScreenTipMap = new HashMap<String, Map<String, String>>();
		qiScreenTipMap = new HashMap<String, Map<String, String>>();
		qifScreenTipMap = new HashMap<String, Map<String, String>>();
		viewsLabelMap = new HashMap<String, Map<String, String>>();
		filterMap = new HashMap<String, String>();
		folderMap = new HashMap<String, Boolean>();
		filterMapApply = new HashMap<String, String>();
		viewsFolders = new HashMap<String, String>();
		

		//to get traductions from AliasRef to name Dimensions
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			if (query_subject.getValue().getType().equalsIgnoreCase("Ref")){
				//init language project one time
				for(Entry<String, String> langKey: query_subject.getValue().getDescriptions().entrySet()){
					// creer un for avec la liste des langues du projets pour créer le labelMap et le ScreenTipMap
					String langue = langKey.getKey();
					if (labelMapRefAliasLabel.get(langue)==null) {
						Map<String, String> lm = new HashMap<String, String>();
						labelMapRefAliasLabel.put(langue, lm);
					}
				}
				for(Entry<String, String> langLabel: query_subject.getValue().getLabels().entrySet()){
					if(langLabel.getValue() == null || langLabel.getValue().equals("")) {
						labelMapRefAliasLabel.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_name());
						} else {
							labelMapRefAliasLabel.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias(), langLabel.getValue());
						}
				}
			}
		}
		// end to get traductions from AliasRef to name Dimensions
				
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			
			if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
				
				fsvc.copyQuerySubject("[PHYSICALUSED]", "[PHYSICAL].[" + query_subject.getValue().getTable_name() + "]");
				fsvc.renameQuerySubject("[PHYSICALUSED].[" + query_subject.getValue().getTable_name() + "]", "FINAL_" + query_subject.getValue().getTable_alias());
				
				fsvc.createQuerySubject("PHYSICALUSED", "FINAL", "FINAL_" + query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias());
				//ajout filter
				String filterNameSpaceSource = "[FINAL]";
				if (query_subject.getValue().getFilters()!=null && !query_subject.getValue().getFilters().isEmpty())
				{
					fsvc.createQuerySubject("FINAL", "FILTER_FINAL", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
					
					Map<String, Filter> qsFilters = query_subject.getValue().getFilters();
					int y = 0;
					for(Entry<String, Filter> f: qsFilters.entrySet()){
						
						String option = "always";
						if (f.getValue().getOption().equals("Facultative")) {
							option = "asNeeded";
						}
						fsvc.createQuerySubjectFilter("[FILTER_FINAL].[" + query_subject.getValue().getTable_alias() + "]" , f.getValue().getExpression(), f.getValue().getName(), option, y);
						y++;
					}

					fsvc.createQuerySubject("FILTER_FINAL", "DATA", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
					filterNameSpaceSource = "[FILTER_FINAL]";
				} else {
					fsvc.createQuerySubject("FINAL", "DATA", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
				}
				//end filter
				//folder pour les qs Finaux
				if(query_subject.getValue().getFolder()!=null && !query_subject.getValue().getFolder().equals("")) {
					
					if(folderMap.get(query_subject.getValue().getFolder())==null) {
						fsvc.createFolder("[DATA]", query_subject.getValue().getFolder());
						folderMap.put(query_subject.getValue().getFolder(), true);
					}
					fsvc.moveQuerySubjectInFolder("[DATA].[" + query_subject.getValue().getTable_alias() + "]", "[DATA].[" + query_subject.getValue().getFolder() + "]");
				}
				//end folder

				//init language project one time
				for(Entry<String, String> langKey: query_subject.getValue().getDescriptions().entrySet()){
					// creer un for avec la liste des langues du projets pour créer le labelMap et le ScreenTipMap
					String langue = langKey.getKey();
					if (labelMap.get(langue)==null) {
						Map<String, String> lm = new HashMap<String, String>();
						labelMap.put(langue, lm);
						Map<String, String> vlm = new HashMap<String, String>();
						viewsLabelMap.put(langue, vlm);
						Map<String, String> qsSTM = new HashMap<String, String>();
						qsScreenTipMap.put(langue, qsSTM);
						Map<String, String> qiSTM = new HashMap<String, String>();
						qiScreenTipMap.put(langue, qiSTM);
						Map<String, String> qifSTM = new HashMap<String, String>();
						qifScreenTipMap.put(langue, qifSTM);
					}		
				}
				//end init
				//map tooltip
				for(Entry<String, String> langDesc: query_subject.getValue().getDescriptions().entrySet()){
					if(langDesc.getValue() == null || langDesc.getValue().equals("")) {
						qsScreenTipMap.get(langDesc.getKey()).put("[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_name());
						} else {
//							System.out.println("QS langDesc : " + langDesc.getValue());
							qsScreenTipMap.get(langDesc.getKey()).put("[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_name() + ": " + langDesc.getValue());
						}
				}
				//end map tooltip
				//end tooltip
				
				//lancement f1 ref
				for(QuerySubject qs: qsList){
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
				
				f1(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCount, "Final", filterNameSpaceSource);
				//end f1
										
				for(Relation rel: query_subject.getValue().getRelations()){
					if(rel.isFin()){
						
						RelationShip RS = new RelationShip("[FINAL].[" + query_subject.getValue().getTable_alias() + "]" , "[FINAL].[" + rel.getPktable_alias() + "]");
						// changer en qs + refobj
						RS.setExpression(rel.getRelationship());
						if (rel.isRightJoin())
						{
							RS.setCard_left_min("zero");
						} else {
							RS.setCard_left_min("one");
						}
						RS.setCard_left_max("many");
	
						if (rel.isLeftJoin())
						{
							RS.setCard_right_min("zero");
						} else {
							RS.setCard_right_min("one");
						}
						RS.setCard_right_max("one");
						RS.setParentNamespace("FINAL");
						rsList.add(RS);					
				
					}
				}				
				//add label map qs
				for(Entry<String, String> langLabel: query_subject.getValue().getLabels().entrySet()){
					if(langLabel.getValue() == null || langLabel.getValue().equals("")) {
						labelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_name());
						} else {
							labelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias(), langLabel.getValue());
						}
				}
				//add label map fields
				for(Field field: query_subject.getValue().getFields()) {
					
					if (field.isCustom()) {
						
					fsvc.createQueryItem("[DATA].[" + query_subject.getValue().getTable_alias() + "]", field.getField_name(), field.getExpression(), cognosDefaultLocale);
						//end regular agg
					}
					
					//test cas ou la langue n'existe pas dans le map
					for(Entry<String, Map<String, String>> langLabel: labelMap.entrySet()){
						String label = field.getLabels().get(langLabel.getKey());
						if (label == null || label.equals("")) {
							labelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias() + "." + field.getField_name(), field.getField_name());
						} else {
							labelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_alias() + "." + field.getField_name(), label);
						}
					}
					//end test
					
					
					//add tooltip

					//maptooltip
					for(Entry<String, String> langDesc: field.getDescriptions().entrySet()){
						if(langDesc.getValue() == null || langDesc.getValue().equals("")) {
							qiScreenTipMap.get(langDesc.getKey()).put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", query_subject.getValue().getTable_name() + "." + field.getField_name());
							} else {
						//		System.out.println("QI langDesc : " + langDesc.getValue());
								qiScreenTipMap.get(langDesc.getKey()).put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", query_subject.getValue().getTable_name() + "." + field.getField_name() + ": " + langDesc.getValue());
							}
					}
					//end map tool tip
					
					//end tooltip
					//change property query item
					fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "usage", field.getIcon().toLowerCase());
					if (!field.getDisplayType().toLowerCase().equals("value"))
					{
						fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "displayType", field.getDisplayType().toLowerCase());
					}
					if (field.isHidden())
					{
						fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "hidden", "true");
						
					}
					//regular agg a préciser avec une liste des types qui entrainent une somme pour chaque type de base.
//						System.out.println("regularAgg : fieldType : " + field.getField_type().toLowerCase());
					if (field.isCustom() && field.getField_type().toLowerCase().equals("decimal")) {
//							System.out.println("regularAgg : fieldType dans le if : " + field.getField_type().toLowerCase());
						fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "regularAggregate", "sum");
					}
				//end change
				}
				// end label
			}
			
		}
		//IICCreateRelation(rsList);
		for(RelationShip rs: rsList){
			fsvc.createRelationship(rs);
		}
		
		
		// multidimensional
		
		Map<String, Map<String, String>> measures = new HashMap<String, Map<String, String>>();
		
		String qSIDStart = "";
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			
			if(query_subject.getValue().isRoot()) {
				qSIDStart = query_subject.getValue().get_id();
				System.out.println(query_subject.getValue().get_id() + " ids : Root");
				createMeasures("", qSIDStart, true, measures);
			}
//			Set<String> set = query_subject.getValue().getLinker_ids();
//			for (String s : set) {
//				if (s.equals("Root")) {
//					qSIDStart = query_subject.getValue().get_id();
//					System.out.println(query_subject.getValue().get_id() + " ids : " + s);
//					createMeasures("", qSIDStart, true, measures);
//						System.out.println("MeasureMap : " + measures.toString());
//				}
//			}
		}

		Map<String, Map<String, String>> dimensions = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> dimensionsBK = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> dimensionsHN = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> dimensionsAT = new HashMap<String, Map<String, String>>();
		scanDimensions(dimensions, dimensionsBK, dimensionsHN, dimensionsAT);
		scanFinalFieldsDimensions(dimensions, dimensionsBK, dimensionsHN, dimensionsAT);
		System.out.println("Map Dimension : " + dimensions.toString());
		createHierarchiesNb(dimensions);				
		renameHierarchies(dimensions, dimensionsHN);
		buildDimensions(dimensions, dimensionsBK, dimensionsAT, measures, dBEngine);

		// end multidimensional
		
		//views
		createViews(cognosDefaultLocale);
		//end viewses
		
		//add locales
				int intLang = 1;
				for(Entry<String, Map<String, String>> langueKey: labelMap.entrySet()){
					fsvc.addLocale(langueKey.getKey().toLowerCase(), cognosDefaultLocale);
					
					//on ecrit les tooltip dans chaque langue pour les QS, QI, QI folder
					for(Entry<String, String> screenTipMap: qsScreenTipMap.get(langueKey.getKey().toLowerCase()).entrySet()){
						fsvc.createScreenTip("querySubject", screenTipMap.getKey() , screenTipMap.getValue(), intLang );
					}
					
					for(Entry<String, String> screenTipMap: qiScreenTipMap.get(langueKey.getKey().toLowerCase()).entrySet()){
						fsvc.createScreenTip("queryItem", screenTipMap.getKey() , screenTipMap.getValue(), intLang );
					}
				
					for(Entry<String, String> screenTipMap: qifScreenTipMap.get(langueKey.getKey().toLowerCase()).entrySet()){
						fsvc.createScreenTip("queryItemFolder", screenTipMap.getKey() , screenTipMap.getValue(), intLang );
					}
					intLang ++;
					
				}
				//end add locales
		
		// add value to labelMap in order to translate Time Dimension Level
		//English
		if (labelMap.get("en")!=null) {
			Map<String, String> m = labelMap.get("en");
			m.put("YEAR", "Year"); m.put("YEAR_KEY", "Year key");
			m.put("QUARTER", "Quarter"); m.put("QUARTER_KEY", "Quarter key"); 
			m.put("MONTH", "Month"); m.put("MONTH_KEY", "Month key");
			m.put("WEEK", "Week"); m.put("WEEK_KEY", "Week key");
			m.put("DAY", "Day"); m.put("DAY_KEY", "Day key");
			m.put("DAY_OF_WEEK", "Day of week"); 	m.put("DAY_OF_WEEK_KEY", "Day of week key");
			m.put("AM/PM","Am/pm"); m.put("AM/PM_KEY","Am/pm key");
			m.put("HOUR","Hour"); m.put("HOUR_KEY","Hour key");
			m.put("MIN","Minute"); m.put("MIN_KEY","Minute key");
			m.put("DATE", "Date"); m.put("DATE_KEY", "Date key");
			m.put("Fact", "(Measures)");
		}
		//French
		if (labelMap.get("fr")!=null) {
			Map<String, String> m = labelMap.get("fr");
			m.put("YEAR", "Année"); m.put("YEAR_KEY", "Année clef");
			m.put("QUARTER", "Trimestre"); m.put("QUARTER_KEY", "Trimestre clef");
			m.put("MONTH", "Mois"); m.put("MONTH_KEY", "Mois clef");
			m.put("WEEK", "Semaine"); m.put("WEEK_KEY", "Semaine clef");
			m.put("DAY", "Jour"); m.put("DAY_KEY", "Jour clef");
			m.put("DAY_OF_WEEK", "Jour de la semaine"); m.put("DAY_OF_WEEK_KEY", "Jour de la semaine clef");
			m.put("AM/PM","Am/pm"); m.put("AM/PM_KEY","Am/pm clef");
			m.put("HOUR","Heure"); m.put("HOUR_KEY","Heure clef");
			m.put("MIN","Minute"); m.put("MIN_KEY","Minute clef");
			m.put("DATE", "Date"); m.put("DATE_KEY", "Date clef");
			m.put("(By month)", "(Par mois)");
			m.put("(By week)", "(Par semaine)");
			m.put("(Rolling month)", "(Mois glissant)");
			m.put("Fact", "(Mesures)");
		}
		// end static value to Map
		
		// tests
		
//		System.out.println(viewsFolders.toString());

	for(Entry<String, Map<String, String>> langueKey: viewsLabelMap.entrySet()){
		for(Entry<String, String> viewLabelm: viewsLabelMap.get(langueKey.getKey().toLowerCase()).entrySet()){
//			System.out.println("label: " + viewLabelm.getKey() + " * * " + viewLabelm.getValue() );
		}
	}
System.out.println("labelMapRefAliasLabel: " + labelMapRefAliasLabel.toString());
		
//		try {
			try {
				csvc.executeAllActions();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (DocumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//		}
/*		catch(Exception e) {
			result.put("STATUS", "KO");
			result.put("ERROR", "Cognos executeAllActions() failed.");
			throw e;
		}
*/		
		// fin tests
	
		//stop();
		csvc.saveModel();
		csvc.closeModel();
		csvc.logoff();
		System.out.println("END COGNOS API");
		
		// code parser xml for labels

		System.out.println("START XML MODIFICATION");
			try {
							
				String modelSharedPath = projectPath + "/model.xml";
				
				Path input = Paths.get(modelSharedPath);
				Path output = Paths.get(modelSharedPath);
				
				String datas = null;
				String inputSearch = "xmlns=\"http://www.developer.cognos.com/schemas/bmt/60/12\"";
				String outputSearch = "queryMode=\"dynamic\"";
				String outputReplace = outputSearch + " " + inputSearch;  
				
				charset = StandardCharsets.UTF_8;
				if(Files.exists(input)){
					datas = new String(Files.readAllBytes(input), charset);
				}

				datas = StringUtils.replace(datas, inputSearch, "");
				
				// modifs
				
//				File xmlFile = new File(ConfigProperties.modelXML);
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(datas.getBytes(StandardCharsets.UTF_8)));

				String namespaceName = "DATA";
				String spath = "/project/namespace/namespace";
				int k=1;
				
				Element namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");			
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				for(Entry<String, Map<String, String>> langLabelMap: labelMap.entrySet()){
					fsvc.recursiveParserQS(document, spath, langLabelMap.getKey().toLowerCase(), langLabelMap.getValue());
				}

				//views
				namespaceName = "VIEWS";
				spath = "/project/namespace/namespace";
				k=1;
				
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");			
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				for(Entry<String, Map<String, String>> langLabelMap: viewsLabelMap.entrySet()){
					fsvc.recursiveParserQSViews(document, spath, langLabelMap.getKey().toLowerCase(), langLabelMap.getValue());
				}
				
				//dimensions
				namespaceName = "DIMENSIONAL";
				spath = "/project/namespace/namespace";
				k=1;
				
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");			
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				for(Entry<String, Map<String, String>> langLabelMap: labelMap.entrySet()){
					fsvc.recursiveParserDimension(document, spath, langLabelMap.getKey().toLowerCase(), langLabelMap.getValue(), labelMapRefAliasLabel.get(langLabelMap.getKey()));
				}
				

				
				try {
	
					datas = document.asXML();

					datas = StringUtils.replace(datas, outputSearch, outputReplace);
					Files.write(output, datas.getBytes());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// fin test writer
				
			} catch (DocumentException | IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("END XML MODIFICATION");		
			//publication
//			System.out.println("Create and Publish Package");	
			
			//start
			csvc = new CognosSVC(cognosDispatcher);
			csvc.setPathToXML(pathToXML);
			fsvc = new FactorySVC(csvc);
			try {
				csvc.logon(cognosLogin, cognosPassword, cognosNamespace);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				csvc.openModel(modelName, cognosFolder);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fsvc.setLocale(cognosDefaultLocale);
			
			@SuppressWarnings("unused")
			String[] locales = {cognosLocales};
			fsvc.changePropertyFixIDDefaultLocale();
//			fsvc.createPackage(modelName, modelName, modelName, locales);
//			fsvc.publishPackage(modelName,"/content");
			
			try {
				csvc.executeAllActions();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			csvc.saveModel();
			csvc.closeModel();
			csvc.logoff();
			
			
			System.out.println("Model Generation Finished");
	}
	
	protected static void createViews(String cognosDefaultLocale) {
		for(Entry<String, QuerySubject> query_subject: query_subjects_views.entrySet()){
			if (query_subject.getValue().getType().equalsIgnoreCase("Final")){

				//Ajout des labels qs dans le map
				for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
					String label = query_subject.getValue().getLabels().get(langLabel.getKey());
					if (label == null || label.equals("")) {
						viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name(), query_subject.getValue().getTable_name());
					} else {
						viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name(), label);
					}
				}
				//end Ajout
				
				
				
				fsvc.createQuerySubjectEmpty("[VIEWS]", query_subject.getValue().getTable_name(), cognosDefaultLocale);
				for(Field field: query_subject.getValue().getFields()) {
					
					if (field.getRole().equals("Field")) {
						String fieldAlias = "";
						if (field.getAlias().equals("")) {
							fieldAlias = field.get_id();
						} else {
							fieldAlias = field.getAlias();
						}
						//gestion des folders
						if (!field.getFolder().equals("")) {
							String folderTab[] = StringUtils.split(field.getFolder(), "/");
							
							for (int i = 0; i < folderTab.length; i++) {
								String folder = folderTab[i];
								if (viewsFolders.get("[VIEWS].[" + query_subject.getValue().getTable_name() + "].[" + folder + "]") == null) {
									viewsFolders.put("[VIEWS].[" + query_subject.getValue().getTable_name() + "].[" + folder + "]", folder);
									String inFolder = "";
									if (i > 0) {
										inFolder = ".[" + folderTab[i - 1] + "]";
									}
									fsvc.createSubFolder("[VIEWS].[" + query_subject.getValue().getTable_name() + "]" + inFolder, folder);
								}
							}
							fsvc.createQueryItemInFolder("[VIEWS].[" + query_subject.getValue().getTable_name() + "]", folderTab[folderTab.length - 1], fieldAlias, field.getExpression());
						
						} else {
							fsvc.createQueryItem("[VIEWS].[" + query_subject.getValue().getTable_name() + "]", fieldAlias, field.getExpression(), cognosDefaultLocale);
						}
						
						//Ajout des labels field dans le map
						for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
							String label = field.getLabels().get(langLabel.getKey());
							if (label == null || label.equals("")) {
								viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name() + "." + fieldAlias, fieldAlias);
							} else {
								viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name() + "." + fieldAlias, label);
							}
						}
						//end Ajout
						
					} else if (field.getRole().equals("Folder")) {
						
						String fieldAlias = "";
						if (field.getAlias().equals("")) {
							fieldAlias = field.getField_name();
						} else {
							fieldAlias = field.getAlias();
						}
						
						//Ajout des labels folder dans le map
						for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
							String label = field.getLabels().get(langLabel.getKey());
							if (label == null || label.equals("")) {
								viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name() + "." + fieldAlias, fieldAlias);
							} else {
								viewsLabelMap.get(langLabel.getKey()).put(query_subject.getValue().getTable_name() + "." + fieldAlias, label);
							}
						}
						//end Ajout

					} else if (field.getRole().equals("FolderRefView")) {
						String subFolderPath = "";
						if (!field.getFolder().equals("")) {
							String folderTab[] = StringUtils.split(field.getFolder(), "/");
							
							for (int i = 0; i < folderTab.length; i++) {
								String folder = folderTab[i];
								if (viewsFolders.get("[VIEWS].[" + query_subject.getValue().getTable_name() + "].[" + folder + "]") == null) {
									viewsFolders.put("[VIEWS].[" + query_subject.getValue().getTable_name() + "].[" + folder + "]", folder);
									String inFolder = "";
									if (i > 0) {
										inFolder = ".[" + folderTab[i - 1] + "]";
									}
									fsvc.createSubFolder("[VIEWS].[" + query_subject.getValue().getTable_name() + "]" + inFolder, folder);
								}
							}
							subFolderPath = ".[" + folderTab[folderTab.length - 1] + "]";
						}
						createViewsf1(cognosDefaultLocale, "[VIEWS].[" + query_subject.getValue().getTable_name() + "]" + subFolderPath, field, "[VIEWS].[" + query_subject.getValue().getTable_name() + "]", "", query_subject.getValue().getTable_name());
					}
				}
			}
		}
	}
	
	protected static void createViewsf1(String cognosDefaultLocale, String racineFolderPath, Field f, String qsFinal, String gDirName, String qsFinalName) {
		String repName = "";
		String repNameTab[] = StringUtils.splitByWholeSeparator(f.getField_name(), ") ");

		if (f.getAlias().equals("")) {
			repName = "." + repNameTab[1];
		} else {
			repName = "." + f.getAlias();
		}
		
		fsvc.createSubFolder(racineFolderPath, repName);
		
		//Ajout des labels folder dans le map
		for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
			String label = f.getLabels().get(langLabel.getKey());
			if (label == null || label.equals("")) {
				viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + repName, repName);
			} else {
				viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + repName, label);
			}
		}
		//end Ajout
		
		QuerySubject qs = query_subjects_views.get(repNameTab[0].substring(1));
		String gDirNameCurrent = gDirName + repName;
		
		for(Field field: qs.getFields()) {
			if (field.getRole().equals("Field")) {
				String fieldAlias = "";
				if (field.getAlias().equals("")) {
					fieldAlias = field.get_id().substring(1);
				} else {
					fieldAlias = "." + field.getAlias();
				}
				if (!field.getFolder().equals("")) {
					
					String folderTab[] = StringUtils.split(field.getFolder(), "/");
					
					for (int i = 0; i < folderTab.length; i++) {
						String folder = folderTab[i];
						if (viewsFolders.get(qsFinal + ".[" + repName + "." + folder + "]") == null) {
							viewsFolders.put(qsFinal + ".[" + repName + "." + folder + "]", repName + "." + folder);
							String inFolder = "";
							if (i > 0) {
								inFolder = "." + folderTab[i - 1];
							}
							fsvc.createSubFolder(qsFinal + ".[" + repName + inFolder + "]", repName + "." + folder);
						}
					}

					fsvc.createQueryItemInFolder(qsFinal, repName + "." + folderTab[folderTab.length - 1], gDirNameCurrent.substring(1) + fieldAlias, f.getExpression() + field.getExpression().substring(1));
				} else {
					fsvc.createQueryItemInFolder(qsFinal, gDirNameCurrent, gDirNameCurrent.substring(1) + fieldAlias, f.getExpression() + field.getExpression().substring(1));
				}
				
				//Ajout des labels field dans le map
				for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
					String label = field.getLabels().get(langLabel.getKey());
					if (label == null || label.equals("")) {
						viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + gDirNameCurrent.substring(1) + fieldAlias, gDirNameCurrent.substring(1) + fieldAlias);
					} else {
						viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + gDirNameCurrent.substring(1) + fieldAlias, label);
					}
				}
				//end Ajout
	
			} else if (field.getRole().equals("Folder")) {
				
				String fieldAlias = "";
				if (field.getAlias().equals("")) {
					fieldAlias = field.getField_name();
				} else {
					fieldAlias = field.getAlias();
				}
				
				//Ajout des labels folder dans le map
				for(Entry<String, Map<String, String>> langLabel: viewsLabelMap.entrySet()){
					String label = field.getLabels().get(langLabel.getKey());
					if (label == null || label.equals("")) {
						viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + repName + "." + fieldAlias, repName + "." + fieldAlias);
					} else {
						viewsLabelMap.get(langLabel.getKey()).put(qsFinalName + "." + repName + "." + fieldAlias, label);
					}
				}
				//end Ajout
				
			} else if (field.getRole().equals("FolderRefView")) {
				//manque recurs count
//				createViewsf1(cognosDefaultLocale, "[VIEWS].[" + qs.getTable_name() + "]", field, qsFinal, gDirNameCurrent);
			}
		}
	}
	
	protected static void f1(String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, String qSleftType, String leftFilterNameSpace) {
		
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
				
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					if (qSleftType.equals("Final")) {
						gFieldName = pkAlias;
						gDirNameCurrent = "." + pkAlias;
					} else {
						gFieldName = gDirName.substring(1) + "." + pkAlias;
						gDirNameCurrent = gDirName + "." + pkAlias;
					}
					
					//add labels from map for folders if the folder takes the names of the QS.
					//on insere le label pour ce path dans le cas du pkalias pour retrouver le label dans le map depuis le factory,
					// dans le cas ou la sequence integre le champ above, on recupèra le label du field, il portera le meme nom que le répertoire
					for(Entry<String, String> langLabel: query_subjects.get(pkAlias + namespaceID).getLabels().entrySet()){
						String label = "";
						if(langLabel.getValue() == null || langLabel.getValue().equals("")){
							label = pkAlias;
						} else {
							label = langLabel.getValue();
						}
						labelMap.get(langLabel.getKey()).put(qsFinalName + gDirNameCurrent, label);
					}
					/* 
					if(query_subjects.get(pkAlias + namespaceID).getLabel() == null || query_subjects.get(pkAlias + namespaceID).getLabel().equals(""))
					{label = pkAlias;} else {label = query_subjects.get(pkAlias + namespaceID).getLabel();
					}
					labelMap.put(qsFinalName + gDirNameCurrent, label);
					*/
				}
				else {
					if (qSleftType.equals("Final")) {
						gFieldName = rel.getAbove();
						gDirNameCurrent = "." + rel.getAbove();
					} else {
						gFieldName = gDirName.substring(1) + "." + rel.getAbove();
						gDirNameCurrent = gDirName + "." + rel.getAbove();
					}
				}
				
				//creation Objects
				fsvc.copyQuerySubject("[PHYSICALUSED]", "[PHYSICAL].[" + rel.getPktable_name() + "]");	
				fsvc.renameQuerySubject("[PHYSICALUSED].[" + rel.getPktable_name() + "]",namespaceName + "_" + pkAlias + String.valueOf(i));
				fsvc.createQuerySubject("PHYSICALUSED", namespaceName, namespaceName + "_" + pkAlias + String.valueOf(i), qsFinalName + gDirNameCurrent);
				
				
				String filterNameSpaceSource = "[" + namespaceName+ "]";
				//Only for Ref and tra (on ne crée pas de repertoire pour tra, on rentre dans ce if uniquement pour pouvoir changer l'expression du champ à traduire)
				if (namespaceID.equals("Ref") || namespaceID.equals("Tra")) {
				
					//filtre
					
				//	String filterReset = "";
					if (query_subjects.get(pkAlias + namespaceID).getFilters()!=null && !query_subjects.get(pkAlias + namespaceID).getFilters().isEmpty())
					{
						
//						fsvc.createQuerySubject(namespaceName, "FILTER_" + namespaceName, qsFinalName + gDirNameCurrent, qsFinalName + gDirNameCurrent);
						System.out.println("Filters: (pkAlias + namespaceID): " + pkAlias + namespaceID);
						
						Map<String, Filter> qsFilters = query_subjects.get(pkAlias + namespaceID).getFilters();
							
						int z = 0;
						boolean isFilterRefCreated = false;
						for(Entry<String, Filter> f: qsFilters.entrySet()){
							
							if (f.getValue().getTarget().equals("[" + namespaceName + "].[" + qsFinalName + gDirNameCurrent + "]") || f.getValue().getTarget().equals("*")) {
								
								if (!isFilterRefCreated) {
									fsvc.createQuerySubject(namespaceName, "FILTER_" + namespaceName, qsFinalName + gDirNameCurrent, qsFinalName + gDirNameCurrent);
									filterNameSpaceSource = "[FILTER_" + namespaceName + "]";
									System.out.println("Filters created for: " + qsFinalName + gDirNameCurrent);
									isFilterRefCreated = true;
								}
								
								String pathRefQs = "[FILTER_" + namespaceName + "].[" + qsFinalName + gDirNameCurrent + "]";
								String name = f.getValue().getName();
								String option = "always";
								if (f.getValue().getOption().equals("Facultative")) {
									option = "asNeeded";
								}
								String exp = f.getValue().getExpression();
								if (f.getValue().getTarget().equals("*")) {
									if (exp.contains("*")) {
										exp = StringUtils.replace(exp, "*", "[" + namespaceName + "].[" + qsFinalName + gDirNameCurrent + "]");
									}
									name = name + z;
								}
							
								fsvc.createQuerySubjectFilter(pathRefQs , exp, name, option, z);
								z++;
							}
						}

//						filterNameSpaceSource = "[FILTER_" + namespaceName + "]";
					}
					//end filtre
					
					String gFieldNameReorder;
					if(qSleftType.equals("Final")) {
						gFieldNameReorder = rel.getAbove();
					} else {
						gFieldNameReorder = gDirName.substring(1) + "." + rel.getAbove();
					}
					String rep = qsFinal + ".[" + gDirName + "]";
					
					if (!rel.isTra()){
						if (qSleftType.equals("Final")) {
							fsvc.createSubFolder("[DATA].[" + qsFinalName + "]", gDirNameCurrent);
						} else {
							fsvc.createSubFolderInSubFolderIIC(rep, gDirNameCurrent);
						}
						//add tooltip
						/*
						String desc = "";
						if(query_subjects.get(pkAlias + namespaceID).getDescription() != null) {desc = ": " + query_subjects.get(pkAlias + namespaceID).getDescription();}
						fsvc.createScreenTip("queryItemFolder", qsFinal + ".[" + gDirNameCurrent + "]", query_subjects.get(pkAlias + namespaceID).getTable_name() + desc);
						*/
						for(Entry<String, String> langDesc: query_subjects.get(pkAlias + namespaceID).getDescriptions().entrySet()){
							if(langDesc.getValue() == null || langDesc.getValue().equals("")) {
								qifScreenTipMap.get(langDesc.getKey()).put(qsFinal + ".[" + gDirNameCurrent + "]", query_subjects.get(pkAlias + namespaceID).getTable_name());
								} else {
//									System.out.println("QSF langDesc : " + langDesc.getValue());
									qifScreenTipMap.get(langDesc.getKey()).put(qsFinal + ".[" + gDirNameCurrent + "]", query_subjects.get(pkAlias + namespaceID).getTable_name() + ": " + langDesc.getValue());
								}
						}
						
						//end tooltip
						
						if(rel.getKey_type().equalsIgnoreCase("F")){
							fsvc.ReorderSubFolderBefore(qsFinal + ".[" + gDirNameCurrent + "]", qsFinal + ".[" + gFieldNameReorder + "]");
						}
					}
					
					for(Field field: query_subjects.get(pkAlias + namespaceID).getFields()){
						
						if (rel.isRef()) {
							if (field.isCustom()) {
								
								fsvc.createQueryItemInFolder(qsFinal, gDirNameCurrent, gFieldName + "." + field.getField_name(), field.getExpression());
						
							} else {
							fsvc.createQueryItemInFolder(qsFinal, gDirNameCurrent, gFieldName + "." + field.getField_name(), filterNameSpaceSource + ".[" + qsFinalName + gDirNameCurrent +"].[" + field.getField_name() + "]");
							}
						}
						
						// on change la valeur de l'expression du champ à traduire, on change donc à un niveau n-1, on change donc l'expression du champ gFieldNameReorder
						
						if (field.isTraduction() && rel.isTra()) {
							
							System.out.println("qsFinal + \".[\" + gFieldNameReorder + \"]\" : " + qsFinal + ".[" + gFieldNameReorder + "]");
							fsvc.modifyQueryItem(qsFinal + ".[" + gFieldNameReorder + "]", "if (" + filterNameSpaceSource + ".[" + qsFinalName + gDirNameCurrent +"].[" + field.getField_name() + "] is null) then (" 
							+ leftFilterNameSpace + ".[" + qsFinalName + gDirName + "].[" + rel.getAbove() + "]) else (" 
							+ filterNameSpaceSource + ".[" + qsFinalName + gDirNameCurrent +"].[" + field.getField_name() + "])", "expression");
						}
						//fin changement de valeur de l'expression
						
						//add label   //cette partie s'execute uniquement pour les ref car les tables de traductions n'apparaissent pas dans data
						if (rel.isRef()) {
/*							for(Entry<String, String> langLabel: field.getLabels().entrySet()){     // cas ou les maps comporte toutes les langue meme si la traduction n'est pas remplie
								String label = "";
								if(langLabel.getValue() == null || langLabel.getValue().equals("")){
									label = field.getField_name();
								} else {
									label = langLabel.getValue();
								}
								labelMap.get(langLabel.getKey()).put(qsFinalName + "." + gFieldName + "." + field.getField_name(), label);
							}
*/							// end label
							
							// test dans le cas de langues manquantes dans le map
							for(Entry<String, Map<String, String>> langLabel: labelMap.entrySet()){
								String label = field.getLabels().get(langLabel.getKey());
								if(label == null || label.equals("")){
									label = field.getField_name();
								}
								labelMap.get(langLabel.getKey()).put(qsFinalName + "." + gFieldName + "." + field.getField_name(), label);
							}
							// end test
							
							// add tooltip
							/*
							desc = "";
							if(field.getDescription() != null) {desc = ": " + field.getDescription();}
							fsvc.createScreenTip("queryItem", qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", query_subjects.get(pkAlias + namespaceID).getTable_name() + "." + field.getField_name() + desc);
							*/
							// map tooltip
							for(Entry<String, String> langDesc: field.getDescriptions().entrySet()){
								if(langDesc.getValue() == null || langDesc.getValue().equals("")) {
									qiScreenTipMap.get(langDesc.getKey()).put(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", query_subjects.get(pkAlias + namespaceID).getTable_name() + "." + field.getField_name());
									} else {
							//			System.out.println("QI langDesc : " + langDesc.getValue());
										qiScreenTipMap.get(langDesc.getKey()).put(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", query_subjects.get(pkAlias + namespaceID).getTable_name() + "." + field.getField_name() + ": " + langDesc.getValue());
									}
							}
							// map tooltip
							// end tooltip
							//change property query item
							fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "usage", field.getIcon().toLowerCase());
							if (!field.getDisplayType().toLowerCase().equals("value"))
							{
								fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "displayType", field.getDisplayType().toLowerCase());
								
							}
							if (field.isHidden())
							{
								fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "hidden", "true");
								
							}
							//end change
						}
					}
				}
				//end only for Ref

				//create relation
				String parentNameSpace = "";
				String leftQsType = qSleftType.toUpperCase();
				if (!qSleftType.equals(namespaceID)) {
					parentNameSpace = "AUTOGENERATION";
				} else {
					parentNameSpace = qSleftType.toUpperCase();
				}
				
				RelationShip RS = new RelationShip("[" + leftQsType + "].[" + qsAliasInc + "]" , "[" + namespaceName + "].[" + qsFinalName + gDirNameCurrent + "]");
				
				// changer en qs + refobj
				String fixedExp = rel.getRelationship();
				if(!qSleftType.equals("Final")) {
					//for recursive QS
					if (qsAlias.equals(pkAlias) && leftQsType.equals(namespaceName)) {
						
						fixedExp = StringUtils.replace(fixedExp, "LEFT[" + leftQsType + "].[" + qsAlias + "]", "[" + leftQsType + "].[" + qsAliasInc + "]");
						fixedExp = StringUtils.replace(fixedExp, "RIGHT[", "[");
						System.out.println("Recursive fixedExp: " + fixedExp);
						System.out.println("Recursive fixedExp: " + fixedExp);
					//end for recursive QS			
						
					} else {
						//original line
						fixedExp = StringUtils.replace(fixedExp, "[" + leftQsType + "].[" + qsAlias + "]", "[" + leftQsType + "].[" + qsAliasInc + "]");
					}
					//end fix recursive tables
				}
				fixedExp = StringUtils.replace(fixedExp, "[" + namespaceName + "].[" + pkAlias + "]", "[" + namespaceName + "].[" + qsFinalName + gDirNameCurrent + "]");

				//				System.out.println("tra : " + fixedExp);
				
				RS.setExpression(fixedExp);
				if (rel.isRightJoin())
				{
					RS.setCard_left_min("zero");
				} else {
					RS.setCard_left_min("one");
				}
				RS.setCard_left_max("many");

				if (rel.isLeftJoin())
				{
					RS.setCard_right_min("zero");
				} else {
					RS.setCard_right_min("one");
				}
				RS.setCard_right_max("one");
				RS.setParentNamespace(parentNameSpace);
				rsList.add(RS);
				
				//end create relation
				
				f1(pkAlias, qsFinalName + gDirNameCurrent, gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, namespaceID, filterNameSpaceSource);	
			}
		}
	}
	
	protected static void createMeasures(String PreviousQSID, String qsID, Boolean first, Map<String, Map<String, String>> mm) {

		QuerySubject query_subject = query_subjects.get(qsID);
		QuerySubject query_subject_prev = query_subjects.get(PreviousQSID);
		
		if(!mm.containsKey("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]")) {
			Boolean folderCreated = false;
			for(Field field: query_subject.getFields()) {
				
				if ((!field.getMeasure().equals(""))) {
					
					if (!folderCreated) {
						if (first) {
						//	fsvc.createFolder("[DIMENSIONAL]", query_subject.getTable_alias());
							first = false;
						} else {
						//	fsvc.createFolderInFolder("[DIMENSIONAL]", query_subject_prev.getTable_alias(), query_subject.getTable_alias());
						}
					folderCreated = true;
				//	fsvc.createMeasureDimension("[DIMENSIONAL].[" + query_subject.getTable_alias() + "]", query_subject.getTable_alias() + " Fact");
					fsvc.createMeasureDimension("[DIMENSIONAL]", query_subject.getTable_alias() + " Fact"); //without folder
					Map<String, String> m = new HashMap<String, String>();
					mm.put("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]", m);
					}
				fsvc.createMeasure("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "]", query_subject.getTable_alias() + "." + field.getField_name(), "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]");
				fsvc.modify("measure/regularAggregate", "/O/regularAggregate[0]/O/[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + query_subject.getTable_alias() + "." + field.getField_name() + "]", field.getMeasure().toLowerCase());
				Map<String, String> m = mm.get("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]");
				m.put(field.getField_name(), "[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + query_subject.getTable_alias() + "." + field.getField_name() + "]");
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				createMeasures(qsID, rel.getPktable_alias() + "Final", first, mm);
			}
		}
	}
	
	protected static void scanDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> dimensionsBK, Map<String, Map<String, String>> dimensionsHN, Map<String, Map<String, String>> dimensionsAT) {
		//On parcours tous les QS Final + Ref pour trouver les différentes dimensions, sans alimenter les hierachies, ni les levels, ni les champs
		
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			for(Field field: query_subject.getValue().getFields()) {
				List<Map<String, String>> fieldDimensions = field.getDimensions();
				if (field.getDimensions()!=null) {
					for (Map<String, String> dimensionAttributes : fieldDimensions) {
						String attDimension = dimensionAttributes.get("dimension");
						if (!attDimension.startsWith("[") && !attDimension.endsWith("]")) {
								//Multi map
								
								System.out.println("Recup fields : " + dimensionAttributes.get("dimension") + " " + dimensionAttributes.get("order") + " " + dimensionAttributes.get("bk") + dimensionAttributes.get("hierarchyName"));
								
								if (!dimensions.containsKey(attDimension)) {
									Map<String, String> hierarchiesFields = new HashMap<String, String>();
									Map<String, String> hierarchiesFieldsBK = new HashMap<String, String>();
									Map<String, String> hierarchiesFieldsHN = new HashMap<String, String>();
									Map<String, String> hierarchiesFieldsAT = new HashMap<String, String>();
									//On crée la dimension
									dimensions.put(attDimension, hierarchiesFields);
									dimensionsBK.put(attDimension, hierarchiesFieldsBK);
									dimensionsHN.put(attDimension, hierarchiesFieldsHN);
									dimensionsAT.put(attDimension, hierarchiesFieldsAT);
								}
							
							//end Multi map
							/*String dim[] = StringUtils.split(field.getDimension(), ",");
							for (int i=0; i < dim.length; i++) {
								//un tableau pour les eventuelles multiples dimensions pour un champ de ref. 
								String dimension = dim[i];
								if (!dimensions.containsKey(dimension)) {
									Map<String, String> hierarchiesFields = new HashMap<String, String>();
									//On crée la dimension
									dimensions.put(dimension, hierarchiesFields);
								}
							}*/
						} else {
							// create time dimension
							System.out.println("Recup fields : " + dimensionAttributes.get("dimension") );
							Map<String, String> hierarchies = new HashMap<String, String>();
							dimensions.put("Time Dimension " + query_subject.getValue().getTable_alias() + "." + field.getField_name(), hierarchies);
							
							//Case in order to create time dimension with options: Quarter ? Weeks ? Rolling month ? Hour ? Minute ? Date ?
							
							String optionsStr = dimensionAttributes.get("dimension");
							optionsStr = StringUtils.replace(optionsStr.substring(1), "]", "");

							String optionsByMonth = "";
							String optionsByWeek = "";
							String optionsRollingMonth = "";
							
							if (optionsStr.contains("Quarter")) {
								optionsByMonth = "YEAR,QUARTER,MONTH,DAY";
								if (optionsStr.contains("Rollling month")) {
									optionsRollingMonth = "YEAR,QUARTER,MONTH,DAY";
								}
							} else {
								optionsByMonth = "YEAR,MONTH,DAY";
								if (optionsStr.contains("Rollling month")) {
									optionsRollingMonth = "YEAR,MONTH,DAY";
								}
							}
							if (optionsStr.contains("Weeks")) {
								optionsByWeek = "YEAR,WEEK,DAY_OF_WEEK";
							}
							if (optionsStr.contains("AM/PM")) {
								optionsByMonth = optionsByMonth + ",AM/PM";
								if (!optionsByWeek.equals("")) {
									optionsByWeek = "YEAR,WEEK,DAY_OF_WEEK,AM/PM";
								}
								if (!optionsRollingMonth.equals("")) {
									optionsRollingMonth = optionsRollingMonth + ",AM/PM";
								}
							}
							if (optionsStr.contains("Date")) {
								optionsByMonth = optionsByMonth + ",HOUR,MIN,DATE";
								if (!optionsByWeek.equals("")) {
									optionsByWeek = optionsByWeek + ",HOUR,MIN,DATE";
								}
								if (!optionsRollingMonth.equals("")) {
									optionsRollingMonth = optionsRollingMonth + ",HOUR,MIN,DATE";
								}
								
							} else if (optionsStr.contains("Minute")) {
								optionsByMonth = optionsByMonth + ",HOUR,MIN";
								if (!optionsByWeek.equals("")) {
									optionsByWeek = optionsByWeek + ",HOUR,MIN";
								}
								if (!optionsRollingMonth.equals("")) {
									optionsRollingMonth = optionsRollingMonth + ",HOUR,MIN";
								}
								
							} else if (optionsStr.contains("Hour")) {
								optionsByMonth = optionsByMonth + ",HOUR";
								if (!optionsByWeek.equals("")) {
									optionsByWeek = optionsByWeek + ",HOUR";
								}
								if (!optionsRollingMonth.equals("")) {
									optionsRollingMonth = optionsRollingMonth + ",HOUR";
								}
							}
							//End Case
							// Add Hierarchy
							hierarchies.put("By month", optionsByMonth);
							if (!optionsByWeek.equals("")) {
								hierarchies.put("By week", optionsByMonth);
							}
							if (!optionsRollingMonth.equals("")) {
								hierarchies.put("Rolling month", optionsByMonth);
							}
							
							// End add hierarchy
						}
					}
				}
			}
		}
	}
	
	protected static void scanFinalFieldsDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> dimensionsBK, Map<String, Map<String, String>> dimensionsHN, Map<String, Map<String, String>> dimensionsAT) {
		
		for (Entry<String, Map<String, String>> dimension: dimensions.entrySet()) {
			System.out.println("dimension.getKey() : " + dimension.getKey());
			for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
				if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
					
					for(Field field: query_subject.getValue().getFields()) {
		
						/*if (field.getDimension().equals(dimension.getKey())) {
							Map<String, String> hierarchiesFields = dimension.getValue();
							//S'il n'y est pas deja, on ajoute le champ dans le map des fields, tous les champs de la dimension seront dans ce map.
							if (!hierarchiesFields.containsKey("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]")) {
								//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
								hierarchiesFields.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", field.getOrder());
							}
						}*/
						
						//multi_map
						List<Map<String, String>> fieldDimensions = field.getDimensions();
						for (Map<String, String> dimensionAttributes : fieldDimensions) {
							String attDimension = dimensionAttributes.get("dimension");
							String attOrder = dimensionAttributes.get("order");
							String attBK = dimensionAttributes.get("bk");
							String attHN = dimensionAttributes.get("hierarchyName");
							String attAT = dimensionAttributes.get("attributs");
							if (attDimension.equals(dimension.getKey())) {
								Map<String, String> hierarchiesFields = dimension.getValue();
								Map<String, String> hierarchiesFieldsBK = dimensionsBK.get(dimension.getKey());
								Map<String, String> hierarchiesFieldsHN = dimensionsHN.get(dimension.getKey());
								Map<String, String> hierarchiesFieldsAT = dimensionsAT.get(dimension.getKey());
								//S'il n'y est pas deja, on ajoute le champ dans le map des fields, tous les champs de la dimension seront dans ce map.
								if (!hierarchiesFields.containsKey("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]")) {
									//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
									hierarchiesFields.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", attOrder);
									hierarchiesFieldsBK.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", attBK);
									hierarchiesFieldsHN.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", attHN);
									hierarchiesFieldsAT.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", attAT);
								}
							}
						}
						//end multi map
					}
					//lancement scanRef ref
					Map<String, Integer> recurseCount = new HashMap<String, Integer>();
					for(QuerySubject qs: qsList){
			        	recurseCount.put(qs.getTable_alias(), 0);
					}
					scanRefFieldsDimensions(dimensions, dimensionsBK, dimensionsHN, dimensionsAT, dimension.getKey(), query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCount, true);
					//End scanRef
				}	
			}
		}
	}
			
	protected static void scanRefFieldsDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> dimensionsBK, Map<String, Map<String, String>> dimensionsHN, Map<String, Map<String, String>> dimensionsAT, String dimension, String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, Boolean qSleftIsFinal) {
		//On va parcourir chaque arbre ref en partant de la table final, afin de pouvoir utiliser le gDirName pour modifier la référence de la clef du map. Ex SYSUSERRef devient pour cognos [DATA].[S_SAMPLE].[SECURITYUSER.SYSUSERDESC]
		
		Map<String, String> hierarchiesFields = dimensions.get(dimension);
		Map<String, String> hierarchiesFieldsBK = dimensionsBK.get(dimension);
		Map<String, String> hierarchiesFieldsHN = dimensionsHN.get(dimension);
		Map<String, String> hierarchiesFieldsAT = dimensionsAT.get(dimension);
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		QuerySubject query_subject;
		if (!qSleftIsFinal) {
			
			query_subject = query_subjects.get(qsAlias + "Ref");
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		} else {
			query_subject = query_subjects.get(qsAlias + "Final");
		}
		if (!qSleftIsFinal) {
			for(Field field: query_subject.getFields()) {
				
				//multi_map
				List<Map<String, String>> fieldDimensions = field.getDimensions();
				if (fieldDimensions!=null) {
					
					for (Map<String, String> dimensionAttributes : fieldDimensions) {
						String attDimension = dimensionAttributes.get("dimension");
						if (!attDimension.startsWith("[") && !attDimension.endsWith("]")) {
					
							String attOrder = dimensionAttributes.get("order");
							String attBK = dimensionAttributes.get("bk");
							String attHN = dimensionAttributes.get("hierarchyName");
							String attAT = dimensionAttributes.get("attributes");
							if (attDimension.equals(dimension)) {
								if (!hierarchiesFields.containsKey("[DATA].[" + qsFinalName + "].[" + gDirName.substring(1) + field.getField_name() + "]")) {
									//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
									String key = StringUtils.replace("[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + gDirName.substring(1) + "." + field.getField_name() + "]") ;
									key = StringUtils.replace(key,"[" + query_subject.getTable_alias() + "]","[" + qsFinalName + "]");
									hierarchiesFields.put(key, attOrder);
									hierarchiesFieldsBK.put(key, attBK);
									hierarchiesFieldsHN.put(key, attHN);
									hierarchiesFieldsAT.put(key, attAT);
								}
							}
						}
					}//end multi map
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			
			if(rel.isRef() && (rel.getUsedForDimensions().equals("true") && !qSleftIsFinal) || (rel.getUsedForDimensions().equals(dimension) && qSleftIsFinal)){
				//seq
				String pkAlias = rel.getPktable_alias();

				String gDirNameCurrent = "";
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){	
					gDirNameCurrent = gDirName + "." + pkAlias;
				}
				else {
					gDirNameCurrent = gDirName + "." + rel.getAbove();
				}
				
				scanRefFieldsDimensions(dimensions, dimensionsBK, dimensionsHN, dimensionsAT, dimension, pkAlias, qsFinalName + gDirNameCurrent, gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, false);
			}
		}
	}
			
	protected static void createHierarchiesNb(Map<String, Map<String, String>> dimensions) {
		
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if(!dimension.getKey().startsWith("Time Dimension")) {
				Map<String, String> dimensionFields = new HashMap<String, String>();
				Map<String, String> hierarchies = new HashMap<String, String>();
				dimensionFields.putAll(dimension.getValue());
				System.out.println("dimensionFields : " + dimensionFields);
				int i = 1;
				for(Entry<String, String> dimensionField: dimensionFields.entrySet()){
					if (dimensionField.getValue().equals("")) {
						String hierarchy = dimensionField.getKey();
						hierarchies.put(Integer.toString(i), hierarchy);
						i = createHierarchiesNbRecurs(dimensionFields, dimensionField.getKey(), hierarchies, i);
						i++;
					}
				}
				System.out.println("Hierarchies : " + hierarchies);
				dimensions.put(dimension.getKey(), hierarchies);
			}
		}
	}
	
	protected static int createHierarchiesNbRecurs (Map<String, String> dimensionFields, String previousField, Map<String, String> hierarchies, int i) {

		String hierarchyBase = hierarchies.get(Integer.toString(i));
		Boolean addHierarchy = false;
		for(Entry<String, String> dimensionField: dimensionFields.entrySet()){
			if (dimensionField.getValue().equals(previousField)) {
				if (addHierarchy) {i++;}
				String hierarchy = hierarchyBase + ";" + dimensionField.getKey();
				hierarchies.put(Integer.toString(i), hierarchy);
				i = createHierarchiesNbRecurs(dimensionFields, dimensionField.getKey(), hierarchies, i);
				addHierarchy = true;
				
			}
		}
		return i;
	}

	protected static void renameHierarchies(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> dimensionsHN){
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if(!dimension.getKey().startsWith("Time Dimension")) {
				Map<String, String> hierarchies = new HashMap<String, String>();
				Map<String, String> namedHierarchies = new HashMap<String, String>();
				hierarchies.putAll(dimension.getValue());
				Map<String, String> hierarchiesFieldsHN = dimensionsHN.get(dimension.getKey());
				
				for(Entry<String, String> hierarchy: hierarchies.entrySet()){
					String hierarchyTab[] = StringUtils.split(hierarchy.getValue(), ";");
					if (hierarchyTab.length > 0) {
						String hierarchyNameExp[] = StringUtils.splitByWholeSeparator(hierarchyTab[hierarchyTab.length - 1], "].[");
						//On donne le Hierarchy Name si celui -ci est rempli, cela pour les modeles durables
					 
						String choosenHierarchyName = hierarchiesFieldsHN.get(hierarchyTab[hierarchyTab.length - 1]);
						
						if (choosenHierarchyName!=null && !choosenHierarchyName.equals("")) {
							String modelHierarchyName = dimension.getKey() + "." + choosenHierarchyName;
							namedHierarchies.put(modelHierarchyName, hierarchy.getValue());
							System.out.println("choosenHierarchyName : " + dimension.getKey() + "." + modelHierarchyName);
						}
						//Sinon on lui donne le nom du niveau le plus haut (Moins durable)
						else if (hierarchyNameExp.length > 2) {
							String hierarchyName = hierarchyNameExp[1] + "." + StringUtils.replace(hierarchyNameExp[2], "]", "");
							namedHierarchies.put(hierarchyName, hierarchy.getValue());
							System.out.println("hierarchyName : " + hierarchyName);
						}
						//test pour traduction hierarchies
			/*			
						if (hierarchyNameExp.length > 2) {
							String hierarchyName = hierarchyNameExp[1] + "." + StringUtils.replace(hierarchyNameExp[2], "]", "");
									System.out.println("Hierarchy Name Label Map : " + hierarchyName + " : " + labelMap.get(hierarchyName));
									// a changer lorsque nous aurons les traductions pour un hierarchyName, 
									//il faudra afficher la trad du HN au lieu de la trad du champ qui porte le dernier level de la hierarchy
									labelMap.put(choosenHierarchyName,labelMap.get(hierarchyName));
									labelMap.put(choosenHierarchyName + "(All)",labelMap.get(hierarchyName));
									System.out.println("Hierarchy Name Label Map renamed : " + labelMap.get(choosenHierarchyName));
						}
				*/		
						//fin test
					}
				}
				dimensions.put(dimension.getKey(), namedHierarchies);
			}
		}
	}
	
	protected static void buildDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> dimensionsBK, Map<String, Map<String, String>> dimensionsAT, Map<String, Map<String, String>> measures, String dbEngine){
		
		Map<String, String> dimensionScreenTip = new HashMap<String, String> ();
		Map<String, String> hierarchyScreenTip = new HashMap<String, String> ();
		Map<String, String> levelScreenTip = new HashMap<String, String> ();
		
		Map<String, String> scopesToDisable = new HashMap<String, String> ();
		Map<String, String> scopesToEnable = new HashMap<String, String> ();
		
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){

			if (dimension.getKey().startsWith("Time Dimension ")) {
				String path = dimension.getKey();
				path = StringUtils.replace(path, "Time Dimension ", "");
				String split[] = StringUtils.split(path, ".");
				String qiName = split[1];
				path = StringUtils.replace(path, ".", "].[");
				path = "[DATA].[" + path + "]";
				System.out.println("createTimeDimension : " + path + ", " + dimension.getKey() + ", " + qiName);
				fsvc.createTimeDimension(path, dimension.getKey(), qiName, dbEngine, dimension.getValue());
			} else 
			{
				fsvc.createDimension("[DIMENSIONAL]", dimension.getKey());		
			}
			
			// time dimension
//			String dateQueryItemPath = "[FINAL].[SDIDATA].[CREATEDT]";
//			String dateQueryItemName = "CREATEDT";
//			String dimensionName = "SDIDATA.CREATEDT";
			if (!dimension.getKey().startsWith("Time Dimension ")) {
				Map<String, String> hierarchiesFieldsBK = dimensionsBK.get(dimension.getKey());
				Map<String, String> hierarchiesFieldsAT = dimensionsAT.get(dimension.getKey());
				
				for (Entry<String, String> hierarchy: dimension.getValue().entrySet()) {
					
					fsvc.createEmptyNewHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]");
					fsvc.createEmptyHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]", hierarchy.getKey());
					System.out.println("hierarchy.getKey() : " + hierarchy.getKey());
					
					String levels[] = StringUtils.split(hierarchy.getValue(), ";");
					
					for (int i = levels.length - 1; i >= 0; i--) {
						
						String name = "";
						String levelNameExp[] = StringUtils.splitByWholeSeparator(levels[i], "].[");
						if (levelNameExp.length > 2) {
							name = levelNameExp[1] + "." + StringUtils.replace(levelNameExp[2], "]", "");
						}
						
						String exp = levels[i];
						
						String expScope[] = StringUtils.split(exp, ".");
						String tableScope = StringUtils.replace(expScope[1], "[", "");
						tableScope = StringUtils.replace(tableScope, "]", "");
						fsvc.createEmptyHierarchyLevel("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", name);
						
						//gestion des bk
						if (hierarchiesFieldsBK.get(exp)!=null && !hierarchiesFieldsBK.get(exp).equals("")) {
							
							fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", name + "_KEY", hierarchiesFieldsBK.get(exp));
							fsvc.createDimensionRole_BK("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "_KEY]");
							//label du champ employé comme BK, si plusieurs champs --> ca ne marchera pas, il faudra créer un champ custom et l'utiliser comme BK.
							
							String bkExpSplit[] = StringUtils.splitByWholeSeparator(hierarchiesFieldsBK.get(exp),"].[");
							System.out.println("hierarchiesFieldsBK.get(exp) : " + hierarchiesFieldsBK.get(exp));
							String bkLabelKey = "";
							if (bkExpSplit.length == 3) {
								bkLabelKey = bkExpSplit[1] + "." + bkExpSplit[2];
								bkLabelKey = StringUtils.replace(bkLabelKey, "]", "");
								System.out.println("bkLabelKey : " + bkLabelKey);
								
								for(Entry<String, Map<String, String>> langLabel: labelMap.entrySet()){
									System.out.println("langLabel.getKey() : " + langLabel.getKey());
									
									String label = langLabel.getValue().get(bkLabelKey);
									System.out.println("bkLabelKey label : " + label);
									if(label != null && !label.equals("")){
										langLabel.getValue().put(name + "_KEY", label);
										System.out.println("name_KEY label : " + label);
									}
								}
							}
							//end label BK
						} else {
							fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", name + "_KEY", exp);
							fsvc.createDimensionRole_BK("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "_KEY]");
							// set hiddenn
							fsvc.changeQueryItemProperty("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "_KEY]", "hidden", "true");
						}
						
						fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", name, exp);
						
						fsvc.createDimensionRole_MC("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
						fsvc.createDimensionRole_MD("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
//						fsvc.createDimensionRole_BK("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
						
						//screenTip QueryItem
						fsvc.createScreenTip("queryItem", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]", exp, 0);
						
						//Add Attribute (Example on BK) To change soon with attribute list
						if (hierarchiesFieldsAT.get(exp)!=null && !hierarchiesFieldsAT.get(exp).equals("")) {
							String AttributeListSplit[] = StringUtils.splitByWholeSeparator(hierarchiesFieldsAT.get(exp),";");
							for (int j = 0; j < AttributeListSplit.length; j++) {
								String nameAttTab[] = StringUtils.splitByWholeSeparator(AttributeListSplit[j],"].[");
								if (nameAttTab.length == 3) {
									String nameAtt = nameAttTab[1] + "." + nameAttTab[2];
									nameAtt = StringUtils.replace(nameAtt, "]", "");
									System.out.println("nameAtt : " + nameAtt);
									fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", nameAtt, AttributeListSplit[j]);
								}
							}
						}
						//End add Attribute
						
						
						//define scope
						for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
							
							String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
							String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
							measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
							
							Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
													
							//System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
							//System.out.println(isHigher);
							if (isHigher) {
								for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
									//scopesToEnable
									// key : measureDimensionPath ; measurePath ; hierarchyPath
									// value : dimensionPath ; level
									scopesToEnable.put(measureDimension.getKey() + ";" + measure.getValue() + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "]" + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
									fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", "0");
									//System.out.println("adjustScopeRelationship( " + measureDimension.getKey() + ", " + measure.getValue() + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "]" + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
								}
								//manage screenTip for dimension, hierarchy, level
								//level
								String measureDimensionName = StringUtils.replace(StringUtils.splitByWholeSeparator(measureDimension.getKey(), "].[")[1], "]", "");
								String lScreenTip = "";
								if (levelScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]")==null) {
									lScreenTip = measureDimensionName;
								} else {
									lScreenTip = levelScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
								}
								if (!lScreenTip.contains(measureDimensionName)) {
									lScreenTip = lScreenTip + ", " + measureDimensionName;
								}
								levelScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", lScreenTip);
								//hierarchy
								String hScreenTip = "";
								if (hierarchyScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]")==null) {
									hScreenTip = measureDimensionName;
								} else {
									hScreenTip = hierarchyScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]");
								}
								if (!hScreenTip.contains(measureDimensionName)) {
									hScreenTip = hScreenTip + ", " + measureDimensionName;
								}
								hierarchyScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", hScreenTip);
								//dimension
								String dScreenTip = "";
								if (dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]")==null) {
									dScreenTip = measureDimensionName;
								} else {
									dScreenTip = dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]");
								}
								if (!dScreenTip.contains(measureDimensionName)) {
									dScreenTip = dScreenTip + ", " + measureDimensionName;
								}
								dimensionScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "]", dScreenTip);
							} else {
								//disable scope
								for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
									String all = "";
									if (i == levels.length - 1) {
										all = "(All)";
										
										// key : measure dimensionPath - measurePath - levelPath
										// value : dimensionPath
								//old	scopesToDisable.put(measureDimension.getKey() + ";" + measure.getValue() + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + all + "]", "[DIMENSIONAL].[" + dimension.getKey() + "]");
										scopesToDisable.put(measureDimension.getKey() + ";" + measure.getValue() + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + hierarchy.getKey() + all + "]", "[DIMENSIONAL].[" + dimension.getKey() + "]");
										
										//fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + all + "]", "1");
									}
								}
							}
						}
					}
				}
			}
		}
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if (dimension.getKey().startsWith("Time Dimension ")) {
				String path = dimension.getKey();
				path = StringUtils.replace(path, "Time Dimension ", "");
				String split[] = StringUtils.split(path, ".");
				String qiName = split[1];
				String tableScope = split[0];
				
				for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
					
					String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
					String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
					measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
					
					Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
					
					//System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
					//System.out.println(isHigher);
					if (!isHigher) {
						for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
							fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By month)].[" + qiName + " (By month)(All)]", "1");
							if (dimension.getValue().get("By week")!=null) {
								fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By week)].[" + qiName + " (By week)(All)]", "1");
							}
							if (dimension.getValue().get("Rolling month")!=null) {
								fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (Rolling month)].[" + qiName + " (Rolling month)(All)]", "1");
							}
						}
					} else {
						
						//screenTip
						//dimension
						String measureDimensionName = StringUtils.replace(StringUtils.splitByWholeSeparator(measureDimension.getKey(), "].[")[1], "]", "");
						String dScreenTip = "";
						if (dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]")==null) {
							dScreenTip = measureDimensionName;
						} else {
							dScreenTip = dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]");
						}
						if (!dScreenTip.contains(measureDimensionName)) {
							dScreenTip = dScreenTip + ", " + measureDimensionName;
						}
						dimensionScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "]", dScreenTip);
						
					}
				}
			}
		}
		//set level screenTip
		for (Entry<String, String> level: levelScreenTip.entrySet()) {
			fsvc.createScreenTip("level", level.getKey(), level.getValue(), 0);
		}
		//set hierarchy screenTip
		for (Entry<String, String> hierarchy: hierarchyScreenTip.entrySet()) {
			fsvc.createScreenTip("hierarchy", hierarchy.getKey(), hierarchy.getValue(), 0);
		}
		//set dimension screenTip
		for (Entry<String, String> dimension: dimensionScreenTip.entrySet()) {
			fsvc.createScreenTip("dimension", dimension.getKey(), dimension.getValue(), 0);
		}
		//disable scope hierarchy after set scope
		for(Entry<String, String> scopeToDisable: scopesToDisable.entrySet()){
			String tab[] = StringUtils.split(scopeToDisable.getKey(), ";");
			String measureDimensionPath = tab[0];
			String measurePath = tab[1];
			String levelPath = tab[2];
			String dimensionPath = scopeToDisable.getValue();
			fsvc.adjustScopeRelationship(measureDimensionPath, measurePath, dimensionPath, levelPath, "1");
		}
		//Re - enable scope hierarchy after set scope, contourning possible FM Bug
				for(Entry<String, String> scopeToEnable: scopesToEnable.entrySet()){
					String tabKey[] = StringUtils.split(scopeToEnable.getKey(), ";");
					String tabValue[] = StringUtils.split(scopeToEnable.getValue(), ";");
					String measureDimensionPath = tabKey[0];
					String measurePath = tabKey[1];
					String dimensionPath = tabValue[0];
					String levelPath = tabValue[1];
					fsvc.adjustScopeRelationship(measureDimensionPath, measurePath, dimensionPath, levelPath, "0");
				}
	}
	
	protected static Boolean isQsHigherThanMeasure(String qsIDMeasure, String searchQsID) {
		QuerySubject qS = query_subjects.get(qsIDMeasure);
		
		if (qsIDMeasure.equals(searchQsID)) {
			return true;
		}
		
		for(Relation rel: qS.getRelations()){
			if(rel.isFin()){
				//System.out.println("recurs isQsHigherThanMeasure : " + rel.getPktable_alias() + "Final" + ", " + searchQsID);
				if (isQsHigherThanMeasure(rel.getPktable_alias() + "Final", searchQsID)) {
					return true;
				}
			}
		}		
		return false;
	}
	
	protected static void moveDimensions(String qsID, Map <String, Map<String, String>> dimensions) {

		QuerySubject query_subject = query_subjects.get(qsID);
		
		for(Field field: query_subject.getFields()) {
			if (!field.getMeasure().equals("")) {
				
				
				for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
					
					String tableScope = "";
					Boolean move = true;
					
					if (dimension.getKey().startsWith("Time Dimension ")) {
						String path = dimension.getKey();
						path = StringUtils.replace(path, "Time Dimension ", "");
						String split[] = StringUtils.split(path, ".");
						tableScope = split[0];
						
						Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final") || isQsHigherThanMeasure(qsID, tableScope + "Ref");
						if (!isHigher || !move) {
							move = false;
						}
					}
					
					for (Entry<String, String> hierarchies: dimension.getValue().entrySet()) {
		
						if (!dimension.getKey().startsWith("Time Dimension ")) {
							String levels[] = StringUtils.split(hierarchies.getValue(), ";");
							String lev[] = StringUtils.split(levels[levels.length - 1], "-");
							String exp = lev[1];
							String expScope[] = StringUtils.split(exp, ".");
							tableScope = StringUtils.replace(expScope[1], "[", "");
							tableScope = StringUtils.replace(tableScope, "]", "");
							
							Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final") || isQsHigherThanMeasure(qsID, tableScope + "Ref");
							if (!isHigher || !move) {
								move = false;
							}
						}
					}	
					if (move) {
						fsvc.moveDimension("[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + query_subject.getTable_alias() + "]");
					}
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				moveDimensions(rel.getPktable_alias() + "Final", dimensions);
			}
		}
	}
}
