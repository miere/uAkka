package io.skullabs.uakka;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActorException extends RuntimeException {

	private static final long serialVersionUID = 150566621346512468L;
	
	public ActorException( String message ){
		this(message, null);
	}

	public ActorException( String message, Throwable cause ) {
		super( message, cause );
	}
}
