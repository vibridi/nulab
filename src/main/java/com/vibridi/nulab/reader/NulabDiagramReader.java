package com.vibridi.nulab.reader;

import java.io.IOException;
import java.util.Objects;

import com.vibridi.nulab.AppConstants;
import com.vibridi.nulab.http.HTTP;

public abstract class NulabDiagramReader implements DiagramReader {

	private String apiKey;
	
	public NulabDiagramReader() {
		apiKey = "";
	}
	
	@Override
	public String fetchDiagram(String diagramId) throws IOException {
		return HTTP.build(AppConstants.ROOT_PATH)
					.appendPath("api/v1/diagrams")
					.appendPath(diagramId)
					.appendPath("contents.xml")
					.setQueryParam("returnValues", "uid,shapeStyle")
					.setQueryParam("apiKey", getApiKey())
					.get();		
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}
	
	@Override
	public void setApiKey(String apiKey) {
		Objects.requireNonNull(apiKey);
		this.apiKey = apiKey;
	}
	
	
}
