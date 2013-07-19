package io.skullabs.uakka.simple;

import io.skullabs.uakka.simple.Actor;
import io.skullabs.uakka.simple.ActorConfiguration;
import io.skullabs.uakka.simple.ActorException;
import io.skullabs.uakka.simple.ActorRef;

import java.util.concurrent.CountDownLatch;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SampleActors {

	public static class FirstLevelConsumerActor extends Actor {
		
		static final int NUMBER_OF_CHILD_ACTORS = 500;
		private ActorRef secondLevelActor;
		
		public void preStart() {
			secondLevelActor = actorOf(SecondLevelConsumerActor.class,
				new ActorConfiguration(NUMBER_OF_CHILD_ACTORS));
		}

		@Override
		public void onReceive(Object message) {
			((CountdownMessage) message).countDown();
			for ( int i=0; i<NUMBER_OF_CHILD_ACTORS; i++ )
				secondLevelActor.tell(message);
		}
	}
	
	public static class SecondLevelConsumerActor extends Actor {
		@Override
		public void onReceive(Object message) {
			((CountdownMessage) message).countDown();
		}
	}
	
	public static class EchoActor extends Actor {

		@Override
		public void onReceive(Object message) throws ActorException {
			getSender().tell(message);
		}
	}

	@Getter
	@AllArgsConstructor
	public static class CountdownMessage {
		CountDownLatch countDownLatch;
		
		public void countDown(){
			takeABreath();
			countDownLatch.countDown();
		}

		private void takeABreath() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
