package com.texoit.uakka.cluster;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;

import com.texoit.uakka.api.HandledActor;
import com.texoit.uakka.api.Receiver;

public class ClusterHandshakeActor extends HandledActor {
	
	ActorSystem actorSystem;
	Map<String, ActorRef> dispatchersByName = new HashMap<String, ActorRef>();

	@Override
	public void preStart() throws Exception {
		Cluster cluster = Cluster.get( getContext().system() );
		cluster.subscribe(getSelf(), MemberUp.class);
		cluster.subscribe(getSelf(), MemberRemoved.class);
	}

	@Receiver
	public void onMemberUp( MemberUp memberUp ){
		System.out.println("Member UP: " + memberUp);
	}

	@Receiver
	public void onMemberRemoved( MemberRemoved memberRemoved ){
		System.out.println("Member Removed: " + memberRemoved);
	}

	@Receiver
	public void onReceiveAvailablesActors( AvailableActors availableActors ){
		Address address = availableActors.from();
		for ( String name : availableActors.actors() )
			createActorDispatcherFor(name, address);
	}

	void createActorDispatcherFor(String name, Address address) {
		ActorRef actorDispatcher = actorSystem.actorOf(Props.create(ClusterDispatcherActor.class, name, address));
		dispatchersByName.put( hostFrom( address ), actorDispatcher);
	}

	String hostFrom( Address address ){
		return String.format( "%s@%s:%s",
				address.system(), address.host(), address.hostPort() );
	}
}
