package com.vibridi.nulab.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import com.vibridi.nulab.exceptions.HttpException;
import com.vibridi.nulab.utils.IOUtils;

public class HTTP {
	
	private static Logger logger = Logger.getLogger("NULAB");
	
	public enum Method {
		GET,
		POST,
		PUT,
		DELETE,
		HEAD,
		OPTIONS,
		CONNECT
	}

	private StringJoiner sj;
	private Map<String,String> params;
	private Map<String,String> headers;
	
	/**
	 * Creates a new HTTP builder
	 * @param url The base URL
	 * @return Instance of an HTTP builder
	 */
	public static HTTP build(String url) {
		return new HTTP(url);
	}
	
	private HTTP(String url) {
		this.sj = new StringJoiner("/").add(url);
		this.params = new HashMap<String,String>();
		this.headers = new HashMap<String,String>();
	}	
	
	// ******* PUBLIC METHODS ********
	public String getFullUrl() {
		return resolveParams();
	}
	
	/**
	 * Appends a path to the current URL. Don't add any '/' prefix or suffix.
	 * @param path Path element to add
	 * @return this
	 */
	public HTTP appendPath(String path) {
		sj.add(path);
		return this;
	}
	
	// ******* QUERY PARAMS ********
	/**
	 * Sets a query parameter, that will be later resolved to ?key=value&... and appended to this URL.
	 * @param key Query key
	 * @param value Query value
	 * @return this
	 */
	public HTTP setQueryParam(String key, String value) {
		params.put(key, value);
		return this;
	}
	
	public HTTP setQueryParams(Map<String,String> params) {
		this.params = params;
		return this;
	}
	
	// ******* HTTP HEADERS ********
	/**
	 * Sets the {@code Accept} header.
	 * @param mimeType Accepted mime type
	 * @return this
	 */
	public HTTP setAccept(MIMEType mimeType) {
		headers.put("Accept", mimeType.stringValue());
		return this;
	}
	
	/**
	 * Set the <code>Connection</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setConnection(String value) {
		headers.put("Connection", value);
		return this;
	}
	
	/**
	 * Set the <code>Content-Type</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setContentType(String mimeType) {
		headers.put("Content-Type", mimeType);
		return this;
	}
	
	/**
	 * Set the <code>Host</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setHost(String value) {
		headers.put("Host", value);
		return this;
	}
	
	/**
	 * Set the <code>Origin</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setOrigin(String value) {
		headers.put("Origin", value);
		return this;
	}
	
	/**
	 * Set the <code>Pragma</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setPragma(String value) {
		headers.put("Pragma", value);
		return this;
	}
	
	/**
	 * Set the <code>User-Agent</code> header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setUserAgent(String value) {
		headers.put("User-Agent", value);
		return this;
	}
	
	// ******* GENERIC HEADERS ********
	/**
	 * Set any header.
	 * @param value header value
	 * @return this
	 */
	public HTTP setHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}
	
	public HTTP setHeaders(Map<String,String> headers) {
		this.headers = headers;
		return this;
	}

	
	// ******* HTTP METHODS ********
	/**
	 * Executes an HTTP GET with the current URL, headers and query parameters
	 * @return Server's response
	 */
	public String get() throws IOException {
		HttpURLConnection conn = getConnection();
		conn.setRequestMethod(Method.GET.name());
		return execute(conn);
	}
	
	/**
	 * Executes an HTTP POST with the current URL, headers and query parameters
	 * @param payload The payload
	 * @return Server's response
	 */
	public String post(String payload) throws IOException {
		HttpURLConnection conn = getConnection();
		conn.setRequestMethod(Method.POST.name());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		try(DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			out.write(payload.getBytes());
			out.flush();
		}
		return execute(conn);		
	}
	
	/**
	 * Executes an HTTP PUT with the current URL, headers and query parameters
	 * @param payload The payload
	 * @return Server's response
	 */
	public String put(String payload) throws IOException {
		HttpURLConnection conn = getConnection();
		conn.setRequestMethod(Method.PUT.name());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		try(DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			out.writeBytes(payload);
			out.flush();
		}
		return execute(conn);
	}
	
	/**
	 * Executes an HTTP DELETE with the current URL, headers and query parameters
	 * @return Server's response
	 */
	public String delete() throws IOException {
		HttpURLConnection conn = getConnection();
		conn.setRequestMethod(Method.DELETE.name());
		return execute(conn);
	}
	
	
	// ******* PRIVATE METHODS ********
	private HttpURLConnection getConnection() throws MalformedURLException, IOException {
		URL url = new URL(resolveParams());
		HttpURLConnection conn = null;
		
		if (url.toString().toLowerCase().startsWith("https")){
			conn = (HttpsURLConnection) url.openConnection();
			((HttpsURLConnection) conn).setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		
		resolveHeaders(conn);
		return conn;
	}
	
	/**
	 * 
	 * @param conn
	 * @return
	 * @throws IOException in case of connection errors (timeouts, connection refused, etc...)
	 * @throws HttpException in case of HTTP errors. The exception object contains the response code and 
	 * the content of the error stream
	 */
	private String execute(HttpURLConnection conn) throws IOException, HttpException {
		int responseCode = conn.getResponseCode(); // throws if it can't get the resp code
		logger.info("HTTP response code: " + responseCode);
			
		try {
			String response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
			logger.debug(response);
			return response; // all good, here usually resp code is 200
			
		} catch(Exception e) { // couldn't read the input stream, try reading error stream
			String error = "";
			try {
				error = IOUtils.toString(conn.getErrorStream(), StandardCharsets.UTF_8);
			} catch(IOException f) { // couldn't read the error stream, though resp code is known
				throw new HttpException(e.getMessage(), f, responseCode);
			}
			throw new HttpException(error, e, responseCode); // could read the error stream and resp code is known
		}
	}
	
	private String resolveParams() {
		if(params.size() < 1)
			return sj.toString();
		
		StringBuffer tmp = new StringBuffer(sj.toString());
		tmp.append("?");
		Iterator<String> itr = params.keySet().iterator();
		do {
			String k = itr.next();
			tmp.append(k).append("=").append(params.get(k));	
		} while(itr.hasNext() && tmp.append("&").length() > 0);
		
		return tmp.toString();
	}
	
	private void resolveHeaders(HttpURLConnection conn) {
		for(Map.Entry<String, String> entry : headers.entrySet())
			conn.setRequestProperty(entry.getKey(), entry.getValue());
	}
	
}
