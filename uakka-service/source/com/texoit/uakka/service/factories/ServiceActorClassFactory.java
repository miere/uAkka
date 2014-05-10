package com.texoit.uakka.service.factories;

import static com.texoit.uakka.commons.Commons.str;
import static com.texoit.uakka.service.factories.Assert.assertFieldTypeIsActorRef;

import java.lang.reflect.Field;

import com.texoit.uakka.api.Service;
import com.texoit.uakka.api.ActorInfo.CreationInfo;
import com.texoit.uakka.api.actor.ActorRef;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

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
