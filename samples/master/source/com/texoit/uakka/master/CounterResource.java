package com.texoit.uakka.master;

import java.util.concurrent.atomic.AtomicInteger;

import com.texoit.uakka.api.Injectable;
import com.texoit.uakka.api.Service;
import com.texoit.uakka.api.actor.ActorRef;
import com.texoit.uakka.api.actor.Ask;

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
