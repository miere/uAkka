package io.skullabs.uakka.inject;

import java.util.Set;

import akka.actor.ActorSystem;

public class InjectableAkkaInitialization {

	private ActorSystem actorSystem;
	private InjectableAkkaActors injectableAkkaActors;

	public void initialize( Set<Class<?>> classes, InjectionConfiguration injectionConfiguration ) throws InjectionException {
		createActorSystem( injectionConfiguration );
		analize( injectionConfiguration, classes );
	}

	private void createActorSystem( InjectionConfiguration configuration ) {
		this.actorSystem = ActorSystem.create( configuration.toString() );
		configuration.setAttribute( ActorSystem.class.getCanonicalName(), this.actorSystem );
	}

	private void analize( InjectionConfiguration configuration, Set<Class<?>> classes ) throws InjectionException {
		InjectableDiscoveryService injectableDiscoveryService = new InjectableDiscoveryService( configuration );
		Injectables injectables = injectableDiscoveryService.discovery( classes );
		this.injectableAkkaActors = new InjectableAkkaActors( injectables );
		configuration.setAttribute( InjectableAkkaActors.class.getCanonicalName(), this.injectableAkkaActors );
		this.injectableAkkaActors.configure( this.actorSystem, classes );
	}
}