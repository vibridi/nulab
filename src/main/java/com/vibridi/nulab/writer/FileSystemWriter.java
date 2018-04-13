package com.vibridi.nulab.writer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import com.vibridi.nulab.writer.DiagramOutputWriter.DiagramWriterStrategy;

public class FileSystemWriter implements DiagramWriterStrategy {

	private static final String EXTENSION = "sql";
	private String destinationFolder;
	
	public FileSystemWriter() {
		try {
			destinationFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch(URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public FileSystemWriter(String destinationFolder) {
		Objects.requireNonNull(destinationFolder);
		this.destinationFolder = destinationFolder;
	}

	@Override
	public void write(String output, String name) throws IOException {
		File folder = new File(destinationFolder).getParentFile();
		File fout = new File(folder, "/sqloutput/" + name + "." + EXTENSION);
		
		if(!fout.getParentFile().exists()) {
			fout.getParentFile().mkdirs();
		}
			
		Files.write(fout.toPath(), output.getBytes());
	}

}
