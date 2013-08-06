package io.skullabs.uakka.api;

import java.lang.reflect.Field;

public interface Injectables {

	public abstract InjectableClassFactory<?> getClassFactory( Field field );

	public abstract InjectableClassFactory<?> getClassFactory( String canonicalName );

}