package com.vibridi.nulab.exceptions;

public class DiagramException extends Exception {
	private static final long serialVersionUID = 1L;

	public DiagramException(String msg) {
		super(msg);
	}
	
	public DiagramException(Throwable t) {
		super(t);
	}
	
	public DiagramException(String msg, Throwable t) {
		super(msg, t);
	}

}
