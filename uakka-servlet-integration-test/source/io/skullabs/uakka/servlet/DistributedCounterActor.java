package io.skullabs.uakka.servlet;

import io.skullabs.uakka.api.Reference;
import io.skullabs.uakka.api.Service;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

@Service
public class DistributedCounterActor extends UntypedActor {

	@Reference( actor = CounterActor.class )
	ActorRef counter;

	@Override
	public void onReceive( Object message ) throws Exception {
		this.counter.tell( message, getSender() );
	}

	@Service
	public static class CounterActor extends UntypedActor {

		@Override
		public void onReceive( Object message ) throws Exception {
			if ( message == null )
				replay( 0 );
			CountMessage countMessage = (CountMessage)message;
			int countValue = countMessage.getCounter().incrementAndGet();
			replay( countValue );
		}

		private void replay( int countValue ) {
			getSender().tell( countValue, getSelf() );
		}
	}

	@RequiredArgsConstructor
	public static class CountMessage {
		@Getter
		final AtomicInteger counter;

		public CountMessage() {
			this.counter = new AtomicInteger( 0 );
		}
	}
}
