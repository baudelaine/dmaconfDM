package com.dma.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dimension {

	String name = "";
	List<Map<String, Object>> orders = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> bks = new ArrayList<Map<String, Object>>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Map<String, Object>> getOrders() {
		return orders;
	}
	public void setOrders(List<Map<String, Object>> orders) {
		this.orders = orders;
	}
	public List<Map<String, Object>> getBks() {
		return bks;
	}
	public void setBks(List<Map<String, Object>> bks) {
		this.bks = bks;
	}
	
}
