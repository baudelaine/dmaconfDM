package com.dma.web;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
 
public class Test14 
{
    public static void main(String[] args) throws Exception 
    {
        //Get DOM Node for XML
//        String fileName= "/home/fr054721/dmagui/ca/employees.xml";
		Path path = Paths.get("/home/fr054721/Documents/antibia/parsingxml/model.xml");
        Document document = getDocument(path);
         
        String xpathExpression = "";
             
        /*******Get attribute values using xpath******/


        //Get all employee ids
        xpathExpression = "/project/namespace/namespace/querySubject";
        System.out.println( evaluateXPath(document, xpathExpression) );
        
//        //Get all employee ids
//        xpathExpression = "/employees/employee/@id";
//        System.out.println( evaluateXPath(document, xpathExpression) );
//         
//        //Get all employee ids in HR department
//        xpathExpression = "/employees/employee[department/name='HR']/@id";
//        System.out.println( evaluateXPath(document, xpathExpression) );
//         
//        //Get employee id of 'Alex'
//        xpathExpression = "/employees/employee[firstName='Alex']/@id";
//        System.out.println( evaluateXPath(document, xpathExpression) );
//         
//        //Get employee ids greater than 5
//        xpathExpression = "/employees/employee/@id[. > 5]";
//        System.out.println( evaluateXPath(document, xpathExpression) );
//         
//        //Get employee whose id contains 1
//        xpathExpression = "/employees/employee[contains(@id,'1')]/firstName/text()";
//        System.out.println( evaluateXPath(document, xpathExpression) );
//         
//        //Get employee whose id contains 1
//        xpathExpression = "descendant-or-self::*[contains(@id,'1')]/firstName/text()";
//        System.out.println( evaluateXPath(document, xpathExpression) );
    }
     
    private static List<String> evaluateXPath(Document document, String xpathExpression) throws Exception 
    {
        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();
         
        // Create XPath object
        XPath xpath = xpathFactory.newXPath();
 
        List<String> values = new ArrayList<>();
        try
        {
            // Create XPathExpression object
            XPathExpression expr = xpath.compile(xpathExpression);
             
            // Evaluate expression result on XML document
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
             
            for (int i = 0; i < nodes.getLength(); i++) {
                values.add(nodes.item(i).getNodeValue());
            }
                 
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return values;
    }
 
    private static Document getDocument(Path path) throws Exception 
    {
    	String xml = new String(Files.readAllBytes(path));
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		return document;
    }
}