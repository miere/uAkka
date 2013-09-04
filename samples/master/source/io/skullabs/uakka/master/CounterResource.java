package io.skullabs.uakka.master;

import io.skullabs.uakka.api.actor.Ask;
import io.skullabs.uakka.api.Injectable;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.api.actor.ActorRef;

import java.util.concurrent.atomic.AtomicInteger;

import layr.api.GET;
import layr.api.WebResource;
import lombok.experimental.ExtensionMethod;

@Injectable( singleton = true )
@WebResource( "counter" )
@ExtensionMethod( Ask.class )
public class CounterResource {

	@Service( actor = DistributedCounterActor.class )
	ActorRef distrCounterActor;

	@GET
	public Object countAndGet() throws Exception {
		return this.distrCounterActor.ask( new AtomicInteger( 0 ) );
	}
}
