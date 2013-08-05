package io.skullabs.uakka.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MethodHandlerException extends UAkkaException {

	public MethodHandlerException( Exception cause ) {
		super( cause );
	}

	private static final long serialVersionUID = 7775131463046285435L;

}
