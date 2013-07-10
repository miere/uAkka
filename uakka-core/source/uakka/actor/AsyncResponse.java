package uakka.actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AsyncResponse implements Future<Object> {

	enum State { WAITING, DONE, CANCELLED }

	@NonNull BlockingQueue<Object> queue;
	State state = State.WAITING;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		Object value = queue.take();
		state = State.DONE;
		return value;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		Object object = queue.poll(timeout, unit);
		if ( object == null ) {
			state = State.CANCELLED;
			throw new TimeoutException();
		}
		state = State.DONE;
		return object;
	}

	@Override
	public boolean isCancelled() {
		return state == State.CANCELLED;
	}

	@Override
	public boolean isDone() {
		return state == State.DONE;
	}

}
