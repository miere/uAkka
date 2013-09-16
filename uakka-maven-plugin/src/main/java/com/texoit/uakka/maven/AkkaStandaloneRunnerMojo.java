package com.texoit.uakka.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

import com.texoit.uakka.standalone.StandaloneAkkaInitialization;

/**
 * @goal run
 * @phase test
 * @execute phase=test
 * @aggregator true
 * @requiresDependencyResolution compile+runtime
 */
public class AkkaStandaloneRunnerMojo extends AbstractMojo {

	final static String SEPARATOR = System.getProperty( "path.separator" );

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	MavenProject project;

	/**
	 * Directory containing the build files.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	File buildDirectory;

	/**
	 * Name of the generated JAR.
	 * 
	 * @parameter alias="jarName" expression="${jar.finalName}"
	 *            default-value="${project.build.finalName}"
	 * @required
	 */
	String finalName;

	/**
	 * @parameter default-value="${plugin.artifacts}"
	 */
	List<Artifact> pluginArtifacts;

	/**
	 * @parameter default-value="${plugin}"
	 */
	PluginDescriptor plugin;

	/** @parameter default-value="${localRepository}" */
	ArtifactRepository localRepository;

	/**
	 * Used to construct artifacts for deletion/resolution...
	 * 
	 * @component
	 */
	ArtifactFactory factory;

	/**
	 * @component
	 */
	ArtifactResolver resolver;

	StringBuilder classPath = new StringBuilder();
	String standaloneJar;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			measureClassPathAndStripRunnableJar();
			String commandLineString = getCommandLineString();
			run( commandLineString );
		} catch ( Exception e ) {
			throw new MojoExecutionException( "Can't initialize Akka.", e );
		}
	}

	void run( String commandLineString ) throws IOException, InterruptedException {
		Process exec = Runtime.getRuntime().exec( commandLineString );
		printAsynchronously( exec.getInputStream() );
		printAsynchronously( exec.getErrorStream() );
		if ( exec.waitFor() > 0 )
			throw new RuntimeException( "The micro Akka has failed to run." );
	}

	void printAsynchronously( InputStream stream ) {
		new Thread( new ProcessOutputPrinter( stream ) ).start();
	}

	String getCommandLineString() {
		return String.format(
				"java -cp %s %s %s",
				this.classPath.toString(),
				StandaloneAkkaInitialization.class.getCanonicalName(),
				this.project.getArtifactId() );
	}

	@SuppressWarnings( "unchecked" )
	void measureClassPathAndStripRunnableJar()
			throws DependencyResolutionRequiredException, ArtifactResolutionException, ArtifactNotFoundException {
		for ( String element : (List<String>)this.project.getRuntimeClasspathElements() )
			this.classPath.append( element ).append( SEPARATOR );
		this.classPath
				.append( getFinalArtifactName() ).append( SEPARATOR )
				.append( resolveUAkkaStadalone() );
	}

	String resolveUAkkaStadalone() throws ArtifactResolutionException, ArtifactNotFoundException {
		Artifact uAkkaStandalone = getUAkkaStandalone();
		this.resolver.resolve( uAkkaStandalone, Collections.EMPTY_LIST, this.localRepository );
		return uAkkaStandalone.getFile().getAbsolutePath();
	}

	Artifact getUAkkaStandalone() {
		return this.factory.createDependencyArtifact(
				this.project.getGroupId(), "uakka-standalone",
				VersionRange.createFromVersion( this.project.getVersion() ), "jar", "", Artifact.SCOPE_RUNTIME );
	}

	String getFinalArtifactName() {
		String fileName = String.format( "%s.%s", this.finalName, this.project.getPackaging() );
		return new File( this.buildDirectory, fileName ).getAbsolutePath();
	}
}
