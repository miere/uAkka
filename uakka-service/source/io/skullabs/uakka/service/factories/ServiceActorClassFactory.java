package io.skullabs.uakka.service.factories;

import static io.skullabs.uakka.service.factories.Assert.assertFieldTypeIsActorRef;
import static io.skullabs.uakka.service.factories.Assert.assertInstanceOfActor;
import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.InjectionException;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.inject.AbstractInjectableClassFactory;
import io.skullabs.uakka.service.actor.WrappedActorRef;

import java.lang.reflect.Field;

import akka.actor.Actor;

public class ServiceActorClassFactory extends AbstractInjectableClassFactory<Service> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertFieldTypeIsActorRef( injectableField );
		assertInstanceOfActor( instance );
		return newInstance( (Actor)instance, injectableField.getAnnotation( Service.class ) );
	}

	private Object newInstance( Actor instance, Service serviceAnnotation ) throws InjectionException {
		CreationInfo creationInfo = new CreationInfo( serviceAnnotation );
		WrappedActorRef actorRef = new WrappedActorRef(
				getAkkaActors(),
				instance.context(),
				creationInfo );
		return actorRef;
	}
}
