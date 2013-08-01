package io.skullabs.uakka.inject;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;
import io.skullabs.uakka.api.InjectableClassFactory;
import io.skullabs.uakka.api.InjectionException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import lombok.Getter;

public abstract class AbstractInjectableClassFactory<T> implements InjectableClassFactory<T> {

	@Getter
	AkkaConfiguration configuration;
	AkkaActors akkaActors;

	@Override
	public void initialize( AkkaConfiguration configuration ) {
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

	@Override
	@SuppressWarnings( "unchecked" )
	public Class<T> getGenericClass() {
		return (Class<T>)( (ParameterizedType)getClass().getGenericSuperclass() )
				.getActualTypeArguments()[0];
	}

	public AkkaActors getAkkaActors() {
		if ( this.akkaActors == null )
			this.akkaActors = this.getConfiguration().getAkkaActors();
		return this.akkaActors;
	}
}