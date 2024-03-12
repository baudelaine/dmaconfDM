package com.dma.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test18 {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Path path = Paths.get("/home/dma/dma/cda0/models/mdl1.json");
		Path output = Paths.get("/home/dma/dma/cda0/models/mdl1-migrated.json");
		
		try {
//			Map<String, List<QuerySubject>> qssAndViews = (Map<String, List<QuerySubject>>) Tools.fromJSON(path.toFile(), new TypeReference<Map<String, List<QuerySubject>>>(){});
//			List<QuerySubject> qss = qssAndViews.get("querySubjects");

			List<QuerySubject> qss = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			
			for(QuerySubject qs: qss) {
				
				Map<String, Filter> filters = qs.getFilters();
				
				String filter = qs.getFilter();
				filter.replaceAll("\\n|\\r", "");
				if(filter.length() > 0) {
					System.out.println(filter);
					if(filter.startsWith("[FINAL]") && !filter.contains(";")) {
						Filter flt = new Filter();
						flt.setName("flt0");
						flt.setTarget(filter.substring(0, filter.lastIndexOf("]") + 1));
						flt.setOption("Mandatory");
						flt.setExpression(filter);
						filters.put(flt.getName(), flt);
						System.out.println(Tools.toJSON(flt));
					}
					if(filter.startsWith("[REF]") && !filter.contains(";")) {
						Filter flt = new Filter();
						flt.setName("flt0");
						if(filter.indexOf(":") > -1) {
							flt.setTarget(filter.substring(0, filter.indexOf(":")));
						}
						flt.setOption("Mandatory");
						flt.setExpression(filter.substring(filter.indexOf(":") + 1));
						filters.put(flt.getName(), flt);
						System.out.println(Tools.toJSON(flt));
					}
					if(filter.startsWith("*") && !filter.contains(";")) {
						Filter flt = new Filter();
						flt.setName("flt0");
						if(filter.indexOf(":") > -1) {
							flt.setTarget(filter.substring(0, filter.indexOf(":")));
						}
						flt.setOption("Mandatory");
						flt.setExpression(filter.substring(filter.indexOf(":") + 1));
						filters.put(flt.getName(), flt);
						System.out.println(Tools.toJSON(flt));
					}
					if(filter.startsWith("[FINAL]") && filter.contains(";")) {
						List<String> filterList = Arrays.asList(filter.split(";"));
						int i = 0;
						for(String fltStr: filterList) {
							Filter flt = new Filter();
							flt.setName("flt" + i++);
							flt.setTarget(fltStr.substring(0, fltStr.lastIndexOf("]") + 1));
							flt.setOption("Mandatory");
							flt.setExpression(fltStr);
							filters.put(flt.getName(), flt);
							System.out.println(Tools.toJSON(flt));
						}
					}
					if(filter.startsWith("[REF]") && filter.contains(";")) {
						List<String> filterList = Arrays.asList(filter.split(";"));
						int i = 0;
						for(String fltStr: filterList) {
							Filter flt = new Filter();
							flt.setName("flt" + i++);
							flt.setTarget(fltStr.substring(0, fltStr.indexOf(":")));
							flt.setOption("Mandatory");
							flt.setExpression(fltStr.substring(fltStr.indexOf(":") + 1));
							filters.put(flt.getName(), flt);
							System.out.println(Tools.toJSON(flt));
						}
					}
				}
			
			}
			
			Files.write(output, Tools.toJSON(qss).getBytes());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
