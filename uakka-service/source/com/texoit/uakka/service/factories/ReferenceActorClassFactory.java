package com.texoit.uakka.service.factories;

import static com.texoit.uakka.commons.Commons.str;
import static com.texoit.uakka.service.factories.Assert.assertFieldTypeIsActorSelection;

import java.lang.reflect.Field;

import com.texoit.uakka.api.Reference;
import com.texoit.uakka.api.ActorInfo.SearchInfo;
import com.texoit.uakka.api.actor.ActorSelection;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

import lombok.extern.java.Log;
import akka.actor.Actor;
import akka.actor.ActorRefFactory;

@Log
public class ReferenceActorClassFactory extends AbstractInjectableClassFactory<Reference> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		assertFieldTypeIsActorSelection( injectableField );
		Reference reference = injectableField.getAnnotation( Reference.class );
		if ( instance instanceof Actor )
			return newInstance( (Actor)instance, reference );
		else
			return newInstance( instance, reference );
	}

	private Object newInstance( Actor instance, Reference reference ) throws InjectionException {
		log.info( str( "Creating new reference from (Actor)%s", instance ) );
		return newInstance( reference, instance.context() );
	}

	private Object newInstance( Object instance, Reference reference ) {
		log.info( str( "Creating new reference from (Object)%s", instance ) );
		return newInstance( reference, getAkkaActors().getActorSystem() );
	}

	private Object newInstance( Reference reference, ActorRefFactory actorRefFactory ) {
		return new ActorSelection(
				getAkkaActors(),
				actorRefFactory,
				new SearchInfo( reference ) );
	}
}
