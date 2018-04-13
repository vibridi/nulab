package com.vibridi.nulab.reader.sql;

public interface SQLStatementGenerator /* might extend a more generic DatabaseStatementGenerator */ {

	public void createTableStatement(String tableName);
	public void declareField(String field, String dataType);
	public void declarePrimaryKey(String... knownFields);
	public void declareForeignKey(String foreignKey);
	
	/**
	 * Outputs the generated statement
	 * @return Generated SQL statement
	 */
	public String generateStatement();
	
	/**
	 * Takes a symbol of abbreviation and converts it to an actual data type declaration
	 * @param symbol Data type symbol, e.g. N (number), S (string), etc.
	 * @param length Length of the data field. A negative value resolves to a default length
	 * @return The appropriate data type declaration for this SQL dialect.
	 */
	String resolveDataType(String symbol, int length);
}
