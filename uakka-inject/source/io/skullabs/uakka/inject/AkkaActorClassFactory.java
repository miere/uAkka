package io.skullabs.uakka.inject;

import java.lang.reflect.Field;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;

public class AkkaActorClassFactory extends InjectableClassFactory<Service> {

	InjectableAkkaActors injectableAkkaActors;

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertIsActor( injectableField );
		ActorRefFactory actorRefFactory = ( (Actor)instance ).context();
		Service serviceAnnotation = injectableField.getAnnotation( Service.class );
		getInjectableAkkaActors().newInstance( actorRefFactory, serviceAnnotation );
		return null;
	}

	private void assertIsActor( Field field ) throws InjectionException {
		Class<?> fieldType = field.getType();
		if ( !isValidActorReferenceClass( fieldType ) ) {
			String errorMsg = String.format(
					"Field %s type is invalid: %s", field.getName(), fieldType );
			throw new InjectionException( errorMsg );
		}
	}

	private boolean isValidActorReferenceClass( Class<?> fieldType ) {
		return ActorRef.class.isAssignableFrom( fieldType )
				|| ActorSelection.class.isAssignableFrom( fieldType );
	}

	public InjectableAkkaActors getInjectableAkkaActors() {
		if ( this.injectableAkkaActors == null )
			this.injectableAkkaActors = (InjectableAkkaActors)this.configuration.getAttribute(
					InjectableAkkaActors.class.getCanonicalName() );
		return this.injectableAkkaActors;
	}
}
