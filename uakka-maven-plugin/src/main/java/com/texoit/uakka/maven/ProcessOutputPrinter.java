package com.texoit.uakka.maven;

import java.io.IOException;
import java.io.InputStream;

public class ProcessOutputPrinter implements Runnable {

	private InputStream pipe;

	public ProcessOutputPrinter( InputStream pipe ) {
		if ( pipe == null )
			throw new NullPointerException( "bad pipe" );
		this.pipe = pipe;
	}

	@Override
	public void run() {
		try {
			doReading();
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if ( this.pipe != null )
				try {
					this.pipe.close();
				} catch ( IOException e ) {
				}
		}
	}

	void doReading() throws IOException {
		byte buffer[] = new byte[2048];
		int read = this.pipe.read( buffer );
		while ( read >= 0 ) {
			System.out.write( buffer, 0, read );
			read = this.pipe.read( buffer );
		}
	}
}