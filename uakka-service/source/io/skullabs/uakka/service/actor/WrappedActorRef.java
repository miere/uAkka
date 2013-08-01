package io.skullabs.uakka.service.actor;

import io.skullabs.uakka.api.ActorInfo.CreationInfo;
import io.skullabs.uakka.api.AkkaActors;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ScalaActorRef;

@RequiredArgsConstructor
public class WrappedActorRef extends ActorRef implements ScalaActorRef {

	private static final long serialVersionUID = 8180316854428764050L;

	final AkkaActors akkaActors;
	final ActorRefFactory actorRefFactory;
	final CreationInfo creationInfo;

	private static interface UnimplementedActorRefMethods {
		boolean isTerminated();

		ActorPath path();
	}

	ActorRef actorRef;

	@Delegate( types = UnimplementedActorRefMethods.class )
	ActorRef getActorRef() {
		if ( this.actorRef == null )
			this.actorRef = this.akkaActors.actor( this.actorRefFactory, this.creationInfo );
		return this.actorRef;
	}

	private static interface UnimplementedScalaActorRefMethods {
		void $bang( Object arg0, ActorRef arg1 );

		ActorRef $bang$default$2( Object arg0 );
	}

	@Delegate( types = UnimplementedScalaActorRefMethods.class )
	ScalaActorRef getScalaActorRef() {
		return (ScalaActorRef)getActorRef();
	}
}
