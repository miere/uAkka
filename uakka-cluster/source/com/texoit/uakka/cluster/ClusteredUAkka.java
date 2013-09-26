package com.texoit.uakka.cluster;

import lombok.extern.java.Log;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;
import com.typesafe.config.ConfigFactory;

@Log
public class ClusteredUAkka {

	public static void main(String[] args) {
		if ( args.length > 0 )
			System.setProperty("akka.remote.netty.tcp.port", args[0]);
		ClusteredUAkka clusteredUAkka = new ClusteredUAkka();
		clusteredUAkka.initialize("ClusterNodeOne");
	}

	public void initialize( String applicationName ){
		ActorSystem actorSystem = createActorSystem( applicationName );
		ActorRef handshakeActor = actorSystem.actorOf(
				Props.create( ClusterHandshakeActor.class ),
				ClusterHandshakeActor.class.getSimpleName());
		Cluster cluster = Cluster.get(actorSystem);
		cluster.subscribe(handshakeActor, MemberUp.class);
		cluster.subscribe(handshakeActor, MemberRemoved.class);
	}

	private ActorSystem createActorSystem( String applicationName ) {
		Config config = readConfig( applicationName );
		log.info( "Creating ActorSystem for " + applicationName );
		return ActorSystem.create( applicationName, config );
	}

	private Config readConfig( String applicationName ) {
		String configFile = System.getProperty( "uakka.configFile", "META-INF/application" );
		Config defaultConfig = ConfigFactory.load();
		Config clusterConfig = ConfigFactory.load("META-INF/cluster");
		Config readConfig = ConfigFactory
				.load( configFile )
				.withFallback( clusterConfig )
				.withFallback( defaultConfig );
		try {
			return readConfig.getConfig( applicationName );
		} catch ( Missing e ) {
			return readConfig;
		}
	}
}
