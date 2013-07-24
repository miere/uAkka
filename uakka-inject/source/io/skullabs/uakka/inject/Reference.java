package io.skullabs.uakka.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import akka.actor.Actor;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface Reference {

	String path() default "";

	Class<? extends Actor> actor();

}
