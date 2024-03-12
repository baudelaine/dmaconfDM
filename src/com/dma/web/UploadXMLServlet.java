package com.dma.web;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "UploadXML", urlPatterns = { "/UploadXML" })
public class UploadXMLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadXMLServlet() {
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

			
			if(ServletFileUpload.isMultipartContent(request)){
				
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// item is the file (and not a field)
						Path xml = Paths.get(prj + "/" + item.getName());
						Files.copy(new BufferedInputStream(item.getInputStream()), xml, StandardCopyOption.REPLACE_EXISTING);
						xml.toFile().setReadable(true, false);
						xml.toFile().setWritable(true, false);
					}
				}
			}

			Path path = Paths.get(prj + "/model.xml");
			
			if(!Files.exists(path)) {
				result.put("STATUS", "KO");
				throw new Exception("model.xml not found.");
			}
			else {
				try {
					
					
					@SuppressWarnings("deprecation")
					String xml = IOUtils.toString(new FileInputStream(path.toFile()));
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(xml)));
					
					XPathFactory xfact = XPathFactory.newInstance();
					XPath xpath = xfact.newXPath();
					
//					NodeList nodeList = null;
					
					NodeList nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace[1]/querySubject", document, XPathConstants.NODESET);
//					if(nodeList.getLength() == 0) {
//						nodeList = (NodeList) xpath.evaluate("/project/namespace/querySubject", document, XPathConstants.NODESET);
//					}	
	
					Map<String, QuerySubject> querySubjects = new HashMap<String, QuerySubject>(); 
					
					for(int index = 0; index < nodeList.getLength(); index++){
						Node qss = nodeList.item(index);
						String table_name = getTextContent(qss, "name");
//						System.out.println(table_name);
						QuerySubject querySubject = new QuerySubject();
						querySubject.setTable_name(table_name);
						
						// Get tableType in definition/dbQuery
						NodeList qssChildNodes = qss.getChildNodes();
						for(int i = 0; i < qssChildNodes.getLength(); i++){
							Node qssChildNode = qssChildNodes.item(i);
							if(qssChildNode.getNodeName() == "definition") {
								NodeList defNodes = qssChildNode.getChildNodes();
								for(int j = 0; j < defNodes.getLength(); j++){
									Node defNode = defNodes.item(j);
									if(defNode.getNodeName() == "dbQuery") {
										String table_type = getTextContent(defNode, "tableType");
//										System.out.println(table_type);
										querySubject.setType(table_type.toUpperCase());
									}
								}
							}
						}
						
						
						
						
						NodeList qs = qss.getChildNodes();
						List<Field> fields = new ArrayList<Field>();
						int fieldPos = 1;
						for(int i = 0; i < qs.getLength(); i++){
							if(qs.item(i).getNodeName().equalsIgnoreCase("queryItem")) {
								Node qis = qs.item(i);
								NodeList qi = qis.getChildNodes();
								Field field = new Field();
								field.setFieldPos(fieldPos++);
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
					request.getSession().setAttribute("QSFromXML", querySubjects);
	
					Map<String, String> tables = new HashMap<String, String>();
					for(Entry<String, QuerySubject> qs: querySubjects.entrySet()) {
						tables.put(qs.getValue().getTable_name(), qs.getValue().getType());
					}
				    result.put("TABLES", tables);
				    result.put("QSFromXML", querySubjects);
					
//					result.put("TABLES", querySubjects.keySet());

					Path relationsPath = Paths.get(prj + "/relation.csv");
					
					if(! Files.exists(relationsPath)) {
				    
						nodeList = (NodeList) xpath.evaluate("/project/namespace/namespace/relationship", document, XPathConstants.NODESET);
						
						List<String> relations = new ArrayList<String>();
						
						for(int index = 0; index < nodeList.getLength(); index++){
							Node qss = nodeList.item(index);
							NodeList qs = qss.getChildNodes();
							for(int i = 0; i < qs.getLength(); i++){
								if(qs.item(i).getNodeName().equalsIgnoreCase("expression")) {
									if(qs.item(i).getTextContent().contains("[PHYSICAL]")) {
										String exp = "";
										exp = qs.item(i).getTextContent();
										exp = exp.replace("[PHYSICAL].", "");
										exp = exp.replaceAll("[\\[\\]]", "");
										relations.addAll(getCSV(exp));
										
									}
								}
							}
							
						}
		
						List<String> header = Arrays.asList("FK_NAME;PK_NAME;FKTABLE_NAME;PKTABLE_NAME;KEY_SEQ;FKCOLUMN_NAME;PKCOLUMN_NAME");
						Files.write(relationsPath, header);
						Files.write(relationsPath, relations, StandardOpenOption.APPEND);
						relationsPath.toFile().setReadable(true, false);
					}
					result.put("MESSAGE", "model.xml uploaded successfully.");
					result.put("STATUS", "OK");
					
				}
				catch (Exception e) {
					throw new Exception("Error when parsing XML file.");
				}
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
	
	protected List<String> getCSV(String exp){
		
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
	
	protected String writeCSVRelation(String exp, String KEY_SEQ) {
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
						";" + KEY_SEQ + ";" + FKCOLUMN_NAME + ";" + PKCOLUMN_NAME;
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	protected String getAttrValue(Node node,String attrName) {
	    if ( ! node.hasAttributes() ) return "";
	    NamedNodeMap nmap = node.getAttributes();
	    if ( nmap == null ) return "";
	    Node n = nmap.getNamedItem(attrName);
	    if ( n == null ) return "";
	    return n.getNodeValue();
	}
	
	@SuppressWarnings("unused")
	protected String getTextContent(Node parentNode,String childName) {
	    NodeList nlist = parentNode.getChildNodes();
	    for (int i = 0 ; i < nlist.getLength() ; i++) {
		    Node n = nlist.item(i);
		    String name = n.getNodeName();
		    if ( name != null && name.equals(childName) ) return n.getTextContent();
	    }
	    return "";
	}		
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}