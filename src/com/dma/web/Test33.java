package com.dma.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test33 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Path xmlFile = Paths.get("/home/fr054721/Documents/antibia/parsingxml/model.xml");
			
		if(Files.exists(xmlFile)) {
			
			@SuppressWarnings("deprecation")
			String xml = IOUtils.toString(new FileInputStream(xmlFile.toFile()));
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));
			
			XPathFactory xfact = XPathFactory.newInstance();
			XPath xpath = xfact.newXPath();
			
//			NodeList nodeList = null;
			
			NodeList nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace[1]/querySubject", document, XPathConstants.NODESET);
//			if(nodeList.getLength() == 0) {
//				nodeList = (NodeList) xpath.evaluate("/project/namespace/querySubject", document, XPathConstants.NODESET);
//			}	

			Map<String, QuerySubject> querySubjects = new HashMap<String, QuerySubject>(); 
			
			for(int index = 0; index < nodeList.getLength(); index++){
				Node qss = nodeList.item(index);
				String table_name = getTextContent(qss, "name");
				System.out.println(table_name);
				QuerySubject querySubject = new QuerySubject();
				querySubject.setTable_name(table_name);
			}
			
		}
	}
	
	@SuppressWarnings("unused")
	protected static String getTextContent(Node parentNode,String childName) {
	    NodeList nlist = parentNode.getChildNodes();
	    for (int i = 0 ; i < nlist.getLength() ; i++) {
		    Node n = nlist.item(i);
		    String name = n.getNodeName();
		    if ( name != null && name.equals(childName) ) return n.getTextContent();
	    }
	    return "";
	}		
	

}
