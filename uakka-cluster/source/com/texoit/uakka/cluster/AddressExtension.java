package com.texoit.uakka.cluster;

import akka.actor.Address;

public class AddressExtension {

	public static String actorSelectionFor( Address address, String actorName ) {
		return String.format( "akka.tcp://%s/user/%s",
				address.hostPort(), actorName );
	}
	
	public static String hostString( Address address ){
		return address.hostPort();
	}
}
