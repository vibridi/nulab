package com.vibridi.nulab;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;

import com.vibridi.nulab.reader.DiagramReader;
import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;
import com.vibridi.nulab.writer.DiagramOutputWriter;
import com.vibridi.nulab.writer.FileSystemWriter;

public class Main {

	private static Logger logger = Logger.getLogger("NULAB");
	
	/**
	 * Entry point. Accepts the diagram type and id as launch parameters.
	 * @param args Launch parameters passed in as such: 
	 * <ul>
	 * <li>args[0] -&gt; diagram type</li>
	 * <li>args[1] -&gt; API key</li> 
	 * <li>args[2] -&gt; diagram id</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if(args.length < 3) {
			System.err.println(Messages.CLI_USAGE);
			System.exit(1);
		}
		
		DiagramType type = null;
		
		try {
			type = DiagramType.valueOf(args[0]);
		} catch(IllegalArgumentException e) {
			
			System.err.println(String.format(Messages.UNSUPPORTED_DIAGRAM_TYPE, args[0]));
			System.exit(1);
		}
		
		logger.debug("Instantiating reader...");
		DiagramReader reader = DiagramReaderFactory.instance.forType(type);
		reader.setApiKey(args[1]);
		
		try {
			logger.info("Fetching diagram...");
			long start = System.currentTimeMillis();
			String xmlSource = reader.fetchDiagram(args[2]);
			long end = System.currentTimeMillis() - start;
			logger.info(String.format("Diagram retrieved in %d millis", end));
			
			logger.info("Reading diagram...");
			reader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			
			logger.info("Writing results...");
			DiagramOutputWriter dow = new DiagramOutputWriter(reader);
			dow.write(new FileSystemWriter());
			// dow.write( /* some other writer strategy, e.g. run the output directly into a database */);
			
		} catch(Exception e) {
			logger.error("An error occurred", e);
			System.exit(1);
		}
		
		logger.info("Task completed");
	}
}
