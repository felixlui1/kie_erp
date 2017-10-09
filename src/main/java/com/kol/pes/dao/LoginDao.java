package com.kol.pes.dao;

import java.sql.SQLException;

import com.kol.pes.item.DataLoginItem;

public interface LoginDao {
	public DataLoginItem login(String userId) throws SQLException;
}
