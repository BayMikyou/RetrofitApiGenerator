package com.mikyou.retrofit.api.generator.json;

import java.util.List;

public class InnerClassEntity {
	private String packName;
	private String className;
	private List<String> fields;

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}