package com.texoit.uakka.api;

import java.lang.reflect.Field;

import com.texoit.uakka.api.exception.InjectionException;

public interface InjectableClassFactory<T> {

	/**
	 * Initialize the class factory. By default it memorizes the injection
	 * configuration for future use.
	 * 
	 * @param configuration
	 */
	public abstract void initialize( AkkaConfiguration configuration );

	public abstract Object newInstance( Object instance, Field injectableField ) throws InjectionException;

	public abstract Class<?> getGenericClass();

}