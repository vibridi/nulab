package com.vibridi.nulab.reader.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import com.vibridi.nulab.reader.NulabDiagramReader;

public class ERDiagramReader extends NulabDiagramReader {	
	
	private List<Entry<String,String>> output;
	
	public ERDiagramReader() {
		super();
	}

	@Override
	public void read(InputStream in) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int outputSize() {
		return output.size();
	}

	@Override
	public String getOutput(int index) {
		return output.get(index).getValue();
	}

	@Override
	public String getOutputName(int index) {
		return output.get(index).getKey();
	}
	
	
	
	
	

}
