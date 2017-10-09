package com.kol.pes.service;

import com.kol.pes.item.DataLoginItem;

public interface LoginService extends DataEnableService {
	public DataLoginItem login(String userId);//登录
}
