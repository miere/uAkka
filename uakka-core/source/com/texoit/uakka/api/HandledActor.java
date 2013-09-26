package com.texoit.uakka.api;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.texoit.uakka.api.actor.Ping;
import com.texoit.uakka.api.actor.Pong;
import com.texoit.uakka.api.exception.MethodHandlerException;
import com.texoit.uakka.api.exception.UnhandledMessageException;

public class HandledActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
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
		log.debug("Replying " + object + " to " + getSender().path() );
		System.err.println("Replying " + object + " to " + getSender().path() );
//		getSender().tell( object, getSelf() );
		getSender().forward(object, context());
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
		log.error( "Failed on handle message from " + getSender().path() + ". Reason: " + cause.getMessage() );
		reply( cause );
		cause.printStackTrace();
	}

	@Override
	public void unhandled(Object message) {
		log.warning( "Unhandled message sent from actor " + getSender().path() + ": " + message );
		super.unhandled(message);
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
	
	/**
	 * Just a ping message receiver. Useful to test actor availability.
	 * 
	 * @param ping
	 * @return
	 */
	@Receiver
	public Pong onPing( Ping ping ) {
		return Pong.message();
	}
}
