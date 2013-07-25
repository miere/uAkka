package io.skullabs.uakka.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import akka.actor.Actor;

public class ActorInfo {

	@Getter
	@RequiredArgsConstructor
	public static class CreationInfo {
		final String name;
		final Class<? extends Actor> targetClass;

		public CreationInfo( Class<? extends Actor> targetActor ) throws InjectionException {
			if ( targetActor.equals( Actor.class ) )
				throw new InjectionException( "Can't create actor for type " + Actor.class.getCanonicalName() );
			Service service = targetActor.getAnnotation( Service.class );
			this.name = oneOf( service.value(), targetActor.getCanonicalName() );
			this.targetClass = targetActor;
		}

		public CreationInfo( Reference service ) throws InjectionException {
			Class<? extends Actor> actorClass = service.actor();
			if ( actorClass.equals( Actor.class ) )
				throw new InjectionException( "Can't create actor for type " + Actor.class.getCanonicalName() );
			this.name = actorClass.getCanonicalName();
			this.targetClass = actorClass;
		}

		public CreationInfo( Service service ) throws InjectionException {
			Class<? extends Actor> actorClass = service.actor();
			if ( actorClass.equals( Actor.class ) )
				throw new InjectionException( "Can't create actor for type " + Actor.class.getCanonicalName() );
			this.name = oneOf( service.value(), actorClass.getCanonicalName() );
			this.targetClass = actorClass;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class SearchInfo {
		final String path;
		final Class<? extends Actor> targetClass;

		public SearchInfo( Class<? extends Actor> actorClass ) {
			this.path = actorClass.getCanonicalName();
			this.targetClass = actorClass;
		}

		public SearchInfo( Reference reference ) {
			this.targetClass = reference.actor();
			this.path = oneOf(
					reference.path(),
					reference.actor().getCanonicalName() );
		}
	}

	static String oneOf( String first, String second ) {
		return first == null || first.isEmpty()
				? second : first;
	}
}
