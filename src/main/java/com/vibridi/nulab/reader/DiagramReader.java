package com.vibridi.nulab.reader;

import java.io.IOException;
import java.io.InputStream;

public interface DiagramReader {

	public void setApiKey(String apiKey);
	public String getApiKey();
	public String fetchDiagram(String diagramId) throws IOException;
	public void read(InputStream in);
	public int outputSize();
	public String getOutput(int index);
	public String getOutputName(int index);
	
}
