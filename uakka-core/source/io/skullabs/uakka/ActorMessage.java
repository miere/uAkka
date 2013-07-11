package io.skullabs.uakka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActorMessage {

	Object message;
	ActorRef sender;

}
