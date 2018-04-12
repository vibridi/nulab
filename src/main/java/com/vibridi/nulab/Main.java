package com.vibridi.nulab;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;

public class Main {

	// API Key TODO remove
	// 

	// TODO add startup scripts for Unix and Win
	
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		
		// check args
		
		DiagramType type = DiagramType.valueOf(args[0]);
		
		DiagramReaderFactory.instance.forType(type);


//		InputStream is = new ByteArrayInputStream(diagramContent.getBytes());
//
//		Document document = DocumentBuilderFactory.newInstance()
//				.newDocumentBuilder().parse(is);
	}
}
