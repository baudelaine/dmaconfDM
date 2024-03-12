package com.dma.web;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XML2CSV {

	static List<String> lines = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String delim = ";";
		String header = "QUERY_SUBJECT" + delim + "FIELD" + delim + "FOLDER" + delim + "ROLE";
		lines.add(header);
		
		Document document = null;
		XPath xpath = null;
		Path modelPath = Paths.get("/home/fr054721/dmagui/ca/model.xml");
		Path csvPath = Paths.get("/home/fr054721/dmagui/ca/model.csv");
		String nsName = "SHARED_ZONE";
		String nsXPath = "/project/namespace/namespace[name='" + nsName + "']";
		String localeXPath = "name[@locale='en-zw']";
		String qssXPath = nsXPath + "/querySubject/" + localeXPath;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(modelPath.toFile());
		
		XPathFactory xfact = XPathFactory.newInstance();
		xpath = xfact.newXPath();

		NodeList qss = (NodeList) xpath.evaluate(qssXPath, document, XPathConstants.NODESET);

		System.out.println(qss.getLength() + " Query Subjects found...");		
		
		for(int i = 0; i < qss.getLength(); i++) {
			String qsName = qss.item(i).getTextContent(); 
			System.out.println("qsName=" + qsName);
			String xpathExp = nsXPath + "/querySubject[name='" + qsName + "']";	
			Node qsNode = (Node) xpath.evaluate(xpathExp, document, XPathConstants.NODE);
			NodeList childNodes = qsNode.getChildNodes();

			recurse(qsName, childNodes, "");
		}
		
		Files.deleteIfExists(csvPath);
		
		Files.write(csvPath, lines, StandardCharsets.UTF_8);		
		
		System.exit(0);
		
	}
	
	static void recurse(String qsName, NodeList nl, String folder) {
		
		for(int i = 0; i < nl.getLength(); i++ ){

			String line = "";
			
			if(nl.item(i).getNodeName().equalsIgnoreCase("queryItemFolder")) {
				String qifName =  getTextContent(nl.item(i), "name");
				if(! qifName.startsWith(".")) {
					NodeList subNl = nl.item(i).getChildNodes();
					recurse(qsName, subNl, qifName);
					line = qsName + ";" + qifName + ";" + folder + ";Folder";
					System.out.println(line);
					lines.add(line);
				}
				else {
					line = qsName + ";" + qifName + ";" + folder + ";FolderRefView";
					System.out.println(line);
					lines.add(line);
				}
			}
			
			if(nl.item(i).getNodeName().equalsIgnoreCase("queryItem")) {
				line = qsName +";" + getTextContent(nl.item(i), "name") + ";" + folder + ";Field";
				System.out.println(line);
				lines.add(line);
			}
			
		}
		
	}
	
	protected static String getAttrValue(Node node,String attrName) {
	    if ( ! node.hasAttributes() ) return "";
	    NamedNodeMap nmap = node.getAttributes();
	    if ( nmap == null ) return "";
	    Node n = nmap.getNamedItem(attrName);
	    if ( n == null ) return "";
	    return n.getNodeValue();
	}
	
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

