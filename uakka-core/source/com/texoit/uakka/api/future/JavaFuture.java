package com.texoit.uakka.api.future;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JavaFuture<T> implements Future<T> {

	final BlockingQueue<T> dataQueue;

	@Override
	public boolean cancel( boolean mayInterruptIfRunning ) {
		throw new UnsupportedOperationException( "This future instance is uncancellable." );
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return this.dataQueue.take();
	}

	@Override
	public T get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		T poll = this.dataQueue.poll( timeout, unit );
		if ( poll == null )
			throw new TimeoutException();
		return poll;
	}
}
