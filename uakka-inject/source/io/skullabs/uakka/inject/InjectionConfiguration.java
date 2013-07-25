package io.skullabs.uakka.inject;

import com.typesafe.config.Config;

public interface InjectionConfiguration {

	public void setAttribute( String name, Object value );

	public Object getAttribute( String name );

	public InjectableAkkaActors getInjectableAkkaActors();

	public void setInjectableAkkaActors( InjectableAkkaActors injectableAkkaActors );

	public void setConfig( Config config );

	public Config getConfig();

}
