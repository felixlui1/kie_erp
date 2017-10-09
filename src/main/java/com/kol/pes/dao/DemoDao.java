package com.kol.pes.dao;

import java.sql.SQLException;

public interface DemoDao {
	public String findById(int id) throws SQLException;
}
