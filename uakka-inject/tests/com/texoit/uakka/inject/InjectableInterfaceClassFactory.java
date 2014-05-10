package com.texoit.uakka.inject;

import java.lang.reflect.Field;

import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

public class InjectableInterfaceClassFactory extends AbstractInjectableClassFactory<InjectableInterface> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) {
		return new DefaultInjectableInterface();
	}

	@Override
	public void initialize(
			AkkaConfiguration configuration ) {
	}

	public static class DefaultInjectableInterface implements InjectableInterface {
		public static final String HELLO_WORLD = "Hello World";

		@Override
		public String getHelloWorld() {
			return HELLO_WORLD;
		}
	}
}
