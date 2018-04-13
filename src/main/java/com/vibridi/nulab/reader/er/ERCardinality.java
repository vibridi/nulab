package com.vibridi.nulab.reader.er;

/**
 * Enumeration of semantically relevant cardinalities.
 * Currently doesn't support options that are easily covered by broader ones.
 * E.g. ONE, arrow head {@code ---|-}, is semantically weaker than ZERO_OR_ONE, arrow head {@code ---o|-}
 * or ONLY_ONE, arrow head {@code ---||-}
 *
 */
public enum ERCardinality {
	ZERO_OR_ONE,				// ---o|-
	ONLY_ONE,					// ---||-
	ZERO_OR_N,					// ---o<-
	ONE_OR_N					// ---|<-
}
