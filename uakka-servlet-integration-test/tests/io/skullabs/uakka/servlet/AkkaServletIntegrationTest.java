package io.skullabs.uakka.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

public class AkkaServletIntegrationTest {

	AsyncHttpClient http = new AsyncHttpClient();
	Server server = new Server( 0 );
	String hostUrl;

	@Before
	public void setup() throws Exception {
		this.server.setHandler( createServletAssets() );
		this.server.start();

		int actualPort = ( (ServerConnector)this.server.getConnectors()[0] ).getLocalPort();
		this.hostUrl = "http://localhost:" + actualPort;
	}

	private WebAppContext createServletAssets() throws URISyntaxException {
		WebAppContext context = new WebAppContext( "test", "/" );
		context.setContextPath( "/" );
		// context.addServlet( CounterServlet.class, "/count" );
		context.setBaseResource( new ResourceCollection(
				new FileResource( new File( "source" ).toURI() ),
				new FileResource( new File( "tests" ).toURI() ),
				new FileResource( new File( "output/classes" ).toURI() ),
				new FileResource( new File( "output/test-classes" ).toURI() )
				) );
		context.setConfigurations( new Configuration[] {
				new WebInfConfiguration(),
				new WebXmlConfiguration(),
				new PlusConfiguration(),
				new AnnotationConfiguration()
		} );
		context.setWar( "source" );
		return context;
	}

	private void create() throws Exception {
		final URL url = new File( "target/classes" ).getAbsoluteFile().toURI().toURL();
		final Resource resource = new FileResource( url );
		final ResourceHandler handler = new ResourceHandler();
		handler.setBaseResource( resource );
	}

	@After
	public void after() throws Exception {
		this.server.stop();
	}

	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		ListenableFuture<Response> future = this.http.prepareGet(
				this.hostUrl + "/count" ).execute();
		Response response = future.get();
		Assert.assertEquals( "1", new String(
				response.getResponseBodyAsBytes() ) );
	}
}
