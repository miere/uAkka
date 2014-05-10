package com.texoit.uakka.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MethodHandlerException extends UAkkaException {

	public MethodHandlerException( Throwable throwable ) {
		super( throwable );
	}

	private static final long serialVersionUID = 7775131463046285435L;

}
