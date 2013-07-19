package io.skullabs.uakka.simple;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActorRef {

	@NonNull
	BlockingQueue<ActorMessageEnvelop> messageQueue;
	@NonNull
	ActorConfiguration configuration;

	public Future<Object> tell( Object message ) {
		try {
			ActorMessageEnvelop envelop = envelop( message );
			this.messageQueue.put( envelop );
			return new AsyncResponse( envelop.sender.messageQueue );
		} catch ( InterruptedException e ) {
			throw new RuntimeException( e );
		}
	}

	public Object ask( Object message ) throws InterruptedException, ExecutionException {
		return tell( message ).get();
	}

	private ActorMessageEnvelop envelop( Object message ) {
		BlockingQueue<ActorMessageEnvelop> newResponseMessageQueue = new ArrayBlockingQueue<ActorMessageEnvelop>( 1 );
		ActorRef senderRef = new ActorRef( newResponseMessageQueue, this.configuration );
		return new ActorMessageEnvelop( message, senderRef );
	}

	public void close() throws InterruptedException, ExecutionException {
		for ( int i = 0; i < this.configuration.numberOfParallelActors; i++ )
			tell( new Actor.Close() );
		this.messageQueue = null;
	}
}
