package io.skullabs.uakka.api.actor;

import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.AkkaActors;

import java.util.concurrent.TimeUnit;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorPath;
import akka.actor.ActorRefFactory;
import akka.actor.ScalaActorRef;
import akka.pattern.AskableActorRef;
import akka.util.Timeout;

@RequiredArgsConstructor
public class ActorRef extends akka.actor.ActorRef implements ScalaActorRef {

	private static final long serialVersionUID = 8180316854428764050L;

	final AkkaActors akkaActors;
	final ActorRefFactory actorRefFactory;
	final CreationInfo creationInfo;

	private static interface UnimplementedActorRefMethods {
		boolean isTerminated();

		ActorPath path();
	}

	akka.actor.ActorRef actorRef;

	@Delegate( types = UnimplementedActorRefMethods.class )
	akka.actor.ActorRef getActorRef() {
		if ( this.actorRef == null )
			this.actorRef = this.akkaActors.actor( this.actorRefFactory, this.creationInfo );
		return this.actorRef;
	}

	private static interface UnimplementedScalaActorRefMethods {
		void $bang( Object arg0, akka.actor.ActorRef arg1 );

		akka.actor.ActorRef $bang$default$2( Object arg0 );
	}

	@Delegate( types = UnimplementedScalaActorRefMethods.class )
	ScalaActorRef getScalaActorRef() {
		return (ScalaActorRef)getActorRef();
	}

	AskableActorRef getAskableActorRef() {
		return new AskableActorRef( getActorRef() );
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
