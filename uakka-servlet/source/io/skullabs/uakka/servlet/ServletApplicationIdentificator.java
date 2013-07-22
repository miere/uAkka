package io.skullabs.uakka.servlet;

import static java.lang.String.format;

import java.util.UUID;

import javax.servlet.ServletContext;

import lombok.Getter;

public class ServletApplicationIdentificator {

	@Getter
	final String identificator;

	public ServletApplicationIdentificator(
			ServletContext servletContext ) {
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
