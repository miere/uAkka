package com.texoit.uakka.test;

import lombok.AllArgsConstructor;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.texoit.uakka.standalone.StandaloneUAkka;

@AllArgsConstructor
public class AsynchronousClassRunner implements Runnable {

	BlockJUnit4ClassRunner classRunner;
	RunNotifier notifier;
	StandaloneUAkka standalone;

	@Override
	public void run() {
		classRunner.run(notifier);
		standalone.shutdown();
	}
}
