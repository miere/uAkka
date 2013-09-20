package com.texoit.uakka.test;

import static com.texoit.uakka.commons.Commons.list;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

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

	@SuppressWarnings("unchecked")
	Set<Class<?>> retrieveKnownAvailableClasses(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.addAll(list(
				ServiceActorClassFactory.class,
				ReferenceActorClassFactory.class
			));
		return classes;
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
