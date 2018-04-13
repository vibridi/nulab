package com.vibridi.nulab.exceptions;

public class XMLException extends Exception {
	private static final long serialVersionUID = 1L;

	public XMLException(String msg) {
		super(msg);
	}
	
	public XMLException(Throwable t) {
		super(t);
	}
	
	public XMLException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
