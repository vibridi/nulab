package com.vibridi.nulab.reader.er;

import com.vibridi.nulab.reader.model.DiagramEdge;

public interface EREdge extends DiagramEdge {
	public ERCardinality getStartElementCardinality();
	public ERCardinality getEndElementCardinality();
}
