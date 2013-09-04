package io.skullabs.uakka.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UAkkaException extends Exception {

	public UAkkaException( String message, Exception cause ) {
		super( message, cause );
	}

	public UAkkaException( Throwable throwable ) {
		super( throwable );
	}

	public UAkkaException( String string ) {
		super( string );
	}

	private static final long serialVersionUID = -4325689809406199540L;
}
