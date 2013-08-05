package io.skullabs.uakka.api;

import io.skullabs.uakka.api.exception.InjectionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import akka.actor.Actor;

public class ActorInfo {

	@Getter
	@ToString
	@RequiredArgsConstructor
	public static class CreationInfo {
		final String name;
		final Class<? extends Actor> targetClass;

		public CreationInfo( Class<? extends Actor> targetActor ) throws InjectionException {
			if ( targetActor.equals( Actor.class ) )
				throw new InjectionException( "Can't create actor for type " + Actor.class.getCanonicalName() );
			Service service = targetActor.getAnnotation( Service.class );
			this.name = oneOf( service.name(), service.value(), targetActor.getCanonicalName() );
			this.targetClass = targetActor;
		}

		public CreationInfo( Reference service ) throws InjectionException {
			Class<? extends Actor> actorClass = service.actor();
			this.name = nameFrom( actorClass );
			this.targetClass = targetClassFrom( actorClass );
		}

		public CreationInfo( Service service ) throws InjectionException {
			Class<? extends Actor> actorClass = service.actor();
			this.name = oneOf( service.name(), service.value(), nameFrom( actorClass ) );
			this.targetClass = targetClassFrom( actorClass );
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
			Class<? extends Actor> actorClass = reference.actor();
			this.targetClass = targetClassFrom( actorClass );
			this.path = oneOf( reference.path(), nameFrom( actorClass ) );
		}
	}

	static String oneOf( String... strings ) {
		for ( String string : strings )
			if ( string != null && !string.isEmpty() )
				return string;
		return null;
	}

	static String nameFrom( Class<? extends Actor> actorClass ) {
		String name = null;
		if ( !actorClass.equals( Actor.class ) )
			name = actorClass.getCanonicalName();
		return name;
	}

	static Class<? extends Actor> targetClassFrom( Class<? extends Actor> actorClass ) {
		Class<? extends Actor> targetClass = null;
		if ( !actorClass.equals( Actor.class ) )
			targetClass = actorClass;
		return targetClass;
	}
}
