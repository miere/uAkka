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
import javax.servlet.annotation.WebListener;

import akka.actor.Actor;

@WebListener
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
			servletContext.log( "Embedded Akka Servlet Integration actived!" );
			InjectionConfiguration injectionConfiguration = createInjectionConfiguration( servletContext );
			InjectableAkkaInitialization akkaInitialization = new InjectableAkkaInitialization( classes, injectionConfiguration );
			akkaInitialization.initialize();
			servletContext.log( "Embedded Akka Servlet Integration initialized!" );
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
		servletContext.log( "Shutting down Embedded Akka Servlet Integration!" );
		InjectionConfiguration injectionConfiguration = new ServletInjectionConfiguration( servletContext );
		InjectableAkkaInitialization akkaInitialization = getInjectableAkkaInitialization( injectionConfiguration );
		akkaInitialization.shutdown();
		injectionConfiguration.setInjectableAkkaActors( null );
	}

	private InjectableAkkaInitialization getInjectableAkkaInitialization( InjectionConfiguration injectionConfiguration ) {
		InjectableAkkaActors injectableAkkaActors = injectionConfiguration.getInjectableAkkaActors();
		return new InjectableAkkaInitialization( injectableAkkaActors );
	}

	@Override
	public void contextInitialized( ServletContextEvent sce ) {
	}
}
