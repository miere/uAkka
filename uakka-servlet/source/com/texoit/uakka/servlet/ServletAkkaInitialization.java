package com.texoit.uakka.servlet;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.annotation.WebListener;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.api.InjectableClassFactory;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.service.AkkaInitialization;

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
			AkkaConfiguration injectionConfiguration = createInjectionConfiguration( servletContext );
			AkkaInitialization akkaInitialization = new AkkaInitialization( classes, injectionConfiguration );
			akkaInitialization.initialize();
			servletContext.log( "Embedded Akka Servlet Integration initialized!" );
		} catch ( InjectionException e ) {
			throw new ServletException( e );
		}
	}

	ServletAkkaConfiguration createInjectionConfiguration( ServletContext servletContext ) {
		return new ServletAkkaConfiguration( servletContext );
	}

	@Override
	public void contextDestroyed( ServletContextEvent sce ) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.log( "Shutting down Embedded Akka Servlet Integration!" );
		AkkaConfiguration injectionConfiguration = new ServletAkkaConfiguration( servletContext );
		AkkaInitialization akkaInitialization = getInjectableAkkaInitialization( injectionConfiguration );
		akkaInitialization.shutdown();
		injectionConfiguration.setAkkaActors( null );
	}

	private AkkaInitialization getInjectableAkkaInitialization( AkkaConfiguration injectionConfiguration ) {
		AkkaActors injectableAkkaActors = injectionConfiguration.getAkkaActors();
		return new AkkaInitialization( injectableAkkaActors );
	}

	@Override
	public void contextInitialized( ServletContextEvent sce ) {
	}
}
