package com.texoit.uakka.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import lombok.experimental.ExtensionMethod;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.texoit.uakka.api.Service;
import com.texoit.uakka.api.actor.ActorRef;
import com.texoit.uakka.api.actor.Ask;
import com.texoit.uakka.test.InMemoryPersistenceActor.FoundRegisters;
import com.texoit.uakka.test.InMemoryPersistenceActor.Persist;
import com.texoit.uakka.test.InMemoryPersistenceActor.Retrieve;

@RunWith(AkkaJUnitRunner.class)
@ExtensionMethod( Ask.class )
public class SimpleTest {
	
	@Service(actor=InMemoryPersistenceActor.class)
	ActorRef persistenceActor;

	@Test
	public void doSomeTests() throws Exception{
		int foundRegisters = retrieveFoundRegisters();
		persistenceActor.tell( Persist.data("Hello World!") );
		persistenceActor.tell( Persist.data("Test Suite is working like a boss.") );
		assertEquals( foundRegisters+2, retrieveFoundRegisters() );
	}

	int retrieveFoundRegisters() throws Exception {
		Object everythingAsObject = persistenceActor.ask(Retrieve.everything()).getOrThrow();
		assertNotNull(everythingAsObject);
		assertTrue(FoundRegisters.class.isInstance(everythingAsObject));
		FoundRegisters registries = (FoundRegisters)everythingAsObject;
		List<String> data = registries.data();
		return ( data != null ) ? data.size() : 0;
	}
	
}
