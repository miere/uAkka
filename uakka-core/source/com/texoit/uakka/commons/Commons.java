package com.texoit.uakka.commons;

public class Commons {

	public static String str( String string, Object... placeholders ) {
		return String.format( string, placeholders );
	}
}
