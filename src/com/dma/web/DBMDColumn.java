package com.dma.web;

public class DBMDColumn {
	
	String column_name = "";
	String column_description = "";
	String column_type = "";
	String column_remarks = "";
	String column_isNullable = "";
	boolean column_isIndexed = false;
	int column_size = 0;
	boolean filtered = false;
	boolean column_isFiltered = false;
	boolean column_isPrimaryKey = false;
	
	public String getColumn_name() {
		return column_name;
	}
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	public String getColumn_description() {
		return column_description;
	}
	public void setColumn_description(String column_description) {
		this.column_description = column_description;
	}
	public String getColumn_type() {
		return column_type;
	}
	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}
	public String getColumn_remarks() {
		return column_remarks;
	}
	public void setColumn_remarks(String column_remarks) {
		this.column_remarks = column_remarks;
	}
	public String getColumn_isNullable() {
		return column_isNullable;
	}
	public void setColumn_isNullable(String column_isNullable) {
		this.column_isNullable = column_isNullable;
	}
	public boolean isColumn_isIndexed() {
		return column_isIndexed;
	}
	public void setColumn_isIndexed(boolean column_isIndexed) {
		this.column_isIndexed = column_isIndexed;
	}
	public int getColumn_size() {
		return column_size;
	}
	public void setColumn_size(int column_size) {
		this.column_size = column_size;
	}
	public boolean isFiltered() {
		return filtered;
	}
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}
	public boolean isColumn_isFiltered() {
		return column_isFiltered;
	}
	public void setColumn_isFiltered(boolean column_isFiltered) {
		this.column_isFiltered = column_isFiltered;
	}
	public boolean isColumn_isPrimaryKey() {
		return column_isPrimaryKey;
	}
	public void setColumn_isPrimaryKey(boolean column_isPrimaryKey) {
		this.column_isPrimaryKey = column_isPrimaryKey;
	}
	
}
