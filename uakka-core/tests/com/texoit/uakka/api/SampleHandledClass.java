package com.texoit.uakka.api;

public class SampleHandledClass {

	@Receiver
	void handle() {
	}

	@Receiver
	void handle( SampleMessage message ) {

	}

	@Receiver
	void handle( Integer integer ) {
		throw new NullPointerException();
	}
}
