package com.texoit.uakka.api.actor;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.val;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
//import akka.actor.ActorRef;
//import akka.actor.ActorSelection;
import akka.pattern.AskableActorRef;
import akka.pattern.AskableActorSelection;
import akka.util.Timeout;

import com.texoit.uakka.api.future.Function;
import com.texoit.uakka.api.future.JavaFuture;
import com.texoit.uakka.api.future.ScalaFutureWrapper;

public class Ask {

	public static final long DEFAULT_TIMEOUT = 30000L;

	public static <T extends akka.actor.ActorRef> Future<Object> ask(
			T self, Object message ) {
		return ask( self, message, DEFAULT_TIMEOUT );
	}
	
	public static <T extends akka.actor.ActorRef> Future<Object> ask(
			T self, Object message, Long milliseconds ) {
		if( milliseconds == null || milliseconds == 0 )
			throw new IllegalArgumentException("Parameter milliseconds cannot be null nor zero valued.");
		return ask( self, message, Duration.create( milliseconds, TimeUnit.MILLISECONDS ) );
	}	

	public static <T extends akka.actor.ActorRef> Future<Object> ask(
			T self, Object message, FiniteDuration duration ) {
		val scalaFuture = promise(self, message, duration);
		return new JavaFuture<Object>( ScalaFutureWrapper.wrap( scalaFuture ) );
	}

	public static <T extends akka.actor.ActorRef> scala.concurrent.Future<java.lang.Object> promise(
			T self, Object message, Long milliseconds ) {
		if( milliseconds == null || milliseconds == 0 )
			throw new IllegalArgumentException("Parameter milliseconds cannot be null nor zero valued.");
		return promise( self, message, Duration.create( milliseconds, TimeUnit.MILLISECONDS ) );
	}

	public static <T extends akka.actor.ActorRef> scala.concurrent.Future<java.lang.Object> promise(
			T self, Object message, FiniteDuration duration) {
		val timeout = new Timeout( duration );
		val askableSelf = new AskableActorRef( unwrap( self ) );
		val scalaFuture = askableSelf.ask( message, timeout );
		return scalaFuture;
	}

	public static <T extends akka.actor.ActorSelection> Future<Object> ask(
			T self, Object message ) {
		return ask( self, message, DEFAULT_TIMEOUT );
	}
	
	public static <T extends akka.actor.ActorSelection> Future<Object> ask(
			T self, Object message, Long milliseconds ) {
		if( milliseconds == null || milliseconds == 0 )
			throw new IllegalArgumentException("Parameter milliseconds cannot be null nor zero valued.");
		return ask( self, message, Duration.create( milliseconds, TimeUnit.MILLISECONDS ) );
	}

	public static <T extends akka.actor.ActorSelection> Future<Object> ask(
			T self, Object message, FiniteDuration duration ) {
		val scalaFuture = promise(self, message, duration);
		return new JavaFuture<Object>( ScalaFutureWrapper.wrap( scalaFuture ) );
	}
	
	public static <T extends akka.actor.ActorSelection> scala.concurrent.Future<java.lang.Object> promise(
			T self, Object message, Long millisencods ) {
		if( millisencods == null || millisencods == 0 )
			throw new IllegalArgumentException("Parameter milliseconds cannot be null nor zero valued.");
		return promise( self, message, Duration.create( millisencods, TimeUnit.MILLISECONDS ) );
	}

	public static <T extends akka.actor.ActorSelection> scala.concurrent.Future<java.lang.Object> promise(
			T self, Object message, FiniteDuration duration) {
		val timeout = new Timeout( duration );
		val askableSelf = new AskableActorSelection( unwrap( self ) );
		val scalaFuture = askableSelf.ask( message, timeout );
		return scalaFuture;
	}

	public static akka.actor.ActorSelection unwrap( akka.actor.ActorSelection actorSelection ) {
		if ( actorSelection instanceof ActorSelection )
			return ( (ActorSelection) actorSelection ).getActorSelection();
		return actorSelection;
	}

	public static akka.actor.ActorRef unwrap( akka.actor.ActorRef actorRef ) {
		if ( actorRef instanceof ActorRef )
			return ( (ActorRef) actorRef ).getActorRef();
		return actorRef;
	}

	public static <T> T getOrThrow( Future<T> future ) throws Exception {
		T t = future.get();
		if ( t instanceof Exception )
			throw (Exception) t;
		return t;
	}
	
	public static <T> void handle( scala.concurrent.Future<T> future, Function<T> onSuccess, Function<Throwable> onFail ) {
		ScalaFutureWrapper.handle(future, onSuccess, onFail);
	}
}
