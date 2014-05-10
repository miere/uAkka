package com.texoit.uakka.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.texoit.uakka.inject.HandledInjectableClass;
import com.texoit.uakka.standalone.StandaloneUAkka;

public class InjectableBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {
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
		Object newInstance = injectableClass.newInstance();
		System.out.println("created test: " + newInstance);
		return newInstance;
	}
}