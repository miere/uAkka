package com.texoit.uakka.cluster;

import java.io.Serializable;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import akka.actor.Address;

@Value
@Accessors(fluent=true)
@RequiredArgsConstructor(staticName="from")
public class AvailableActors implements Serializable {

	private static final long serialVersionUID = -95147030385059738L;

	final Address from;
	final Collection<String> actors;

}
