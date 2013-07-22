package io.skullabs.uakka.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InjectableField {

	Method method;
	Field field;
	InjectableClassFactory<?> classFactory;

	public InjectableField( Class<?> targetClass, Field originalField, InjectableClassFactory<?> classFactory ) {
		this.classFactory = classFactory;
		this.field = originalField;
		this.method = tryFindOutSetterMethod( targetClass );
		setAccessible();
	}

	private Method tryFindOutSetterMethod( Class<?> targetClass ) {
		try {
			String getterName = generateSetterNameFor( this.field.getName() );
			return targetClass.getDeclaredMethod( getterName, this.field.getType() );
		} catch ( Exception e ) {
			return null;
		}
	}

	private String generateSetterNameFor( String fieldName ) {
		Character upperCase = Character.toUpperCase( fieldName.charAt( 0 ) );
		return String.format( "set%s%s", upperCase, fieldName.substring( 1 ) );
	}

	private void setAccessible() {
		if ( this.method != null )
			this.method.setAccessible( true );
		else
			this.field.setAccessible( true );
	}

	public void inject( Object target ) throws InjectionException {
		Object newInstance = this.classFactory.newInstance( target, this.field );
		set( target, newInstance );
	}

	public void set( Object instance, Object value ) {
		try {
			if ( this.method != null )
				this.method.invoke( instance, value );
			else
				this.field.set( instance, value );
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}
}
