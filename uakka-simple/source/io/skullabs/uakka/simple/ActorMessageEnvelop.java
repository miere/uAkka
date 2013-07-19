package io.skullabs.uakka.simple;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActorMessageEnvelop {

	Object message;
	ActorRef sender;

}
