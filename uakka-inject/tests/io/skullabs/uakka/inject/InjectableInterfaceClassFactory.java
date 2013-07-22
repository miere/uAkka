package io.skullabs.uakka.inject;

import java.lang.reflect.Field;

public class InjectableInterfaceClassFactory extends InjectableClassFactory<InjectableInterface> {

	@Override
	public Object newInstance( Object instance, Field injectableField ) {
		return new DefaultInjectableInterface();
	}

	@Override
	public void initialize(
			InjectionConfiguration configuration ) {
	}

	public static class DefaultInjectableInterface implements InjectableInterface {
		public static final String HELLO_WORLD = "Hello World";

		@Override
		public String getHelloWorld() {
			return HELLO_WORLD;
		}
	}
}
