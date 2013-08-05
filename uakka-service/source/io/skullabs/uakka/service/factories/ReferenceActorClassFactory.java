package io.skullabs.uakka.service.factories;

import static io.skullabs.uakka.service.factories.Assert.assertFieldTypeIsActorSelection;
import static io.skullabs.uakka.service.factories.Assert.assertInstanceOfActor;
import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.api.Reference;
import io.skullabs.uakka.inject.AbstractInjectableClassFactory;
import io.skullabs.uakka.service.actor.WrappedActorSelection;

import java.lang.reflect.Field;

import akka.actor.Actor;

public class ReferenceActorClassFactory extends AbstractInjectableClassFactory<Reference> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertFieldTypeIsActorSelection( injectableField );
		assertInstanceOfActor( instance );
		return newInstance( (Actor)instance, injectableField.getAnnotation( Reference.class ) );
	}

	private Object newInstance( Actor instance, Reference reference ) throws InjectionException {
		return new WrappedActorSelection(
				getAkkaActors(),
				instance.context(),
				new SearchInfo( reference ) );
	}
}
