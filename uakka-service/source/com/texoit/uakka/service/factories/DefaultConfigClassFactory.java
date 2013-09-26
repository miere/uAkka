package com.texoit.uakka.service.factories;

import java.lang.reflect.Field;

import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;
import com.typesafe.config.Config;

public class DefaultConfigClassFactory 
	extends AbstractInjectableClassFactory<Config> {

	@Override
	public Object newInstance(Object instance, Field injectableField)
			throws InjectionException {
		return getConfiguration().getConfig();
	}

}
