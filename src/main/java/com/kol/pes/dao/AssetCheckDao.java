/*-----------------------------------------------------------

-- PURPOSE

--    处理点检信息的数据库操作接口

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataAssetCheckAssetListItem;
import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.item.DataAssetCheckItem;
import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataAssetMachineFailItem;


public interface AssetCheckDao {
	
	public List<DataAssetCheckItem> findAssetWithError() throws SQLException;
	
	public String getCheckId() throws SQLException;
	
	public DataAssetInfoItem getAssetInfo(String tagNum) throws SQLException;
	
	public int insertCheck(String checkId, Date CREATION_DATE, int CREATED_BY, 
						   Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
							 String ASSET_ID, Date CHECK_TIME, int CHECK_RESULT, 
							 Date EST_REPAIR_START, Date EST_REPAIR_END, 
							 String CHECK_REMARKS) throws SQLException;
	
	public int insertPicPathDesc(String checkId, String pathDescList, boolean isRepair, boolean isSeq) throws SQLException;
	
	public List<DataAssetMachineFailItem> getFailTypeList(String tag) throws SQLException;

	public int updateCheckRepair(String CHECK_ID, Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
								 Date ACT_REPAIR_START, Date ACT_REPAIR_END, int DOWN_TIME, String REPAIR_REMARKS, int FAILURE_CODE) throws SQLException; 

	public List<DataAssetCheckCheckItem> getAssetCheckCheckItemList(String assetId) throws SQLException;
	
	public String getAssetCheckLastCheckTimeOfAsset(String assetId) throws SQLException;
	
	public DataAssetInfoItem getAssetCheckAssetInfo(String assetId) throws SQLException;
	
	public String assetCheckGetCheckId() throws SQLException;
	
	public int assetCheckSubmitCheck(String assetId, String checkId, String userId, DataAssetCheckCheckItem checkValue) throws SQLException;

	public int runProcedureAfterInsertCheck(String checkId) throws SQLException;
	
	public int assetCheckUpdateCheck(String assetId, String userId, DataAssetCheckCheckItem checkValue, int chkCycle) throws SQLException;
	
	public List<DataAssetCheckAssetListItem> assetCheckGetAssetList(Date startDateS, Date endDateS) throws SQLException;
	
	public List<DataAssetCheckSetAssetCheckItem> assetCheckGetSetAssetCheckList(Date date, int shift, String staffNo) throws SQLException;

	public int assetCheckUpdateAssetCheckList(Date date, int shift, String userId, List<DataAssetCheckSetAssetCheckItem> assetList) throws SQLException;

	public void assetCheckInsertChangedParts(String checkId, String changedParts, String staffNo, String opCode) throws SQLException;
	public List<String> assetCheckGetChangedPartsHistoryList(String opCode) throws SQLException;
	
	public boolean assetCheckCancelAssetCheck(String tagNum) throws SQLException;
}
