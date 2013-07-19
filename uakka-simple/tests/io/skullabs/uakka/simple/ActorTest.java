package io.skullabs.uakka.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import io.skullabs.uakka.simple.SampleActors.CountdownMessage;
import io.skullabs.uakka.simple.SampleActors.EchoActor;
import io.skullabs.uakka.simple.SampleActors.FirstLevelConsumerActor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActorTest {

	private ActorSystem actorSystem;

	@Before
	public void setup() {
		this.actorSystem = new DefaultActorSystem();
	}

	@After
	public void tearDown() throws InterruptedException, ExecutionException {
		this.actorSystem.close();
	}

	@Test( timeout = 3000 )
	public void grantThatCountDownAsExpected() throws InterruptedException {
		ActorRef firstLevelActor = this.actorSystem.actorOf( FirstLevelConsumerActor.class );
		CountDownLatch countDownLatch = new CountDownLatch( 501 );
		firstLevelActor.tell( new CountdownMessage( countDownLatch ) );
		countDownLatch.await();
	}

	@Test
	public void grantThatCouldReceiveFutureAsExpected() throws InterruptedException, ExecutionException {
		ActorRef echoActor = this.actorSystem.actorOf( EchoActor.class );
		String helloMessage = "Hello World";
		Object receivedResponse = echoActor.ask( helloMessage );
		assertEquals( helloMessage, receivedResponse );
		assertSame( helloMessage, receivedResponse );
	}
}
