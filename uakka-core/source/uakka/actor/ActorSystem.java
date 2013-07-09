package uakka.actor;

public interface ActorSystem {

	public abstract ActorRef actorOf(
			Class<? extends Actor> targetClass);

	public abstract ActorRef actorOf(
			Class<? extends Actor> targetClass, ActorConfiguration configuration);

	public abstract ActorRef actorFrom(Class<? extends Actor> targetClass);

	public abstract ActorRef actorFrom(String name);

	public abstract void close();

}