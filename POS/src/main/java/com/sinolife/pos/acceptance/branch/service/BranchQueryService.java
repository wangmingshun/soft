package com.sinolife.pos.acceptance.branch.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.im.http.client.IM;
import com.sinolife.im.http.model.Constant;
import com.sinolife.im.http.model.ImageInfo;
import com.sinolife.pos.acceptance.branch.dto.ClientLocateCriteriaDTO;
import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_20;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_8;
import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.ClientQualityDTO;
import com.sinolife.pos.common.dto.ClientTouchHistoryDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO.InsProductInfo;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_8;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.vip.dao.VipManageDAO;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 注意：该类所自动注入的对象，必须在BUILD.XML中打进给LINTF的JAR包中，否则导出的JAR包，LINTF引用将会报异常
 */
@SuppressWarnings({ "unchecked" })
@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class BranchQueryService {

	private Logger logger = Logger.getLogger(getClass());

	private static final String INSURED_FLAG = "I";

	private static final String APPLICANT_FLAG = "A";

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private VipManageDAO vipManageDAO;

	@Autowired
	private PrintDAO printDAO;

	@Autowired
	private IM imageClient;

	@Autowired @Qualifier("mailService01")
	private MailService mailService;

	/**
	 * 客户定位服务. 可选根据如下查询方式：<br/>
	 * <ul>
	 * <li>byId:根据客户证件信息</li>
	 * <li>byClientNo:根据客户号查询</li>
	 * <li>byPolicy:根据保单信息查询</li>
	 * <li>byName:根据客户姓名、性别、生日查询</li>
	 * </ul>
	 * 
	 * @param criteria
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClientInformationDTO> queryClientByCriteria(
			ClientLocateCriteriaDTO criteria) {
		String queryType = criteria.getQueryType();
		List<ClientInformationDTO> retList = null;
		String applicantOrInsured = criteria.getApplicantOrInsured();
		String policyNo = criteria.getPolicyNo();

		if ("byId".equals(queryType)) {
			retList = clientInfoDAO.selClientinfoPro(null, null, null,
					criteria.getIdTypeCode(), criteria.getClientIdNo());
		} else if ("byName".equals(queryType)) {
			retList = clientInfoDAO.selClientinfoPro(criteria.getClientName(),
					criteria.getSex(), criteria.getBirthday(), null, null);
		} else if ("byClientNo".equals(queryType)) {
			retList = clientInfoDAO.selClientinfoForClientno(criteria
					.getClientNo());
		} else if ("byPolicy".equals(queryType)) {
			retList = new ArrayList<ClientInformationDTO>();
			if (StringUtils.isBlank(applicantOrInsured)
					|| APPLICANT_FLAG.equals(applicantOrInsured)) {
				String clientNo = commonQueryDAO
						.getApplicantByPolicyNo(policyNo);
				retList.addAll(clientInfoDAO.selClientinfoForClientno(clientNo));
			}
			if (StringUtils.isBlank(applicantOrInsured)
					|| INSURED_FLAG.equals(applicantOrInsured)) {
				List<String> clientNoList = commonQueryDAO
						.getInsuredByPolicyNo(policyNo);
				if (clientNoList != null && !clientNoList.isEmpty()) {
					for (String clientNo : clientNoList) {
						retList.addAll(clientInfoDAO
								.selClientinfoForClientno(clientNo));
					}
				}
			}
		} else if("byPhoneNo".equals(queryType)){ 
			//增加电话号码查询
			retList = new ArrayList<ClientInformationDTO>();
			String phoneNo = criteria.getPhoneNo();
			List<String> clientNoList = clientInfoDAO.getClientNoByPhoneNo(phoneNo);
			if (clientNoList != null && !clientNoList.isEmpty()) {
				for (String clientNo : clientNoList) {
					retList.addAll(clientInfoDAO
							.selClientinfoForClientno(clientNo));
				}
			}
		} else {
			throw new RuntimeException("无效的查询类型");
		}
		if (retList != null && !retList.isEmpty()) {
			// 除重
			Iterator<ClientInformationDTO> it = retList.iterator();
			Set<String> clientNoSet = new HashSet<String>();
			while (it.hasNext()) {
				ClientInformationDTO clientInfo = it.next();
				if (!clientNoSet.add(clientInfo.getClientNo())) {
					it.remove();
				} else {
					String clientNo = clientInfo.getClientNo();
					// 查询地址
					List<ClientAddressDTO> addressList = clientInfoDAO
							.queryAddressByClientNo(clientNo);
					clientInfo.setClientAddressList(addressList);

					// 查询电话
					List<ClientPhoneDTO> phoneList = clientInfoDAO
							.queryPhoneByClientNo(clientNo);
					clientInfo.setClientPhoneList(phoneList);

					// 查询品质信息
					List<ClientQualityDTO> clientQualityList = clientInfoDAO
							.selClientQualBehaPro(clientNo);
					clientInfo.setClientQualityList(clientQualityList);

					// 查询接触历史
					List<ClientTouchHistoryDTO> clientTouchHistoryList = clientInfoDAO
							.selClientTouchHistoryPro(clientNo);
					clientInfo
							.setClientTouchHistoryList(clientTouchHistoryList);

					// 查询客户产品列表
					List<String> clientProductList = commonQueryDAO
							.queryPolicyNoListByClientNo(clientNo);
					clientInfo.setClientProductList(clientProductList);

					// 查询客户是否为VIP
					Map<String, Object> retMap = vipManageDAO
							.getClientVipGradeByClientNo(clientNo);
					if ("0".equals(retMap.get("p_flag"))) {
						String vipGrade = (String) retMap.get("p_vip_grade");
						String vipDesc = (String) retMap.get("p_vip_desc");
						clientInfo.setVipGrade(vipGrade);
						clientInfo.setVipGradeDesc(vipDesc);
					}
				}
			}
		}
		return retList;
	}

	/**
	 * 根据posNo查询受理录入页面的详细信息
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemsInputDTO queryAcceptDetailInputByPosNo(String posNo) {
		String serviceItems = commonQueryDAO.queryServiceItemsByPosNo(posNo);

		ServiceItemsDAO itemsDAO = (ServiceItemsDAO) PlatformContext
				.getApplicationContext().getBean(
						"serviceItemsDAO_" + serviceItems);

		// 查询保全信息
		ServiceItemsInputDTO itemsInputDTO = itemsDAO
				.queryCommonInputByPosNo(posNo);

		// 查询签名影像地址
		itemsInputDTO.setAutographImageURL(getAutographImageURL(
				itemsInputDTO.getPolicyNo(), itemsInputDTO.getClientNo()));

		// 更新数据状态快照
		itemsInputDTO.updateSnapshot();

		return itemsInputDTO;
	}

	/**
	 * 查询签名影像地址
	 * 
	 * @param policyNo
	 * @param clientNo
	 * @return
	 */
	public String getAutographImageURL(String policyNo, String clientNo) {
		Map<String, Object> retMap = commonQueryDAO.querySignBarcode(policyNo,
				clientNo);
		String url = null;
		try {
			List<ImageInfo> imageList = imageClient
					.queryImageByMainIndexAndDocType2(
							(String) retMap.get("barcodeNo"),
							(String) retMap.get("image"), Constant.CONTENT_YES);
			for (int i = 0; imageList != null && i < imageList.size(); i++) {
				ImageInfo image = imageList.get(i);
				if (!("tif".equals(image.getMime().toLowerCase()) || "tiff"
						.equals(image.getMime().toLowerCase()))) {
					url = image.getURL();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("审批查签名影像", e);
			// 忽略掉查询不到签名影像的异常
		}
		return url;
	}

	/**
	 * 根据保全号查询处理结果信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryProcessResult(String posNo) {
		return commonQueryDAO.queryProcessResult(posNo);
	}

	/**
	 * 查询简要批次受理信息
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<Map<String, String>> queryBatchPosInfo(String posBatchNo) {
		return commonQueryDAO.queryBatchPosInfo(posBatchNo);
	}

	/**
	 * 根据客户号查询客户作为投保人和被保人的保单号
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<String> queryPolicyNoListByClientNo(String clientNo) {
		return commonQueryDAO.queryPolicyNoListByClientNo(clientNo);
	}

	/**
	 * 判断是否有生存金未领取
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean checkHasSurvivalNotPay(String policyNo) {
		boolean isHasSurvivalNotPay = false;
		// 查询生存金应领信息
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", policyNo);
		List<PosSurvivalDueDTO> survivalDueList = commonQueryDAO
				.queryPosSurvivalDueByCriteria(criteriaMap);

		if (survivalDueList != null && !survivalDueList.isEmpty()) {
			isHasSurvivalNotPay = true;
		}
		return isHasSurvivalNotPay;
	}

	/**
	 * 保全开放趸交可贷产品保单利率上浮最高至保单现价的90%
	 * 
	 * @param policyNo
	 *            ,posNo
	 * @return
	 */

	public double queryMaxLoanCash(String policyNo, String posNo,
			String specialFunc) {

		ServiceItemsInputDTO_8 serviceItemsInputDTO = new ServiceItemsInputDTO_8();
		serviceItemsInputDTO.setPolicyNo(policyNo);
		serviceItemsInputDTO.setPosNo(posNo);
		serviceItemsInputDTO.setSpecialFunc(specialFunc);
		ServiceItemsDAO_8 itemsDAO = (ServiceItemsDAO_8) PlatformContext
				.getApplicationContext().getBean("serviceItemsDAO_8");
		ServiceItemsInputDTO item = itemsDAO
				.calPolicyLoanInfo(serviceItemsInputDTO);

		return ((ServiceItemsInputDTO_8) item).getLoanMaxSum().doubleValue();
	}

	/**
	 * 查询健康告知信息
	 * 
	 * @param posNo
	 * @param queryType
	 *            app投保人 ins被保人 all所有
	 * @return PersonalNoticeDTO
	 */
	public List<PersonalNoticeDTO> queryPosPersonalNoticeByPosNo(String posNo,
			String queryType) {
		List<PersonalNoticeDTO> resultList = new ArrayList<PersonalNoticeDTO>();
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		String serviceItems = posInfo.getServiceItems();
		String serviceItemsDesc = PosUtils.getDescByCodeFromCodeTable(
				CodeTableNames.PRODUCT_SERVICE_ITEMS, serviceItems);
		posInfo.setServiceItemsDesc(serviceItemsDesc);
		String policyNo = posInfo.getPolicyNo();

		List<String> clientNoList = new ArrayList<String>();
		String applicantNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
		String insuredNo = commonQueryDAO
				.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
		if ("20".equals(serviceItems)) {
			// 投保人变更查询新投保人客户号
			applicantNo = PlatformContext.getApplicationContext()
					.getBean(ServiceItemsDAO_20.class)
					.getNewApplicantNoByPosNo(posNo);
		}
		if ("app".equalsIgnoreCase(queryType)
				|| "all".equalsIgnoreCase(queryType)) {
			clientNoList.add(applicantNo);
		}
		if ("ins".equalsIgnoreCase(queryType)
				|| "all".equalsIgnoreCase(queryType)) {
			clientNoList.add(insuredNo);
		}
		for (String clientNo : clientNoList) {
			List<Map<String, Object>> retList = commonQueryDAO
					.queryPosPersonalNoticeByPosNoAndClientNo(posNo, clientNo);
			if (retList == null || retList.isEmpty())
				continue;

			PersonalNoticeDTO pn = new PersonalNoticeDTO();
			Set<String> itemOption = new HashSet<String>();
			pn.setItemOption(itemOption);

			List<InsProductInfo> insProductList = new ArrayList<InsProductInfo>();
			pn.setInsProductList(insProductList);
			pn.setPosInfo(posInfo);

			ClientInformationDTO clientInfo = clientInfoDAO
					.selClientinfoForClientno(clientNo).get(0);
			pn.setClientInfo(clientInfo);

			if (clientNo.equals(applicantNo)) {
				pn.setClientOption("A");
			}
			if (clientNo.equals(insuredNo)) {
				pn.setClientOption("I");
			}

			for (Map<String, Object> m : retList) {
				String noticeType = (String) m.get("NOTICE_TYPE");
				String noticeCheck = (String) m.get("NOTICE_CHECK");
				String noticeContentCode = (String) m
						.get("NOTICE_CONTENT_CODE");
				String noticeDescription = (String) m.get("NOTICE_DESCRIPTION");

				if ("1".equals(noticeType)) {
					itemOption.add("1");
					pn.setItemAnswer_1(noticeCheck);
					if ("2".equals(noticeContentCode)) {
						String[] arr = noticeDescription.replaceAll("##",
								" ## ").split("##");
						for (int i = 0; arr != null && i < arr.length; i++) {
							String company = arr[i].trim();
							InsProductInfo ipi = addAndGetInsProductInfoAt(
									insProductList, i);
							ipi.setCompany(company);
						}
					} else if ("3".equals(noticeContentCode)) {
						String[] arr = noticeDescription.replaceAll("##",
								" ## ").split("##");
						for (int i = 0; arr != null && i < arr.length; i++) {
							String insName = arr[i].trim();
							InsProductInfo ipi = addAndGetInsProductInfoAt(
									insProductList, i);
							ipi.setInsName(insName);
						}
					} else if ("4".equals(noticeContentCode)) {
						String[] arr = noticeDescription.replaceAll("##",
								" ## ").split("##");
						for (int i = 0; arr != null && i < arr.length; i++) {
							String insSum = arr[i].trim();
							InsProductInfo ipi = addAndGetInsProductInfoAt(
									insProductList, i);
							ipi.setInsSum(new BigDecimal(insSum));
						}
					} else if ("5".equals(noticeContentCode)) {
						String[] arr = noticeDescription.replaceAll("##",
								" ## ").split("##");
						for (int i = 0; arr != null && i < arr.length; i++) {
							String hospitalizationPremPerDay = arr[i].trim();
							InsProductInfo ipi = addAndGetInsProductInfoAt(
									insProductList, i);
							ipi.setHospitalizationPremPerDay(new BigDecimal(
									hospitalizationPremPerDay));
						}
					} else if ("6".equals(noticeContentCode)) {
						String[] arr = noticeDescription.replaceAll("##",
								" ## ").split("##");
						for (int i = 0; arr != null && i < arr.length; i++) {
							String effDate = arr[i].trim();
							InsProductInfo ipi = addAndGetInsProductInfoAt(
									insProductList, i);
							try {
								ipi.setEffDate(new SimpleDateFormat(
										"yyyy-MM-dd").parse(effDate));
							} catch (ParseException e) {
								throw new RuntimeException(e);
							}
						}
					}
				} else if ("2".equals(noticeType)) {
					itemOption.add("2");
					pn.setItemAnswer_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_2(noticeDescription);
					} else if ("7".equals(noticeContentCode)) {
						pn.setHeight(new BigDecimal(noticeDescription));
					} else if ("8".equals(noticeContentCode)) {
						pn.setWeight(new BigDecimal(noticeDescription));
					}
				} else if ("3".equals(noticeType)) {
					itemOption.add("3");
					pn.setItemAnswer_3(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_3(noticeDescription);
					}
				} else if ("4".equals(noticeType)) {
					itemOption.add("4");
					pn.setItemAnswer_4(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_4(noticeDescription);
					}
				} else if ("5".equals(noticeType)) {
					itemOption.add("5");
					pn.setItemAnswer_5(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_5(noticeDescription);
					}
				} else if ("6".equals(noticeType)) {
					itemOption.add("6");
					pn.setItemAnswer_6_1(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_6_1(noticeDescription);
					} else if ("9".equals(noticeContentCode)) {
						pn.setSmokeYear(new BigInteger(noticeDescription));
					} else if ("10".equals(noticeContentCode)) {
						pn.setSmokePerDay(new BigInteger(noticeDescription));
					} else if ("11".equals(noticeContentCode)) {
						pn.setSmokeQuitReason(noticeDescription);
					}
				} else if ("7".equals(noticeType)) {
					itemOption.add("6");
					pn.setItemAnswer_6_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_6_2(noticeDescription);
					} else if ("12".equals(noticeContentCode)) {
						pn.setDrinkYear(new BigInteger(noticeDescription));
					} else if ("13".equals(noticeContentCode)) {
						pn.setDrinkWeightPerWeek(new BigDecimal(
								noticeDescription));
					} else if ("14".equals(noticeContentCode)) {
						pn.setDrinkType(noticeDescription);
					} else if ("15".equals(noticeContentCode)) {
						pn.setDrinkQuitReason(noticeDescription);
					}
				} else if ("8".equals(noticeType)) {
					itemOption.add("7");
					pn.setItemAnswer_7(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_7(noticeDescription);
					}
				} else if ("9".equals(noticeType)) {
					itemOption.add("8");
					pn.setItemAnswer_8(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_8(noticeDescription);
					}
				} else if ("10".equals(noticeType)) {
					itemOption.add("9");
					pn.setItemAnswer_9(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_9(noticeDescription);
					}
				} else if ("11".equals(noticeType)) {
					itemOption.add("10");
					pn.setItemAnswer_10_1(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_10_1(noticeDescription);
					}
				} else if ("12".equals(noticeType)) {
					itemOption.add("10");
					pn.setItemAnswer_10_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_10_2(noticeDescription);
					}
				} else if ("13".equals(noticeType)) {
					itemOption.add("10");
					pn.setItemAnswer_10_3(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_10_3(noticeDescription);
					}
				} else if ("14".equals(noticeType)) {
					itemOption.add("10");
					pn.setItemAnswer_10_4(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_10_4(noticeDescription);
					}
				} else if ("15".equals(noticeType)) {
					itemOption.add("11");
					pn.setItemAnswer_11_1(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_11_1(noticeDescription);
					}
				} else if ("16".equals(noticeType)) {
					itemOption.add("11");
					pn.setItemAnswer_11_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_11_2(noticeDescription);
					}
				} else if ("17".equals(noticeType)) {
					itemOption.add("12");
					pn.setItemAnswer_12_1(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_12_1(noticeDescription);
					} else if ("16".equals(noticeContentCode)) {
						pn.setFetationWeeks(new BigInteger(noticeDescription));
					}
				} else if ("18".equals(noticeType)) {
					itemOption.add("12");
					pn.setItemAnswer_12_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_12_2(noticeDescription);
					}
				} else if ("19".equals(noticeType)) {
					itemOption.add("12");
					pn.setItemAnswer_12_3(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_12_3(noticeDescription);
					}
				} else if ("20".equals(noticeType)) {
					itemOption.add("12");
					pn.setItemAnswer_12_4(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_12_4(noticeDescription);
					}
				} else if ("21".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_1(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_1(noticeDescription);
					}
				} else if ("22".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_2(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_2(noticeDescription);
					}
				} else if ("23".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_3(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_3(noticeDescription);
					}
				} else if ("24".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_4(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_4(noticeDescription);
					}
				} else if ("25".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_5(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_5(noticeDescription);
					}
				} else if ("26".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_6(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_6(noticeDescription);
					}
				} else if ("27".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_7(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_7(noticeDescription);
					}
				} else if ("28".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_8(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_8(noticeDescription);
					}
				} else if ("29".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_9(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_9(noticeDescription);
					}
				} else if ("30".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_10(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_10(noticeDescription);
					}
				} else if ("31".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_11(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_11(noticeDescription);
					}
				} else if ("32".equals(noticeType)) {
					itemOption.add("13");
					pn.setItemAnswer_13_12(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_13_12(noticeDescription);
					}
				} else if ("33".equals(noticeType)) {
					itemOption.add("14");
					pn.setItemAnswer_14(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_14(noticeDescription);
					}
				} else if ("34".equals(noticeType)) {
					itemOption.add("15");
					pn.setItemAnswer_15(noticeCheck);
					if ("1".equals(noticeContentCode)) {
						pn.setItemRemark_15(noticeDescription);
					} else if ("17".equals(noticeContentCode)) {
						pn.setBirthWeight(new BigDecimal(noticeDescription));
					} else if ("18".equals(noticeContentCode)) {
						pn.setBirthStayHispitalDays(new BigInteger(
								noticeDescription));
					} else if ("19".equals(noticeContentCode)) {
						pn.setBirthHeight(new BigDecimal(noticeDescription));
					} else if ("20".equals(noticeContentCode)) {
						pn.setBirthHospital(noticeDescription);
					}
				}
			}
			resultList.add(pn);
		}
		return resultList;
	}

	private InsProductInfo addAndGetInsProductInfoAt(
			List<InsProductInfo> insProductList, int i) {
		InsProductInfo ipi = null;
		if (insProductList.size() > i) {
			ipi = insProductList.get(i);
			if (ipi == null) {
				ipi = new InsProductInfo();
				insProductList.set(i, ipi);
			}
		} else {
			ipi = new InsProductInfo();
			PosUtils.appendNullToSize(insProductList, i + 1);
			insProductList.add(i, ipi);
		}
		return ipi;
	}

	/**
	 * 查询未结案保全信息
	 * 
	 * @param clientNo
	 *            客户号
	 * @return List&lt;PosInfoDTO&gt;
	 */
	public List<PosInfoDTO> queryNotCompletePosInfoRecord(String clientNo) {
		return commonQueryDAO.queryNotCompletePosInfoRecord(clientNo);
	}

	/**
	 * 查询批次受理断点位置
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return Map&lt;String, Object&gt;包含Key:<br/>
	 *         breakStatusCode:断点的保全状态<br/>
	 *         posNo:批次受理时需跳转的保全号<br/>
	 *         processResultList:预处理完成时跳转时需要的经办结果
	 */
	public Map<String, Object> queryBreakInfoByPosBatchNo(String posBatchNo) {
		List<Map<String, Object>> breakInfoList = commonQueryDAO
				.queryBreakInfoByPosBatchNo(posBatchNo);
		Map<String, Object> retMap = new HashMap<String, Object>();
		String breakStatusCode = null; // 上条记录的受理状态
		String breakServiceItems = null;// 上条记录的保全项目
		String breakBarcodeNo = null; // 上调记录的条码号
		for (int i = 0; breakInfoList != null && i < breakInfoList.size(); i++) {
			Map<String, Object> breakInfo = breakInfoList.get(i);
			String posNo = (String) breakInfo.get("POS_NO");
			String serviceItems = (String) breakInfo.get("SERVICE_ITEMS");
			String acceptStatusCode = (String) breakInfo
					.get("ACCEPT_STATUS_CODE");
			String barcodeNo = (String) breakInfo.get("BARCODE_NO");
			if ("A01".equals(acceptStatusCode) && breakStatusCode == null) {
				// 批次录入状态只有可能处理一单，并且应该是找到的第一单
				retMap.put("breakStatusCode", acceptStatusCode);
				retMap.put("posNo", posNo);
				return retMap;
			} else if ("A19".equals(acceptStatusCode)
					|| "C01".equals(acceptStatusCode)) {
				if (breakBarcodeNo == null || !breakBarcodeNo.equals(barcodeNo)) {
					breakBarcodeNo = barcodeNo;
					breakStatusCode = acceptStatusCode;
					breakServiceItems = serviceItems;
				}
			}
		}
		List<Map<String, Object>> processResultList = new ArrayList<Map<String, Object>>();
		List<String> posNoList = commonQueryDAO
				.queryBreakPosNoByBarcodeAndServiceItems(posBatchNo,
						breakBarcodeNo, breakServiceItems);
		if (posNoList != null && !posNoList.isEmpty()) {
			for (String posNo : posNoList) {
				Map<String, Object> processResult = this
						.queryProcessResult(posNo);
				String acceptStatusCode = (String) processResult
						.get("ACCEPT_STATUS_CODE");
				if ("E01".equals(acceptStatusCode)) {
					continue;
				} else if ("C01".equals(acceptStatusCode)) {
					List<String> msgList = this.queryRuleCheckMsgByPosNo(posNo);
					StringBuilder sb = new StringBuilder("规则检查不通过：<br/>");
					for (String msg : msgList) {
						sb.append(msg).append("<br/>");
					}
					processResult.put("RULE_CHECK_MSG", sb.toString());
					processResult.put("RESULT_MSG", sb.toString());
				} else if ("A03".equals(acceptStatusCode)) {
					processResult.put("RULE_CHECK_MSG", "由于延时规则，待处理规则检查");
					processResult.put("RESULT_MSG", "由于延时规则，待处理规则检查");
					processResult.put("DELAY_FLAG", "Y");
				} else if ("A01".equals(acceptStatusCode)) {
					// 这里发现了A01项目，说明已经重新生成过了受理，直接跳转到A01受理的界面
					retMap.put("breakStatusCode", acceptStatusCode);
					retMap.put("posNo", posNo);
					return retMap;
				} else if (!"A19".equals(acceptStatusCode)) {
					throw new RuntimeException("保全项目" + posNo + "处于未知的状态");
				}
				processResultList.add(processResult);
			}
			retMap.put("breakStatusCode", breakStatusCode);
			retMap.put("processResultList", processResultList);
		}
		return retMap.isEmpty() ? null : retMap;
	}

	/**
	 * 查询保单原账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map<String, String> getPolicyBankacctno(String policyNo) {
		return commonQueryDAO.getPolicyBankacctno(policyNo);
	}

	/**
	 * 查询保全规则检查不通过信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryRuleCheckMsgByPosNo(String posNo) {
		return commonQueryDAO.queryRuleCheckMsgByPosNo(posNo);
	}

	public List<String> querySpecialRetreatReason(String specialFunc) {
		return commonQueryDAO.querySpecialRetreatReason(specialFunc);
	}

	/**
	 * 查询批单是否有保证价值表或险种条款需要打印
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean isEndorsementHasNoteOrPlanProvision(String posNo) {
		// 看批单是否存在，这里只查满足批单打印条件的
		EndorsementPrintCriteriaDTO criteria = new EndorsementPrintCriteriaDTO();
		criteria.setPosNo(posNo);
		// 逐单打印标记，逐单打印可以打印订阅电子函件的批单和限价表，批次打印不能打印订阅电子函件 edit by gaojiaming
		criteria.setSinglePrintFlag("Y");
		List<Map<String, Object>> retList = printDAO
				.queryEndorsementInfoByCriteria(criteria);
		if (retList == null || retList.size() != 1) {
			return false;
		}

		// 看现金价值表有没有数据
		List<PosNoteDTO> posNoteList = printDAO.queryPosNoteMainByPosNo(posNo);
		if (posNoteList != null && !posNoteList.isEmpty()) {
			return true;
		}

		// 查批单条款信息
		Set<String> productSet = printDAO.getAddProduct(posNo);
		if (productSet != null && !productSet.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 如果此项送核送审了，又没有影像 给受理人发个邮件催他赶紧扫描影像
	 * 
	 * @param flag
	 * @param posNo
	 */
	public void noImageEmailAcceptor(String flag, String posNo) {
		try {
			if ("A08".equals(flag) || "A12".equals(flag)) {
				Map<String, String> map = commonQueryDAO.queryImageExist(posNo);

				if (StringUtils.isBlank(map.get("BAR_CODE"))) {
					Mail mail = new Mail();
					mail.setSubject("[SL_POS]保全影像扫描催办通知"); // 邮件标题
					mail.setTo(new String[] { map.get("ACCEPTOR") }); // 收件人
					mail.setForm("sl_pos_mail_admin@sino-life.com"); // 发件人
					mail.setMailType(MailType.HTML_CONTENT); // 邮件类型
					mail.setContent(new StringBuilder(
							"<html><body style=\"font-size:14px;text-align:left;\">")
							.append("尊敬的保全系统用户 ")
							.append(map.get("USER_NAME"))
							.append("：<br/>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br/>")
							.append("&nbsp;&nbsp;&nbsp;&nbsp;您所受理的保全件尚未扫描相关影像资料，目前已送")
							.append("A08".equals(flag) ? "审批" : "核保")
							.append("，请及时扫描影像资料，谢谢！<br/><br/>")
							.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保单号：&nbsp;")
							.append(map.get("POLICY_NO"))
							.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保全批单号：&nbsp;")
							.append(posNo)
							.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保全项目：&nbsp;")
							.append(map.get("SERVICE_ITEMS"))
							.append("<br/><br/><br/><br/><br/><br/><br/><br/>")
							.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>")
							.toString()); // 邮件内容
					mailService.send(mail);
				}
			}
		} catch (Exception e) {
			logger.error("noImageEmailAcceptor_Exception+" + posNo + flag, e);
		}
	}

	/**
	 * 查询保单渠道信息
	 */
	public String queryPolicyChannelType(String policyNo) {
		return commonQueryDAO.getChannelTypeByPolicyNo(policyNo);
	}

	/**
	 * 查询续期缴费账户信息
	 */
	public Map<String, Object> queryAccountByPolicyNo(String policy_no) {

		List<Map<String, Object>> accountList = commonQueryDAO
				.queryAccountByPolicyNo(policy_no);
		Map<String, Object> map = new HashMap<String, Object>();
		if (accountList.size() > 0) {

			map = (Map<String, Object>) accountList.get(0);
		}

		return map;

	}

	/**
	 * 查询银行大类
	 * 
	 * @param bankCode
	 * @return
	 */
	public String getBankCategoryByBankCode(String bankCode) {

		return commonQueryDAO.getBankCategoryByBankCode(bankCode);
	}

	/**
	 * 查询保单联系地址
	 * 
	 * @param policy_no
	 * @return
	 */

	public String getPolicyAddressByPolicyNo(String policy_no) {

		return commonQueryDAO.getPolicyAddressByPolicyNo(policy_no);
	}

    /**
     * 免填单获取条形码
     * 
     * @param serviceItem
     * @return String
     * @author GaoJiaMing
     * @time 2014-5-28
     */
    public String generateBarcodeNo(String serviceItems) {
        String v_doc_type = "";
        // 保险合同变更申请书（补退费类）
        if ("27".equals(serviceItems) || "1".equals(serviceItems) || "2".equals(serviceItems)
                || "9".equals(serviceItems) || "4".equals(serviceItems) || "11".equals(serviceItems)
                || "17".equals(serviceItems) || "10".equals(serviceItems) || "13".equals(serviceItems)
                || "33".equals(serviceItems) || "34".equals(serviceItems)) {
            v_doc_type = "1213";
        }
        // 保险合同变更申请书（核保类）
        else if ("12".equals(serviceItems) || "15".equals(serviceItems) || "22".equals(serviceItems)
                || "20".equals(serviceItems) || "16".equals(serviceItems) || "6".equals(serviceItems)
                || "14".equals(serviceItems) || "3".equals(serviceItems) || "5".equals(serviceItems)
                || "35".equals(serviceItems)) {
            v_doc_type = "1212";
        }
        // 保险合同变更申请书（非补退费类）
        else if ("19".equals(serviceItems) || "23".equals(serviceItems) || "21".equals(serviceItems)
                || "18".equals(serviceItems) || "28".equals(serviceItems) || "7".equals(serviceItems)
                || "26".equals(serviceItems) || "25".equals(serviceItems) || "24".equals(serviceItems)
                || "30".equals(serviceItems) || "31".equals(serviceItems) || "32".equals(serviceItems)
                || "29".equals(serviceItems) || "43".equals(serviceItems) || "44".equals(serviceItems)
                || "45".equals(serviceItems) || "46".equals(serviceItems)) {
            v_doc_type = "1234";
        }
        // 投资账户变更申请书
        else if ("38".equals(serviceItems) || "39".equals(serviceItems) || "36".equals(serviceItems)
                || "37".equals(serviceItems)) {
            v_doc_type = "1214";
        }
        // 个人寿险保单贷款申请书
        else if ("8".equals(serviceItems)) {
            v_doc_type = "1215";
        } else {
            throw new RuntimeException("该保全项目不能受理");
        }
        String barcodeNoTemp = v_doc_type + "09" + commonQueryDAO.queryEpointBarcodeNoSequence();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("barCode", barcodeNoTemp);
        // 获取条形码后两位
        commonQueryDAO.getBarCodeSum(map);
        String barcodeNo = barcodeNoTemp + map.get("barCodeSum");
        return barcodeNo;
    }

    /**
     * 根据保全号查询是否免填单
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-5
     */
    public String checkIsApplicationFree(String posNo) {
        return printDAO.checkIsApplicationFree(posNo);
    }

    
    /**
     * 根据保全号查询条形码
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-5
     */
    public String getBarCodeNoByPosNo(String posNo) {
        PosInfoDTO posInfoDTO = commonQueryDAO.queryPosInfoRecord(posNo);
        if (posInfoDTO != null) {
            return posInfoDTO.getBarcodeNo();
        } else {
            return "";
        }
    }
	/**
	 * 根据保单查询电销项目代码
	 * 
	 * @param PolicyNo
	 * @return
	 */
	public String getProjectCodeByPolicyNo(String PolicyNo) {
 
		return commonQueryDAO.getProjectCodeByPolicyNo(PolicyNo);
	}
	
	/**
	 * 校验代办代审退费金额超限制和规则特殊件原因
	 * 
	 * @param posNo
	 * @return Map<String, Object>
	 * @author GaoJiaMing
	 * @time 2014-9-19
	 */
	public Map<String, Object> checkIsExceedExamPrivs(String posNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("isExceedExamPrivs", commonQueryDAO.checkIsExceedExamPrivs(posNo));
		resultMap.put("specialRuleReason", commonQueryDAO.getSpecialRuleReason(posNo));
		return resultMap;
	}
	
	/**
	 * 获取代审类别
	 * 
	 * @param posNo
	 * @return
	 */
	public String getExamType(String posNo) {
 
		return commonQueryDAO.getExamType(posNo);
	}
	
	/**
	 * @Description: 获取用户所在的二级机构编码
	 * @methodName: getUserApproveBranch
	 * @param userId
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-12-18
	 * @throws
	 */
	public String getUserApproveBranch(String userId) {
		return commonQueryDAO.getUserApproveBranch(userId);
	}
	
	/**
	 * @Description: 获取指定用户所能受理的保全项目
	 * @methodName: getUserCanDoServiceItems
	 * @param userId
	 * @return
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2015-12-18
	 * @throws
	 */
	public List<String> getUserCanDoServiceItems(String userId) {
		return commonQueryDAO.getUserCanDoServiceItems(userId);
	}
} 
