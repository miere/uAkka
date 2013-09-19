package com.texoit.uakka.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.texoit.uakka.inject.HandledInjectableClass;
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
			standalone = new StandaloneUAkka(name);
			standalone.initialize();
		} catch ( Exception cause ) {
			throw new InitializationError(cause);
		}
	}

	@Override
	public Description getDescription() {
		return classRunner.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {
		classRunner.run(notifier);
		standalone.shutdown();
	}

	public static class InjectableBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {
		StandaloneUAkka standalone;
		Class<?> targetClass;

		public InjectableBlockJUnit4ClassRunner(StandaloneUAkka standalone, Class<?> klass)
				throws InitializationError {
			super(klass);
			this.standalone = standalone;
			this.targetClass = klass;
		}

		@Override
		protected Object createTest() throws Exception {
			HandledInjectableClass injectableClass = new HandledInjectableClass(
					standalone.getInjectables(), targetClass);
			return injectableClass.newInstance();
		}
	}
}
