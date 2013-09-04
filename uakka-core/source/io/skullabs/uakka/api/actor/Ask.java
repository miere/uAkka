package io.skullabs.uakka.api.actor;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.val;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.pattern.AskableActorRef;
import akka.pattern.AskableActorSelection;
import akka.util.Timeout;

public class Ask {

	public static <T extends akka.actor.ActorRef> Future<Object> ask(
			T self, Object message ) {
		return ask( self, message, Duration.create( 30L, TimeUnit.SECONDS ) );
	}

	public static <T extends akka.actor.ActorRef> Future<Object> ask(
			T self, Object message, FiniteDuration duration ) {
		val timeout = new Timeout( duration );
		val askableSelf = new AskableActorRef( unwrap( self ) );
		val scalaFuture = askableSelf.ask( message, timeout );
		return new JavaFuture<Object>( ScalaFutureWrapper.wrap( scalaFuture ) );
	}

	public static <T extends akka.actor.ActorSelection> Future<Object> ask(
			T self, Object message ) {
		return ask( self, message, Duration.create( 30L, TimeUnit.SECONDS ) );
	}

	public static <T extends akka.actor.ActorSelection> Future<Object> ask(
			T self, Object message, FiniteDuration duration ) {
		val timeout = new Timeout( duration );
		val askableSelf = new AskableActorSelection( unwrap( self ) );
		val scalaFuture = askableSelf.ask( message, timeout );
		return new JavaFuture<Object>( ScalaFutureWrapper.wrap( scalaFuture ) );
	}

	public static akka.actor.ActorSelection unwrap( akka.actor.ActorSelection actorSelection ) {
		if ( actorSelection instanceof ActorSelection )
			return ( (ActorSelection)actorSelection ).getActorSelection();
		return actorSelection;
	}

	public static akka.actor.ActorRef unwrap( akka.actor.ActorRef actorRef ) {
		if ( actorRef instanceof ActorRef )
			return ( (ActorRef)actorRef ).getActorRef();
		return actorRef;
	}

	public static <T> T getOrThrow( Future<T> future ) throws Exception {
		T t = future.get();
		if ( t instanceof Exception )
			throw (Exception)t;
		return t;
	}
}
