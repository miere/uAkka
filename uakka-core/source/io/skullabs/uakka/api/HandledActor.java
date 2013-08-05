package io.skullabs.uakka.api;

import io.skullabs.uakka.api.exception.MethodHandlerException;
import io.skullabs.uakka.api.exception.UnhandledMessageException;
import akka.actor.UntypedActor;

public class HandledActor extends UntypedActor {

	MethodHandler handledMethods = new MethodHandler( this );

	@Override
	public void onReceive( Object message ) throws Exception {
		try {
			handledMethods.runHandlerMethodFor( message );
		} catch ( UnhandledMessageException e ) {
			unhandled( message );
		} catch ( MethodHandlerException e ) {
			throw (Exception)e.getCause();
		}
	}
}
