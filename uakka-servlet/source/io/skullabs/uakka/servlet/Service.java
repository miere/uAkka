package io.skullabs.uakka.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import akka.actor.Actor;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Service {

	String name();

	String path();

	Class<? extends Actor> actorClass();

}
