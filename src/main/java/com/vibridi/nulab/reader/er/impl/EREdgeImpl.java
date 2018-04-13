package com.vibridi.nulab.reader.er.impl;

import java.util.StringJoiner;

import com.vibridi.nulab.reader.er.ERCardinality;
import com.vibridi.nulab.reader.er.EREdge;

/**
 * Read-only struct that describes an E/R edge, including identifiers and cardinality of the terminal elements.
 */
public class EREdgeImpl implements EREdge {

	private final String startElementUid;
	private final ERCardinality startElementCardinality;

	private final String endElementUid;
	private final ERCardinality endElementCardinality;
	
	public EREdgeImpl(String startElementUid, ERCardinality startElementCardinality,
			String endElementUid, ERCardinality endElementCardinality) {
		
		this.startElementUid = startElementUid;
		this.startElementCardinality = startElementCardinality;
		this.endElementUid = endElementUid;
		this.endElementCardinality = endElementCardinality;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(", ", "[", "]");
		sj.add(startElementUid);
		sj.add(startElementCardinality.name());
		sj.add(endElementUid);
		sj.add(endElementCardinality.name());
		return sj.toString();
	}

	@Override
	public String getStartElementUid() {
		return startElementUid;
	}
	
	@Override
	public ERCardinality getStartElementCardinality() {
		return startElementCardinality;
	}

	@Override
	public String getEndElementUid() {
		return endElementUid;
	}


	@Override
	public ERCardinality getEndElementCardinality() {
		return endElementCardinality;
	}

}
