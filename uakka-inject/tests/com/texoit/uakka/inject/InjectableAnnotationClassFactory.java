package com.texoit.uakka.inject;

import java.lang.reflect.Field;

import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

public class InjectableAnnotationClassFactory extends AbstractInjectableClassFactory<InjectableAnnotation> {

	public static final String HELLO_WORLD = "Hello World";

	@Override
	public Object newInstance( Object instance, Field injectableField ) {
		return HELLO_WORLD;
	}

	@Override
	public void initialize(
			AkkaConfiguration configuration ) {
	}
}
