package com.sinolife.pos.pubInterface.biz.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_13;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_17;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;

public interface CmpPosAcceptInterface {
	 /**
     * 查询基表
     */
    public List  queryCodeTableList(String codeTableName);
	public List queryBranchTreeRoot(Map pMap);
	
	public List queryBranchTree(Map pMap);
	
	
	public List queryCityByProvince(String province);
	
	public List queryAreaByCity(String city);
	
	public List queryBranchInProvinceCity(String province,String city,String channel);
	
	public List queryCounter(String branchCode);
	/**
	 * 财务接口10：查询银行代码列表接口
	 * @param countryCode 国家代码（非空）
	 * @param provinceCode 省/直辖市代码（非空）
	 * @param cityCode 城市代码（非空）
	 * @param bankCategory 银行大类编码（非空）
	 * @return
	 */
	public List queryBankList(String countryCode, String provinceCode, String cityCode, String bankCategory);
	/**
	 * 生成CMP用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateCMParcode(String serviceItems);
	/**
	 * 根据证件号码查询客户信息
	 * 
	 * @param idTypeCode
	 *            id类型
	 * @param idNo
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProById(String idTypeCode,
			String idNo);

	/**
	 * 根据客户姓名查询
	 * 
	 * @param clientName
	 *            客户性别
	 * @param sex
	 *            性别
	 * @param birthday
	 *            生日
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyName(String clientName,
			String sex, Date birthday);

	/**
	 * 根据客户号查询
	 * 
	 * @param clientNo
	 *            客户号
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyClientNo(String clientNo);

	/**
	 * 根据保单号查询
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyPolicyNo(
			String policyNO, String applicantOrInsured);
	/**
	 * 根据保单号查询客户信息
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	public List<Map<String, Object>> getClientinfobyPolicyNo(
			String policyNO, String applicantOrInsured);
	/***
	 * 根据保单号查险种信息
	 * @param posNo
	 * @return
	 */
	public List<Map<String, String>> ProductInfoListByPolicyNO(String policy_no);
	/***
	 * 根据保单号查询保单账号信息
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryAccountInfoByPolicyNo(String policy_no);
	/**
	 * 写入批次记录、申请书记录，及保全记录
	 * @param applyInfo
	 * @return
	 */
	public  Map<String, Object> createBatch(Map<String, Object>  applyInfoMap);

    /**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里（传入map）
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInterl( Map<String, Object> accMap);
	/**
	 * 经办结果确认
	 * 
	 * @param posNoList
	 * @param 
	 * @return
	 */
	public Map<String, Object> processResultSubmit(
			List<Map<String, Object>> posNoList);
	
	/**
	 * 经办结果撤销
	 * 
	 * @param cancelCause
	 * @param cancelRemark
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultCancel(String cancelCause,
			String cancelRemark, List<Map<String, Object>> processResultList);
	/**
	 * 受理撤销，对于已经是结束状态的受理，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public void cancelAccept(PosInfoDTO posInfo, String acceptUser);;

	/**
	 * 受理撤销并重新生成受理
	 * 
	 * @param posNo
	 * @param rollbackCause
	 *            撤销原因
	 * @param rollbackCauseDetail
	 *            撤销原因描述
	 * @param acceptUser
	 *            受理用户
	 * @return
	 */
	public Map<String, Object> cancelAndReaccept(String posNo,
			String rollbackCause, String rollbackCauseDetail);
	/**
	 * 犹豫期退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfoExtraToBia_1(Map map) ;
	/**
	 * 退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfoExtraToBia_2(Map map) ;
	/**
	 * 保单还款经办信息录入页面
	 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfo_9(Map map) ;
	/**
	 * 交费频次变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_13 queryServiceItemsInfo_13(Map map);
	/**
	 * 续期保费退费经办信息录入页面
	 */
	public ServiceItemsInputDTO_17 queryServiceItemsInfo_17(Map map) ;
 
	/**
	 * 续期交费方式变更经办信息录入
	 */
    public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_23(Map map);

}
