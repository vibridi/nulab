package com.vibridi.nulab.reader;

import java.io.IOException;
import java.io.InputStream;

public interface DiagramReader {

	/**
	 * Sets the API key needed for authentication.
	 * @param apiKey The API key
	 */
	public void setApiKey(String apiKey);
	
	/**
	 * Gets an API key set previously
	 * @return The API key.
	 */
	public String getApiKey();
	
	/**
	 * Retrieves the diagram content from a remote source. Authenticates with the API key set previously.
	 * @param diagramId Unique identifier of the diagram.
	 * @return A string representing the diagram content in XML format. The string encoding is implementation dependent. 
	 * @throws IOException If retrieval fails.
	 */
	public String fetchDiagram(String diagramId) throws IOException;
	
	/**
	 * Reads a diagram into an output string.
	 * @param in Stream containing the entire diagram input. 
	 * @throws Exception If any exception occurs. 
	 * The method signature throws a generic exception so that implementors aren't forced toward a particular error classification.
	 * Client code can then catch any specific sub-class.
	 */
	public void read(InputStream in) throws Exception;
	
	/**
	 * The size of the output, based on the fact that a single diagram may result in multiple output units.
	 * @return Size of the output.
	 */
	public int outputSize();
	
	/**
	 * Gets the i-th output. 
	 * @param index 0-indexed accessor
	 * @return The i-th output
	 */
	public String getOutput(int index);
	
	/**
	 * Gets the name associated with this output. Allows the reader to set names or other identifiers that might come from the 
	 * diagram itself.
	 * @param index 0-indexed accessor
	 * @return The name associated with the i-th output
	 */
	public String getOutputName(int index);
	
}
