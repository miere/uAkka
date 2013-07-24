package io.skullabs.uakka.inject;

import io.skullabs.uakka.inject.ActorInfo.CreationInfo;
import io.skullabs.uakka.inject.ActorInfo.SearchInfo;

import java.lang.reflect.Field;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;

public class ReferenceActorClassFactory extends AbstractInjectableClassFactory<Reference> {

	InjectableAkkaActors injectableAkkaActors;

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		ActorRefFactory actorRefFactory = ( (Actor)instance ).context();
		return newInstance( injectableField, actorRefFactory );
	}

	private Object newInstance( Field injectableField, ActorRefFactory actorRefFactory ) throws InjectionException {
		Reference reference = injectableField.getAnnotation( Reference.class );
		Class<?> fieldType = injectableField.getType();

		if ( ActorRef.class.isAssignableFrom( fieldType ) )
			return getActorReference( fieldType, reference );
		else if ( ActorSelection.class.isAssignableFrom( fieldType ) )
			return getInjectableAkkaActors().actor( actorRefFactory, new SearchInfo( reference ) );

		throw new InjectionException( "Invalid assignment: type " + fieldType );
	}

	private ActorRef getActorReference( Class<?> fieldType, Reference reference ) throws InjectionException {
		SearchInfo searchInfo = new SearchInfo( reference );
		ActorRef actorRef = getInjectableAkkaActors().actor( searchInfo );

		if ( actorDependencyNotPreInitialized( reference, searchInfo, actorRef ) )
			actorRef = getInjectableAkkaActors().actor( new CreationInfo( reference.actor() ) );

		return actorRef;
	}

	private boolean actorDependencyNotPreInitialized( Reference reference, SearchInfo searchInfo, ActorRef actorRef ) {
		if ( actorRef != null )
			return false;

		Class<? extends Actor> actor = reference.actor();
		Service service = actor.getAnnotation( Service.class );
		return searchInfo.getPath().equals( actor.getCanonicalName() ) && service != null
				|| isReferenceToService( reference, service );
	}

	private boolean isReferenceToService( Reference reference, Service service ) {
		return isEmpty( service.value() ) && isEmpty( reference.path() )
				|| !isEmpty( service.value() ) && !isEmpty( reference.path() ) && reference.path().equals( service.value() );
	}

	private boolean isEmpty( String str ) {
		return str == null || str.isEmpty();
	}

	public InjectableAkkaActors getInjectableAkkaActors() {
		if ( this.injectableAkkaActors == null )
			this.injectableAkkaActors = (InjectableAkkaActors)this.configuration.getAttribute(
					InjectableAkkaActors.class.getCanonicalName() );
		return this.injectableAkkaActors;
	}
}
