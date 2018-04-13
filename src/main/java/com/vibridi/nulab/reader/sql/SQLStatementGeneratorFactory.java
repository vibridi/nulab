package com.vibridi.nulab.reader.sql;

import com.vibridi.nulab.Messages;

public class SQLStatementGeneratorFactory {
	
	public static SQLStatementGenerator newGenerator(SQLDialect dialect) {
		SQLStatementGenerator sqlGen = null;
		switch(dialect) {
		case ORACLE:
			sqlGen = new OracleStatementGenerator();
			break;
			
		case POSTGRES:
			sqlGen = new PostgresStatementGenerator();
			break;
			
		default:
			throw new IllegalStateException(Messages.UNKNOWN_ENUM);
		}
		
		return sqlGen;
	}
	

}
