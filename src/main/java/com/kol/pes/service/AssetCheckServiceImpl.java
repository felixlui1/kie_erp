/*-----------------------------------------------------------

-- PURPOSE

--    点检设备相关的Service

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.AssetCheckDao;
import com.kol.pes.item.DataAssetCheckAssetListItem;
import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.item.DataAssetCheckItem;
import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataAssetMachineFailItem;

@Service("assetCheckService")
public class AssetCheckServiceImpl extends DataEnableServiceImpl implements AssetCheckService {
	
	@Autowired
	@Qualifier("assetCheckDao")
	private AssetCheckDao assetCheckDao;
	
	public List<DataAssetCheckItem> findAssetWithError() {
		try {
			return this.assetCheckDao.findAssetWithError();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getCheckId() {
		try {
			return this.assetCheckDao.getCheckId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "0";
	}
	
	public DataAssetInfoItem getAssetInfo(String tagNum) {
		try {
			return this.assetCheckDao.getAssetInfo(tagNum);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public int insertCheck(String CHECK_ID, Date CREATION_DATE, int CREATED_BY, 
							 Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
							 String ASSET_ID, Date CHECK_TIME, int CHECK_RESULT, 
							 Date EST_REPAIR_START, Date EST_REPAIR_END, 
							 String CHECK_REMARKS) {
		try {
			return this.assetCheckDao.insertCheck(CHECK_ID, CREATION_DATE, CREATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY, ASSET_ID, CHECK_TIME, CHECK_RESULT, EST_REPAIR_START, EST_REPAIR_END, CHECK_REMARKS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int insertPicPathDesc(String CHECK_ID, String pathDescList, boolean isRepair, boolean isSeq) {
		try {
			return this.assetCheckDao.insertPicPathDesc(CHECK_ID, pathDescList, isRepair, isSeq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public List<DataAssetMachineFailItem> getFailTypeList(String tag) {
		try {
			return this.assetCheckDao.getFailTypeList(tag);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int updateCheckRepair(String CHECK_ID, Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
								 Date ACT_REPAIR_START, Date ACT_REPAIR_END, int DOWN_TIME, 
								 String REPAIR_REMARKS, int FAILURE_CODE) {
		try {
			return this.assetCheckDao.updateCheckRepair(CHECK_ID, LAST_UPDATE_DATE, LAST_UPDATED_BY, ACT_REPAIR_START, ACT_REPAIR_END, DOWN_TIME, REPAIR_REMARKS, FAILURE_CODE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public List<DataAssetCheckCheckItem> getAssetCheckCheckItemList(String assetId) {
		try {
			return this.assetCheckDao.getAssetCheckCheckItemList(assetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAssetCheckLastCheckTimeOfAsset(String assetId) {
		try {
			return this.assetCheckDao.getAssetCheckLastCheckTimeOfAsset(assetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public String getAssetCheckResultFromHistory(String assetId, String itemSeq, int chkCycle) {
//		try {
//			return this.assetCheckDao.getAssetCheckResultFromHistory(assetId, itemSeq, chkCycle);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public DataAssetInfoItem getAssetCheckAssetInfo(String assetId) {
		try {
			return this.assetCheckDao.getAssetCheckAssetInfo(assetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String assetCheckGetCheckId() {
		try {
			return this.assetCheckDao.assetCheckGetCheckId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int assetCheckSubmitCheck(String assetId, String checkId, String userId,
									 DataAssetCheckCheckItem checkValue) {
		try {
			return this.assetCheckDao.assetCheckSubmitCheck(assetId, checkId, userId, checkValue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int runProcedureAfterInsertCheck(String checkId) {
		try {
			return this.assetCheckDao.runProcedureAfterInsertCheck(checkId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int assetCheckUpdateCheck(String assetId, String userId, DataAssetCheckCheckItem checkValue, int chkCycle) {
		try {
			return this.assetCheckDao.assetCheckUpdateCheck(assetId, userId, checkValue, chkCycle);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<DataAssetCheckAssetListItem> assetCheckGetAssetList(Date startDateS, Date endDateS) {
		try {
			return this.assetCheckDao.assetCheckGetAssetList(startDateS, endDateS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<DataAssetCheckSetAssetCheckItem> assetCheckGetSetAssetCheckList(Date date, int shift, String staffNo) {
		try {
			return this.assetCheckDao.assetCheckGetSetAssetCheckList(date, shift, staffNo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int assetCheckUpdateAssetCheckList(Date date, int shift, String userId, List<DataAssetCheckSetAssetCheckItem> assetList) {
		try {
			return this.assetCheckDao.assetCheckUpdateAssetCheckList(date, shift, userId, assetList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void assetCheckInsertChangedParts(String checkId, String changedParts, String staffNo, String opCode) {
		try {
			this.assetCheckDao.assetCheckInsertChangedParts(checkId, changedParts, staffNo, opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> assetCheckGetChangedPartsHistoryList(String opCode) {
		try {
			return this.assetCheckDao.assetCheckGetChangedPartsHistoryList(opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean assetCheckCancelAssetCheck(String tagNum) {
		try {
			return this.assetCheckDao.assetCheckCancelAssetCheck(tagNum);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
