package com.dma.web;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Test13 {
	
	static Map<String, String> labelMap_FR;
	static Map<String, String> ScreenTipMap_FR;
	static Map<String, String> labelMap_EN;
	static Map<String, String> ScreenTipMap_EN;
	static Properties properties = new Properties();
	static Path logFile = null;
	static Boolean debug = false;
	
	public static void main(String[] args){
		// TODO Auto-generated method stub

		String entryArgs[] = args;
//		String entryArgs[] = {"/opt/wks/ca/prj0/res/DWC_MTDH_20200505.csv","/mnt/models","/opt/wks/ca/prj0/res/logTestChemin"};
		
		labelMap_FR = new HashMap<String, String>();
		ScreenTipMap_FR = new HashMap<String, String>();
		
		labelMap_EN = new HashMap<String, String>();
		ScreenTipMap_EN = new HashMap<String, String>();
		
		Boolean fr = false;
		Boolean en = false;
		
		String printLog = "";
		
		String projectName;
		String javaModelsPath;
		Path projectPath = null;
		
		try {
		
			try {
	
				try {
					properties.load(Test13.class.getClassLoader().getResourceAsStream("conf.properties"));
				}
				catch (NullPointerException npe) { 
					throw new Exception("Error when loading conf.properties.");
				}
	
			    /*
			     * 
			     *Ecrire dans le fichier de log en mode ajout 
			     * 
			     */
				Date date = new java.util.Date();
				String stringDate = new SimpleDateFormat("yyyyMMdd.HHmmss").format(date);
				
				String logFilePath = "";
				if (entryArgs.length > 2) {
					logFilePath = entryArgs[2] + "." + stringDate;
				} else {
					logFilePath = "DictionayJava_log" + "." + stringDate;
				}
				logFile = Paths.get(logFilePath);
		            try {
						Files.createFile(logFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						throw new Exception("Error when creating log file : " + logFilePath);
					}
		            logFile.toFile().setWritable(true);
		        
		        printLog = "Start execution date : " + new java.util.Date().toString();
			    printLog(printLog);
			    
			    if(entryArgs.length < 3) {
			    	throw new Exception ("Parameters missing : args[0] = Dictionary path, args[1] = Models path, args[2] = Log file path");   	
				} else if (entryArgs.length > 3) {
					throw new Exception ("Parameters in excess : args[0] = Dictionary path, args[1] = Models path, args[2] = Log file path");
				} else {
					printLog = "args[0] = Dictionary path, args[1] = Models path";
					printLog(printLog);
				}
			    
			    printLog = "Main entry parameters : ";
			    printLog(printLog);
			    printLog = "(Dictionary CSV Path) args[0] = " + entryArgs[0];
			    printLog(printLog);
			    printLog = "(Frameworks directory Path) args[1] = " + entryArgs[1];
			    printLog(printLog);
			    
				Path path = Paths.get(entryArgs[0]);
				String dictionaryCSV_File_Name = path.getFileName().toString();
				
			    printLog = "dictionaryCSV_File_Name = " + dictionaryCSV_File_Name;
			    printLog(printLog);
			    
			    printLog = "Execution parameters (conf.properties) : ";
			    printLog(printLog);
			    printLog = "projectName : " + properties.getProperty("projectName");
			    printLog(printLog);
			    printLog = "logFile : " + logFilePath;
			    printLog(printLog);
			    printLog = "searchNamespace : " + properties.getProperty("searchNamespace");
			    printLog(printLog);
			    printLog = "CSV_charset : " + properties.getProperty("CSV_charset");
			    printLog(printLog);
			    printLog = "requestCSV : " + properties.getProperty("requestCSV");
			    printLog(printLog);
			    printLog = "locales : " + properties.getProperty("locales");
			    printLog(printLog);
			    printLog = "modeDebug : " + properties.getProperty("modeDebug");
			    printLog(printLog);
			    
			    // Path for model.xml
			    projectName = properties.getProperty("projectName");
			    javaModelsPath = entryArgs[1];
				projectPath = Paths.get(javaModelsPath + "/" + projectName);
			    
				printLog( "model.xml Path : " + projectPath + "/model.xml");
		        
		        if (properties.getProperty("modeDebug").equals("true")) {
		        	debug = true;
		        }
		        
				String sLocales = properties.getProperty("locales");
				String tabLocales[] = StringUtils.split(sLocales,";");
	
				for (int i = 0; i < tabLocales.length; i++) {
					if (tabLocales[i].equals("fr")) {
						fr = true;
						printLog = "Choosen language : fr";
						printLog(printLog);
					}
					if (tabLocales[i].equals("en")) {
						en = true;
						printLog = "Choosen language : en";
						printLog(printLog);
					}
				}
				
				// Load the driver.
				Class.forName("org.relique.jdbc.csv.CsvDriver");
	
				// Create a connection using the directory containing the file(s)
				Properties props = new java.util.Properties();
				props.put("separator",";");
				props.put("charset", properties.getProperty("CSV_charset"));
	
	//			props.put("suppressHeaders","true");
				Connection conn = null;
				try {
					conn = DriverManager.getConnection("jdbc:relique:csv:" + path.getParent().toString(), props);
					printLog( "Find Dictionary !");
				} catch (Exception e) {
					// TODO: handle exception
					String jdbcError = "";
					if (e != null && e.getMessage() != null) {
						jdbcError = e.getMessage();
					}
					throw new Exception ("Connection CSV JDBC Error : " + jdbcError);
				}
	
				String dictionaryCSV_Name = StringUtils.replace(dictionaryCSV_File_Name, ".csv", "");
				
				// Create a Statement object to execute the query with.
				String sql = properties.getProperty("requestCSV") + " " + dictionaryCSV_Name;
				printLog( "sql request : " + sql);
	
				PreparedStatement stm = conn.prepareStatement(sql);
	
				// Query the table. The name of the table is the name of the file without ".csv"
				ResultSet results = stm.executeQuery();
				int nbLine = 0;
				while(results.next()) {
					String sNomVue = results.getString("NomVue_Physique");
					String sNomChamp = results.getString("NomChamp_Physique");
					if (!sNomVue.equals("") && !sNomChamp.equals("")) {
						nbLine ++;
					}
					if (fr) {
						String sLabelfr = results.getString("Label_FR");
						String sScreenTipfr = results.getString("Definition_FR");
						sLabelfr = replaceSpecialChar(sLabelfr);
						sScreenTipfr = replaceSpecialChar(sScreenTipfr);
						labelMap_FR.put(sNomVue + '.' + sNomChamp, sLabelfr);
						if (debug) {
							printLog( "Name fr     : " + sNomVue + '.' + sNomChamp + ", " + sLabelfr);
						}
						ScreenTipMap_FR.put(sNomVue + '.' + sNomChamp, sScreenTipfr);
						if (debug) {
							printLog("ScreenTip fr: " + sNomVue + '.' + sNomChamp + ", " + sScreenTipfr);
						}
					}
					if (en) {
						String sLabelen = results.getString("Label_EN");
						String sScreenTipen = results.getString("Definition_EN");
						sLabelen = replaceSpecialChar(sLabelen);
						sScreenTipen = replaceSpecialChar(sScreenTipen);
						labelMap_EN.put(sNomVue + '.' + sNomChamp, sLabelen);
						if (debug) {
							printLog("Name en     : " + sNomVue + '.' + sNomChamp + ", " + sLabelen);
						}
						ScreenTipMap_EN.put(sNomVue + '.' + sNomChamp, sScreenTipen);
						if (debug) {
							printLog("ScreenTip en: " + sNomVue + '.' + sNomChamp + ", " + sScreenTipen);
						}
					}
				}

				if(results != null) {results.close();}
				if(stm != null) {stm.close();}
				if(conn != null) {conn.close();}
				
				printLog("CSV dictionary lines number : " + nbLine);
				if (nbLine < 1) {
					throw new Exception ("CSV Dictionary file is empty");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw e;
			}	
	
			Charset charset = StandardCharsets.UTF_8;
			
			try {
	
				String modelSharedPath = projectPath + "/model.xml";
				
				printLog("Looking for model.xml on path : " + projectPath + "/model.xml");
				
				Path input = Paths.get(modelSharedPath);
				Path output = Paths.get(modelSharedPath);
				
				String datas = null;
							
				String inputSearch = "xmlns=\"http://www.developer.cognos.com/schemas/bmt/60/12\"";
				String outputSearch = "queryMode=\"dynamic\"";
				String outputReplace = outputSearch + " " + inputSearch;
				
				if(Files.exists(input)){
					datas = new String(Files.readAllBytes(input), charset);		
					printLog("Find model.xml");
					printLog("Start model.xml modifications");
				}
	
				datas = StringUtils.replace(datas, inputSearch, "");
				
				// modifs XML
				
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(datas.getBytes(charset)));
	
				String namespaceName = properties.getProperty("searchNamespace");
				String spath = "/project/namespace/namespace";
				int k=1;
				
				Element namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				
				printLog("Find namespace : " + namespace.getStringValue());
	
				if (fr) {
					printLog("Start model.xml modifications French");
					recursiveParserQS(document, spath, "fr", labelMap_FR, ScreenTipMap_FR);
				}
				if (en) {
					printLog("Start model.xml modifications English");
					recursiveParserQS(document, spath, "en", labelMap_EN, ScreenTipMap_EN);
				}
				
				try {
	
					datas = document.asXML();
	
					datas = StringUtils.replace(datas, outputSearch, outputReplace);
				
					printLog("End model.xml modifications : " + new java.util.Date().toString());
					
					Files.write(output, datas.getBytes());
					printLog("End Execution Date : " + new java.util.Date().toString());
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new Exception("Error when writing to model.xml file.");
				}
				
				// fin test writer
				
			} catch (Exception e ) {
				// TODO Auto-generated catch block
				throw e;
			}
			
			printLog("Execution successful");
			
		} catch (Exception e) {
			if(e != null && e.getMessage() != null){
				try {
					printLog(e.getMessage());
					printLog("System.exit(1)");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
			System.exit(1);
		}
		System.exit(0);
	}
	
	public static void recursiveParserQS(Document document, String spath, String locale, Map<String, String> labelMap, Map<String, String> screenTiplMap) throws Exception {
		
		try {
			int i = 1;
			Element qsname = (Element) document.selectSingleNode(spath + "/querySubject[" + i + "]/name");
			
			int j = 1;
			Element qsfname = (Element) document.selectSingleNode(spath + "/folder[" + j + "]/name");
			
			while (qsfname != null)
			{
				if (debug) {
					printLog("Find QuerySubject Directory : " + qsfname.getStringValue());
				}
				
				String nextFPath = spath + "/folder[" + j + "]";
				recursiveParserQS(document, nextFPath, locale, labelMap, screenTiplMap);
				j++;
				qsfname = (Element) document.selectSingleNode(spath + "/folder[" + j + "]/name");
			}
			
			while (qsname != null)
			{
				
				if (debug) {
					printLog("Find QuerySubject : " + qsname.getStringValue());
				}
				
				String nextQIPath = spath + "/querySubject[" + i + "]";
				recursiveParserQI(document, nextQIPath, locale, labelMap, screenTiplMap, qsname.getStringValue(), "", qsname.getStringValue(), "");
				i++;
				qsname = (Element) document.selectSingleNode(spath + "/querySubject[" + i + "]/name");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void recursiveParserQI(Document document, String spath, String locale, Map <String, String> labelMap, Map <String, String> screenTipMap, String qsFinal, String prefix, String inView, String inFolder) throws Exception {
		
		try {
			
			int i = 1;
			Element qiname = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/name");
			Element qiNameLocale = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/name[@locale=\"" + locale + "\"]");
			Element qiScreenTipLocale = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/screenTip[@locale=\"" + locale + "\"]");
			
			int j = 1;
			Element qifname = (Element) document.selectSingleNode(spath + "/queryItemFolder[" + j + "]/name");
			Element qifNameLocale = (Element) document.selectSingleNode(spath + "/queryItemFolder[" + j + "]/name[@locale=\"" + locale + "\"]");
			
			while (qifname != null)
			{
				if (debug) {
					printLog("In view : " + inView + ", Find QueryItem Folder : " + qifname.getStringValue() + " Get locale value : " + qifNameLocale.getStringValue());
				}
				
				prefix = "(" + qifNameLocale.getStringValue() + ") ";
				String nextsPath = spath + "/queryItemFolder[" + j + "]";
				// cas de répertoires ou on a mis le TEC_PARTY
				if (qifname.getStringValue().startsWith(".")) {
					if (debug) {
						printLog( "In view : " + inView + ", startsWith(\".\") : IT'S TEC_PARTY REFERENCE");
					}
					recursiveParserQI(document, nextsPath, locale, labelMap, screenTipMap, "TEC_PARTY", prefix, inView, qifname.getStringValue());
				}
				else {
					if (debug) {
						printLog( "In view : " + inView + ", startsWith(\".\") : NO, IT'S NOT TEC_PARTY REFERENCE");
					}
					recursiveParserQI(document, nextsPath, locale, labelMap, screenTipMap, qsFinal, "", inView, qifname.getStringValue());
				}
				// end cas TEC_PARTY
				prefix = "";
				j++;
				qifname = (Element) document.selectSingleNode(spath + "/queryItemFolder[" + j + "]/name");
				qifNameLocale = (Element) document.selectSingleNode(spath + "/queryItemFolder[" + j + "]/name[@locale=\"" + locale + "\"]");
			}
			
			while (qiname != null)
			{
				String fieldNameTab[] = StringUtils.split(qiname.getStringValue(), ".");
				String fieldName = fieldNameTab[fieldNameTab.length - 1];
				//Label
				String label = prefix + labelMap.get(qsFinal + "." + fieldName);
				String inFolderTxt = "";
				if (!inFolder.equals("")) {
					inFolderTxt = ", In folder : " + inFolder;
				}
	
				if (labelMap.get(qsFinal + "." + fieldName) != null) {
					if (!labelMap.get(qsFinal + "." + fieldName).equals("")) {
						if (debug) {
							printLog("In view : " + inView + inFolderTxt + ", Traduction exists : " + label  + " (" + fieldName + ")");
						}
						qiNameLocale.setText(label  + " (" + fieldName + ")");      // valeur de map
						} else {
							if (debug) {
								printLog("In view : " + inView + inFolderTxt + ", Traduction is empty : " + label  + " (" + fieldName + ")");
								qiNameLocale.setText(label  + " (" + fieldName + ")");
							}
						}
					} else {
						
						printLog( "In view : " + inView + inFolderTxt + ", Traduction doesn't exist : " + prefix + " (" + fieldName + ")");
						qiNameLocale.setText(prefix + " (" + fieldName + ")");
						
					}
				String desc = "";
				if (screenTipMap.get(qsFinal + "." + fieldName) != null) {
					desc = " " + screenTipMap.get(qsFinal + "." + fieldName);
				}
				String screenTip = "(" + qsFinal + "." + fieldName + ")" + desc;
				if (qiScreenTipLocale != null) {
					if (debug) {
						printLog( "In view : " + inView + inFolderTxt + ", " + qsFinal + "." + fieldName + " ScreenTip : " + screenTip);
					}
					qiScreenTipLocale.setText(screenTip);
				}
				i++;
				qiname = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/name");
				qiNameLocale = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/name[@locale=\"" + locale + "\"]");
				qiScreenTipLocale = (Element) document.selectSingleNode(spath + "/queryItem[" + i + "]/screenTip[@locale=\"" + locale + "\"]");
				
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String replaceSpecialChar(String s) {
		s = s.replaceAll("[\\u00c2\\u0092]", "'");
		return s;
	}
	
	public static void printLog(String printLog) throws Exception{
		
		Date date = new java.util.Date();
		String stringDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		
		System.out.println(stringDate + ": " + printLog);
		
		printLog = stringDate + ": " + printLog + "\n";
		try {
			Files.write(logFile, printLog.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception("Error when writing to log file.");
			
		}
	}
}
