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
		String clusterName = "ClusterNodeOne";
		if ( args.length > 0 )
			System.setProperty("akka.remote.netty.tcp.port", args[0]);
		if ( args.length > 1 )
			clusterName = args[1];
		ClusteredUAkka clusteredUAkka = new ClusteredUAkka();
		clusteredUAkka.initialize( clusterName );
	}

	public void initialize( String applicationName ){
		Config config = readConfig( applicationName );
		log.info( "Creating ActorSystem for " + applicationName );
		ActorSystem actorSystem = ActorSystem.create( applicationName, config );
		ActorRef handshakeActor = actorSystem.actorOf(
				Props.create( ClusterHandshakeActor.class, config ),
				ClusterHandshakeActor.class.getSimpleName());
		Cluster cluster = Cluster.get(actorSystem);
		cluster.subscribe(handshakeActor, MemberUp.class);
		cluster.subscribe(handshakeActor, MemberRemoved.class);
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
