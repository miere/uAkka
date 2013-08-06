package io.skullabs.uakka.api.actor;

import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.AkkaActors;

import java.util.concurrent.TimeUnit;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import scala.collection.immutable.IndexedSeq;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRefFactory;
import akka.pattern.AskableActorSelection;
import akka.util.Timeout;

@RequiredArgsConstructor
public class ActorSelection extends akka.actor.ActorSelection {
	private static final long serialVersionUID = -6424061174797363112L;

	private static interface UnimplementedMethods {
		akka.actor.ActorRef anchor();

		IndexedSeq<Object> path();
	}

	final AkkaActors akkaActors;
	final ActorRefFactory context;
	final SearchInfo searchInfo;
	akka.actor.ActorSelection actorSelection;

	@Delegate( types = UnimplementedMethods.class )
	akka.actor.ActorSelection getActorSelection() {
		if ( this.actorSelection == null )
			this.actorSelection = this.akkaActors.actor( this.context, this.searchInfo );
		return this.actorSelection;
	}

	AskableActorSelection getAskableActorRef() {
		return new AskableActorSelection( getActorSelection() );
	}

	public Object ask( Object message ) throws Exception {
		return ask( message, Duration.create( 30, TimeUnit.SECONDS ) );
	}

	public Object ask( Object message, FiniteDuration duration ) throws Exception {
		final Timeout timeout = new Timeout( duration );
		Future<Object> futureResponse = this.getAskableActorRef().ask( message, timeout );
		return Await.result( futureResponse, duration );
	}
}
