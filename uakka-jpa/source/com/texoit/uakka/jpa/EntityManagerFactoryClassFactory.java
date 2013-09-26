package com.texoit.uakka.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import lombok.Synchronized;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

import com.texoit.uakka.api.exception.InjectionException;
import com.texoit.uakka.commons.Commons;
import com.texoit.uakka.inject.AbstractInjectableClassFactory;

@Log
@ExtensionMethod(Commons.class)
public class EntityManagerFactoryClassFactory extends AbstractInjectableClassFactory<PersistenceUnit> {

	static final String FACTORIES = EntityManagerFactoryClassFactory.class.getCanonicalName() + ".FACTORIES";

	Map<String, EntityManagerFactory> factories;

	@SuppressWarnings( "unchecked" )
	@Override
	public void initialize() {
		this.factories = (Map<String, EntityManagerFactory>)getConfiguration().getAttribute( FACTORIES );
		if ( getConfiguration().getAttribute( FACTORIES ) == null ) {
			this.factories = new HashMap<String, EntityManagerFactory>();
			getConfiguration().setAttribute( FACTORIES, this.factories );
		}
	}

	@Synchronized
	@Override
	public Object newInstance( Object instance, Field injectableField ) throws InjectionException {
		String persistUnitName = extractPersistenceUnitName( injectableField );
		EntityManagerFactory factory = this.factories.get( persistUnitName );
		if ( factory == null )
			factory = createAndMemorizeFactory( persistUnitName );
		return factory;
	}

	EntityManagerFactory createAndMemorizeFactory( String persistUnitName ) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory( persistUnitName );
		this.factories.put( persistUnitName, factory );
		log.info( "Creating Entity Manager Factory( persistUnitName=\"%s\" )".str( persistUnitName ) );
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
