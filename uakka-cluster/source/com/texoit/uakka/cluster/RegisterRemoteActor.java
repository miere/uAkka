package com.texoit.uakka.cluster;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import akka.actor.Address;

@Value
@Accessors(fluent=true)
@RequiredArgsConstructor(staticName="at")
public class RegisterRemoteActor {
	final Address address;
}
