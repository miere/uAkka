package io.skullabs.uakka.inject;

import io.skullabs.uakka.inject.ActorInfo.CreationInfo;
import io.skullabs.uakka.inject.ActorInfo.SearchInfo;

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
public class InjectableAkkaActors {

	final Map<String, InjectableClass<?>> actors = new HashMap<String, InjectableClass<?>>();
	final Map<String, ActorRef> actorReferences = new HashMap<String, ActorRef>();
	final Injectables injectables;
	final ActorSystem actorSystem;

	public void analise( Collection<Class<?>> classes ) throws InjectionException {
		for ( Class<?> clazz : classes )
			if ( isActorClass( clazz ) )
				memorizeInjectableActor( clazz );
	}

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

	public void memorizeInjectableActor( Class<?> clazz ) {
		this.actors.put(
				clazz.getCanonicalName(),
				InjectableClass.newInstance(
						this.injectables, clazz ) );
	}

	private void tryInitializeActor( Class<? extends Actor> clazz ) throws InjectionException {
		Service service = clazz.getAnnotation( Service.class );
		if ( service != null )
			actor( new CreationInfo( clazz ) );
	}

	public ActorRef actor( CreationInfo creationInfo ) {
		String name = creationInfo.getName();
		ActorRef actorRef = this.actorReferences.get( name );
		if ( actorRef == null ) {
			actorRef = actor( this.actorSystem, creationInfo );
			this.actorReferences.put( name, actorRef );
		}
		return actorRef;
	}

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

	public ActorRef actor( SearchInfo searchInfo ) {
		return this.actorReferences.get( searchInfo.getPath() );
	}

	public ActorSelection actor( ActorRefFactory actorRefFactory, SearchInfo searchInfo ) {
		return actorRefFactory.actorSelection( searchInfo.getPath() );
	}

	// improve to a graceful shutdown before shutdown actor system.
	public void shutdown() {
		FiniteDuration duration = Duration.create( 30, TimeUnit.SECONDS );

		// ask( actorSystem.actorSelection("*"), new PoisonPill() {}, 0 );
		// actorSystem.actorSelection("/user/*") ;
		// ! PoisonPill
		this.actorSystem.shutdown();
		this.actorSystem.awaitTermination( duration );
	}
}
