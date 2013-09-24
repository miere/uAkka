package com.texoit.uakka.cluster;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.java.Log;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;

import com.texoit.uakka.api.HandledActor;
import com.texoit.uakka.api.Receiver;

@Log
public class ClusterDispatcherActor extends HandledActor {

	final String actorName;
	CircularQueue<ActorSelection> actors = CircularQueue.create();
	Map<String, ActorSelection> clusteredActorsByAddress = new HashMap<String, ActorSelection>();

	public ClusterDispatcherActor( String actorName, Address address ) {
		this.actorName = actorName;
		registerNewAddress( address );
	}

	@Receiver
	public void onRegisterNewAddress( RegisterRemoteActor registerRemoteActor ){
		Address address = registerRemoteActor.address();
		registerNewAddress(address);
	}

	void registerNewAddress(Address address) {
		String selectionAddress = selectionAddressFrom( address );
		ActorSelection actorSelection = actorSystem().actorSelection( selectionAddress );
		actors.pull(actorSelection);
		String addressHost = hostFrom( address );
		clusteredActorsByAddress.put(addressHost, actorSelection);
	}

	@Receiver
	public void onUnRegisterNewAddress( UnregisterRemoteActor registerRemoteActor ){
		String addressHost = hostFrom( registerRemoteActor.address() );
		ActorSelection item = clusteredActorsByAddress.get(addressHost);
		actors.remove(item);
		clusteredActorsByAddress.remove(addressHost);
	}

	@Override
	public void unhandled(Object message) {
		try {
			actors.next().tell( message, getSender() );
		} catch ( Throwable cause ) {
			log.severe( cause.getMessage() );
			reply( cause );
		}
	}

	ActorSystem actorSystem(){
		return getContext().system();
	}

	String hostFrom( Address address ){
		return String.format( "%s@%s:%s",
				address.system(), address.host(), address.hostPort() );
	}

	String selectionAddressFrom( Address address ) {
		return String.format( "akka.tcp://%s@%s:%s/user/%s",
				address.system(), address.host(), address.hostPort(), actorName );
	}
}
