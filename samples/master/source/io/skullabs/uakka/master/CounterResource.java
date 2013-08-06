package io.skullabs.uakka.master;

import io.skullabs.uakka.api.Injectable;
import io.skullabs.uakka.api.Service;
import io.skullabs.uakka.api.actor.ActorRef;

import java.util.concurrent.atomic.AtomicInteger;

import layr.api.GET;
import layr.api.WebResource;

@WebResource( "counter" )
@Injectable
public class CounterResource {

	@Service( actor = DistributedCounterActor.class )
	ActorRef distrCounterActor;

	@GET
	public Object countAndGet() throws Exception {
		return this.distrCounterActor.ask( new AtomicInteger( 0 ) );
	}
}
