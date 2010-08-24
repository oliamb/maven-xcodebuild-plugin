package ch.livingweb.mojo.xcodebuild;

import java.io.*;
import java.util.*;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Run the xcodebuild command line program
 * @goal xcodebuild
 * @phase compile
 */
public class RunXcodeBuild extends AbstractMojo {
	
	/**
	 * Location of the xcodebuild executable.
	 * @parameter expression="${xcodebuild}" default-value="/usr/bin/xcodebuild"
	 */
	private File xcodeCommandLine;
	
	/**
	 * Project Name
	 *
	 * @parameter
	 */
	private String xcodeProject;
	
	/**
	 * Target to be built
	 *
	 * @parameter
	 */
	private String xcodeTarget;
	
    /**
     * config to be built
     *
     * @parameter
     */
    private String xcodeConfig;
    
    /**
     * sdk to be built
     *
     * @parameter
     */
    private String xcodeSdk;

	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	/**
	 * @parameter expression="${basedir}"
	 */
	private String basedir;
	
	/**
	 * @parameter default-value="build"
	 */
	private String buildDir;
	
	/**
	* Build directory.
	* @parameter expression="${project.build.directory}"
	* @required
	*/
	private File buildDirectory;
	
	/**
	 * Execute the xcode command line utility.
	 */
	public void execute() throws MojoExecutionException {
		if(! xcodeCommandLine.exists()){
			throw new MojoExecutionException("Invalid path, invalid xcodebuild file: " + xcodeCommandLine.getAbsolutePath());
		}
		/*
		// Compute archive name
		String archiveName = project.getBuild().getFinalName() + ".cust";
		File finalDir = new File(buildDirectory, archiveName);
		
		// Configure archiver
		MavenArchiver archiver = new MavenArchiver();
	    archiver.setArchiver(jarArchiver);
	    archiver.setOutputFile(finalDir);
		*/
		
		try {
			ProcessBuilder pb = new ProcessBuilder(xcodeCommandLine.getAbsolutePath());
			// Include errors in output
			pb.redirectErrorStream(true);
			
			if(xcodeProject != null){
				pb.command().add("-project");
				pb.command().add(xcodeProject + ".xcodeproj");
			}
			if(xcodeTarget != null){
				pb.command().add("-target " + xcodeTarget);
			}
            if(xcodeConfig != null){
                pb.command().add("-config " + xcodeConfig);
            }
            if(xcodeSdk != null){
                pb.command().add("-sdk " + xcodeSdk);
            }
			pb.command().add("install");
			pb.directory(new File(basedir));
			getLog().info("Executing " + pb.command());
			Process child = pb.start();
			
			// Consume subprocess output and write to stdout for debugging
			InputStream is = new BufferedInputStream(child.getInputStream());
			int singleByte = 0;
            while ((singleByte = is.read()) != -1) {
                //output.write(buffer, 0, bytesRead);
                System.out.write(singleByte);
            }
			
			child.waitFor();
			getLog().info("Exit Value: " + child.exitValue());
			/*
			child.waitFor();
			
			InputStream in = child.getInputStream();
			InputStream err = child.getErrorStream();
			getLog().error(sb.toString());
			*/
	    } catch (IOException e) {
			getLog().error("An IOException occured.");
			throw new MojoExecutionException("An IOException occured", e);
	    } catch (InterruptedException e){
			getLog().error("The clean process was been interrupted.");
			throw new MojoExecutionException("The clean process was been interrupted", e);
		}
		File directory = new File(this.basedir + "/pom.xml");
		this.project.getArtifact().setFile(directory);
	}
}