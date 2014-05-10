package com.texoit.uakka.master;

import com.texoit.uakka.api.Reference;
import com.texoit.uakka.api.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

@Service
public class DistributedCounterActor extends UntypedActor {

	@Service( name = "sum", actor = CounterActor.class )
	ActorRef counter;

	@Reference( path = "akka.tcp://slave@127.0.0.1:9002/user/PersistenceDataActor" )
	ActorSelection slaveCounter;

	@Override
	public void onReceive( Object message ) throws Exception {
		this.counter.tell( message, getSender() );
		this.slaveCounter.tell( message, getSender() );
	}

	@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		System.out.println( "Stoping " + this );
	}
}
