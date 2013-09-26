package com.texoit.uakka.cluster;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.ExtensionMethod;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.HandledActor;
import com.texoit.uakka.api.Receiver;
import com.texoit.uakka.api.Service;

@Service("ClusterHandshakeActor")
@ExtensionMethod(AddressExtension.class)
public class ClusterHandshakeActor extends HandledActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Map<String, ActorRef> dispatchersByName = new HashMap<String, ActorRef>();

	AkkaActors akkaActors;
	ActorSystem actorSystem;
	Cluster cluster;

	@Override
	public void preStart() throws Exception {
		cluster = Cluster.get( getContext().system() );
	}

	/**
	 * Listen to notifications for remote nodes added to cluster.
	 * 
	 * @param memberUp
	 */
	@Receiver
	public void onMemberUp( MemberUp memberUp ){
		Address address = memberUp.member().address();

		if ( isSelfAddress(address) )
			return;

		log.debug("New cluster member at " + address.hostString());
		String selectionAddress = address.actorSelectionFor( "ClusterHandshakeActor" );
		log.debug("New cluster member at " + address.hostString());
		ActorSelection actor = actorSystem().actorSelection( selectionAddress );
		actor.tell(ProvideAvailableActors.create(), getSelf());
	}

	/**
	 * Listen to notifications for remote nodes removed from cluster.
	 * 
	 * @param memberRemoved
	 */
	@Receiver
	public void onMemberRemoved( MemberRemoved memberRemoved ){
		Address address = memberRemoved.member().address();
		for ( ActorRef actorRef : dispatchersByName.values() )
			actorRef.tell( UnregisterRemoteActor.at(address) , ActorRef.noSender() );
	}

	boolean isSelfAddress(Address address) {
		return address.hostString().equals( cluster.selfAddress().hostString() );
	}

	/**
	 * Listen to notifications for new available actors on cluster remote nodes.
	 * 
	 * @param availableActors
	 */
	@Receiver
	public void onReceiveAvailablesActors( AvailableActors availableActors ){
		Address address = availableActors.from();
		log.debug("Found actors available at " + address.hostString() );
		for ( String name : availableActors.actors() )
			registerDispatchAddressForActor(address, name);
	}

	/**
	 * Create an actor dispatcher for remote address with specified name.
	 * If a dispatcher is already created, them it will just register a new
	 * address location.
	 * 
	 * @param address
	 * @param name
	 */
	void registerDispatchAddressForActor(Address address, String name) {
		if ( name.equals( getClass().getSimpleName() ) )
			return;
		if ( actorAlreadyCreated(name) )
			registerActorNewAddress(name, address);
		else
			createActorDispatcherFor(name, address);
	}

	boolean actorAlreadyCreated(String name) {
		return dispatchersByName.containsKey(name);
	}

	/**
	 * It register a new address location at actor dispatcher
	 * named with parameter name.
	 * 
	 * @param name
	 * @param address
	 */
	void registerActorNewAddress( String name, Address address ){
		ActorRef dispatcher = dispatchersByName.get( name );
		dispatcher.tell(RegisterRemoteActor.at(address), ActorRef.noSender());
		log.info("New location available for actor: " + name + " at " + address.hostString());
	}

	/**
	 * Create actor dispatcher named with parameter name. It will be
	 * initially configured to handle communication with specified 
	 * address parameter.
	 * 
	 * @param name
	 * @param address
	 */
	void createActorDispatcherFor(String name, Address address) {
		log.info( "Creating actor dispatcher for " + name + " at " + address.hostString() );
		Props actorCreationProperties =
				Props.create(ClusterDispatcherActor.class, name, address)
					 .withDispatcher("uakka.cluster.dispatcher");
		ActorRef actorDispatcher = actorSystem().actorOf(actorCreationProperties, name);
		dispatchersByName.put( name, actorDispatcher);
		log.info("New actor available on cluster: " + name + " pointing " + address.hostString());
	}

	/**
	 * Provide the available actor names in cluster node to the cluster master node.
	 * It's auto-started by uAkka, and should be started only on cluster nodes.
	 * 
	 * @param provideAvailableActors
	 * @return
	 */
	@Receiver
	public AvailableActors provideAvailableActors( ProvideAvailableActors provideAvailableActors ){
		return AvailableActors.from (
				cluster.selfAddress(),
				akkaActors.getAvailableActorNames()
			);
	}

	ActorSystem actorSystem() {
		if ( actorSystem == null )
			actorSystem = getContext().system();
		return actorSystem;
	}
}
