package com.vibridi.nulab.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vibridi.nulab.writer.DiagramOutputWriter.DiagramWriterStrategy;

public class FileSystemWriter implements DiagramWriterStrategy {

	private static final String EXTENSION = "sql";
	private String destinationFolder;
	
	public FileSystemWriter() {
		destinationFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
	}
	
	public FileSystemWriter(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	@Override
	public void write(String output, String name) throws IOException {
		File fout = new File(destinationFolder, name + "." + EXTENSION);
		Path path = Paths.get(fout.toURI());

		if(!Files.isWritable(path)) {
			throw new IOException(String.format("Cannot write to path %s", fout.toString()));
		} 
		
		Files.write(path, output.getBytes());
	}

}
