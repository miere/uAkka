package io.skullabs.uakka.inject;

public interface InjectionConfiguration {

	public void setAttribute( String name, Object value );

	public Object getAttribute( String name );

	public InjectableAkkaActors getInjectableAkkaActors();

	public void setInjectableAkkaActors( InjectableAkkaActors injectableAkkaActors );

}
