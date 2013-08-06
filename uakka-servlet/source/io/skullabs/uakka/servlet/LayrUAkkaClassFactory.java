package io.skullabs.uakka.servlet;

import io.skullabs.uakka.api.Injectable;
import io.skullabs.uakka.api.Injectables;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.HandledInjectableClass;
import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.ClassFactoryException;

@Handler
public class LayrUAkkaClassFactory implements ClassFactory<Injectable> {

	@Override
	public Object newInstance(
			ApplicationContext applicationContext,
			RequestContext requestContext,
			Class<?> clazz ) throws ClassFactoryException
	{
		try {
			Injectables injectables = (Injectables)applicationContext.getAttribute( Injectables.class.getCanonicalName() );
			HandledInjectableClass handledInjectableClass = HandledInjectableClass.newInstance( injectables, clazz );
			return handledInjectableClass.newInstance();
		} catch ( InjectionException e ) {
			throw new ClassFactoryException( "Can't create @Injectable route", e );
		}
	}
}
