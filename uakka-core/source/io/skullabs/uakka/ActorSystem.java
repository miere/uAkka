package io.skullabs.uakka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface ActorSystem {

	public abstract ActorRef actorOf(
			Class<? extends Actor> targetClass);

	public abstract ActorRef actorOf(
			Class<? extends Actor> targetClass, ActorConfiguration configuration);

	public abstract ActorRef actorFrom(Class<? extends Actor> targetClass);

	public abstract ActorRef actorFrom(String name);

	public abstract void close() throws InterruptedException, ExecutionException;

	public abstract ExecutorService getExecutorService();

}