package com.texoit.uakka.api.actor;

import java.util.concurrent.Future;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.ActorInfo.CreationInfo;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorPath;
import akka.actor.ActorRefFactory;
import akka.actor.ScalaActorRef;

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

	public void tell( Object message ) {
		tell( message, noSender() );
	}
	
	public Future<Object> ask( Object message ) {
		return Ask.ask(getActorRef(), message);
	}
}
