package io.skullabs.uakka.inject;

import io.skullabs.uakka.api.AkkaConfiguration;

import java.lang.reflect.Field;

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
