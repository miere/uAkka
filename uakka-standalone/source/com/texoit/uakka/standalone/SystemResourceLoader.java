package com.texoit.uakka.standalone;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/*
 * XXX: Old implementation becoming from web TODO: Include respective authors
 * TODO: Apply some clean-code principles in the source
 */
public class SystemResourceLoader {

	public static final String DEFAULT_RESOURCE_PATH = "";

	final ClassLoader classLoader;
	final String resourcePath;

	public Set<Class<?>> retrieveAvailableClasses() throws IOException, ClassNotFoundException {
		Set<Class<?>> classes = new HashSet<Class<?>>();

		List<String> resources = retrieveAvailableResources();

		for ( String resource : resources ) {
			if ( !resource.endsWith( ".class" ) || resource.contains( "$" ) )
				continue;
			try {
				String normalizedClassName = normalizeClassName( resource );
				Class<?> clazz = getClassLoader().loadClass(
						normalizedClassName );
				classes.add( clazz );
			} catch ( Throwable e ) {
			}
		}

		return classes;
	}

	public String normalizeClassName( String className ) {
		return className
				.replace( "/WEB-INF/classes/", "" )
				.replace( ".class", "" )
				.replaceAll( "^/+", "" )
				.replace( "/", "." );
	}

	public List<String> retrieveAvailableResources() throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		ClassLoader classLoader = getClassLoader();

		Enumeration<URL> resources = classLoader.getResources( this.resourcePath );
		while ( resources.hasMoreElements() ) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode( fileName, "UTF-8" );
			File file = new File( fileNameDecoded );

			if ( !file.exists() )
				continue;

			list.addAll( retrieveAvailableResources( file, this.resourcePath ) );
		}

		return list;
	}

	public List<String> retrieveAvailableResources( File directory, String path ) {
		ArrayList<String> list = new ArrayList<String>();

		File[] listOfFiles = directory.listFiles();
		if ( listOfFiles == null )
			return list;

		for ( File file : listOfFiles ) {
			String fileName = path + "/" + file.getName();
//			System.out.println(fileName);
			if ( file.isDirectory() )
				list.addAll( retrieveAvailableResources( file, fileName ) );
			else
				list.add( fileName );
		}

		return list;
	}

}
