package io.skullabs.uakka.servlet;

import static akka.pattern.Patterns.ask;
import io.skullabs.uakka.inject.ActorInfo.SearchInfo;
import io.skullabs.uakka.inject.InjectableAkkaActors;
import io.skullabs.uakka.inject.InjectionConfiguration;
import io.skullabs.uakka.servlet.DistributedCounterActor.CountMessage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.util.Timeout;

@WebServlet( "/count" )
public class CounterServlet extends HttpServlet {

	private static final long serialVersionUID = 4962279871466216133L;
	private ActorRef distrCounterActor;

	@Override
	public void init() throws ServletException {
		InjectableAkkaActors akkaActors = getInjectableAkkaActors();
		this.distrCounterActor = akkaActors.actor( new SearchInfo( DistributedCounterActor.class ) );
		System.out.println( "CounterServlet initialized!" );
	}

	private InjectableAkkaActors getInjectableAkkaActors() {
		InjectionConfiguration injectionConfiguration = new ServletInjectionConfiguration( getServletContext() );
		return injectionConfiguration.getInjectableAkkaActors();
	}

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		FiniteDuration duration = Duration.create( 5, TimeUnit.SECONDS );
		final Timeout timeout = new Timeout( duration );
		Future<Object> futureResponse = ask( this.distrCounterActor, new CountMessage(), timeout );
		try {
			Object result = Await.result( futureResponse, duration );
			response.getWriter().write( result.toString() );
		} catch ( Exception e ) {
			throw new ServletException( e );
		}
	}
}
