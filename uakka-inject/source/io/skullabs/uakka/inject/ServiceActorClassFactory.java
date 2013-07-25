package io.skullabs.uakka.inject;

import io.skullabs.uakka.inject.ActorInfo.CreationInfo;

import java.lang.reflect.Field;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;

public class ServiceActorClassFactory extends AbstractInjectableClassFactory<Service> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertFieldTypeIsActorRef( injectableField );
		assertInstanceOfActor( instance );
		return newInstance( (Actor)instance, injectableField.getAnnotation( Service.class ) );
	}

	private void assertFieldTypeIsActorRef( Field injectableField ) throws InjectionException {
		Class<?> fieldType = injectableField.getType();
		if ( !ActorRef.class.isAssignableFrom( fieldType ) )
			throw new InjectionException( "Invalid assignment: type " + fieldType );
	}

	private void assertInstanceOfActor( Object instance ) throws InjectionException {
		Class<?> instanceType = instance.getClass();
		if ( !Actor.class.isAssignableFrom( instanceType ) )
			throw new InjectionException( "@Service injection is allowed only for Actor instances. Can't inject into "
					+ instanceType.getCanonicalName() );
	}

	private Object newInstance( Actor instance, Service serviceAnnotation ) throws InjectionException {
		ActorContext context = instance.context();
		InjectableAkkaActors injectableAkkaActors = getConfiguration().getInjectableAkkaActors();
		return injectableAkkaActors.actor( context, new CreationInfo( serviceAnnotation ) );
	}

}
