package com.texoit.uakka.api;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.texoit.uakka.api.MethodHandler;
import com.texoit.uakka.api.exception.MethodHandlerException;
import com.texoit.uakka.api.exception.UnhandledMessageException;

public class MethodHandlerTest {

	SampleHandledClass sampleHandledClass = new SampleHandledClass();
	MethodHandler methodHandler;

	@Before
	public void setup() throws NoSuchMethodException, SecurityException {
		this.sampleHandledClass = spy( this.sampleHandledClass );
		this.methodHandler = spy( new MethodHandler( this.sampleHandledClass ) );
		this.methodHandler.memorizeHandledMethods();
		Method method = retrieveInvalidMethod();
		verify( this.methodHandler ).logCantMemorizeMethod( method );
	}

	private Method retrieveInvalidMethod() throws NoSuchMethodException, SecurityException {
		return this.sampleHandledClass.getClass().getDeclaredMethod( "handle" );
	}

	@Test
	public void assertThatHandleSampleMessage() throws MethodHandlerException {
		SampleMessage message = new SampleMessage();
		this.methodHandler.runHandlerMethodFor( message );
		verify( this.sampleHandledClass ).handle( message );
	}

	@Test( expected = UnhandledMessageException.class )
	public void assertThatCantHandleUnhandledSampleMessage() throws MethodHandlerException {
		UnhandledSampleMessage message = new UnhandledSampleMessage();
		this.methodHandler.runHandlerMethodFor( message );
	}

	@Test( expected = MethodHandlerException.class )
	public void assertThatCantHandleNullPointerException() throws MethodHandlerException {
		this.methodHandler.runHandlerMethodFor( new Integer( 12 ) );
	}
}
