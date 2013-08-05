package io.skullabs.uakka.api.exception;

public class InjectionException extends UAkkaException {

	public InjectionException( String message, Exception cause ) {
		super( message, cause );
	}

	public InjectionException( Exception e ) {
		super( e );
	}

	public InjectionException( String string ) {
		super( string );
	}

	private static final long serialVersionUID = 9085427867521502627L;

}
