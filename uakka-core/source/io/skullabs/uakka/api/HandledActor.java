package io.skullabs.uakka.api;

import io.skullabs.uakka.api.exception.MethodHandlerException;
import io.skullabs.uakka.api.exception.UnhandledMessageException;
import akka.actor.UntypedActor;

public class HandledActor extends UntypedActor {

	MethodHandler handledMethods = new MethodHandler( this );

	@Override
	public void onReceive( Object message ) throws Exception {
		try {
			beforeMessage();
			this.handledMethods.runHandlerMethodFor( message );
		} catch ( UnhandledMessageException e ) {
			unhandled( message );
		} catch ( MethodHandlerException e ) {
			onFail( e );
		} finally {
			afterMessage();
		}
	}

	/**
	 * Handles failures handling messages
	 * 
	 * @param e
	 * @throws Exception
	 */
	public void onFail( MethodHandlerException e ) throws Exception {
		throw (Exception)e.getCause();
	}

	/**
	 * Event dispatched before a message arrive. A convenient method to be
	 * overridden by developers to run something before every arrived message.
	 */
	public void beforeMessage() {
	}

	/**
	 * Event dispatched after a message arrive. A convenient method to be
	 * overridden by developers to run something after every arrived message.
	 */
	public void afterMessage() {
	}
}
