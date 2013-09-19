package com.texoit.uakka.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UnhandledMessageException extends MethodHandlerException {

	private static final long serialVersionUID = -6713772581672589187L;

	final Object unhandledMessage;

}
