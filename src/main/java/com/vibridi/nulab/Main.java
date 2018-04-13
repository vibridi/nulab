package com.vibridi.nulab;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.vibridi.nulab.reader.DiagramReader;
import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;
import com.vibridi.nulab.writer.DiagramOutputWriter;
import com.vibridi.nulab.writer.FileSystemWriter;

public class Main {

	// TODO add startup scripts for Unix and Win
	/**
	 * Entry point. Accepts the diagram type and id as launch parameters.
	 * @param args Launch parameters passed in as such: 
	 * <ul>
	 * <li>args[0] -&gt; diagram type</li>
	 * <li>args[1] -&gt; API key</li> 
	 * <li>args[2] -&gt; diagram id</li>
	 * </ul>
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
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
		// TODO logging
		
		DiagramReader reader = DiagramReaderFactory.instance.forType(type);
		reader.setApiKey(args[1]);
		
		try {
			System.out.println("Fetching diagram...");
			// TODO add logging with completion times
			// log4j must output to stdout and to debug
			String xmlSource = reader.fetchDiagram(args[2]);
			reader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			
			DiagramOutputWriter dow = new DiagramOutputWriter();
			dow.write(new FileSystemWriter());
			// dow.write( /* some other writer strategy, e.g. write directly into a database */);
			
		} catch(Exception e) {
			// TODO log error
			System.exit(1);
		}
		
		System.out.println("Task completed");
	}
}
