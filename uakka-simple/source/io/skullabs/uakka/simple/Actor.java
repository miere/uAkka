package io.skullabs.uakka.simple;

import java.util.concurrent.BlockingQueue;

import lombok.Data;
import lombok.Delegate;
import lombok.extern.log4j.Log4j;

@Data
@Log4j
public abstract class Actor implements Runnable, ActorSystem {

	@Delegate
	private ActorSystem rootActorSystem;
	private BlockingQueue<ActorMessageEnvelop> mailbox;
	ActorRef sender;

	@Override
	public void run() {
		try {
			mainloop();
		} catch ( InterruptedException e ) {
			log.debug("Interrupted?", e);
		} catch ( NullPointerException e ) {
			log.error("Bad Actor, it throws NPE. Did you forget to test it?", e);
			e.printStackTrace();
		} finally {
			try {
				close();
			} catch (Exception e) {
				log.debug("Interrupted?", e);
			}
		}
	}

	private void mainloop() throws InterruptedException {
		while( true ) {
			ActorMessageEnvelop actorMessage = waitForMessage();
			interruptIfIsCloseMessage(actorMessage);
			setSender( actorMessage.sender );
			try {
				onReceive(actorMessage.message);
			} catch ( ActorException e ) {
				e.printStackTrace();
				log.error("Can't process " + actorMessage.message);
			}
		}
	}

	private void interruptIfIsCloseMessage(ActorMessageEnvelop actorMessage) throws CloseException {
			if ( actorMessage.message instanceof Close )
				throw new CloseException();
	}

	private ActorMessageEnvelop waitForMessage() throws InterruptedException {
		log.debug("Waiting for messages...");
		ActorMessageEnvelop actorMessage = mailbox.take();
		return actorMessage;
	}

	public void preStart() throws ActorException {}

	public abstract void onReceive(Object message) throws ActorException;
	
	public static class Close {}

	public static class CloseException extends InterruptedException {
		private static final long serialVersionUID = 4167808134415819506L;
	}
}
