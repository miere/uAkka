package com.texoit.uakka.slave;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.texoit.uakka.api.Service;

import akka.actor.UntypedActor;

@Service( "PersistenceDataActor" )
public class PersistenceDataActor extends UntypedActor {

	@PersistenceUnit( unitName = "default" )
	EntityManagerFactory entityManagerFactory;

	@Override
	public void onReceive( Object message ) throws Exception {
		AtomicInteger atomicMessage = (AtomicInteger)message;
		System.out.println( "Message received: " + atomicMessage );
		persist( atomicMessage );
		getSender().tell( atomicMessage.incrementAndGet(), getSelf() );
	}

	void persist( AtomicInteger message ) {
		PersistedData data = new PersistedData( message );
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		entityManager.persist( data );
		entityManager.close();
	}
}
