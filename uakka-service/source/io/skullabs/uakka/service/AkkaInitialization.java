package io.skullabs.uakka.service;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;
import io.skullabs.uakka.api.Injectables;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.InjectableDiscoveryService;

import java.util.Set;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;
import com.typesafe.config.ConfigFactory;

@RequiredArgsConstructor
public class AkkaInitialization {

	private static interface DelegatedMethods {
		void initialize() throws InjectionException;

		void shutdown();
	}

	@Delegate( types = DelegatedMethods.class )
	final private AkkaActors akkaActors;

	public AkkaInitialization( Set<Class<?>> classes, AkkaConfiguration configuration ) throws InjectionException {
		ActorSystem actorSystem = createActorSystem( configuration );
		InjectableDiscoveryService injectableDiscoveryService = new InjectableDiscoveryService( configuration );
		Injectables injectables = injectableDiscoveryService.discovery( classes );
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