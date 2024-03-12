package com.dma.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test6 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Path prj = Paths.get("/home/dma/dma/p0");
		List<Path> csvs = new ArrayList<Path>();
		csvs.add(Paths.get(prj + "/tableLabel.csv"));
		csvs.add(Paths.get(prj + "/tableDescription.csv"));
		csvs.add(Paths.get(prj + "/columnLabel.csv"));
		csvs.add(Paths.get(prj + "/columnDescription.csv"));
		
		Map<String, List<Map<String, Object>>> datas = (Map<String, List<Map<String, Object>>>) new HashMap<String, List<Map<String, Object>>>();
		
		for(Path csv: csvs) {
			if(Files.exists(csv)) {
				LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(csv.toFile())));
				try{
					String key = csv.getFileName().toString().split("\\.")[0];
					if(!datas.containsKey(key)) {
						datas.put(key, new ArrayList<Map<String,Object>>());
					}
					for (String line; ((line = reader.readLine()) != null) && reader.getLineNumber() <= 2; ) {
						Map<String, Object> record = new HashMap<String, Object>();
						switch(key) {
							case "tableLabel":
								record.put("tableName", line.split(";")[0]);
								record.put("tableLabel", line.split(";")[1]);
								break;
							case "tableDescription":
								record.put("tableName", line.split(";")[0]);
								record.put("tableDescription", line.split(";")[1]);
								break;
							case "columnLabel":
								record.put("tableName", line.split(";")[0]);
								record.put("columnName", line.split(";")[1]);
								record.put("columnLabel", line.split(";")[2]);
								break;
							case "columnDescription":
								record.put("tableName", line.split(";")[0]);
								record.put("columnName", line.split(";")[1]);
								record.put("columnDescription", line.split(";")[2]);
								break;
							default:
						}
						datas.get(key).add(record);
					}
				}
				finally{
					reader.close();
				}			
			}
		}
		
		System.out.println(Tools.toJSON(datas));
		
		
	}

}
