package com.sinolife.esbpos.irs;

import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;

public interface IrsCompareService {
	/**
	 * 比对服务
	 * @param map   key=clientName     客户姓名
	 * 		  	    key=birthday       客户生日
	 * 	 	  	    key=sexCode        性别代码（0:无关，1：男性，2:女性）
	 * 		  	    key=idType         证件类型代码（01:居民身份证，02：居民户口簿，03:驾驶证...）
	 * 		  	    key=idNo           证件号
	 * 		  	    key=imgBase64               影像base64代码
	 * 				key=businessType   业务类型（01:联系方式变更，02：退保）
	 *				key=businessNo	        关联业务号
	 * 		  	    key=clientSource   客户来源系统（01:生命云服务，02：E动生命）
	 * 		  	    key=sourceVersion  来源系统版本（app版本）
	 * 				key=controlVersion 当前识别控件版本（人脸识别控件版本）
	 * 				key=equipSeq	        操作终端设备号
	 *				key=terminalType   操作终端类型（1:pc，2：ios，3:安卓）
	 * 				key=compareType    比对操作方式（1:柜面，2：客户自助）
	 * 				key=operatorUser   经办人工号
	 * 				key=cryptType	        图片是否经过加密（0-未加密，1-加密方式一，2加密方式二）
	 * 				key=glass		        环境是否有玻璃（ 1：是；0否）
	 * 				key=comScene	        比对场景（如果没有，则传入固定4位值，默认0000）
	 * 				key=compareModel   比对模式（301,302,303）
	 * 				key=compareSystem  指定比对系统(可以为空)
	 * 				key=branchCode     业务单所属机构代码
	
	 * @return map  key=flag           比对结果标识（Y:一致，N：不一致，F:不确定，E：异常，G:传参异常）
	 * 				key=message        返回信息
	 * 				key=compareNo	        比对序列号
	 * 				key=compareSystem  比对系统     1：全国公民身份信息中心比对系统（nciic）2：smartbios内部系统
	 * 				key=compareTimesNCIIC 当天到全国公民身份信息中心比对系统NCIIC比对失败次数
	 */
	@EsbMethod(esbServiceId="com.sinolife.irs.irsCompareService")
	public Map<String, Object> checkClientInfo(Map<String, Object> iMap);

	/**
	 * 获取控件参数服务
	 * @param map   	key=terminalType   设备类型（1：pc 2:ios 3:安卓 4：flex）
	 * 					key=compareModel   比对模式（ 301:保全柜面  303:生命云服务 ，305:E动生命）
	 * @return String   
	 */
	@EsbMethod(esbServiceId="com.sinolife.irs.irsGetControlParameters")
	public String getControlParameters(Map<String,Object> param);
} 

