package io.skullabs.uakka.master;

import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.UntypedActor;

public class CounterActor extends UntypedActor {

	@Override
	public void onReceive( Object counter ) throws Exception {
		int value = ( (AtomicInteger)counter ).incrementAndGet();
		getSender().tell( value, getSelf() );
	}
}
