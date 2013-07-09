package uakka.actor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lombok.extern.log4j.Log4j;

@Log4j
public class ActorPoolInitialization implements ServletContextListener {

	private ActorSystem actorSystem;

    @Override
    public void contextInitialized(ServletContextEvent event) {
		initializeActorPool(event);
    	log.info("Actor Pool created!");
    }

	private void initializeActorPool(ServletContextEvent event) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		actorSystem = new DefaultActorSystem(executorService );
    	ServletActorFactory.setSystem(event.getServletContext(), actorSystem);
	}

	@Override
    public void contextDestroyed(ServletContextEvent event) {
    	log.info("Destroying actors...");
    	actorSystem.close();
    }
}
