package com.texoit.uakka.standalone;

import java.io.IOException;
import java.util.Set;

import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.service.AkkaInitialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class StandaloneAkkaInitialization {

	final String applicationName;

	public void initialize() throws InjectionException, ClassNotFoundException, IOException {
		log.info( "Initializing micro Akka..." );
		Set<Class<?>> classes = retrieveAvailableClasses();
		StandaloneAkkaConfiguration configuration = new StandaloneAkkaConfiguration( this.applicationName );
		AkkaInitialization initialization = new AkkaInitialization( classes, configuration );
		initialization.initialize();
		configuration.getAkkaActors().getActorSystem().awaitTermination();
	}

	Set<Class<?>> retrieveAvailableClasses() throws IOException, ClassNotFoundException {
		log.info( "Taking a look at classpath for actors..." );
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		SystemResourceLoader resourceLoader = new SystemResourceLoader(
				classLoader, SystemResourceLoader.DEFAULT_RESOURCE_PATH );
		return resourceLoader.retrieveAvailableClasses();
	}

	public static void main( String[] args ) throws InjectionException, ClassNotFoundException, IOException {
		new StandaloneAkkaInitialization( args[0] ).initialize();
	}

}
