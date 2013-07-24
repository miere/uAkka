package io.skullabs.uakka.inject;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class InjectableClass<T> {

	Injectables injectables;
	Class<T> targetClass;
	Set<InjectableField> fields;

	public InjectableClass( Injectables injectables, Class<T> targetClass ) {
		this.injectables = injectables;
		this.targetClass = targetClass;
		this.fields = discoveryActorFields( targetClass );
	}

	public static <T> InjectableClass<T> newInstance( Injectables injectables, Class<T> targetClass ) {
		return new InjectableClass<T>( injectables, targetClass );
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

	public T newInstance() throws InjectionException {
		try {
			T newInstance = newInstance( this.targetClass );
			for ( InjectableField field : this.fields )
				field.inject( newInstance );
			return newInstance;
		} catch ( Exception e ) {
			throw new InjectionException( "Can't to instantiate " + this.targetClass.getCanonicalName(), e );
		}
	}

	public T newInstance( Class<T> targetClass ) throws InstantiationException, IllegalAccessException {
		return targetClass.newInstance();
	}
}
