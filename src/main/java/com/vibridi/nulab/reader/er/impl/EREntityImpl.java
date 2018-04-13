package com.vibridi.nulab.reader.er.impl;

import java.util.ArrayList;
import java.util.List;

import com.vibridi.nulab.reader.er.EREntity;

public class EREntityImpl implements EREntity {

	private String uid;
	private String name;
	private String pk;
	private List<String> fields;
	
	public EREntityImpl(String uid, String name, String primaryKey, List<String> fields) {
		this.uid = uid;
		this.name = name;
		this.pk = primaryKey;
		this.fields = new ArrayList<>(fields);
	}
	
	@Override
	public String toString() {
		return String.format("[%s, PK %s, %s]", name, pk, fields.toString());
	}
	
	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPrimaryKey() {
		return pk;
	}

	@Override
	public List<String> getFields() {
		// Defensive copy. We want to enforce immutability.
		return new ArrayList<>(fields);
	}

	@Override
	public String getField(int index) {
		return fields.get(index);
	}

}
