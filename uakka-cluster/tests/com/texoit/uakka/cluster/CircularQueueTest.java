package com.texoit.uakka.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class CircularQueueTest {

	static final String TWICE_HEROIC = "TWICE HEROIC!";
	static final String HELLO_WORLD = "Hello World";
	CircularQueue<String> queue;

	@Before
	public void populateQueue(){
		queue = CircularQueue.create();
		queue.pull( HELLO_WORLD );
	}
	
	@Test
	public void grantThatCircularQueueWorksAsExpected(){
		queue.pull(TWICE_HEROIC);
		
		assertEquals(HELLO_WORLD, queue.next());
		assertEquals(TWICE_HEROIC, queue.next());
		assertEquals(HELLO_WORLD, queue.next());
		assertEquals(TWICE_HEROIC, queue.next());
		
		queue.remove(HELLO_WORLD);

		assertEquals(TWICE_HEROIC, queue.next());
		assertEquals(TWICE_HEROIC, queue.next());
		assertEquals(TWICE_HEROIC, queue.next());
		
		queue.remove(TWICE_HEROIC);
		
		assertNull(queue.next());
		assertNull(queue.next());
		assertNull(queue.next());
		
		queue.pull(TWICE_HEROIC);
		queue.pull(HELLO_WORLD);
		
		assertEquals(TWICE_HEROIC, queue.next());
		assertEquals(HELLO_WORLD, queue.next());
		assertEquals(TWICE_HEROIC, queue.next());
		assertEquals(HELLO_WORLD, queue.next());
	}
	
}
