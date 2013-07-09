package uakka.actor;

import java.util.concurrent.BlockingQueue;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActorRef {

	@NonNull private BlockingQueue<Object> messageQueue;
	@NonNull ActorConfiguration configuration;

	public void tell( Object message ) {
		try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void close(){
		for ( int i=0; i<configuration.numberOfParallelActors; i++ )
			tell( new Actor.Close() );
		messageQueue = null;
	}
}
