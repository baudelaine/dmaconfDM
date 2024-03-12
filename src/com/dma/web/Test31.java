package com.dma.web;

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

public class Test31 {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		Path path = Paths.get("/home/fr054721/Downloads/bdxmet/model.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(path.toFile());
		
		XPathFactory xfact = XPathFactory.newInstance();
		XPath xpath = xfact.newXPath();
		
		NodeList nodeList = null;
		
		
		nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace/querySubject", document, XPathConstants.NODESET);
		if(nodeList.getLength() == 0) {
			nodeList = (NodeList) xpath.evaluate("/project/namespace/querySubject", document, XPathConstants.NODESET);
		}
		
		for(int index = 0; index < nodeList.getLength(); index++){
			Node qss = nodeList.item(index);
			String table_name = getTextContent(qss, "name");
			System.out.println(table_name);
			
			NodeList qssChildNodes = qss.getChildNodes();
			for(int i = 0; i < qssChildNodes.getLength(); i++){
				Node qssChildNode = qssChildNodes.item(i);
				if(qssChildNode.getNodeName() == "definition") {
					NodeList defNodes = qssChildNode.getChildNodes();
					for(int j = 0; j < defNodes.getLength(); j++){
						Node defNode = defNodes.item(j);
						if(defNode.getNodeName() == "dbQuery") {
							String table_type = getTextContent(defNode, "tableType");
							System.out.println(table_type);
						}
					}
				}
			}
		}
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
