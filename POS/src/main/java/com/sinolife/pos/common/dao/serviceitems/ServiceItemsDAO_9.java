package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.unihub.framework.util.common.DateUtil;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.LoanPayBackDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 保单还款
 */
@Repository("serviceItemsDAO_9")
public class ServiceItemsDAO_9 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("loanSum", "1|086|1|3|policyNo");// 贷款本金
		map.put("interestSum", "1|087|1|3|policyNo");// 贷款利息
		map.put("aplLoanSum", "1|088|1|3|policyNo");// 自垫本金
		map.put("aplInterestSum", "1|089|1|3|policyNo");// 自垫利息
		map.put("aplExtraFee", "1|090|1|3|policyNo");// 应补差额保费
		map.put("loanPayBackSum", "1|148|1|3|policyNo");// 还款金额

		PROC_PROPERTY_CONFIG_MAP = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_9 item = (ServiceItemsInputDTO_9) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();
		
		//判断保单是否自垫
		List<LoanPayBackDTO> loanPayBackList = queryForList("getLoanPayBackInfo", policyNo);
		
		Date applyDate = null;
		if (item.getPosNo() != null && !item.getPosNo().equals("")) {
			applyDate = commonQueryDAO.getApplyDateByPosNo(item.getPosNo());
		} else {
			if(item.getApplyDate() == null) 
				applyDate = commonQueryDAO.getSystemDate();
			else
				applyDate = DateUtil.stringToDate(item.getApplyDate(), "yyyy-MM-dd");
		}
		
		Map<String, Object> resultMap = null;
		String flag = null;
		
		// 查询贷款
		resultMap = commonQueryDAO.calcPolicyLoanInterestByPolicyNo(policyNo,
				applyDate);
		flag = (String) resultMap.get("p_flag");
		if ("0".equals(flag)) {
			BigDecimal loanAllSum = (BigDecimal) resultMap.get("p_loan_all_sum");
			BigDecimal interestSum = (BigDecimal) resultMap.get("p_interest_sum");
			item.setLoanAllSum(loanAllSum);
			item.setInterestSum(interestSum);
		}
		
		//自垫采用以前的方案
		if(loanPayBackList == null || loanPayBackList.size() == 0) {
			
			// 查询自垫
			resultMap = commonQueryDAO.calcPolicyAplInterestByPolicyNo(policyNo,
					applyDate);
			flag = (String) resultMap.get("p_flag");
			if ("0".equals(flag)) {
				BigDecimal aplLoanAllSum = (BigDecimal) resultMap
				.get("p_loan_all_sum");
				BigDecimal aplInterestSum = (BigDecimal) resultMap
				.get("p_interest_sum");
				item.setAplLoanAllSum(aplLoanAllSum);
				item.setAplInterestSum(aplInterestSum);
			}
			
			// 查询应补差额保费
			resultMap = commonQueryDAO.calcPolicyAplExtraFeeByPolicyNo(policyNo);
			flag = (String) resultMap.get("p_flag");
			if ("0".equals(flag)) {
				BigDecimal aplExtraFee = (BigDecimal) resultMap
				.get("p_policy_apl_extra_fee");
				item.setAplExtraFee(aplExtraFee);
			}
		} else {
			//贷款的时候自垫肯定为0,设置为0主要是给外围系统来使用的
			item.setAplLoanAllSum(new BigDecimal(0));
			item.setAplInterestSum(new BigDecimal(0));
			item.setAplExtraFee(new BigDecimal(0));
			//采用新的方案
			for(LoanPayBackDTO lpb : loanPayBackList) {
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("posNo", lpb.getPosNo());
				paraMap.put("endDate", applyDate);
				queryForObject("getLoanAllSum", paraMap);
				//贷款利息
				BigDecimal interestSum = (BigDecimal) paraMap.get("interestSum");
				//本息合计
				BigDecimal loanAllSum = (BigDecimal) paraMap.get("loanAllSum");
				lpb.setLoanInterest(interestSum);
				lpb.setTotalSum(loanAllSum);
			}
			item.setLoanPayBackList(loanPayBackList);
		}
		return item;
	}

	/* 提供给外围系统部调用 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfo(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		ServiceItemsInputDTO_9 item = (ServiceItemsInputDTO_9) queryServiceItemsInfoExtra(serviceItemsInputDTO);
		// 非柜面受理时现全部还款
		item.setLoanPayBackSum(item.getLoanAllSum());
		
		return item;
	}
	
	/*  ESB接口提供给MCC 根据保单号查保单自垫及借款信息 */
	public ServiceItemsInputDTO_9 getAplLoanAndAplExtraFeeInfo(
			String policyNo) {
		ServiceItemsInputDTO_9 item = new ServiceItemsInputDTO_9();
		Date applyDate = null;
		applyDate = commonQueryDAO.getSystemDate();
		
		Map<String, Object> resultMap = null;
		String flag = null;

		// 查询贷款
		resultMap = commonQueryDAO.calcPolicyLoanInterestByPolicyNo(policyNo,
				applyDate);
		flag = (String) resultMap.get("p_flag");
		if ("0".equals(flag)) {
			BigDecimal loanAllSum = (BigDecimal) resultMap
					.get("p_loan_all_sum");
			BigDecimal interestSum = (BigDecimal) resultMap
					.get("p_interest_sum");
			item.setLoanAllSum(loanAllSum);
			item.setInterestSum(interestSum);
		}

		// 查询自垫
		resultMap = commonQueryDAO.calcPolicyAplInterestByPolicyNo(policyNo,
				applyDate);
		flag = (String) resultMap.get("p_flag");
		if ("0".equals(flag)) {
			BigDecimal aplLoanAllSum = (BigDecimal) resultMap
					.get("p_loan_all_sum");
			BigDecimal aplInterestSum = (BigDecimal) resultMap
					.get("p_interest_sum");
			item.setAplLoanAllSum(aplLoanAllSum);
			item.setAplInterestSum(aplInterestSum);
		}

		// 查询应补差额保费
		resultMap = commonQueryDAO.calcPolicyAplExtraFeeByPolicyNo(policyNo);
		flag = (String) resultMap.get("p_flag");
		if ("0".equals(flag)) {
			BigDecimal aplExtraFee = (BigDecimal) resultMap
					.get("p_policy_apl_extra_fee");
			item.setAplExtraFee(aplExtraFee);
		}

		// 非柜面受理时现全部还款
		item.setLoanPayBackSum(item.getLoanAllSum());
		return item;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_9 item = (ServiceItemsInputDTO_9) serviceItemsInputDTO;
		
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		
		//贷款模式处理
		if(item.getLoanPayBackList() != null 
				&& item.getLoanPayBackList().size() > 0) {
			List<LoanPayBackDTO> list = item.getLoanPayBackList();
			
			//查询受理渠道
			String acceptChannel = commonQueryDAO.getacceptChannelByPosNo(item.getPosNo());
			//柜面处理方式，不做任何处理,这些渠道是通过页面录入的,所以不需要自动组装数据
			//预约退保时，渠道默认为1，但是也属于自动组装数据，判断barcodeNo以APPO开头的则进入自动组装
			if(("1".equals(acceptChannel) && !item.getBarcodeNo().startsWith("APPO")) 
					|| "2".equals(acceptChannel) || "3".equals(acceptChannel) 
					|| "6".equals(acceptChannel) || "20".equals(acceptChannel) 
					|| "21".equals(acceptChannel) || "22".equals(acceptChannel) 
					|| "23".equals(acceptChannel)) {
				//此种情况下，是通过页面操作还款的，不需要做任何处理，以页面为准即可。
			} else {
				//非柜面处理方式，即自动组装数据
				//非全额还款，还款采用按时间顺序逐笔扣款
				if(item.getLoanPayBackSum().compareTo(item.getLoanAllSum()) < 0) {
					//剩余还款金额
					BigDecimal loanPayBackSum = item.getLoanPayBackSum();
					for(LoanPayBackDTO lb : list) {
						if(loanPayBackSum.compareTo(new BigDecimal(0)) > 0) {
							if(loanPayBackSum.compareTo(lb.getTotalSum()) > 0) {
								//剩余还款金额大于本笔的本息合计，直接将本笔的还款金额设置为本息合计
								lb.setLoanPayBackSumDetail(lb.getTotalSum());
								//剩余还款金额  = 剩余还款金额 - 本笔本息合计
								loanPayBackSum = loanPayBackSum.subtract(lb.getTotalSum());
							} else {
								//剩余还款金额小于或者等于本笔本息合计，则本笔还款金额设置为剩余还款金额
								lb.setLoanPayBackSumDetail(loanPayBackSum);
								//剩余还款金额设置为0
								loanPayBackSum = new BigDecimal(0);
							}
						} else {
							//剩余还款等于0时，结束
							break;
						}
					}
				} else {
					//如果全额还款，则将每次的还款明细的“本笔还款金额”设置为“本笔的本息合计”
					for(LoanPayBackDTO lb : list) 
						lb.setLoanPayBackSumDetail(lb.getTotalSum());
				}
			}
			
			//逐笔写入明细表
			for(LoanPayBackDTO lb : list) {
				if(lb.getLoanPayBackSumDetail() != null 
						&& ((lb.getLoanPayBackSumDetail()).compareTo(new BigDecimal(0)) > 0)) {
					acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1", "6", 
							lb.getPosNo(), "166", lb.getTotalSum().toString(), 
							lb.getLoanPayBackSumDetail().toString()));
				}
			}
			
		}
		
		acceptDetailList.addAll(generator.getResult());
				
		return acceptDetailList; 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com
	 * .sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "148");
		posObjectAndItemNoList.add(item);
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "086");
		posObjectAndItemNoList.add(item);
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "087");
		posObjectAndItemNoList.add(item);

		double loanAndinterestSum = 0.0d;
		DecimalFormat df=new DecimalFormat("######0.00");//格式化保留两位小数
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("1".equals(detail.getPosObject())
						&& "148".equals(detail.getItemNo())) {
					// 本次还款金额
					bom.setLoanPayBackSum(Double.parseDouble(df.format(Double.parseDouble(detail
							.getNewValue()))));

				}

				if (("1".equals(detail.getPosObject()) && "086".equals(detail
						.getItemNo()))
						|| ("1".equals(detail.getPosObject()) && "087"
								.equals(detail.getItemNo()))) {

					loanAndinterestSum = loanAndinterestSum
							+ Double.parseDouble(detail.getNewValue());

				}

			}
			// 保单贷款本息和
			bom.setLoanAndinterestSum(Double.parseDouble(df.format(loanAndinterestSum)));
		}

		bom.setEntireSingleSurrender(posRulesDAO.entireSingleSurrender(bom
				.getPosNo()));

		super.fillBOMForProcessRuleCheck(bom);
	}

}
