package com.vibridi.nulab.reader.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OracleStatementGenerator implements SQLStatementGenerator {

	private static final int DEFAULT_NUMERIC_LEN = 10;
	private static final int DEFAULT_VARCHAR_LEN = 50;
	
	private String createTable;
	private String primaryKey;
	private List<String> fieldDeclarations;
	
	public OracleStatementGenerator() {
		fieldDeclarations = new ArrayList<>();
	}
	
	@Override 
	public String generateStatement() {
		if(primaryKey != null || !primaryKey.isEmpty())
			fieldDeclarations.add(primaryKey);
		
		return String.format(
				"%s (\n%s\n);", 
				createTable, 
				fieldDeclarations.stream().collect(Collectors.joining(",\n")));
	}

	@Override
	public void createTableStatement(String tableName) {
		createTable = String.format("CREATE TABLE %s", tableName);
	}

	@Override
	public void declareField(String field, String dataType) {
		fieldDeclarations.add(String.format("%s %s", field.trim(), dataType));
	}

	@Override
	public void declarePrimaryKey(String... knownFields) {
		if(knownFields.length < 1)
			return;
		
		primaryKey = String.format("CONSTRAINT pk PRIMARY KEY (%s)", 
				Stream.of(knownFields).collect(Collectors.joining(",")));
	}

	@Override
	public void declareForeignKey(String foreignKey) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Foreign keys currently not supported");
	}

	@Override
	public String resolveDataType(String symbol, int length) {
		switch(symbol.trim()) {
		case "N":
			return String.format("numeric(%d)", length < 0 ? DEFAULT_NUMERIC_LEN : length); 
		
		case "S":
			return String.format("varchar2(%d)", length < 0 ? DEFAULT_VARCHAR_LEN : length);
		
		case "D":
			return String.format("date");
					
		default:
			return String.format("varchar2(%d)", length < 0 ? DEFAULT_VARCHAR_LEN : length);
		}
	}

}
