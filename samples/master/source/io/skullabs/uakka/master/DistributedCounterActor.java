package io.skullabs.uakka.master;

import io.skullabs.uakka.api.Reference;
import io.skullabs.uakka.api.Service;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

@Service
public class DistributedCounterActor extends UntypedActor {

	@Service( name = "sum", actor = CounterActor.class )
	ActorRef counter;

	@Reference( path = "akka.tcp://uakka-servlet-integration-slave@127.0.0.1:9002/user/slavecounter" )
	ActorSelection slaveCounter;

	@Override
	public void onReceive( Object message ) throws Exception {
		this.counter.tell( message, getSender() );
		this.slaveCounter.tell( message, getSender() );
	}
}
