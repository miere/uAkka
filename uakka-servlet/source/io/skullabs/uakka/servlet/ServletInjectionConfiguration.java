package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableAkkaActors;
import io.skullabs.uakka.inject.InjectionConfiguration;

import javax.servlet.ServletContext;

import lombok.Delegate;
import lombok.Getter;

import com.typesafe.config.Config;

public class ServletInjectionConfiguration implements InjectionConfiguration {

	@Delegate
	final ServletContext servletContext;

	@Getter
	final String identificator;

	public ServletInjectionConfiguration(
			ServletContext servletContext ) {
		this.servletContext = servletContext;
		this.identificator = generateIdentificator( servletContext );
	}

	private String generateIdentificator( ServletContext servletContext ) {
		return servletContext.getContextPath().replaceFirst( "/", "" );
	}

	@Override
	public InjectableAkkaActors getInjectableAkkaActors() {
		return (InjectableAkkaActors)getAttribute( InjectableAkkaActors.class.getCanonicalName() );
	}

	@Override
	public void setInjectableAkkaActors( InjectableAkkaActors injectableAkkaActors ) {
		setAttribute( InjectableAkkaActors.class.getCanonicalName(), injectableAkkaActors );
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
