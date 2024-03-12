package com.dma.web;

import java.util.HashMap;
import java.util.Map;

public class DBMDTable {

	String table_name = "";
	int table_primaryKeyFieldsCount = 0;
	int table_importedKeysSeqCount = 0;
	String table_description = "";
	int table_exportedKeysCount = 0;
	String table_stats = "";
	String table_remarks = "";
	int table_exportedKeysSeqCount = 0;
	int table_indexesCount = 0;
	String table_schema = "";
	int table_importedKeysCount = 0;
	String table_type = "";
	long table_recCount = 0L;
	Map<String, DBMDColumn> columns = new HashMap<String, DBMDColumn>();
	int table_fieldCount = 0;
	
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public int getTable_primaryKeyFieldsCount() {
		return table_primaryKeyFieldsCount;
	}
	public void setTable_primaryKeyFieldsCount(int table_primaryKeyFieldsCount) {
		this.table_primaryKeyFieldsCount = table_primaryKeyFieldsCount;
	}
	public int getTable_importedKeysSeqCount() {
		return table_importedKeysSeqCount;
	}
	public void setTable_importedKeysSeqCount(int table_importedKeysSeqCount) {
		this.table_importedKeysSeqCount = table_importedKeysSeqCount;
	}
	public String getTable_description() {
		return table_description;
	}
	public void setTable_description(String table_description) {
		this.table_description = table_description;
	}
	public int getTable_exportedKeysCount() {
		return table_exportedKeysCount;
	}
	public void setTable_exportedKeysCount(int table_exportedKeysCount) {
		this.table_exportedKeysCount = table_exportedKeysCount;
	}
	public String getTable_stats() {
		return table_stats;
	}
	public void setTable_stats(String table_stats) {
		this.table_stats = table_stats;
	}
	public String getTable_remarks() {
		return table_remarks;
	}
	public void setTable_remarks(String table_remarks) {
		this.table_remarks = table_remarks;
	}
	public int getTable_exportedKeysSeqCount() {
		return table_exportedKeysSeqCount;
	}
	public void setTable_exportedKeysSeqCount(int table_exportedKeysSeqCount) {
		this.table_exportedKeysSeqCount = table_exportedKeysSeqCount;
	}
	public int getTable_indexesCount() {
		return table_indexesCount;
	}
	public void setTable_indexesCount(int table_indexesCount) {
		this.table_indexesCount = table_indexesCount;
	}
	public String getTable_schema() {
		return table_schema;
	}
	public void setTable_schema(String table_schema) {
		this.table_schema = table_schema;
	}
	public int getTable_importedKeysCount() {
		return table_importedKeysCount;
	}
	public void setTable_importedKeysCount(int table_importedKeysCount) {
		this.table_importedKeysCount = table_importedKeysCount;
	}
	public String getTable_type() {
		return table_type;
	}
	public void setTable_type(String table_type) {
		this.table_type = table_type;
	}
	public long getTable_recCount() {
		return table_recCount;
	}
	public void setTable_recCount(long table_recCount) {
		this.table_recCount = table_recCount;
	}
	public Map<String, DBMDColumn> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, DBMDColumn> columns) {
		this.columns = columns;
	}
	public int getTable_fieldCount() {
		return table_fieldCount;
	}
	public void setTable_fieldCount(int table_fieldCount) {
		this.table_fieldCount = table_fieldCount;
	}
	
}
