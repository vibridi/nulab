package com.vibridi.nulab;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vibridi.nulab.reader.DiagramReader;
import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;

public class MainTest {

	// TODO remove finally
	public static final String key = "RpCk37NtMaRtVz4t0mxJ";
	
	@Test
    public void instantiateReader() {
		DiagramReader r = DiagramReaderFactory.instance.forType(DiagramType.ER);
		assertNotNull(r);
    }
}
