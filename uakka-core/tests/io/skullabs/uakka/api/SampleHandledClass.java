package io.skullabs.uakka.api;

public class SampleHandledClass {

	void handle() {
	}

	void handle( SampleMessage message ) {

	}

	void handle( Integer integer ) {
		throw new NullPointerException();
	}
}
