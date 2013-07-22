package io.skullabs.uakka.inject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class InjectableClassFactory<T> {

	public abstract void initialize( InjectionConfiguration configuration );

	public abstract Object newInstance( Object instance, Field injectableField );

	@SuppressWarnings( "unchecked" )
	public Class<T> getGenericClass() {
		return (Class<T>)( (ParameterizedType)getClass().getGenericSuperclass() )
				.getActualTypeArguments()[0];
	}
}