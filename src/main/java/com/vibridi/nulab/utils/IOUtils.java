package com.vibridi.nulab.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class IOUtils {

	/**
	 * Reads the stream into a string
	 */
	public static String toString(InputStream stream, Charset chs) throws IOException{
		if (stream == null)
			return "";

		BufferedReader rd = new BufferedReader(new InputStreamReader(stream, chs));
		int r;
		char[] buffer = new char[1024];
		StringBuilder response = new StringBuilder();
		while((r = rd.read(buffer,0,buffer.length)) != -1) {
			response.append(buffer,0,r);
		}
		rd.close();
		return response.toString();
	}

	/**
	 * Reads the required resource basing the relative path on working base possibly set using initConfiguration
	 * @param resourcePath relative path
	 * @return the resource as InputStream
	 * @throws IOException If the resource isn't available
	 */
	public static InputStream readResourceAsStream(String resourcePath) throws IOException {
		File f = new File(resourcePath);
		if (!f.exists())
			throw new IllegalArgumentException("Specified file [" + resourcePath + "] does not exist.");
		return new FileInputStream(f);
	}
}
