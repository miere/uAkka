package com.texoit.uakka.service.factories;

import java.lang.reflect.Field;

import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

public class DefaultAkkaConfigurationClassFactory 
	extends AbstractInjectableClassFactory<AkkaConfiguration> {

	@Override
	public Object newInstance(Object instance, Field injectableField)
			throws InjectionException {
		return getConfiguration();
	}

}
