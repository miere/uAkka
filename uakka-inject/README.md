# uAkka Dependency Injection Package
Allow dependency injection in handled Akka actors. It was designed to be useful with
both Java and Scala Akka Actors API.

## Basic usage
At bellow code we provide a simple way to inject a String in
fields annotated with an hipotetic ```@MyInjection```.

```java
public @interface MyInjection {}

public class MyInjectionClassFactory extends ClassFactory<MyInjection> {

	public Object newInstance( Field injectableField ) {
		return "Injected String...";
	}
}

public class MyActor extends Actor {

	@MyInjection String string;

	public void onReceive( Object message ) {
		System.out.print( "String with @MyInjection: " + string );
	}
}

```
