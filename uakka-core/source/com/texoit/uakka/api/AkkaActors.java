package com.texoit.uakka.api;

import java.util.Collection;

import com.texoit.uakka.api.ActorInfo.CreationInfo;
import com.texoit.uakka.api.ActorInfo.SearchInfo;
import com.texoit.uakka.api.exception.InjectionException;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

public interface AkkaActors {

	public abstract void initialize() throws InjectionException;

	public ActorRef actor( CreationInfo creationInfo );

	public abstract ActorRef actor( ActorRefFactory actorRefFactory, CreationInfo creationInfo );

	public abstract ActorRef actor( SearchInfo searchInfo );

	public abstract ActorSelection actor( ActorRefFactory actorRefFactory, SearchInfo searchInfo );

	public abstract void shutdown();

	public abstract void analyze( Collection<Class<?>> classes ) throws InjectionException;

	public ActorSystem getActorSystem();

	public Collection<String> getAvailableActorNames();

}