package io.skullabs.uakka.servlet;

import static java.lang.String.format;
import io.skullabs.uakka.inject.InjectionConfiguration;

import java.util.UUID;

import javax.servlet.ServletContext;

import lombok.Delegate;
import lombok.Getter;

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
		return format( "%s%s", servletContext.getContextPath(),
				new UUID( System.currentTimeMillis(), System.nanoTime() ) );
	}

	@Override
	public String toString() {
		return getIdentificator();
	}

}
