package com.vibridi.nulab.reader;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import com.vibridi.nulab.TestResources;
import com.vibridi.nulab.reader.er.ERCardinality;
import com.vibridi.nulab.reader.er.EREdge;
import com.vibridi.nulab.reader.er.EREntity;
import com.vibridi.nulab.reader.er.impl.ERDiagramReader;
import com.vibridi.nulab.reader.factory.DiagramReaderFactory;

public class TestERDiagramReader {


	@Test
	public void findEdges() throws Exception {
		Document dom = TestResources.getAsDOM("er-diagram.xml");
		ERDiagramReader dr = (ERDiagramReader) DiagramReaderFactory.instance.forType(DiagramType.ER);
		List<EREdge> edges = dr.findEdges(dom);
		
		assertEquals(edges.size(), 6);
		assertEquals(edges.get(2).getStartElementUid(), "Course");
		assertEquals(edges.get(2).getStartElementCardinality(), ERCardinality.ONLY_ONE);
		assertEquals(edges.get(2).getEndElementUid(), "Class");
		assertEquals(edges.get(2).getEndElementCardinality(), ERCardinality.ONE_OR_N);
		assertEquals(edges.get(5).getStartElementUid(), "Section");
		assertEquals(edges.get(5).getStartElementCardinality(), ERCardinality.ZERO_OR_N);
		assertEquals(edges.get(5).getEndElementUid(), "Professor");
		assertEquals(edges.get(5).getEndElementCardinality(), ERCardinality.ONLY_ONE);
	}

	@Test
	public void findEntities() throws Exception {
		Document dom = TestResources.getAsDOM("er-diagram.xml");
		ERDiagramReader dr = (ERDiagramReader) DiagramReaderFactory.instance.forType(DiagramType.ER);
		List<EREntity> entities = dr.findNodes(dom);
				
		assertEquals(entities.size(), 7);
		assertEquals(entities.get(2).getName(), "Course");
		assertEquals(entities.get(2).getPrimaryKey(), "course_id");
		assertEquals(entities.get(3).getField(0), "instructor_name");
		assertEquals(entities.get(4).getName(), "Class");
		assertEquals(entities.get(4).getFields().size(), 4);
		assertEquals(entities.get(6).getName(), "Professor");
		assertEquals(entities.get(6).getField(1), "professor_faculty");
	}
	
	@Test
	public void generateStatement() throws Exception {
		InputStream in = TestResources.getAsStream("er-diagram.xml");
		ERDiagramReader dr = (ERDiagramReader) DiagramReaderFactory.instance.forType(DiagramType.ER);
		dr.read(in);
		String s = dr.getOutput(0);
		String benchmark = TestResources.getAsString("test-table-student.sql");
		assertEquals(s, benchmark);
	}
	
	
}
