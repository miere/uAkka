package com.texoit.uakka.standalone;

import java.io.IOException;
import java.util.Set;

import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.service.AkkaInitialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class StandaloneUAkka {

	final String applicationName;
	AkkaInitialization initialization;
	StandaloneAkkaConfiguration configuration;

	public void initialize() throws InjectionException, ClassNotFoundException, IOException {
		log.info( "Initializing micro Akka..." );
		Set<Class<?>> classes = retrieveAvailableClasses();
		configuration = new StandaloneAkkaConfiguration( this.applicationName );
		initialization = new AkkaInitialization( classes, configuration );
		initialization.initialize();
	}

	Set<Class<?>> retrieveAvailableClasses() throws IOException, ClassNotFoundException {
		log.info( "Searching for actors at classpath..." );
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		SystemResourceLoader resourceLoader = new SystemResourceLoader(
				classLoader, SystemResourceLoader.DEFAULT_RESOURCE_PATH );
		return resourceLoader.retrieveAvailableClasses();
	}

	public void awaitTermination() {
		configuration.getAkkaActors().getActorSystem().awaitTermination();
	}

	public void shutdown() {
		configuration.getAkkaActors().shutdown();
	}
	
	public Injectables getInjectables(){
		return initialization.getInjectables();
	}

	public static void main( String[] args ) throws InjectionException, ClassNotFoundException, IOException {
		if ( args.length < 1 ){
			System.out.println("Missing application name parameter.");
			return;
		}

		StandaloneUAkka standalone = new StandaloneUAkka( args[0] );
		standalone.initialize();
		standalone.awaitTermination();
	}
}
