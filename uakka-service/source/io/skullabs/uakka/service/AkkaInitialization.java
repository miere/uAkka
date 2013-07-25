package io.skullabs.uakka.service;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;
import io.skullabs.uakka.api.InjectionException;
import io.skullabs.uakka.inject.InjectableDiscoveryService;
import io.skullabs.uakka.inject.Injectables;

import java.util.Set;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorSystem;

import com.typesafe.config.Config;
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
		this.akkaActors = new DefaultAkkaActors( injectables, actorSystem );
		configuration.setAkkaActors( this.akkaActors );
		this.akkaActors.analyze( classes );
	}

	private ActorSystem createActorSystem( AkkaConfiguration configuration ) {
		Config defaultConfig = ConfigFactory.load();
		Config config = ConfigFactory.load( "META-INF/application" ).withFallback( defaultConfig );
		configuration.setConfig( config );
		return ActorSystem.create( configuration.toString(), config );
	}
}