package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableClassFactory;
import io.skullabs.uakka.inject.InjectableDiscoveryService;
import io.skullabs.uakka.inject.Injectables;
import io.skullabs.uakka.inject.InjectionConfiguration;
import io.skullabs.uakka.inject.InjectionException;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;

@HandlesTypes( {
		Actor.class,
		UntypedActor.class,
		InjectableClassFactory.class
} )
public class ServletAkkaInitialization implements ServletContainerInitializer {

	private ActorSystem actorSystem;
	private ServletAkkaActors servletActors;

	@Override
	public void onStartup(
			Set<Class<?>> classes,
			ServletContext servletContext ) throws ServletException {
		try {
			InjectionConfiguration injectionConfiguration = createInjectionConfiguration( servletContext );
			initialize( classes, injectionConfiguration );
		} catch ( InjectionException e ) {
			throw new ServletException( e );
		}
	}

	ServletInjectionConfiguration createInjectionConfiguration( ServletContext servletContext ) {
		return new ServletInjectionConfiguration( servletContext );
	}

	public void initialize( Set<Class<?>> classes,
			InjectionConfiguration injectionConfiguration ) throws InjectionException {
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
		this.servletActors = new ServletAkkaActors( injectables );
		this.servletActors.configure( this.actorSystem, classes );
	}
}
