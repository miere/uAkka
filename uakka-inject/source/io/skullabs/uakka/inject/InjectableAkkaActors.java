package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableClass;
import io.skullabs.uakka.inject.Injectables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import akka.actor.Actor;
import akka.actor.ActorRefFactory;
import akka.actor.Props;
import akka.japi.Creator;

@RequiredArgsConstructor
public class ServletAkkaActors {

	final Map<String, InjectableClass<?>> actors = new HashMap<String, InjectableClass<?>>();
	final Injectables injectables;

	@SuppressWarnings( "unchecked" )
	public void configure( ActorRefFactory actorRefFactory, Collection<Class<?>> classes ) {
		for ( Class<?> clazz : classes )
			if ( Actor.class.isAssignableFrom( clazz ) ) {
				memorizeInjectableActor( clazz );
				tryInitializeActor( actorRefFactory, (Class<? extends Actor>)clazz );
			}
	}

	public void memorizeInjectableActor( Class<?> clazz ) {
		this.actors.put(
				clazz.getCanonicalName(),
				InjectableClass.newInstance(
						this.injectables, clazz ) );
	}

	private void tryInitializeActor( ActorRefFactory actorRefFactory, Class<? extends Actor> clazz ) {
		Service service = clazz.getAnnotation( Service.class );
		if ( service != null )
			newInstance( actorRefFactory, clazz, service );
	}

	// TODO: create a newinstance method for any possible creator
	public void newInstance( ActorRefFactory actorRefFactory, Class<? extends Actor> clazz, Service serviceAnnotation ) {
		String name = serviceAnnotation.name();
		newInstance( actorRefFactory, clazz, name );
	}

	private void newInstance( ActorRefFactory actorRefFactory, Class<? extends Actor> clazz, String name ) {
		actorRefFactory.actorOf( createActorProps( clazz.getCanonicalName() ), name );
	}

	private Props createActorProps( String canonicalName ) {
		InjectableClass<?> injectableClass = this.actors.get( canonicalName );
		Creator<Actor> creator = new DefaultActorCreator( injectableClass );
		return Props.create( creator );
	}
}
