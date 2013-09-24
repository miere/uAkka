package com.texoit.uakka.cluster;

import lombok.extern.java.Log;
import akka.actor.ActorSystem;
import akka.actor.Props;

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
		actorSystem.actorOf(Props.create( ClusterHandshakeActor.class ));
	}

	private ActorSystem createActorSystem( String applicationName ) {
		Config defaultConfig = ConfigFactory.load();
		Config config = readConfig( applicationName, defaultConfig );
		log.info( "Creating ActorSystem for " + applicationName );
		return ActorSystem.create( applicationName, config );
	}

	private Config readConfig( String applicationName, Config defaultConfig ) {
		String configFile = System.getProperty( "uakka.configFile", "META-INF/application" );
		Config readConfig = ConfigFactory.load( configFile ).withFallback( defaultConfig );
		try {
			return readConfig.getConfig( applicationName );
		} catch ( Missing e ) {
			return readConfig;
		}
	}
}
