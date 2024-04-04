package io.github.nahkd123.inking.maven;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "strip-preview-flags", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class StripPreviewFlagsMojo extends AbstractMojo {
	private static final byte[] CAFEBABE = new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };

	@Parameter(defaultValue = "${project.build.outputDirectory}")
	private File inputDir;

	public void execute() throws MojoExecutionException {
		int processed = processFile(inputDir);
		getLog().info("Processed " + processed + " classes");
	}

	private int processFile(File file) {
		if (file.isDirectory()) {
			int processed = 0;
			for (File child : file.listFiles()) processed += processFile(child);
			return processed;
		}

		if (!file.getName().endsWith(".class")) return 0;

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(0L);
			byte[] header = new byte[4];
			raf.readFully(header);

			if (!Arrays.equals(header, CAFEBABE)) {
				getLog().error(file + ": Header does not starts with 0xCAFEBABE");
				return 0;
			}

			raf.writeShort(0); // 0x0000 - minor version
		} catch (IOException e) {
			e.printStackTrace();
			getLog().error("Cannot process " + file);
		}

		return 1;
	}
}
