package com.texoit.uakka.service;

import java.lang.reflect.Field;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

public class DefaultAkkaActorsClassFactory 
	extends AbstractInjectableClassFactory<AkkaActors> {

	@Override
	public Object newInstance(Object instance, Field injectableField)
			throws InjectionException {
		return getConfiguration().getAkkaActors();
	}

}
