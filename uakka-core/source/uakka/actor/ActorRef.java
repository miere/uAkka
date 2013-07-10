package uakka.actor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class ActorRef {

	@NonNull BlockingQueue<ActorMessage> messageQueue;
	@NonNull ActorConfiguration configuration;
	BlockingQueue<Object> responseMessageQueue;

	public Future<Object> tell( Object message ) {
		try {
			ActorMessage actorMessage = createActorMessage(message);
			messageQueue.put(actorMessage);
			if ( responseMessageQueue != null )
				responseMessageQueue.put( message );
			return new AsyncResponse(actorMessage.sender.responseMessageQueue);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public Object ask( Object message ) throws InterruptedException, ExecutionException {
		return tell( message ).get();
	}

	private ActorMessage createActorMessage(Object message) {
		BlockingQueue<Object> newResponseMessageQueue = new ArrayBlockingQueue<Object>(1);
		ActorRef senderRef = new ActorRef(messageQueue, configuration, newResponseMessageQueue);
		return new ActorMessage(message, senderRef);
	}

	public void close() throws InterruptedException, ExecutionException{
		for ( int i=0; i<configuration.numberOfParallelActors; i++ )
			tell( new Actor.Close() );
		messageQueue = null;
	}
}
