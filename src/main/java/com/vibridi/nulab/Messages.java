package com.vibridi.nulab;

public interface Messages {

	public static final String NOT_IMPLEMENTED = "Not implemented";
	public static final String UNKNOWN_ENUM = "Unknown enum value: %s";
	public static final String CLI_USAGE = "Arguments: 'diagramType' 'apiKey' 'diagramId'";
	
	public static final String UNSUPPORTED_DIAGRAM_TYPE = "Diagram type %s is not supported.";
	public static final String INVALID_EDGE = "Invalid edge. Must have one start and one end.";
	public static final String INVALID_ER_ENTITY = "Invalid E/R entity. Must have at least a name and a PK.";
}
