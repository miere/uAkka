package io.skullabs.uakka.api;

import static io.skullabs.uakka.commons.Commons.str;
import io.skullabs.uakka.api.exception.MethodHandlerException;
import io.skullabs.uakka.api.exception.UnhandledMessageException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
		memorizeHandledMethods();
	}

	public void memorizeHandledMethods() {
		for ( Method method : getHandledObject().getClass().getDeclaredMethods() )
			if ( method.getName().equals( "handle" ) )
				memorize( method );
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
		this.methods.put( handledMessageType.getCanonicalName(), method );
	}

	public void runHandlerMethodFor( Object message ) throws MethodHandlerException {
		Method method = this.methods.get( message.getClass().getCanonicalName() );
		if ( method == null )
			throw new UnhandledMessageException();
		try {
			method.invoke( this.handledObject, message );
		} catch ( Exception e ) {
			throw new MethodHandlerException( e );
		}
	}
}
