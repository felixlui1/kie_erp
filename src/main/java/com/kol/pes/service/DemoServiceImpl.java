package com.kol.pes.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.DemoDao;

@Service("demoService")
public class DemoServiceImpl implements DemoService {
	
	@Autowired
	@Qualifier("demoDao")
	private DemoDao demoDao;

	public String find(int id) {
		try {
			return this.demoDao.findById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
