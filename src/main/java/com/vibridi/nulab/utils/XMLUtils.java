package com.vibridi.nulab.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vibridi.nulab.exceptions.XMLException;

/**
 * Classic XML helpers suite. 
 * Provides a collection of static utility methods to manage XML manipulations.
 * Reusable objects are cached. 
 *
 */
public class XMLUtils {

	private static Map<String,Transformer> xslCache;
	private static Map<String,XPathExpression> expressionsCache;

	static {
		xslCache = new HashMap<String,Transformer>();
		expressionsCache = new HashMap<String, XPathExpression>();
	}

	protected XMLUtils() {
	}

	/**
	 * Create and return an empty XML document
	 * 
	 * @return empty document
	 * @throws XMLException if there are configuration problems
	 */
	public static Document emptyDocument() throws XMLException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch(ParserConfigurationException e) {
			throw new XMLException(e);
		}
	}
	
	/**
	 * Create and return an XML document with only the root element
	 * @param rootElementName The root element
	 * @return Document with the specified root element
	 * @throws XMLException if there are configuration problems
	 */
	public static Document emptyDocument(String rootElementName) throws XMLException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			doc.appendChild(doc.createElement(rootElementName));
			return doc;
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
		
	}

	/**
	 * Create and return an empty namespace aware XML document 
	 * 
	 * @return empty document
	 * @throws XMLException if there are configuration problems
	 */
	public static Document emptyDocumentNS() throws XMLException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().newDocument();
		} catch(ParserConfigurationException e) {
			throw new XMLException(e);
		}
	}


	/* ---------------------------------------- */
	/* ---------- CONVERSION METHODS ---------- */
	/* ---------------------------------------- */

	/**
	 * Convert the document to its string representation
	 * @param doc document to convert
	 * @return the XML string
	 * @throws XMLException if the transformation fails
	 */
	public static String documentToString(Document doc) throws XMLException {
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc.getDocumentElement());

		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.INDENT, "yes");			
			t.transform(source, result);
			return sw.toString();

		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}
	
	public static String elementToString(Element el) throws XMLException {
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(el);
		
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(source, result);
			return sw.toString();
			
		} catch (TransformerException e) {
			throw new XMLException(e);
		} 
		
		
	}
	
	/**
	 * Writes an XML string to a file. Overwrites any existing files
	 * @param xml XML string
	 * @param file The file to which the DOM will be written
	 * @param encoding A registered charset
	 * @throws XMLException if the transformation fails
	 * @throws FileNotFoundException if the file isn't found
	 */
	public static void stringToFile(String xml, File file, String encoding) throws XMLException, FileNotFoundException {
		documentToFile(stringToDocument(xml), file, encoding);
	}
	
	
	/**
	 * Writes a DOM object to a file. Overwrites any existing files
	 * @param doc Source DOM
	 * @param file The file to which the DOM will be written
	 * @param encoding A registered charset
	 * @throws XMLException if the transformation fails
	 * @throws FileNotFoundException if the file isn't found
	 */
	public static void documentToFile(Document doc, File file, String encoding) throws XMLException, FileNotFoundException {
		try {
			Writer writer = new PrintWriter(file, encoding);
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource(doc.getDocumentElement());

			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, encoding);
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(source, result);	

		} catch(UnsupportedEncodingException e) {	
			throw new XMLException("Trying to write to a file with unsupported encoding", e);

		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Writes a DOM object to stderr. Used for debug purposes.
	 * @param doc Source DOM
	 * @throws XMLException if the transformation fails
	 */
	public static void printToErr(Document doc) throws XMLException {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(System.err, "UTF-8");
			StreamResult result = new StreamResult(osw);
			DOMSource source = new DOMSource(doc.getDocumentElement());

			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(source, result);

		} catch(TransformerException e) {
			throw new XMLException(e);

		} catch(Exception e) {
			throw new XMLException("Could not execute transformation.", e);
		}
	}
	
	public static Document streamToDocument(InputStream in) throws XMLException {
		if(in == null)
			return null;
		
		try {
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(in);
			xmlDoc.normalize();
			return xmlDoc;

		} catch(Exception e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Takes the XML string and produces a Document object
	 * @param xml XML text
	 * @return the DOM representation of the given string
	 * @throws XMLException if the transformation fails
	 */
	public static Document stringToDocument(String xml) throws XMLException {
		if (xml == null || xml.trim().length() == 0)
			return null;
		return streamToDocument(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * Takes the XML string and produces a namespace-aware Document object
	 * @param xml XML text
	 * @return the DOM representation of the given string
	 * @throws XMLException if the transformation fails
	 */
	public static Document stringToDocumentNS(String xml) throws XMLException {
		if (xml == null || xml.trim().length() == 0)
			return null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document xmlDoc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
			xmlDoc.normalize();
			return xmlDoc;

		} catch(Exception e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Reads the content of a file into a DOM
	 * @param path Source file path
	 * @return Document object
	 * @throws XMLException if the transformation fails
	 * @throws IOException if the file cannot be processed
	 */
	public static Document fileToDocument(String path) throws XMLException, IOException {
		try(InputStream in = IOUtils.readResourceAsStream(path)) {
			if (in == null)
				throw new IllegalArgumentException("Source file yields a null stream");
			return streamToDocument(in);
		}
	}

	/**
	 * Reads the content of a file into a DOM
	 * 
	 * @param file A file with XML content 
	 * @return Document object
	 * @throws XMLException if the transformation fails
	 * @throws IOException if the file cannot be processed
	 */
	public static Document fileToDocument(File file) throws XMLException, IOException {
		InputStream in = new FileInputStream(file);

		try {	
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			return dbf.newDocumentBuilder().parse(in);
		} catch (Exception e) {
			throw new XMLException(e);
		} finally {
			in.close();
		}
	}

	/**
	 * Reads the content of a file into a namespace-aware DOM
	 * 
	 * @param file A file with XML content 
	 * @return Document object
	 * @throws XMLException if the transformation fails
	 * @throws IOException if the file cannot be processed
	 */
	public static Document fileToDocumentNS(File file) throws XMLException, IOException {
		InputStream in = new FileInputStream(file);

		try {	
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(in);
		} catch (Exception e) {
			throw new XMLException(e);
		} finally {
			in.close();
		}
	}

	/* ---------------------------------------- */
	/* ------- TRANSFORMATION METHODS --------- */
	/* ---------------------------------------- */

	/**
	 * Transforms the source XML text using the given XSL string. Namespace-unaware.
	 * 
	 * @param xml Original XML string
	 * @param xsl XSL string
	 * @return The transformed XML text, or the original text if no appropriate transformer is found
	 * @throws XMLException if the transformation fails
	 */
	public static String applyXSL(String xml, String xsl) throws XMLException {		
		Document doc = stringToDocument(xml);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		DOMSource sourceXml = new DOMSource(doc);
		StreamSource sourceXsl = new StreamSource(new StringReader(xsl));

		try {
			Transformer t = TransformerFactory.newInstance().newTransformer(sourceXsl);
			t.transform(sourceXml, result);
			return writer.toString();

		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}
	
	/**
	 * Transforms the source XML text using the given XSL string. Namespace-aware.
	 * 
	 * @param xml Original XML string
	 * @param xsl XSL string
	 * @return The transformed XML text, or the original text if no appropriate transformer is found
	 * @throws XMLException if the transformation fails
	 */
	public static String applyXSLNS(String xml, String xsl) throws XMLException {		
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		StreamSource sourceXml = new StreamSource(new StringReader(xml));
		StreamSource sourceXsl = new StreamSource(new StringReader(xsl));

		try {
			Transformer t = TransformerFactory.newInstance().newTransformer(sourceXsl);
			t.transform(sourceXml, result);
			return writer.toString();

		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Transform the source XML object using the xsltName resource/file,
	 *
	 * @param doc Source XML object
	 * @param xsl Path to XSL file
	 * @return Transformed document object, or the original document if no appropriate transformer is found
	 * @throws IOException if the XSL file cannot be found
	 * @throws XMLException if the transformation fails
	 */
	public static Document applyXSL(Document doc, String xsl) throws XMLException, IOException {
		DOMSource source = new DOMSource(doc);
		DOMResult result = new DOMResult();
		StreamSource sourceXsl = new StreamSource(new StringReader(xsl));
		
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer(sourceXsl);
			t.transform(source, result);
			return (Document) result.getNode();
			
		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}
	
	/**
	 * Transforms the source XML text using the given XSL resource
	 * @param xml Original XML string
	 * @param xslPath Path to XSL file
	 * @return The transformed XML text, or the original text if no appropriate transformer is found
	 * @throws XMLException if the transformation fails
	 * @throws IOException If the XSL file cannot be found
	 */
	public static String applyXSLFromFile(String xml, String xslPath) throws XMLException, IOException {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer); 

		Transformer t = findTransformer(xslPath);
		if (t == null)
			return xml;

		Document doc = stringToDocument(xml);
		DOMSource source = new DOMSource(doc);
		
		try {
			t.transform(source, result);
			return writer.toString();

		} catch(TransformerException e) {
			throw new XMLException(e);
		}	
	}
	
	/**
	 * Transforms the source XML text using the given XSL resource
	 * @param xmlSource Original XML string
	 * @param xslPath Path to XSL file
	 * @return The transformed XML text, or the original text if no appropriate transformer is found
	 * @throws XMLException if the transformation fails
	 * @throws IOException If the XSL file cannot be found
	 */
	public static String applyXSLFromFileNS(String xmlSource, String xslPath) throws XMLException, IOException {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer); 
		StreamSource source = new StreamSource(new StringReader(xmlSource));

		Transformer t = findTransformer(xslPath);
		if (t == null)
			return xmlSource;

		try {
			t.transform(source, result);
			return writer.toString();

		} catch(TransformerException e) {
			throw new XMLException(e);
		}	
	}

	/**
	 * Transform the source XML object using a given XSL string,
	 *
	 * @param doc Source XML object
	 * @param xslPath Path to XSL file
	 * @return Transformed document object, or the original document if no appropriate transformer is found
	 * @throws IOException if the XSL file cannot be found
	 * @throws XMLException if the transformation fails
	 */
	public static Document applyXSLFromFile(Document doc, String xslPath) throws IOException, XMLException {
		StreamSource source = new StreamSource(new StringReader(documentToString(doc)));
		DOMResult result = new DOMResult();
		
		try {
			Transformer t = findTransformer(xslPath);
			if (t == null) 
				return doc;
			
			t.transform(source, result);
			return (Document) result.getNode();
			
		} catch(TransformerException e) {
			throw new XMLException(e);
		}
	}

	private static Transformer findTransformer(String xslPath) throws XMLException, IOException {
		if(xslPath == null || xslPath.trim().length() == 0)
			throw new IllegalArgumentException("XSL filename [" + xslPath + "] must be a valid path");

		Transformer t = xslCache.get(xslPath);
		if(t != null)
			return t;

		InputStream in = IOUtils.readResourceAsStream(xslPath);
		if(in == null)  
			throw new IllegalArgumentException("XSL file yields a null stream");

		Source xsl = new StreamSource(in);
		try {
			if(xslPath.toLowerCase().endsWith(".xsl"))

				t = TransformerFactory.newInstance().newTemplates(xsl).newTransformer();
			else
				t = TransformerFactory.newInstance().newTransformer(xsl);
		} catch (TransformerConfigurationException e) {
			throw new XMLException("Cannot create transformer for xsl [" + xslPath + "]", e);
		}

		synchronized(xslCache) {
			xslCache.put(xslPath, t);
		}

		return t;
	}

	/* ---------------------------------------- */
	/* ------------ UTILITY METHODS ----------- */
	/* ---------------------------------------- */

	/**
	 * Lightweight stylesheet name extractor method. 
	 * 
	 * @param xmlDoc Document XML text to process
	 * @return The XSL pointed to or an empty string 
	 */
	public static String getDocumentStyleSheetName(String xmlDoc) {
		String head = "<?xml-stylesheet";
		String href = "href=";

		int n = xmlDoc.indexOf(head);
		if(n == -1)
			return "";

		int s = xmlDoc.indexOf(href, n + head.length());		
		if (s == -1)
			return "";

		int t = s + href.length() + 1;
		int u = t;
		for(; u < xmlDoc.length(); u++) {
			if(xmlDoc.charAt(u) == '"' || xmlDoc.charAt(u) == '\'')
				break;

			if(xmlDoc.charAt(u) == '>')
				return "";
		}

		if(u == xmlDoc.length())
			return "";

		return xmlDoc.substring(t, u);
	}
	
	/**
	 * Finds the prefix bound to a namespace declaration.
	 * 
	 * @param xml The original message
	 * @param namespace Value of the xmlns declaration
	 * @return Namespace prefix, or an empty string for the default namespace, or null
	 */
	public static String getNamespacePrefix(String xml, String namespace) {
		int n = xml.indexOf("=\"" + namespace + "\"");
		if (n == -1)
			return null;
		int q = xml.lastIndexOf(":", n);
		if (q == -1)
			return "";
		return xml.substring(q+1, n);
	}
	
	/**
	 * Fast routine to extract the content of a tag. Doesn't make use of XPaths.
	 * 
	 * @param tagName Name of the tag with namespace prefix
	 * @param body XML body
	 * @return Content of the tag. Example <code>&lt;MyTag&gt;ABCD&lt;/MyTag&gt;</code> returns <code>ABCD</code>. 
	 * <br>Or the original string if the algorithm fails. 
	 */
	public static String getTagTextContent(String tagName, String body) {
		String startTag = "<" + tagName;
		String endTag = "</" + tagName + ">";
		
		int start = -1;
		int end = -1;
		
		String lBody = body.toLowerCase();
		String lsTag = startTag.toLowerCase();
		String leTag = endTag.toLowerCase();
		
		start = lBody.indexOf(lsTag);
		
		if(start == -1)
			return body;
		
		int space = lBody.indexOf(" ", start);
		int gt = lBody.indexOf(">", start);

		if(gt == -1)
			return body;

		if(space == -1)
			space = gt;
		
		// check that the tag is actually the one we are looking for
		String checkTag = lBody.substring(start, Math.min(space, gt)).trim();
		if(!checkTag.equals(lsTag)) {
			return getTagTextContent(tagName, body.substring(gt+1));
		}

		end = lBody.indexOf(leTag, gt);
		return end != -1 ? body.substring(gt+1, end) : "";		
	}
	
	/**
	 * Fast routine to extract the content of a tag which has certain attributes. Doesn't make use of XPaths.
	 * Returns the tag content only. Attributes are used to identify the tag.
	 * 
	 * @param tagName
	 * @param attributes
	 * @param body
	 * @return
	 */
	public static String getTagTextContentAttr(String tagName, String[] attributes, String body) {
		String startTag = "<" + tagName;
		String endTag = "</" + tagName + ">";
		
		int start = -1;
		int end = -1;
		
		String lBody = body.toLowerCase();
		String lsTag = startTag.toLowerCase();
		String leTag = endTag.toLowerCase();
		
		start = lBody.indexOf(lsTag);
		
		if(start == -1)
			return "";
		
		int space = lBody.indexOf(" ", start);
		int gt = lBody.indexOf(">", start);

		if(gt == -1)
			return "";
		
		if(space == -1)
			space = gt;

		// check that the tag is actually the one we are looking for
		String checkTag = lBody.substring(start, Math.min(space, gt)).trim();
		if(!checkTag.equals(lsTag)) {
			return getTagTextContentAttr(tagName, attributes, body.substring(gt+1));
		}
		
		// check that the tag contains all attributes
		String attrTag = lBody.substring(start, gt).trim();
		
		boolean match = false;
		for(String a : attributes) {
			match = attrTag.contains(a.toLowerCase());
		}
		
		if(!match) {
			return getTagTextContentAttr(tagName, attributes, body.substring(gt+1));
		}
		
		end = lBody.indexOf(leTag, gt);	
		return end != -1 ? body.substring(gt+1, end) : "";
	}


	/**
	 * Applies the given XPath on the document object.
	 * Namespace awareness should be set on the <code>Document</code> object.
	 * 
	 * @param doc Source DOM
	 * @param request XPath directive
	 * @param ctx Namespace context
	 * @param returnType An <code>XPathConstants</code> element
	 * @return Object obtained from running the given XPath on the DOM
	 * @throws XMLException if the transformation fails
	 */	
	public static Object applyXPath(Document doc, String request, NamespaceContext ctx, QName returnType) throws XMLException {
		return applyXPath(doc.getDocumentElement(), request, ctx, returnType);
	}
	
	public static Object applyXPath(Node node, String request, NamespaceContext ctx, QName returnType) throws XMLException {
		if (request == null)
			return null;

		try {
			XPathExpression expr = expressionsCache.get(request);
			if(expr == null) {
				XPath xpath = XPathFactory.newInstance().newXPath();
				
				if(ctx != null)
					xpath.setNamespaceContext(ctx);
				
				expr = xpath.compile(request);
				expressionsCache.put(request, expr);
			}
			return expr.evaluate(node, returnType);
		} catch(XPathExpressionException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Applies the given XPath on the document object.
	 * Namespace unaware
	 * 
	 * @param xml Source DOM
	 * @param xpath XPath directive
	 * @param returnType An <code>XPathConstants</code> element
	 * @return Object obtained from running the given XPath on the DOM
	 * @throws XMLException if the transformation fails
	 */
	public static Object applyXPath(String xml, String xpath, QName returnType) throws XMLException {
		return applyXPath(stringToDocument(xml), xpath, null, returnType);
	}
	
	/**
	 * Applies the given XPath on the document object.
	 * Namespace aware
	 * 
	 * @param xml Source DOM
	 * @param xpath XPath directive
	 * @param returnType An <code>XPathConstants</code> element
	 * @return Object obtained from running the given XPath on the DOM
	 * @throws XMLException if the transformation fails
	 */
	public static Object applyXPathNS(String xml, String xpath, NamespaceContext ns, QName returnType) throws XMLException {
		return applyXPath(stringToDocumentNS(xml), xpath, ns, returnType);
	}
	
	public static NodeList getElements(String xml, String tagName) throws XMLException {
		return getElements(stringToDocument(xml), tagName);
	}
	
	public static NodeList getElements(Document doc, String tagName) {
		return doc.getElementsByTagName(tagName);
	}
	
	public static NodeList getElementsNS(String xml, String nsURI, String tagName) throws XMLException {
		return getElementsNS(stringToDocument(xml), nsURI, tagName);
	}
	
	public static NodeList getElementsNS(Document doc, String nsURI, String tagName) {
		return doc.getElementsByTagNameNS(nsURI, tagName);
	}
	
	
	public static String setNodeValue(String xml, String xpath, String value) throws XMLException {	
		return documentToString(setNodeValue(stringToDocument(xml), xpath, null, value));
	}
	
	public static String setNodeValueNS(String xml, String xpath, NamespaceContext ns, String value) throws XMLException {	
		return documentToString(setNodeValue(stringToDocumentNS(xml), xpath, ns, value));
	}
	
	public static Document setNodeValue(Document doc, String xpath, NamespaceContext ns, String value) throws XMLException {
		Node n = (Node) applyXPath(doc, xpath, ns, XPathConstants.NODE);
		n.getFirstChild().setTextContent(value);
		return doc;
	}
	
	public static String appendChild(String xml, String xpath, Node child) throws XMLException {
		return documentToString(appendChild(stringToDocument(xml), xpath, null, child));
	}
	
	public static String appendChildNS(String xml, String xpath, NamespaceContext ns, Node child) throws XMLException {
		return documentToString(appendChild(stringToDocumentNS(xml), xpath, ns, child));
	}
	
	public static Document appendChild(Document doc, String xpath, NamespaceContext ns, Node child) throws XMLException {
		Node n = (Node) applyXPath(doc, xpath, ns, XPathConstants.NODE);
		n.appendChild(child);
		return doc;
	}
	
	public static String removeChild(String xml, String xpath) throws XMLException {
		return documentToString(removeChild(stringToDocument(xml), xpath, null));
	}
	
	public static String removeChildNS(String xml, String xpath, NamespaceContext ns) throws XMLException {
		return documentToString(removeChild(stringToDocumentNS(xml), xpath, ns));
	}
	
	public static Document removeChild(Document doc, String xpath, NamespaceContext ns) throws XMLException {
		Node n = (Node) applyXPath(doc, xpath, ns, XPathConstants.NODE);
		n.getParentNode().removeChild(n);
		return doc;
	}
	
	/**
	 * Explode the XML document transforming it with the XSLT and splitting each tagName
	 * 
	 * @param xmlDoc source XML document
	 * @param xsl XSLT file/resource path (null to ignore XSLT transformation on the given xmlDoc)
	 * @param tagName name of each document bean tag in XML
	 * @return the list of <code>tagName</code> nodes from the transformed document
	 * @throws XMLException if the transformation fails
	 * @throws IOException if a read error occurs
	 */
	public static List<Node> extractNodeList(String xmlDoc, String xsl, String tagName) throws XMLException, IOException {
		List<Node> results = new ArrayList<Node>();
		
		Document tmp = stringToDocument(applyXSL(xmlDoc, xsl));
		NodeList nl = tmp.getElementsByTagName(tagName);
		
		if (nl != null){
			for(int i = 0; i < nl.getLength(); i++) {
				results.add(nl.item(i));
			}
		}
		
		return results;	
	}

	/**
	 * Explode the XML document transforming it with the XSLT and splitting each tagName
	 * 
	 * @param xmlDoc source XML document
	 * @param xslPath XSLT file/resource path (null to ignore XSLT transformation on the given xmlDoc)
	 * @param tagName name of each document bean tag in XML
	 * @return the list of <code>tagName</code> nodes from the transformed document
	 * @throws XMLException if the transformation fails
	 * @throws IOException if a read error occurs
	 */
	public static List<Node> extractNodeListFromFile(String xmlDoc, String xslPath, String tagName) throws XMLException, IOException {
		List<Node> results = new ArrayList<Node>();
		
		Document tmp = stringToDocument(applyXSLFromFile(xmlDoc, xslPath));
		NodeList nl = tmp.getElementsByTagName(tagName);
		
		if (nl != null){
			for(int i = 0; i < nl.getLength(); i++) {
				results.add(nl.item(i));
			}
		}
		
		return results;	
	}

}
