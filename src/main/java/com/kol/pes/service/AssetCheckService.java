package com.kol.pes.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataAssetCheckAssetListItem;
import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.item.DataAssetCheckItem;
import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataAssetMachineFailItem;

public interface AssetCheckService extends DataEnableService {
	
	public List<DataAssetCheckItem> findAssetWithError();//获取设备点检列表
	
	public String getCheckId();//生成设备ID
	
	public DataAssetInfoItem getAssetInfo(String assetId);//获取设备信息
	
	public int insertCheck(String CHECK_ID, Date CREATION_DATE, int CREATED_BY, 
						 Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
						 String ASSET_ID, Date CHECK_TIME, int CHECK_RESULT, 
						 Date EST_REPAIR_START, Date EST_REPAIR_END, 
						 String CHECK_REMARKS);//插入点检信息
	
	public int insertPicPathDesc(String CHECK_ID, String pathDescList, boolean isRepair, boolean isSeq);//插入点检或者维修时拍摄的照片文件名到数据表
	
	public List<DataAssetMachineFailItem> getFailTypeList(String tag);//获取设备故障状态列表
	
	public int updateCheckRepair(String CHECK_ID, Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
								 Date ACT_REPAIR_START, Date ACT_REPAIR_END, int DOWN_TIME, 
								 String REPAIR_REMARKS, int FAILURE_CODE); //维修设备
	
	public List<DataAssetCheckCheckItem> getAssetCheckCheckItemList(String assetId);
	
	public String getAssetCheckLastCheckTimeOfAsset(String assetId);

	public DataAssetInfoItem getAssetCheckAssetInfo(String assetId);
	
	public String assetCheckGetCheckId();
	
	public int assetCheckSubmitCheck(String assetId, String checkId, String userId, DataAssetCheckCheckItem checkValue);

	public int runProcedureAfterInsertCheck(String checkId);
	
	public int assetCheckUpdateCheck(String assetId, String userId, DataAssetCheckCheckItem checkValue, int chkCycle);
	
	public List<DataAssetCheckAssetListItem> assetCheckGetAssetList(Date startDateS, Date endDateS);
	
	public List<DataAssetCheckSetAssetCheckItem> assetCheckGetSetAssetCheckList(Date date, int shift, String staffNo);
	
	public int assetCheckUpdateAssetCheckList(Date date, int shift, String userId, List<DataAssetCheckSetAssetCheckItem> assetList);

	public void assetCheckInsertChangedParts(String checkId, String changedParts, String staffNo, String opCode);
	public List<String> assetCheckGetChangedPartsHistoryList(String opCode);
	public boolean assetCheckCancelAssetCheck(String tagNum);
}
