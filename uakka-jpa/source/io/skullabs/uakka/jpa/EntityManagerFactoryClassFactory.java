package io.skullabs.uakka.jpa;

import io.skullabs.uakka.api.exception.InjectionException;
import io.skullabs.uakka.inject.AbstractInjectableClassFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

public class EntityManagerFactoryClassFactory extends AbstractInjectableClassFactory<PersistenceUnit> {

	static final String FACTORIES = EntityManagerFactoryClassFactory.class.getCanonicalName() + ".FACTORIES";

	Map<String, EntityManagerFactory> factories = new HashMap<String, EntityManagerFactory>();

	@Override
	public void initialize() {
		getConfiguration().setAttribute( FACTORIES, this.factories );
	}

	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		String persistUnitName = extractPersistenceUnitName( injectableField );
		EntityManagerFactory factory = this.factories.get( persistUnitName );
		if ( factory == null ) {
			factory = Persistence.createEntityManagerFactory( persistUnitName );
			this.factories.put( persistUnitName, factory );
		}
		return factory;
	}

	String extractPersistenceUnitName( Field injectableField ) {
		PersistenceUnit persistenceUnit = injectableField.getAnnotation( PersistenceUnit.class );
		String persistUnitName = firstOf( persistenceUnit.name(), persistenceUnit.unitName() );
		return persistUnitName;
	}

	String firstOf( String... strings ) {
		for ( String string : strings )
			if ( string != null && !string.isEmpty() )
				return string;
		return null;
	}
}
