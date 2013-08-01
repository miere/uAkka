package io.skullabs.uakka.service.actor;

import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.AkkaActors;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import scala.collection.immutable.IndexedSeq;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;

@RequiredArgsConstructor
public class WrappedActorSelection extends ActorSelection {
	private static final long serialVersionUID = -6424061174797363112L;

	private static interface UnimplementedMethods {
		ActorRef anchor();

		IndexedSeq<Object> path();
	}

	final AkkaActors akkaActors;
	final ActorContext context;
	final SearchInfo searchInfo;
	ActorSelection actorSelection;

	@Delegate( types = UnimplementedMethods.class )
	ActorSelection getActorSelection() {
		if ( actorSelection == null )
			actorSelection = akkaActors.actor( context, searchInfo );
		return this.actorSelection;
	}
}
