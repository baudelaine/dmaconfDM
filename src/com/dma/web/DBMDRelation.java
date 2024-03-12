package com.dma.web;

import java.util.HashMap;
import java.util.Map;

public class DBMDRelation {
	
	String keyName = "";
	Map<Short, Seq> keySeqs = new HashMap<Short, Seq>();
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public Map<Short, Seq> getKeySeqs() {
		return keySeqs;
	}
	public void setKeySeqs(Map<Short, Seq> keySeqs) {
		this.keySeqs = keySeqs;
	}

}
