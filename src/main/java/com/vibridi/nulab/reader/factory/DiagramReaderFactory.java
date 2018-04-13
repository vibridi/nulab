package com.vibridi.nulab.reader.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.vibridi.nulab.Messages;
import com.vibridi.nulab.reader.DiagramReader;
import com.vibridi.nulab.reader.DiagramType;
import com.vibridi.nulab.reader.er.impl.ERDiagramReader;

public enum DiagramReaderFactory {
	instance;
	
	private Map<DiagramType,DiagramReader> cache;
	
	private DiagramReaderFactory() {
		cache = new HashMap<>();
	}

	public DiagramReader forType(DiagramType type) {
		return cache.computeIfAbsent(type, this::newReader);
	}

	
	private DiagramReader newReader(DiagramType type) {
		switch(type) {
		case CLASS:
			throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED);
			
		case ER:
			return new ERDiagramReader();
	
		default:
			new IllegalStateException(String.format(Messages.UNKNOWN_ENUM, type.name()));
		}
		
		// Unreachable. 
		// However the compiler complains about a missing return statement due to having throws inside the switch body. 
		// Therefore by forcing a NullPointerException this acts as guard against changes done at a later time.  
		return Optional.of((DiagramReader)null).get();
	}
	
	
}
