package io.skullabs.uakka.service.factories;

import static io.skullabs.uakka.commons.Commons.str;
import static io.skullabs.uakka.service.factories.Assert.assertFieldTypeIsActorRef;
import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.api.actor.ActorRef;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.AbstractInjectableClassFactory;

import java.lang.reflect.Field;

import lombok.extern.java.Log;
import akka.actor.Actor;
import akka.actor.ActorRefFactory;

@Log
public class ServiceActorClassFactory extends AbstractInjectableClassFactory<Service> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertFieldTypeIsActorRef( injectableField );
		Service service = injectableField.getAnnotation( Service.class );
		if ( instance instanceof Actor )
			return newInstance( (Actor)instance, service );
		else
			return newInstance( instance, service );
	}

	private Object newInstance( Actor instance, Service serviceAnnotation ) throws InjectionException {
		log.info( str( "Creating new service from (Actor)%s", instance ) );
		return newInstance( serviceAnnotation, instance.context() );
	}

	private Object newInstance( Object instance, Service serviceAnnotation ) throws InjectionException {
		log.info( str( "Creating new service from (Object)%s", instance ) );
		return newInstance( serviceAnnotation, getAkkaActors().getActorSystem() );
	}

	private Object newInstance( Service serviceAnnotation, ActorRefFactory actorRefFactory )
			throws InjectionException {
		return new ActorRef(
				getAkkaActors(),
				actorRefFactory,
				new CreationInfo( serviceAnnotation ) );
	}
}
