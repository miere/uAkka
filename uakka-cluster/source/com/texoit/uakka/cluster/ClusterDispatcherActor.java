package com.texoit.uakka.cluster;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.texoit.uakka.api.HandledActor;
import com.texoit.uakka.api.Receiver;
import com.texoit.uakka.api.actor.Ask;
import com.texoit.uakka.api.actor.Ping;

@ExtensionMethod({AddressExtension.class, Ask.class})
@RequiredArgsConstructor
public class ClusterDispatcherActor extends HandledActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	CircularQueue<ActorSelection> actors = CircularQueue.create();
	Map<String, ActorSelection> clusteredActorsByAddress = new HashMap<String, ActorSelection>();

	final String actorName;
	final Address address;

	@Override
	public void preStart() throws Exception {
		super.preStart();
		registerNewAddress( address );
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		clusteredActorsByAddress.clear();
		actors.clear();
	}

	@Receiver
	public void onRegisterNewAddress( RegisterRemoteActor registerRemoteActor ) throws Exception {
		Address address = registerRemoteActor.address();
		registerNewAddress(address);
	}

	void registerNewAddress(Address address) throws Exception {
		String addressHost = address.hostString();
		String selectionAddress = address.actorSelectionFor(actorName);
		log.debug( "Looking up remote actor " + actorName + " at " + addressHost );
		ActorSelection actorSelection = actorSystem().actorSelection( selectionAddress );
		log.debug( "Checking if node is available through Ping-Pong." );
		actorSelection.ask(Ping.message()).getOrThrow();
		log.info( "Registering remote actor " + actorName + " at " + addressHost );
		actors.pull(actorSelection);
		clusteredActorsByAddress.put(addressHost, actorSelection);
	}

	@Receiver
	public void onUnRegisterNewAddress( UnregisterRemoteActor registerRemoteActor ){
		String addressHost = registerRemoteActor.address().hostString();
		log.info( "Unregistering remote actor " + actorName + " at " + addressHost );
		ActorSelection item = clusteredActorsByAddress.get(addressHost);
		actors.remove(item);
		clusteredActorsByAddress.remove(addressHost);
		stopIfHaveNoActorsToHandleAndDispatch();
	}

	void stopIfHaveNoActorsToHandleAndDispatch() {
		if ( actors.size() == 0 ){
			log.info("No actor name " + actorName + " remains up. Stopping dispatcher.");
			context().stop( getSelf() );
		}
	}

	@Override
	public void unhandled(Object message) {
		try {
			ActorSelection next = actors.next();
			if ( next != null ){
				System.err.println( "Dispatching " + message + " to " + next.toString() );
				next.tell( message, getSender() );
				return;
			}
			log.warning("No actors available for " + actorName );
			super.unhandled(message);
		} catch ( Throwable cause ) {
			cause.printStackTrace();
			log.error(cause, "Can't handle message " + message );
			reply( cause );
		}
	}

	ActorSystem actorSystem(){
		return getContext().system();
	}
}
