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

	/**
	 * Creates a new diagram reader.
	 * @param type Type of the diagram reader
	 * @return New reader instance or a cached one, if present.
	 */
	public DiagramReader forType(DiagramType type) {
		return cache.computeIfAbsent(type, this::newReader);
	}

	private DiagramReader newReader(DiagramType type) {
		DiagramReader dr = null;
		
		switch(type) {
		case CLASS:
			throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED);
			
		case ER:
			dr = new ERDiagramReader();
			break;
	
		default:
			throw new IllegalStateException(String.format(Messages.UNKNOWN_ENUM, type.name()));
		}
		
		return dr;
	}
	
	
}
