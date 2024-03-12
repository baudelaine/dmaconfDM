package com.dma.web;

import java.util.ArrayList;
import java.util.List;

public class Test27 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String var = "($Tables)";
		String query = "SELECT table AS Table_Name, label AS Table_Label FROM tablelabels where table IN " + var.toUpperCase() + " and lang = 'en'";
		List<String> fullList = new ArrayList<String>();
		fullList.add("PROJECT");
		fullList.add("DEPARTMENT");
		fullList.add("EMPLOYEE");
		int limit = 1000;
		
		System.out.println(Tools.splitInClause(query, fullList, limit));
		

	}

}
