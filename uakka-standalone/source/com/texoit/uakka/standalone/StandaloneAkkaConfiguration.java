package com.texoit.uakka.standalone;

import java.util.Map;
import java.util.TreeMap;

import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.AkkaConfiguration;
import com.texoit.uakka.api.Injectables;
import com.typesafe.config.Config;

@Getter
@Setter
@RequiredArgsConstructor
public class StandaloneAkkaConfiguration
		implements AkkaConfiguration {

	final String applicationName;

	@Delegate
	Map<String, Object> attributes = new TreeMap<String, Object>();
	Injectables injectables;
	AkkaActors akkaActors;
	Config config;
		
	@Override
	public void setAttribute( String name, Object value ) {
		this.attributes.put( name, value );
	}

	@Override
	public Object getAttribute( String name ) {		
		return this.attributes.get( name );
	}

	@Override
	public String toString() {
		return this.applicationName;
	}
}