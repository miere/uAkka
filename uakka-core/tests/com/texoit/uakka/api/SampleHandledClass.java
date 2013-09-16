package com.texoit.uakka.api;

public class SampleHandledClass {

	void handle() {
	}

	void handle( SampleMessage message ) {

	}

	void handle( Integer integer ) {
		throw new NullPointerException();
	}
}
