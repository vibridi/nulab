package com.vibridi.nulab.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.vibridi.nulab.reader.DiagramReader;

public class DiagramOutputWriter {
	
	private static Logger logger = Logger.getLogger("NULAB");
	
	/**
	 * Defines a writer interface that allows to call {@link DiagramOutputWriter#write(DiagramWriterStrategy)} 
	 * multiple times with different output results.
	 */
	@FunctionalInterface
	public interface DiagramWriterStrategy {
		public void write(String output, String name) throws IOException;
	}
	
	private DiagramReader reader;
	private DiagramWriterStrategy writer; 
	
	public DiagramOutputWriter(DiagramReader reader) {
		this.reader = reader;
	}

	public DiagramOutputWriter setWriteStrategy(DiagramWriterStrategy writer) {
		this.writer = writer;
		return this;
	}
	
	/**
	 * Same as calling write() with the default writer. The default writer writes to <user.home>/nulab/ on the local filesystem.
	 * Call {@code setWriteStrategy} to override the default writer implementation.
	 * @throws IOException
	 */
	public void write() throws IOException {
		if(writer == null) {
			logger.warn("Using default writer.");
		}
		
		this.write(writer == null ? getDefaultWriter() : writer);
	}
	
	/**
	 * Atomically writes all output using this writing strategy.
	 * @param writer Writer object
	 * @return Number of files written. Since the operation is atomical, it must be equal to the number of outputs of the reader.
	 * @throws IOException If writing fails
	 */
	public int write(DiagramWriterStrategy writer) throws IOException {
		for(int i = 0; i < reader.outputSize(); i++)
			writer.write(reader.getOutput(i), reader.getOutputName(i));
		
		return reader.outputSize();
	}
	
	private DiagramWriterStrategy getDefaultWriter() {
		final File f = new File(System.getProperty("user.home"), "nulab");
		if(!f.exists())
			f.mkdirs();
				
		return (output, name) -> {
			File fout = new File(f, name);
			if(!fout.exists())
				f.createNewFile();
			try(FileWriter w = new FileWriter(fout)) {
				w.write(output);
			}
		};		
	}
}
