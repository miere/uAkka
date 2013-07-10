package uakka.actor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uakka.actor.SampleActors.*;
import static org.junit.Assert.*;

public class ActorTest {

	private ActorSystem actorSystem;

	@Before
	public void setup() {
		actorSystem = new DefaultActorSystem();
	}
	
	@After
	public void tearDown() throws InterruptedException, ExecutionException{
		actorSystem.close();
	}

	@Test(timeout=3000)
	public void grantThatCountDownAsExpected() throws InterruptedException{
		ActorRef firstLevelActor = actorSystem.actorOf(FirstLevelConsumerActor.class);
		CountDownLatch countDownLatch = new CountDownLatch(501);
		firstLevelActor.tell(new CountdownMessage(countDownLatch));
		countDownLatch.await();
	}
	
	@Test
	public void grantThatCouldReceiveFutureAsExpected() throws InterruptedException, ExecutionException{
		ActorRef echoActor = actorSystem.actorOf(EchoActor.class);
		String helloMessage = "Hello World";
		Future<Object> response = echoActor.tell( helloMessage );
		Object receivedResponse = response.get();
		assertEquals( helloMessage, receivedResponse );
		assertSame( helloMessage, receivedResponse );
	}
}
