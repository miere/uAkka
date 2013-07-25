package io.skullabs.uakka.servlet;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;

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
		return (AkkaActors)getAttribute( AkkaActors.class.getCanonicalName() );
	}

	@Override
	public void setAkkaActors( AkkaActors injectableAkkaActors ) {
		setAttribute( AkkaActors.class.getCanonicalName(), injectableAkkaActors );
	}

	@Override
	public Config getConfig() {
		return (Config)getAttribute( Config.class.getCanonicalName() );
	}

	@Override
	public void setConfig( Config config ) {
		setAttribute( Config.class.getCanonicalName(), config );
	}

	@Override
	public String toString() {
		return getIdentificator();
	}

}
