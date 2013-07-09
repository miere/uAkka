package uakka.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultActorSystem implements ActorSystem {

	final  ExecutorService executorService;
	Map<String, ActorRef> actorReferences = new HashMap<String, ActorRef>();

	@Override
	public ActorRef actorOf( Class<? extends Actor> targetClass ) {
		ActorConfiguration configuration = createDefaultConfiguration();
		return actorOf(targetClass, configuration);
	}

	public ActorConfiguration createDefaultConfiguration() {
		ActorConfiguration configuration = new ActorConfiguration();
		configuration.setMessagePoolSize(500);
		configuration.setNumberOfParallelActors(1);
		return configuration;
	}

	@Override
	public ActorRef actorOf(Class<? extends Actor> targetClass, ActorConfiguration configuration) {
		try {
			BlockingQueue<Object> mailboxMessages = initializeActors( targetClass, configuration );
			ActorRef actorRef = new ActorRef( mailboxMessages, configuration );
			actorReferences.put( retrieveActorName( targetClass, configuration ), actorRef );
			return actorRef;
		} catch ( Throwable e ) {
			throw new ActorException("Can't initialize actor", e);
		}
	}

	@Override
	public ActorRef actorFrom(Class<? extends Actor> targetClass) {
		return actorFrom(targetClass.getCanonicalName());
	}

	@Override
	public ActorRef actorFrom(String name) {
		return actorReferences.get(name);
	}

	public String retrieveActorName( Class<? extends Actor> targetClass, ActorConfiguration configuration ) {
		return ( configuration.name == null || configuration.name.isEmpty() )
				? targetClass.getCanonicalName()
				: configuration.name;
	}

	public BlockingQueue<Object> initializeActors(
			Class<? extends Actor> targetClass, ActorConfiguration configuration)
			throws InstantiationException, IllegalAccessException {
		BlockingQueue<Object> mailboxMessages = new ArrayBlockingQueue<Object>(configuration.getMessagePoolSize());

		for ( int i=0; i<configuration.getNumberOfParallelActors(); i++ )
			newActor(targetClass, mailboxMessages);

		return mailboxMessages;
	}

	public void newActor(Class<? extends Actor> targetClass, BlockingQueue<Object> mailboxMessages)
			throws InstantiationException, IllegalAccessException {
		Actor actor = targetClass.newInstance();
		actor.setMailbox(mailboxMessages);
		actor.setRootActorSystem(this);
		actor.initialize();
		getExecutorService().submit(actor);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	public void close(){
		for ( String name : actorReferences.keySet() )
			actorReferences.get(name).close();
		actorReferences.clear();
		actorReferences = null;
		executorService.shutdownNow();
	}
}
