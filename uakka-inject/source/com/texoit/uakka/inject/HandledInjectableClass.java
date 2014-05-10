package com.texoit.uakka.inject;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.texoit.uakka.api.InjectableClassFactory;
import com.texoit.uakka.api.Injectables;
import com.texoit.uakka.api.exception.InjectionException;

import lombok.Getter;

@Getter
public class HandledInjectableClass {

	Injectables injectables;
	Class<?> targetClass;
	Set<InjectableField> fields;

	public HandledInjectableClass( Injectables injectables, Class<?> targetClass ) {
		this.injectables = injectables;
		this.targetClass = targetClass;
		this.fields = discoveryActorFields( targetClass );
	}

	public static HandledInjectableClass newInstance( Injectables injectables, Class<?> targetClass ) {
		return new HandledInjectableClass( injectables, targetClass );
	}

	private Set<InjectableField> discoveryActorFields( Class<?> targetClass ) {
		Set<InjectableField> fields = new HashSet<InjectableField>();

		Class<?> clazz = targetClass;
		while ( clazz != Object.class ) {
			discoveryAndMemoryActorFields( clazz, fields );
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	private void discoveryAndMemoryActorFields( Class<?> clazz, Set<InjectableField> fields ) {
		for ( Field field : clazz.getDeclaredFields() ) {
			InjectableClassFactory<?> classFactory = this.injectables.getClassFactory( field );
			if ( classFactory != null )
				fields.add( new InjectableField( this.targetClass, field, classFactory ) );
		}
	}

	public Object newInstance() throws InjectionException {
		try {
			Object newInstance = newInstance( this.targetClass );
			for ( InjectableField field : this.fields )
				field.inject( newInstance );
			return newInstance;
		} catch ( Exception e ) {
			throw new InjectionException( "Can't instantiate " + this.targetClass.getCanonicalName(), e );
		}
	}

	public Object newInstance( Class<?> targetClass ) throws InstantiationException, IllegalAccessException {
		return targetClass.newInstance();
	}
}
