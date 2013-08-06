package io.skullabs.uakka.servlet;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;
import io.skullabs.uakka.api.Injectables;

import javax.servlet.ServletContext;

import lombok.Delegate;
import lombok.Getter;

import com.typesafe.config.Config;

public class ServletAkkaConfiguration implements AkkaConfiguration {

	@Delegate
	final ServletContext servletContext;

	@Getter
	final String identificator;

	public ServletAkkaConfiguration(
			ServletContext servletContext ) {
		this.servletContext = servletContext;
		this.identificator = generateIdentificator( servletContext );
	}

	private String generateIdentificator( ServletContext servletContext ) {
		return servletContext.getContextPath().replaceFirst( "/", "" );
	}

	@Override
	public AkkaActors getAkkaActors() {
		return getAttribute( AkkaActors.class );
	}

	@Override
	public void setAkkaActors( AkkaActors injectableAkkaActors ) {
		setAttribute( AkkaActors.class.getCanonicalName(), injectableAkkaActors );
	}

	@Override
	public void setInjectables( Injectables injectables ) {
		setAttribute( Injectables.class.getCanonicalName(), injectables );
	}

	@Override
	public Injectables getInjectables() {
		return getAttribute( Injectables.class );
	}

	@Override
	public Config getConfig() {
		return getAttribute( Config.class );
	}

	@Override
	public void setConfig( Config config ) {
		setAttribute( Config.class.getCanonicalName(), config );
	}

	@SuppressWarnings( "unchecked" )
	public <T> T getAttribute( Class<T> clazz ) {
		return (T)getAttribute( clazz.getCanonicalName() );
	}

	@Override
	public String toString() {
		String identificator = getIdentificator();
		if ( identificator == null
				|| identificator.isEmpty()
				|| identificator.equals( "/" ) )
			identificator = "root";
		return identificator;
	}
}
