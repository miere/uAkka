package io.skullabs.uakka.inject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import lombok.Getter;

public abstract class AbstractInjectableClassFactory<T> implements InjectableClassFactory<T> {

	@Getter
	InjectionConfiguration configuration;

	@Override
	public void initialize( InjectionConfiguration configuration ) {
		this.configuration = configuration;
		initialize();
	}

	/**
	 * Useful method which developers could override to handle the class factory
	 * initialization.
	 */
	public void initialize() {
	}

	@Override
	public abstract Object newInstance( Object instance, Field injectableField ) throws InjectionException;

	@SuppressWarnings( "unchecked" )
	public Class<T> getGenericClass() {
		return (Class<T>)( (ParameterizedType)getClass().getGenericSuperclass() )
				.getActualTypeArguments()[0];
	}
}