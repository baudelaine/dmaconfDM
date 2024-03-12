package com.dma.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;

public class Test22 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Path json = Paths.get("/home/dma/dma/projects.json");
		Path output = Paths.get("/tmp/projects.json");
		
		
		
		if(Files.exists(json)) {
			
			Map<String, Project> projects = (Map<String, Project>) Tools.fromJSON(json.toFile(), new TypeReference<Map<String, Project>>(){});
			
			System.out.println(Tools.toJSON(projects));
			
			for(Entry<String, Project> project: projects.entrySet()) {
				String strTs = project.getValue().getTimestamp();
				strTs = strTs.replaceAll("-", "");
				project.getValue().setTs(Long.parseLong(strTs));
				
			}
			
			System.out.println(Tools.toJSON(projects));
			
			List<Map.Entry<String, Project>> entryList = new ArrayList<Map.Entry<String, Project>>(projects.entrySet());

            Collections.sort(
                    entryList, new Comparator<Map.Entry<String, Project>>() {
                @Override
                public int compare(Map.Entry<String, Project> integerEmployeeEntry2,
                                   Map.Entry<String, Project> integerEmployeeEntry) {
                    return integerEmployeeEntry.getValue().getTs()
                            .compareTo(integerEmployeeEntry2.getValue().getTs());
                }
            }
        );

        System.out.println(Tools.toJSON(entryList));			
		}		

		
		
		File dir = new File("/home/dma/dma/");
		
		File[] fs = dir.listFiles(File::isDirectory);
		
		
		Arrays.sort(fs, new Comparator<File>() {
		    public int compare(File f1, File f2) {
		        return Long.compare(f2.lastModified(), f1.lastModified());
		    }
		});			
		
		for(File file: fs) {
			System.out.println(file.getName());
		}
		
		System.out.println(Tools.toJSON(fs));
		
	}

}
