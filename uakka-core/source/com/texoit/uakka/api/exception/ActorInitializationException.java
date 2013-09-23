package com.texoit.uakka.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActorInitializationException extends UAkkaException {

	public ActorInitializationException( Throwable throwable ) {
		super( throwable );
	}

	private static final long serialVersionUID = 1837343823879088192L;
}