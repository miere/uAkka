package io.skullabs.uakka.inject;

import java.util.Set;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorSystem;

@RequiredArgsConstructor
public class InjectableAkkaInitialization {

	private static interface DelegatedMethods {
		void initialize() throws InjectionException;

		void shutdown();
	}

	@Delegate( types = DelegatedMethods.class )
	final private InjectableAkkaActors injectableAkkaActors;

	public InjectableAkkaInitialization( Set<Class<?>> classes, InjectionConfiguration configuration ) throws InjectionException {
		ActorSystem actorSystem = createActorSystem( configuration );
		InjectableDiscoveryService injectableDiscoveryService = new InjectableDiscoveryService( configuration );
		Injectables injectables = injectableDiscoveryService.discovery( classes );
		this.injectableAkkaActors = new InjectableAkkaActors( injectables, actorSystem );
		configuration.setInjectableAkkaActors( this.injectableAkkaActors );
		this.injectableAkkaActors.analise( classes );
	}

	private ActorSystem createActorSystem( InjectionConfiguration configuration ) {
		return ActorSystem.create( configuration.toString() );
	}
}