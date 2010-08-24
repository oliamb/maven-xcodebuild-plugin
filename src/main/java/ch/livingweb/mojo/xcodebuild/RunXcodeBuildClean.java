package ch.livingweb.mojo.xcodebuild;

import java.io.*;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Run the xcodebuild command line program
 * @goal xcodebuild-clean
 * @phase clean
 */
public class RunXcodeBuildClean extends AbstractMojo {
	
	/**
	 * Location of the xcodebuild executable.
	 * @parameter expression="${xcodebuild}" default-value="/usr/bin/xcodebuild"
	 */
	private File xcodeCommandLine;
	
	/**
	 * Project Name
	 *
	 * @parameter
	 * @required
	 */
	private String xcodeProject;
	
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
	 * @parameter expression="${basedir}"
	 */
	private String basedir;
	
	/**
	 * Execute the xcode command line utility.
	 */
	public void execute() throws MojoExecutionException {
		if(! xcodeCommandLine.exists()){
			throw new MojoExecutionException("Invalid path, invalid xcodebuild file: " + xcodeCommandLine.getAbsolutePath());
		}
		try {
			ProcessBuilder pb = new ProcessBuilder(xcodeCommandLine.getAbsolutePath());
			if(xcodeProject != null){
			    pb.command().add("-project");
			    pb.command().add(xcodeProject + ".xcodeproj");
			}
            if(xcodeConfig != null){
                pb.command().add("-config " + xcodeConfig);
            }
            if(xcodeSdk != null){
                pb.command().add("-sdk " + xcodeSdk);
            }
			pb.command().add("clean");
			pb.directory(new File(basedir));
			getLog().info("Executing " + pb.command());
			Process child = pb.start();
			child.waitFor();
			getLog().info("Exit Value: " + child.exitValue());
	    } catch (IOException e) {
			getLog().error("An IOException occured.");
			throw new MojoExecutionException("An IOException occured", e);
	    } catch (InterruptedException e){
			getLog().error("The clean process was been interrupted.");
			throw new MojoExecutionException("The clean process was been interrupted", e);
		}
	}
}