package com.vibridi.nulab.reader.sql;

public class PostgresStatementGenerator implements SQLStatementGenerator {

	private static final int DEFAULT_NUMERIC_LEN = 10;
	private static final int DEFAULT_VARCHAR_LEN = 50;
	
	@Override
	public void createTableStatement(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareField(String field, String dataType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declarePrimaryKey(String... knownFields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareForeignKey(String foreignKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateStatement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resolveDataType(String symbol, int length) {
		switch(symbol) {
		case "N":
			return String.format("numeric(%d)", length == -1 ? DEFAULT_NUMERIC_LEN : length); 
		
		case "S":
			return String.format("varchar2(%d)", length == -1 ? DEFAULT_VARCHAR_LEN : length);
		
		case "D":
			return String.format("date");
					
		default:
			return String.format("varchar2(%d)", length == -1 ? DEFAULT_VARCHAR_LEN : length);
		}
	}

}
