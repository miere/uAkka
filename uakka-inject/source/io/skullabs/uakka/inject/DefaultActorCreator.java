package io.skullabs.uakka.servlet;

import io.skullabs.uakka.inject.InjectableClass;
import lombok.RequiredArgsConstructor;
import akka.actor.Actor;
import akka.japi.Creator;

@RequiredArgsConstructor
public class DefaultActorCreator implements Creator<Actor> {

	private static final long serialVersionUID = 1688217760400479333L;
	final InjectableClass<?> injectableClass;

	@Override
	public Actor create() throws Exception {
		return (Actor)this.injectableClass.newInstance();
	}
}
