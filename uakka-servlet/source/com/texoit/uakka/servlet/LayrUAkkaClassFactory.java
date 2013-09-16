package com.texoit.uakka.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.texoit.uakka.api.Injectable;
import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.inject.HandledInjectableClass;

import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.ClassFactoryException;

@Handler
public class LayrUAkkaClassFactory implements ClassFactory<Injectable> {

	Map<String, Object> singletons = new ConcurrentHashMap<String, Object>();
	Injectables injectables;

	@Override
	public Object newInstance(
			ApplicationContext applicationContext,
			RequestContext requestContext,
			Class<?> clazz ) throws ClassFactoryException {
		try {
			return newInstance( applicationContext, clazz );
		} catch ( InjectionException e ) {
			throw new ClassFactoryException( "Can't create @Injectable route", e );
		}
	}

	Object newInstance( ApplicationContext applicationContext, Class<?> clazz ) throws InjectionException {
		boolean singleton = isSingleton( clazz );

		Object object = !singleton
				? initialize( applicationContext, clazz )
				: getFromCacheOrInitialize( applicationContext, clazz );

		return object;
	}

	Object getFromCacheOrInitialize( ApplicationContext applicationContext, Class<?> clazz ) throws InjectionException {
		synchronized ( this.singletons ) {
			Object object = this.singletons.get( clazz.getCanonicalName() );

			if ( object == null ) {
				object = initialize( applicationContext, clazz );
				this.singletons.put( clazz.getCanonicalName(), object );
			}

			return object;
		}
	}

	Object initialize( ApplicationContext applicationContext, Class<?> clazz ) throws InjectionException {
		Injectables injectables = getInjectables( applicationContext );
		HandledInjectableClass handledInjectableClass = HandledInjectableClass.newInstance( injectables, clazz );
		return handledInjectableClass.newInstance();
	}

	Injectables getInjectables( ApplicationContext applicationContext ) {
		if ( this.injectables == null )
			this.injectables = (Injectables)applicationContext.getAttribute( Injectables.class.getCanonicalName() );
		return this.injectables;
	}

	boolean isSingleton( Class<?> clazz ) {
		Injectable injectableAnnotation = clazz.getAnnotation( Injectable.class );
		return injectableAnnotation.singleton();
	}
}
