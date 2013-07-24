package io.skullabs.uakka.inject;

import java.lang.reflect.Field;

public interface InjectableClassFactory<T> {

	/**
	 * Initialize the class factory. By default it memorizes the injection
	 * configuration for future use.
	 * 
	 * @param configuration
	 */
	public abstract void initialize( InjectionConfiguration configuration );

	public abstract Object newInstance( Object instance, Field injectableField ) throws InjectionException;

	public abstract Class<?> getGenericClass();

}