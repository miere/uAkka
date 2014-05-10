package com.texoit.uakka.commons;

import java.util.ArrayList;
import java.util.List;

public class Commons {

	public static String str( String string, Object... placeholders ) {
		return String.format( string, placeholders );
	}
	
	public static <T> List<T> list( T ... items ){
		List<T> list = new ArrayList<T>();
		for ( T item : items )
			list.add( item );
		return list;
	}
}
