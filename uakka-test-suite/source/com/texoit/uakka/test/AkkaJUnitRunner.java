package com.texoit.uakka.test;

import static com.texoit.uakka.commons.Commons.list;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.texoit.uakka.inject.AbstractInjectableClassFactory;
import com.texoit.uakka.service.factories.ReferenceActorClassFactory;
import com.texoit.uakka.service.factories.ServiceActorClassFactory;
import com.texoit.uakka.standalone.StandaloneUAkka;

public class AkkaJUnitRunner extends Runner {

	StandaloneUAkka standalone;
	Class<?> targetClass;
	private InjectableBlockJUnit4ClassRunner classRunner;

	public AkkaJUnitRunner(Class<?> klass) throws InitializationError {
		targetClass = klass;
		initializeUAkka();
		classRunner = new InjectableBlockJUnit4ClassRunner(standalone, targetClass);
	}

	void initializeUAkka() throws InitializationError {
		try {
			String name = this.targetClass.getSimpleName();
			standalone = createStandaloneUAkka(name);
			standalone.initialize();
		} catch ( Exception cause ) {
			throw new InitializationError(cause);
		}
	}

	StandaloneUAkka createStandaloneUAkka(String name) {
		StandaloneUAkka standaloneUAkka = new StandaloneUAkka(name);
		standaloneUAkka.setKnownAvailableClasses(retrieveKnownAvailableClasses());
		return standaloneUAkka;
	}

	Set<Class<?>> retrieveKnownAvailableClasses(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.addAll(getWellKnownClassFactories());
		classes.addAll(getClassesFromClassPathAnnotation());
		return classes;
	}

	Collection<? extends Class<?>> getClassesFromClassPathAnnotation() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		ClassPath classPath = this.targetClass.getAnnotation( ClassPath.class );
		if ( classPath != null )
			for ( Class<?> clazz : classPath.value() )
				list.add(clazz);
		return list;
	}

	@SuppressWarnings("unchecked")
	List<Class<? extends AbstractInjectableClassFactory<? extends Annotation>>> getWellKnownClassFactories() {
		return list(
				ServiceActorClassFactory.class,
				ReferenceActorClassFactory.class
			);
	}

	@Override
	public Description getDescription() {
		return classRunner.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {
		AsynchronousClassRunner asynchronousClassRunner = new AsynchronousClassRunner(classRunner, notifier, standalone);
		Executors.newSingleThreadExecutor().submit(asynchronousClassRunner);
		standalone.awaitTermination();
	}
}
