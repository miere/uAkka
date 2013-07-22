package io.skullabs.uakka.inject;

public class InjectionException extends Exception {

	public InjectionException( String message, Exception cause ) {
		super( message, cause );
	}

	public InjectionException( Exception e ) {
		super( e );
	}

	private static final long serialVersionUID = 9085427867521502627L;

}
