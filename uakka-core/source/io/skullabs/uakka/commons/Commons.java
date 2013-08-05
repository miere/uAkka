package io.skullabs.uakka.commons;

public class Commons {

	public static String str( String string, Object... placeholders ) {
		return String.format( string, placeholders );
	}

	public static <T> void lambda( T... optionals ) {

	}
}
