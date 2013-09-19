package com.texoit.uakka.service;

import java.util.Set;

import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import akka.actor.ActorSystem;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.InjectableDiscoveryService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;
import com.typesafe.config.ConfigFactory;

@Log
@RequiredArgsConstructor
public class AkkaInitialization {

	private static interface DelegatedMethods {
		void initialize() throws InjectionException;

		void shutdown();
	}

	@Delegate( types = DelegatedMethods.class )
	final private AkkaActors akkaActors;
	
	@Getter
	Injectables injectables;

	public AkkaInitialization( Set<Class<?>> classes, AkkaConfiguration configuration ) throws InjectionException {
		ActorSystem actorSystem = createActorSystem( configuration );
		InjectableDiscoveryService injectableDiscoveryService = new InjectableDiscoveryService( configuration );
		injectables = injectableDiscoveryService.discovery( classes );
		configuration.setInjectables( injectables );
		this.akkaActors = new DefaultAkkaActors( injectables, actorSystem );
		configuration.setAkkaActors( this.akkaActors );
		this.akkaActors.analyze( classes );
	}

	private ActorSystem createActorSystem( AkkaConfiguration configuration ) {
		String applicationName = configuration.toString();
		Config defaultConfig = ConfigFactory.load();
		Config config = readConfig( applicationName, defaultConfig );
		configuration.setConfig( config );
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