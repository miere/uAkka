package io.skullabs.uakka.standalone;

import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;
import io.skullabs.uakka.api.Injectables;

import java.util.Map;

import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.typesafe.config.Config;

@Getter
@Setter
@RequiredArgsConstructor
public class StandaloneAkkaConfiguration
		implements AkkaConfiguration {

	final String applicationName;

	@Delegate
	Map<String, Object> attributes;
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
