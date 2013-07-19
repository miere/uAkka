uAkka
=====

Really Tiny Akka Actor Simulation

It provides only a tiny set of functionality that Akka provides, but it's useful to accomplish some goals that akka provides you like Domain Driven Developement, low coupling code and distributed business logic.

uAkka have no intent to provide all Akka funcionality. I just fill a lil' gap I faced on my personal projects.

uAkka in Action
=====

```java
// what you should to import
import uakka.actor.*;

// Simple Actor
public class PrintActor extends Actor {

  @Override
	public void onReceive( Object message ){
		System.out.println( message );
	}
}

// An actor that create and call another actor
public class RequestHandlerActor extends Actor {

	ActorRef printActor;

	@Override // called when the actor is created
	public void preStart() {
		printActor = actorOf( PrintActor.class );
	}

	@Override
	public void onReceive(Object message) {
		// You could receive any object as message
		if (  message instanceOf MyMessage )
			printMe( (MyMessage) message );
	}

	public void printMe( MyMessage message ) {
		// You could send any object as message
		printActor.tell( new PrintMessage( message ) );
	}
}

@WebServlet("/hello")
// Small servlet that call an actor
public class MyServlet extends HttpServlet {
	ActorRef actor;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ActorSystem system = ServletActorFactory.getSystem(config.getServletContext());
		actor = system.actorOf(RequestHandlerActor.class);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		actor.tell( new MyMessage( req ) );
	}
}
```

