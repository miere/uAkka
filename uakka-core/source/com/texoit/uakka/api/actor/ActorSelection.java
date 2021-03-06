package com.texoit.uakka.api.actor;

import java.util.concurrent.Future;

import com.texoit.uakka.api.AkkaActors;
import com.texoit.uakka.api.ActorInfo.SearchInfo;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import scala.collection.immutable.IndexedSeq;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;

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

	@Override
	public void tell( Object message ) {
		tell( message, ActorRef.noSender() );
	}

	public Future<Object> ask( Object message ) {
		return Ask.ask(getActorSelection(), message);
	}
}
