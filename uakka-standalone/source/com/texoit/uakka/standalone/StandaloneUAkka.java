package com.texoit.uakka.standalone;

import java.io.IOException;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.service.AkkaInitialization;

@Log
@RequiredArgsConstructor
public class StandaloneUAkka {

	final String applicationName;
	AkkaInitialization initialization;
	StandaloneAkkaConfiguration configuration;
	
	@Setter
	Set<Class<?>> knownAvailableClasses;

	@Setter
	ClassLoader classLoader;

	public void initialize() throws InjectionException, ClassNotFoundException, IOException {
		log.info( "Initializing micro Akka..." );
		Set<Class<?>> classes = retrieveAvailableClasses();
		configuration = new StandaloneAkkaConfiguration( this.applicationName );
		initialization = new AkkaInitialization( classes, configuration );
		initialization.initialize();
	}

	Set<Class<?>> retrieveAvailableClasses() throws IOException, ClassNotFoundException {
		log.info( "Searching for actors at classpath..." );
		SystemResourceLoader resourceLoader = new SystemResourceLoader(
				getClassLoader(), SystemResourceLoader.DEFAULT_RESOURCE_PATH );
		Set<Class<?>> availableClasses = resourceLoader.retrieveAvailableClasses();
		if ( this.knownAvailableClasses != null )
			availableClasses.addAll( this.knownAvailableClasses );
		return availableClasses;
	}

	ClassLoader getClassLoader(){
		if ( classLoader == null )
			classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader;
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
