package io.skullabs.uakka.api;

import com.typesafe.config.Config;

public interface AkkaConfiguration {

	public void setAttribute( String name, Object value );

	public Object getAttribute( String name );

	public AkkaActors getAkkaActors();

	public void setAkkaActors( AkkaActors injectableAkkaActors );

	public void setConfig( Config config );

	public Config getConfig();

}
