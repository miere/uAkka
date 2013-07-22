package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableAkkaInitialization;
import io.skullabs.uakka.inject.InjectableClassFactory;
import io.skullabs.uakka.inject.InjectionConfiguration;
import io.skullabs.uakka.inject.InjectionException;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import akka.actor.Actor;
import akka.actor.UntypedActor;

@HandlesTypes( {
		Actor.class,
		UntypedActor.class,
		InjectableClassFactory.class
} )
public class ServletAkkaInitialization extends InjectableAkkaInitialization implements ServletContainerInitializer {

	@Override
	public void onStartup(
			Set<Class<?>> classes,
			ServletContext servletContext ) throws ServletException {
		try {
			InjectionConfiguration injectionConfiguration = createInjectionConfiguration( servletContext );
			initialize( classes, injectionConfiguration );
		} catch ( InjectionException e ) {
			throw new ServletException( e );
		}
	}

	ServletInjectionConfiguration createInjectionConfiguration( ServletContext servletContext ) {
		return new ServletInjectionConfiguration( servletContext );
	}
}
