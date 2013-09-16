package com.texoit.uakka.jpa;

import static com.texoit.uakka.commons.Commons.str;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.servlet.ServletAkkaConfiguration;

import lombok.extern.java.Log;

@WebListener
@Log
public class EntityManagerFactoryCloser implements ServletContextListener {

	@Override
	public void contextInitialized( ServletContextEvent sce ) {
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void contextDestroyed( ServletContextEvent sce ) {
		ServletContext servletContext = sce.getServletContext();
		AkkaConfiguration akkaConfiguration = new ServletAkkaConfiguration( servletContext );
		Map<String, EntityManagerFactory> factories = (Map<String, EntityManagerFactory>)
				akkaConfiguration.getAttribute( EntityManagerFactoryClassFactory.FACTORIES );
		close( factories );
	}

	void close( Map<String, EntityManagerFactory> factories ) {
		for ( EntityManagerFactory entityManagerFactory : factories.values() )
			close( entityManagerFactory );
	}

	void close( EntityManagerFactory entityManagerFactory ) {
		log.info( str( "Closing %s", entityManagerFactory ) );
		entityManagerFactory.close();
	}
}
