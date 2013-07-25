package io.skullabs.uakka.service;

import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.InjectionException;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.inject.InjectableClass;
import io.skullabs.uakka.inject.Injectables;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

@RequiredArgsConstructor
public class DefaultAkkaActors implements AkkaActors {

	final Map<String, InjectableClass<?>> actors = new HashMap<String, InjectableClass<?>>();
	final Map<String, ActorRef> actorReferences = new HashMap<String, ActorRef>();
	final Injectables injectables;
	final ActorSystem actorSystem;

	public void analyze( Collection<Class<?>> classes ) throws InjectionException {
		for ( Class<?> clazz : classes )
			analyzeClass( clazz );
	}

	private void analyzeClass( Class<?> clazz ) {
		if ( isActorClass( clazz ) )
			memorizeInjectableActor( clazz );
	}

	public void memorizeInjectableActor( Class<?> clazz ) {
		this.actors.put(
				clazz.getCanonicalName(),
				InjectableClass.newInstance(
						this.injectables, clazz ) );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void initialize() throws InjectionException {
		Collection<InjectableClass<?>> classes = this.actors.values();
		for ( InjectableClass<?> injectableClazz : classes ) {
			Class<?> clazz = injectableClazz.getTargetClass();
			if ( isActorClass( clazz ) )
				tryInitializeActor( (Class<? extends Actor>)clazz );
		}
	}

	private boolean isActorClass( Class<?> clazz ) {
		int modifiers = clazz.getModifiers();
		return !Modifier.isAbstract( modifiers )
				&& !Modifier.isInterface( modifiers )
				&& Modifier.isPublic( modifiers )
				&& Actor.class.isAssignableFrom( clazz )
				&& !clazz.getPackage().getName().startsWith( "akka." )
				&& !clazz.getPackage().getName().equals( "akka" );
	}

	private void tryInitializeActor( Class<? extends Actor> clazz ) throws InjectionException {
		Service service = clazz.getAnnotation( Service.class );
		if ( service != null )
			actor( new CreationInfo( clazz ) );
	}

	@Override
	public ActorRef actor( CreationInfo creationInfo ) {
		String name = creationInfo.getName();
		ActorRef actorRef = this.actorReferences.get( name );
		if ( actorRef == null ) {
			actorRef = actor( this.actorSystem, creationInfo );
			this.actorReferences.put( name, actorRef );
		}
		return actorRef;
	}

	@Override
	public ActorRef actor( ActorRefFactory actorRefFactory, CreationInfo creationInfo ) {
		return newInstance( actorRefFactory, creationInfo.getTargetClass(), creationInfo.getName() );
	}

	private ActorRef newInstance( ActorRefFactory actorRefFactory, Class<? extends Actor> clazz, String name ) {
		return actorRefFactory.actorOf( createActorProps( clazz.getCanonicalName() ), name );
	}

	private Props createActorProps( String canonicalName ) {
		InjectableClass<?> injectableClass = this.actors.get( canonicalName );
		Creator<Actor> creator = new DefaultActorCreator( injectableClass );
		return Props.create( creator );
	}

	@Override
	public ActorRef actor( SearchInfo searchInfo ) {
		return this.actorReferences.get( searchInfo.getPath() );
	}

	@Override
	public ActorSelection actor( ActorRefFactory actorRefFactory, SearchInfo searchInfo ) {
		return actorRefFactory.actorSelection( searchInfo.getPath() );
	}

	// TODO: improve to a graceful shutdown before shutdown actor system.
	@Override
	public void shutdown() {
		FiniteDuration duration = Duration.create( 30, TimeUnit.SECONDS );
		this.actorSystem.shutdown();
		this.actorSystem.awaitTermination( duration );
	}
}
