package com.texoit.uakka.api;

import com.texoit.uakka.api.exception.MethodHandlerException;
import com.texoit.uakka.api.exception.UnhandledMessageException;

import lombok.extern.java.Log;
import akka.actor.UntypedActor;

@Log
public class HandledActor extends UntypedActor {

	MethodHandler handledMethods = new MethodHandler( this );

	@Override
	public void onReceive( Object message ) throws Exception {
		try {
			beforeMessage();
			Object returnedObject = this.handledMethods.runHandlerMethodFor( message );
			if ( returnedObject != null )
				reply( returnedObject );
			onSuccess( returnedObject );
		} catch ( UnhandledMessageException e ) {
			unhandled( message );
		} catch ( MethodHandlerException e ) {
			onFail( e );
		} finally {
			afterMessage();
		}
	}

	/**
	 * Send message as a reply response to the sender
	 * 
	 * @param object
	 */
	protected void reply( Object object ) {
		getSender().tell( object, getSelf() );
	}

	/**
	 * Handles success handling messages. By default it does nothing.
	 * 
	 * @param returnedObject
	 */
	public void onSuccess( Object returnedObject ) {
	}

	/**
	 * Handles failures handling messages
	 * 
	 * @param e
	 * @throws Exception
	 */
	public void onFail( MethodHandlerException e ) throws Exception {
		Throwable cause = e.getCause();
		if ( cause == null )
			cause = e;
		log.severe( cause.getMessage() );
		reply( cause );
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
