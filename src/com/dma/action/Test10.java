package com.dma.action;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Tools;

public class Test10 {

	public static void main(String[] args) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/dmagui/model.xml");
		
		if(Files.exists(path)) {
			
			@SuppressWarnings("deprecation")
			String xml = IOUtils.toString(new FileInputStream(path.toFile()));
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));
			
			XPathFactory xfact = XPathFactory.newInstance();
			XPath xpath = xfact.newXPath();
			
			NodeList nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace/querySubject", document, XPathConstants.NODESET);
			
			Map<String, QuerySubject> querySubjects = new HashMap<String, QuerySubject>(); 
			
			for(int index = 0; index < nodeList.getLength(); index++){
				Node qss = nodeList.item(index);
				String table_name = getTextContent(qss, "name");
				System.out.println(table_name);
				QuerySubject querySubject = new QuerySubject();
				querySubject.setTable_name(table_name);
				NodeList qs = qss.getChildNodes();
				List<Field> fields = new ArrayList<Field>();
				for(int i = 0; i < qs.getLength(); i++){
					if(qs.item(i).getNodeName().equalsIgnoreCase("queryItem")) {
						Node qis = qs.item(i);
						NodeList qi = qis.getChildNodes();
						Field field = new Field();
						for(int j = 0; j < qi.getLength(); j++) {
							System.out.println("\t" + qi.item(j).getNodeName() + "\t" + qi.item(j).getTextContent());
							switch(qi.item(j).getNodeName()) {
								case "name":
									field.setField_name(qi.item(j).getTextContent());
									break;
								case "datatype":
									field.setField_type(qi.item(j).getTextContent());
									break;
							}
						}
						fields.add(field);
					}
					querySubject.setFields(fields);
				}
				querySubjects.put(table_name, querySubject);
			}
			
			System.out.println(querySubjects.keySet().toArray().length);
			
			Path qssPath = Paths.get("/tmp/qss.json");
			Files.write(qssPath, Tools.toJSON(querySubjects).getBytes());
			qssPath.toFile().setReadable(true, false);

			
			nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace/relationship", document, XPathConstants.NODESET);
			
			List<String> relations = new ArrayList<String>();
			
			for(int index = 0; index < nodeList.getLength(); index++){
				Node qss = nodeList.item(index);
				NodeList qs = qss.getChildNodes();
				for(int i = 0; i < qs.getLength(); i++){
					if(qs.item(i).getNodeName().equalsIgnoreCase("expression")) {
//						if(qs.item(i).getTextContent().contains("[PHYSICAL]")) {
							String exp = "";
							exp = qs.item(i).getTextContent();
							exp = exp.replace("[PHYSICAL].", "");
							exp = exp.replaceAll("[\\[\\]]", "");
							relations.addAll(getCSV(exp));
							
//						}
					}
				}
				
			}

			Path relationsPath = Paths.get("/tmp/relation.csv");
			List<String> header = Arrays.asList("FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME");
			Files.write(relationsPath, header);
			Files.write(relationsPath, relations, StandardOpenOption.APPEND);
			relationsPath.toFile().setReadable(true, false);
			
			
		}
		
		
	}
	
	static List<String> getCSV(String exp){
		
		List<String> result = new ArrayList<String>();

		int KEY_SEQ_NUM = StringUtils.split(exp, "and").length;
		
		if(KEY_SEQ_NUM == 0) {
			String relation = writeCSVRelation(exp, "1");
			result.add(relation);
		}
		else {
			for(int i = 0; i < KEY_SEQ_NUM; i++) {
				String relation = writeCSVRelation(StringUtils.split(exp, "and")[i], String.valueOf(i + 1));
				result.add(relation);
			}
		}
		
		return result;
		
	}
	
	static String writeCSVRelation(String exp, String KEY_SEQ) {
		String result = "";
		
		String FK_NAME = "";
		String PK_NAME = "";
		String FKTABLE_NAME = "";
		String PKTABLE_NAME = "";
		String FKCOLUMN_NAME = "";
		String PKCOLUMN_NAME = "";
		if(exp.split("=").length == 2) {
			String left = exp.split("=")[1];
			String right = exp.split("=")[0];
			if(left.split("\\.").length == 2 && right.split("\\.").length == 2) {
				FK_NAME = "FK" + left.split("\\.")[0] + "_" + right.split("\\.")[0];
				PK_NAME = "PK" + right.split("\\.")[0];
				FKTABLE_NAME = left.split("\\.")[0];
				PKTABLE_NAME = right.split("\\.")[0];
				FKCOLUMN_NAME = left.split("\\.")[1];
				PKCOLUMN_NAME = right.split("\\.")[1];
				result = FK_NAME + ";" + PK_NAME + ";" + FKTABLE_NAME + ";" + PKTABLE_NAME + 
						";" + Short.valueOf(KEY_SEQ) + ";" + FKCOLUMN_NAME + ";" + PKCOLUMN_NAME;
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	static private String getAttrValue(Node node,String attrName) {
	    if ( ! node.hasAttributes() ) return "";
	    NamedNodeMap nmap = node.getAttributes();
	    if ( nmap == null ) return "";
	    Node n = nmap.getNamedItem(attrName);
	    if ( n == null ) return "";
	    return n.getNodeValue();
	}
	
	@SuppressWarnings("unused")
	static private String getTextContent(Node parentNode,String childName) {
	    NodeList nlist = parentNode.getChildNodes();
	    for (int i = 0 ; i < nlist.getLength() ; i++) {
		    Node n = nlist.item(i);
		    String name = n.getNodeName();
		    if ( name != null && name.equals(childName) ) return n.getTextContent();
	    }
	    return "";
	}		

}