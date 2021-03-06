package com.vibridi.nulab.reader.er.impl;

import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vibridi.nulab.Messages;
import com.vibridi.nulab.exceptions.DiagramException;
import com.vibridi.nulab.exceptions.XMLException;
import com.vibridi.nulab.reader.NulabDiagramReader;
import com.vibridi.nulab.reader.er.ERCardinality;
import com.vibridi.nulab.reader.er.EREdge;
import com.vibridi.nulab.reader.er.EREntity;
import com.vibridi.nulab.reader.sql.SQLDialect;
import com.vibridi.nulab.reader.sql.SQLStatementGenerator;
import com.vibridi.nulab.reader.sql.SQLStatementGeneratorFactory;
import com.vibridi.nulab.utils.XMLUtils;

public class ERDiagramReader extends NulabDiagramReader {	

	private static final String _vattrUid = "uid";
	private static final String _vtagGroupText = "text";
	private static final String _vtagStart = "start";
	private static final String _vtagEnd = "end";
	private static final String _vattrStyle = "style";
	private static final String _vattrEdgeStart = "attr-edge-start";
	private static final String _vattrEdgeEnd = "attr-edge-end";
	private static final String _xlines = "/diagram/sheet/line";
	private static final String _xgroups = "/diagram/sheet/group";
	
	// Possible improvement: this field could be configured externally and passed to the constructor at runtime
	private SQLDialect dialect;
	private List<EREdge> erEdges;
	private List<EREntity> erEntities;
	private List<Entry<String,String>> output;
	
	public ERDiagramReader() {
		super();
		this.dialect = SQLDialect.ORACLE;
	}

	@Override
	public void read(InputStream in) throws Exception {
		Document dom = XMLUtils.streamToDocument(in);
		
		output = new ArrayList<>();
		erEntities = findNodes(dom);
		erEdges = findEdges(dom);
		
		// Basic implementation, assuming that one entity always corresponds to one DB table
		for(EREntity entity : erEntities) {
			SQLStatementGenerator generator = SQLStatementGeneratorFactory.newGenerator(dialect);
			generator.createTableStatement(entity.getName());
			
			for(String fieldAndType : entity.getFields()) {
				String[] tmp = fieldAndType.split(":", -1);
				String dataType = generator.resolveDataType(
						tmp.length > 1 ? tmp[1] : "", 
						tmp.length > 2 ? Integer.parseInt(tmp[2]) : -1);
				generator.declareField(tmp[0], dataType);
			}
			
			// Improvement: use the edges to determine foreign keys.
			// We would have to make several assumptions for this to work right here right now, so this is left out.
			// The idea is to assume that tables may have FKs pointing only to related tables, 
			// therefore when processing an entity, check for those connected to this one by looking at the edge ends,
			// then if the second table's PK matches any field in the first table, the first table declares a FK.
			
			generator.declarePrimaryKey(entity.getPrimaryKey());
			output.add(new AbstractMap.SimpleEntry<>(entity.getName().trim(), generator.generateStatement()));
		}
	}

	@Override
	public int outputSize() {
		return output.size();
	}

	@Override
	public String getOutput(int index) {
		return output.get(index).getValue();
	}

	@Override
	public String getOutputName(int index) {
		return output.get(index).getKey();
	}
	
	/**
	 * Finds the E/R entities from the diagram description.
	 * @param dom Source DOM
	 * @return list of entities
	 * @throws XMLException If an XML parsing error occurs
	 * @throws DiagramException If the diagram is semantically incorrect
	 */
	public List<EREntity> findNodes(Document dom) throws XMLException, DiagramException {
		NodeList nodes = (NodeList) XMLUtils.applyXPath(dom, _xgroups, null, XPathConstants.NODESET);
		
		List<EREntity> entities = new ArrayList<>();
		for(int i = 0; i < nodes.getLength(); i++) {
			entities.add(readEntity(nodes.item(i)));
		}
		
		return entities;		
	}
	
	/**
	 * Finds the edges from the diagram description. 
	 * Edges must be represented in this format:
	 * <pre>
		&lt;line attr-edge-end="Course" attr-edge-start="Student" uid="111111"&gt;
			&lt;start style="er_one_or_more"/&gt;
			&lt;end style="er_one_or_more"/&gt;
		&lt;/line&gt;
	 * </pre>
	 * 
	 * @param dom Source DOM
	 * @return list of edges
	 * @throws XMLException If an XML parsing error occurs
	 * @throws DiagramException If the diagram is semantically incorrect
	 */
	public List<EREdge> findEdges(Document dom) throws XMLException, DiagramException {
		NodeList nodes = (NodeList) XMLUtils.applyXPath(dom, _xlines, null, XPathConstants.NODESET);
		
		List<EREdge> edges = new ArrayList<>();
		for(int i = 0; i < nodes.getLength(); i++) {
			edges.add(readEdge(nodes.item(i)));
		}
		
		return edges;
	}

	private EREdge readEdge(Node node) throws DiagramException {
		String startElUid = node.getAttributes().getNamedItem(_vattrEdgeStart).getTextContent();
		String endElUid = node.getAttributes().getNamedItem(_vattrEdgeEnd).getTextContent();
		
		if(node.getNodeType() != Node.ELEMENT_NODE) {
			// Fail fast if for some weird reason we are reading attributes from a non-element node
			throw new IllegalStateException("<line> node is not an Element");
		}
		Element el = (Element) node;
		
		NodeList tag = el.getElementsByTagName(_vtagStart);
		if(tag.getLength() != 1)
			throw new DiagramException(Messages.INVALID_EDGE);
		
		// Cast is safe because we called getElementsByTagName
		ERCardinality startElCardinality = switchEdgeStyle((Element)tag.item(0));
		
		tag = el.getElementsByTagName(_vtagEnd);
		if(tag.getLength() != 1)
			throw new DiagramException(Messages.INVALID_EDGE);
		
		ERCardinality endElCardinality = switchEdgeStyle((Element)tag.item(0));
		
		
		return new EREdgeImpl(startElUid, startElCardinality, endElUid, endElCardinality);
	}
	
	private ERCardinality switchEdgeStyle(Element edgeTerminal) {
		String style = edgeTerminal.getAttribute(_vattrStyle);
		switch(style) {
		case "er_zero_or_one":
			return ERCardinality.ZERO_OR_ONE;
			
		case "er_zero_or_many":
			return ERCardinality.ZERO_OR_N;
			
		case "er_only_one":
			return ERCardinality.ONLY_ONE;
		
		case "er_one_or_more":
			return ERCardinality.ONE_OR_N;
		
		// We give any unaccounted case the broadest possible meaning
		default:
			return ERCardinality.ZERO_OR_N;
		}
	}
	
	private EREntity readEntity(Node node) throws DiagramException {
		String uid = node.getAttributes().getNamedItem(_vattrUid).getTextContent();
		if(node.getNodeType() != Node.ELEMENT_NODE) {
			// Fail fast if for some weird reason we are reading attributes from a non-element node
			throw new IllegalStateException("<group> node is not an Element");
		}
		Element el = (Element) node;
		NodeList tag = el.getElementsByTagName(_vtagGroupText);
		if(tag.getLength() < 2)
			throw new DiagramException(Messages.INVALID_ER_ENTITY);
		
		// We know the element order
		String name = tag.item(0).getTextContent().trim();
		String pk = tag.item(1).getTextContent().trim();
		
		List<String> fields = new ArrayList<>();
		fields.add(pk);
		String fieldsRaw = tag.item(2).getTextContent().trim();
		if(fieldsRaw != null && !fieldsRaw.isEmpty()) {
			fields.addAll(Arrays.asList(fieldsRaw.replaceAll("\r\n", "\n").split("\n", -1)));
		}
		
		return new EREntityImpl(uid, name, pk.split(":")[0], fields);
	}
	
}
