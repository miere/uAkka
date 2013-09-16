package io.skullabs.uakka.service;

import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.Injectables;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.HandledInjectableClass;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

@Getter
@RequiredArgsConstructor
@Log
public class DefaultAkkaActors implements AkkaActors {

	final Map<String, HandledInjectableClass> actors = new HashMap<String, HandledInjectableClass>();
	final Map<String, ActorRef> actorReferences = new HashMap<String, ActorRef>();
	final Injectables injectables;
	final ActorSystem actorSystem;

	@Override
	public void analyze( Collection<Class<?>> classes ) throws InjectionException {
		for ( Class<?> clazz : classes )
			analyzeClass( clazz );
	}

	@SuppressWarnings( "unchecked" )
	private void analyzeClass( Class<?> clazz ) {
		if ( isActorClass( clazz ) )
			memorizeInjectableActor( (Class<? extends Actor>)clazz );
	}

	public void memorizeInjectableActor( Class<? extends Actor> clazz ) {
		this.actors.put(
				clazz.getCanonicalName(),
				HandledInjectableClass.newInstance(
						this.injectables, clazz ) );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void initialize() throws InjectionException {
		Collection<HandledInjectableClass> classes = this.actors.values();
		for ( HandledInjectableClass injectableClazz : classes ) {
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
		log.info( String.format( "Creating actor %s", creationInfo.getTargetClass() ) );
		return actor( this.actorSystem, creationInfo );
	}

	@Override
	public ActorRef actor( ActorRefFactory actorRefFactory, CreationInfo creationInfo ) {
		return actor( actorRefFactory, creationInfo.getTargetClass(), creationInfo.getName() );
	}

	private ActorRef actor( ActorRefFactory actorRefFactory, Class<? extends Actor> clazz, String name ) {
		ActorRef actorRef = this.actorReferences.get( name );
		if ( actorRef == null ) {
			actorRef = actorRefFactory.actorOf( createActorProps( clazz.getCanonicalName() ), name );
			this.actorReferences.put( name, actorRef );
		}
		return actorRef;
	}

	private Props createActorProps( String canonicalName ) {
		HandledInjectableClass injectableClass = this.actors.get( canonicalName );
		Creator<Actor> creator = new DefaultActorCreator( injectableClass );
		return Props.create( creator );
	}

	@Override
	public ActorRef actor( SearchInfo searchInfo ) {
		return this.actorReferences.get( searchInfo.getPath() );
	}

	@Override
	public ActorSelection actor( ActorRefFactory actorRefFactory, SearchInfo searchInfo ) {
		ActorSelection actorSelection = actorRefFactory.actorSelection( searchInfo.getPath() );
		return actorSelection;
	}

	// TODO: improve to a graceful shutdown before shutdown actor system.
	@Override
	public void shutdown() {
		FiniteDuration duration = Duration.create( 30, TimeUnit.SECONDS );
		this.actorSystem.shutdown();
		this.actorSystem.awaitTermination( duration );
	}
}
