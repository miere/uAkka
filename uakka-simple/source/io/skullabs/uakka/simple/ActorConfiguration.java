package io.skullabs.uakka.simple;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActorConfiguration {
	int messagePoolSize;
	int numberOfParallelActors;
	String name;

	public ActorConfiguration( int size ){
		messagePoolSize = size;
		numberOfParallelActors = size;
	}

	public void setNumberOfParallelActors( int numberOfParallelActors ) {
		if ( numberOfParallelActors < 1 )
			throw new ActorException("numberOfParallelActors should be greater than 0");
		this.numberOfParallelActors = numberOfParallelActors;
	}
	
	public void setMessagePoolSize( int messagePoolSize ) {
		if ( messagePoolSize < 1 )
			throw new ActorException("messagePoolSize should be greater than 0");
		this.messagePoolSize = messagePoolSize;
	}
}