package io.skullabs.uakka.service;

import io.skullabs.uakka.inject.InjectableActorClass;
import lombok.RequiredArgsConstructor;
import akka.actor.Actor;
import akka.japi.Creator;

@RequiredArgsConstructor
public class DefaultActorCreator implements Creator<Actor> {

	private static final long serialVersionUID = 1688217760400479333L;
	final InjectableActorClass injectableClass;

	@Override
	public Actor create() throws Exception {
		return (Actor)this.injectableClass.newInstance();
	}
}
