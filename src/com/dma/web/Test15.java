package com.dma.web;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Test15 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/fr054721/Documents/antibia/parsingxml/model.xml");

		SAXReader reader = new SAXReader();
		Document document = reader.read(path.toFile());

		String namespaceName = "Model";
		String spath = "/project/namespace/namespace";
		int k=1;
		
		Element namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
		while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
		{
		k++;
		namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
		}
		
		spath = spath + "[" + k + "]";
		
		System.out.println("Find namespace : " + namespace.getStringValue());
		
		recursiveParserQS(document, spath, "en-zw");		
		
	}
	
	public static void recursiveParserQS(Document document, String spath, String locale) throws Exception {
		
		try {
			int i = 1;
			Element qsname = (Element) document.selectSingleNode(spath + "/querySubject[" + i + "]/name");

			System.out.println(qsname.getStringValue());
			
			int j = 1;
			Element qsfname = (Element) document.selectSingleNode(spath + "/folder[" + j + "]/name");
			
			System.out.println(qsfname.getStringValue());
			
			while (qsfname != null)
			{
				System.out.println("Find QuerySubject Directory : " + qsfname.getStringValue());
				
				String nextFPath = spath + "/folder[" + j + "]";
				recursiveParserQS(document, nextFPath, locale);
				j++;
				qsfname = (Element) document.selectSingleNode(spath + "/folder[" + j + "]/name");
			}
			
			while (qsname != null)
			{
				
				System.out.println("Find QuerySubject : " + qsname.getStringValue());
				
				String nextQIPath = spath + "/querySubject[" + i + "]";
//				recursiveParserQI(document, nextQIPath, locale);
				i++;
				qsname = (Element) document.selectSingleNode(spath + "/querySubject[" + i + "]/name");
			}
		} catch (Exception e) {
			throw e;
		}
	}	
	
	

}
