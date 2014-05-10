package com.texoit.uakka.service.factories;

import java.lang.reflect.Field;

import akka.actor.ActorSystem;

import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

public class DefaultActorSystemClassFactory 
	extends AbstractInjectableClassFactory<ActorSystem> {

	@Override
	public Object newInstance(Object instance, Field injectableField)
			throws InjectionException {
		return getConfiguration().getAkkaActors().getActorSystem();
	}

}
