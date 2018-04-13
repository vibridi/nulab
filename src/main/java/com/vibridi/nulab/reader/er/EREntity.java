package com.vibridi.nulab.reader.er;

import java.util.List;

import com.vibridi.nulab.reader.model.DiagramEntity;

public interface EREntity extends DiagramEntity {
	public String getPrimaryKey();
	public List<String> getFields();
	public String getField(int index);
}
