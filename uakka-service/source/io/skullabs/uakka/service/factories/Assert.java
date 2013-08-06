package io.skullabs.uakka.service.factories;

import static io.skullabs.uakka.commons.Commons.str;
import io.skullabs.uakka.api.exception.InjectionException;

import java.lang.reflect.Field;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

class Assert {

	static void assertFieldTypeIsActorRef( Field injectableField ) throws InjectionException {
		Class<?> fieldType = injectableField.getType();
		if ( !ActorRef.class.isAssignableFrom( fieldType ) )
			throw new InjectionException(
					str( "Invalid assignment to type '%s'. Expected: %s",
							fieldType, ActorRef.class.getCanonicalName() ) );
	}

	static void assertFieldTypeIsActorSelection( Field injectableField ) throws InjectionException {
		Class<?> fieldType = injectableField.getType();
		if ( !ActorSelection.class.isAssignableFrom( fieldType ) )
			throw new InjectionException(
					str( "Invalid assignment to type '%s'. Expected: %s",
							fieldType, ActorSelection.class.getCanonicalName() ) );
	}
}
