package io.skullabs.uakka.servlet;

import static akka.pattern.Patterns.ask;
import io.skullabs.uakka.api.ActorInfo.SearchInfo;
import io.skullabs.uakka.api.AkkaActors;
import io.skullabs.uakka.api.AkkaConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
		AkkaActors akkaActors = getInjectableAkkaActors();
		this.distrCounterActor = akkaActors.actor( new SearchInfo( DistributedCounterActor.class ) );
		System.out.println( "CounterServlet initialized!" );
	}

	private AkkaActors getInjectableAkkaActors() {
		AkkaConfiguration injectionConfiguration = new ServletAkkaConfiguration( getServletContext() );
		return injectionConfiguration.getAkkaActors();
	}

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		try {
			Object result = askAndWait( new AtomicInteger( 0 ) );
			response.getWriter().write( result.toString() );
		} catch ( Exception e ) {
			throw new ServletException( e );
		}
	}

	private Object askAndWait( Object message ) throws Exception {
		FiniteDuration duration = Duration.create( 5, TimeUnit.SECONDS );
		final Timeout timeout = new Timeout( duration );
		Future<Object> futureResponse = ask( this.distrCounterActor, message, timeout );
		return Await.result( futureResponse, duration );
	}
}
