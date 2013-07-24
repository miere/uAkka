package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableAkkaActors;
import io.skullabs.uakka.inject.InjectableAkkaInitialization;
import io.skullabs.uakka.inject.InjectableClassFactory;
import io.skullabs.uakka.inject.InjectionConfiguration;
import io.skullabs.uakka.inject.InjectionException;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import akka.actor.Actor;

@HandlesTypes( {
		Actor.class,
		InjectableClassFactory.class
} )
public class ServletAkkaInitialization
		implements ServletContainerInitializer, ServletContextListener {

	@Override
	public void onStartup(
			Set<Class<?>> classes,
			ServletContext servletContext ) throws ServletException {
		try {
			createInjectableAkkaInitialization( classes, servletContext );
		} catch ( InjectionException e ) {
			throw new ServletException( e );
		}
	}

	ServletInjectionConfiguration createInjectionConfiguration( ServletContext servletContext ) {
		return new ServletInjectionConfiguration( servletContext );
	}

	@Override
	public void contextDestroyed( ServletContextEvent sce ) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.log( "Shutting down Akka Servlet Integration!" );
		InjectionConfiguration injectionConfiguration = new ServletInjectionConfiguration( servletContext );
		InjectableAkkaInitialization akkaInitialization = getInjectableAkkaInitialization( injectionConfiguration );
		akkaInitialization.shutdown();
		injectionConfiguration.setInjectableAkkaActors( null );
	}

	@Override
	public void contextInitialized( ServletContextEvent sce ) {
		try {
			ServletContext servletContext = sce.getServletContext();
			InjectableAkkaInitialization akkaInitialization = getInjectableAkkaInitialization( servletContext );
			akkaInitialization.initialize();
			servletContext.log( "Akka Servlet Integration initialized!" );
		} catch ( InjectionException e ) {
			throw new RuntimeException( "Could not initialize actors", e );
		}
	}

	public InjectableAkkaInitialization getInjectableAkkaInitialization( ServletContext servletContext ) {
		ServletInjectionConfiguration injectionConfiguration = new ServletInjectionConfiguration( servletContext );
		return getInjectableAkkaInitialization( injectionConfiguration );
	}

	private InjectableAkkaInitialization getInjectableAkkaInitialization( InjectionConfiguration injectionConfiguration ) {
		InjectableAkkaActors injectableAkkaActors = injectionConfiguration.getInjectableAkkaActors();
		return new InjectableAkkaInitialization( injectableAkkaActors );
	}

	public void createInjectableAkkaInitialization( Set<Class<?>> classes, ServletContext servletContext ) throws InjectionException {
		InjectionConfiguration injectionConfiguration = createInjectionConfiguration( servletContext );
		new InjectableAkkaInitialization( classes, injectionConfiguration );
	}
}
