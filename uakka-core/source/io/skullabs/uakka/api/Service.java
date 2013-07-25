package io.skullabs.uakka.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import akka.actor.Actor;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.FIELD } )
public @interface Service {

	String value() default "";

	Class<? extends Actor> actor() default Actor.class;

}
