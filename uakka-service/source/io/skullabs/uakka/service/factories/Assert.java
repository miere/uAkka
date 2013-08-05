package io.skullabs.uakka.service.factories;

import io.skullabs.uakka.api.exception.InjectionException;

import java.lang.reflect.Field;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;

class Assert {

	static void assertFieldTypeIsActorRef( Field injectableField ) throws InjectionException {
		Class<?> fieldType = injectableField.getType();
		if ( !ActorRef.class.isAssignableFrom( fieldType ) )
			throw new InjectionException( "Invalid assignment: type " + fieldType );
	}

	static void assertInstanceOfActor( Object instance ) throws InjectionException {
		Class<?> instanceType = instance.getClass();
		if ( !Actor.class.isAssignableFrom( instanceType ) )
			throw new InjectionException( "Injection is allowed only for Actor instances. Can't inject into "
					+ instanceType.getCanonicalName() );
	}

	static void assertFieldTypeIsActorSelection( Field injectableField ) throws InjectionException {
		Class<?> fieldType = injectableField.getType();
		if ( !ActorSelection.class.isAssignableFrom( fieldType ) )
			throw new InjectionException( "Invalid assignment: type " + fieldType );
	}
}
