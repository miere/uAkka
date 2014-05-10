package com.texoit.uakka.inject;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.api.InjectableClassFactory;
import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InjectableDiscoveryService {

	Map<String, InjectableClassFactory<?>> classFactories = new HashMap<String, InjectableClassFactory<?>>();
	final AkkaConfiguration configuration;

	public Injectables discovery( Collection<Class<?>> classes ) throws InjectionException {
		analize( classes );
		return new DefaultInjectables( this.classFactories );
	}

	public void analize( Collection<Class<?>> classes ) throws InjectionException {
		for ( Class<?> clazz : classes )
			if ( isInjectableClassFactory( clazz ) )
				memorizeFactory( clazz );
	}

	private boolean isInjectableClassFactory( Class<?> clazz ) {
		return InjectableClassFactory.class.isAssignableFrom( clazz )
				&& !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() );
	}

	private void memorizeFactory( Class<?> clazz ) throws InjectionException {
		try {
			InjectableClassFactory<?> factory = (InjectableClassFactory<?>)clazz.newInstance();
			factory.initialize( this.configuration );
			this.classFactories.put( factory.getGenericClass().getCanonicalName(), factory );
		} catch ( Exception e ) {
			throw new InjectionException( "Can't memorize class factory " + clazz.getCanonicalName(), e );
		}
	}
}
