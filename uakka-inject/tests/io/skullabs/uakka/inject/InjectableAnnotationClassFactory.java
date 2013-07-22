package io.skullabs.uakka.inject;

import java.lang.reflect.Field;

public class InjectableAnnotationClassFactory extends InjectableClassFactory<InjectableAnnotation> {

	public static final String HELLO_WORLD = "Hello World";

	@Override
	public Object newInstance( Object instance, Field injectableField ) {
		return HELLO_WORLD;
	}

	@Override
	public void initialize(
			InjectionConfiguration configuration ) {
	}
}
