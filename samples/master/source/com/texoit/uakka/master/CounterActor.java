package com.texoit.uakka.master;

import java.util.concurrent.atomic.AtomicInteger;

import akka.actor.UntypedActor;

public class CounterActor extends UntypedActor {

	@Override
	public void onReceive( Object counter ) throws Exception {
		( (AtomicInteger)counter ).incrementAndGet();
	}

	@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		System.out.println( "Stoping " + this );
	}
}
