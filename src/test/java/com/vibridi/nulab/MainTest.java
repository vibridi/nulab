package com.vibridi.nulab;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.vibridi.nulab.reader.DiagramReader;
import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;

public class MainTest {

	public static final String key = "RpCk37NtMaRtVz4t0mxJ";
	
	// TODO remove this test case
	@Test
    public void instantiateReader() throws IOException {
		DiagramReader r = DiagramReaderFactory.instance.forType(DiagramType.ER);
		r.setApiKey(key);
		String xml = r.fetchDiagram("zCpdyDhENJIeaS2J");
		//System.out.println(xml);
		assertNotNull(xml);
    }
}
