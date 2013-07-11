package io.skullabs.uakka;

import javax.servlet.ServletContext;

public class ServletActorFactory {

	public static ActorSystem getSystem( ServletContext servletContext ) {
		return (ActorSystem) servletContext.getAttribute(ActorSystem.class.getCanonicalName());
	}

	public static void setSystem( ServletContext servletContext, ActorSystem actorSystem ) {
		servletContext.setAttribute(ActorSystem.class.getCanonicalName(), actorSystem);
	}

}
