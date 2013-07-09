package uakka.actor;

import java.util.concurrent.BlockingQueue;

import lombok.Data;
import lombok.Delegate;
import lombok.extern.log4j.Log4j;

@Data
@Log4j
public abstract class Actor implements Runnable, ActorSystem {

	@Delegate
	private ActorSystem rootActorSystem;
	private BlockingQueue<Object> mailbox;

	@Override
	public void run() {
		try {
			mainloop();
		} catch ( InterruptedException e ) {
			log.debug("Interrupted?", e);
		}
	}

	private void mainloop() throws InterruptedException {
		while( true ) {
			log.debug("Waiting for messages...");
			Object message = mailbox.take();
			if ( message instanceof Close )
				return;
			try {
				onReceive(message);
			} catch ( ActorException e ) {
				log.error("Can't process " + message);
			}
		}
	}
	
	public void initialize() throws ActorException {}

	public abstract void onReceive(Object message) throws ActorException;
	
	public static class Close {}

}
