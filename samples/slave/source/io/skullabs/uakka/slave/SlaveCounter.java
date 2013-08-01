package io.skullabs.uakka.slave;

import io.skullabs.uakka.api.Service;

import java.util.ArrayList;
import java.util.List;

import akka.actor.UntypedActor;

@Service( "slavecounter" )
public class SlaveCounter extends UntypedActor {

	List<Object> data = new ArrayList<Object>();

	@Override
	public void onReceive( Object message ) throws Exception {
		System.out.println( "Message received: " + message );
		this.data.add( message );
	}

}
