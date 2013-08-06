package io.skullabs.uakka.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.skullabs.uakka.api.Injectables;
import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.InjectableInterfaceClassFactory.DefaultInjectableInterface;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class InjectablesTest {

	HandledInjectableClass handledInjectableClass;

	@Before
	public void setup() throws InjectionException {
		InjectableDiscoveryService injectableDiscoveryService = new InjectableDiscoveryService( null );
		Injectables injectables = injectableDiscoveryService.discovery( collection(
				InjectableInterfaceClassFactory.class,
				InjectableAnnotationClassFactory.class
				) );
		this.handledInjectableClass = HandledInjectableClass.newInstance( injectables, SampleHelloClass.class );
	}

	@Test
	public void grantThatInjectThroughtInterface() throws InjectionException {
		SampleHelloClass sampleHelloClass = (SampleHelloClass)this.handledInjectableClass.newInstance();
		InjectableInterface injectableFromInterface = sampleHelloClass.getInjectableFromInterface();
		assertNotNull( injectableFromInterface );
		assertTrue( injectableFromInterface instanceof DefaultInjectableInterface );
		assertEquals( DefaultInjectableInterface.HELLO_WORLD, injectableFromInterface.getHelloWorld() );
		String injectableFromAnnotation = sampleHelloClass.getInjectableFromAnnotation();
		assertNotNull( injectableFromAnnotation );
		assertFalse( injectableFromAnnotation.isEmpty() );
		assertEquals( InjectableAnnotationClassFactory.HELLO_WORLD, injectableFromAnnotation );
	}

	@Test( timeout = 500 )
	public void grantThatRunInjectionStressInAffordableTime() throws InjectionException {
		for ( int i = 0; i < 1000000; i++ )
			grantThatInjectThroughtInterface();
	}

	static Collection<Class<?>> collection( Class<?>... classList ) {
		Collection<Class<?>> set = new HashSet<Class<?>>();
		for ( Class<?> clazz : classList )
			set.add( clazz );
		return set;
	}
}
