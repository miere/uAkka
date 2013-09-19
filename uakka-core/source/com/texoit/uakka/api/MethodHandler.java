package com.texoit.uakka.api;

import static com.texoit.uakka.commons.Commons.str;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.texoit.uakka.api.exception.MethodHandlerException;
import com.texoit.uakka.api.exception.UnhandledMessageException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter( AccessLevel.PRIVATE )
@Log
public class MethodHandler {

	Map<String, Method> methods = new HashMap<String, Method>();
	Object handledObject;

	public MethodHandler( Object handledObject ) {
		setHandledObject( handledObject );
		memorizeReceiverMethods();
	}

	public void memorizeReceiverMethods() {
		Class<? extends Object> clazz = getHandledObject().getClass();
		while ( !clazz.equals(Object.class) ) {
			for ( Method method : clazz.getDeclaredMethods() )
				if ( method.isAnnotationPresent(Receiver.class) )
					memorize( method );
			clazz = clazz.getSuperclass();
		}
	}

	public void memorize( Method method ) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if ( parameterTypes.length != 1 ) {
			logCantMemorizeMethod( method );
			return;
		}

		Class<?> handledMessageType = parameterTypes[0];
		memorize( method, handledMessageType );
	}

	void logCantMemorizeMethod( Method method ) {
		log.warning( str( "%s has invalid number of arguments.", method ) );
	}

	private void memorize( Method method, Class<?> handledMessageType ) {
		method.setAccessible( true );
		this.methods.put( handledMessageType.getCanonicalName(), method );
	}

	public Object runHandlerMethodFor( Object message ) throws MethodHandlerException {
		Method method = this.methods.get( message.getClass().getCanonicalName() );
		if ( method == null )
			throw new UnhandledMessageException( message );
		try {
			return method.invoke( this.handledObject, message );
		} catch ( InvocationTargetException e ) {
			throw new MethodHandlerException( e.getCause() );
		} catch ( Exception e ) {
			throw new MethodHandlerException( e );
		}
	}
}
