package com.kol.pes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("demoDao")
public class DemoDaoImpl implements DemoDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	public String findById(int id) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select * from kol_pes_char_action", new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (!rs.next()) {
		            return null;
		        }
				
				return rs.getString(4);
			}
		});
	}
}
