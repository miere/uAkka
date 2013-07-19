package io.skullabs.uakka.simple;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AsyncResponse implements Future<Object> {

	enum State {
		WAITING, DONE, CANCELLED
	}

	@NonNull
	BlockingQueue<ActorMessageEnvelop> queue;
	State state = State.WAITING;

	@Override
	public boolean cancel( boolean mayInterruptIfRunning ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		ActorMessageEnvelop envelop = this.queue.take();
		this.state = State.DONE;
		return envelop.getMessage();
	}

	@Override
	public Object get( long timeout, TimeUnit unit ) throws InterruptedException,
			ExecutionException, TimeoutException {
		ActorMessageEnvelop envelop = this.queue.poll( timeout, unit );
		if ( envelop == null ) {
			this.state = State.CANCELLED;
			throw new TimeoutException();
		}
		this.state = State.DONE;
		return envelop.getMessage();
	}

	@Override
	public boolean isCancelled() {
		return this.state == State.CANCELLED;
	}

	@Override
	public boolean isDone() {
		return this.state == State.DONE;
	}

}
