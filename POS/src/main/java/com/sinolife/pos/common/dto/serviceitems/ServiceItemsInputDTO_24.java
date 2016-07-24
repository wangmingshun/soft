package com.sinolife.pos.common.dto.serviceitems;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

/**
 * 24 养老 年金给付方式变更
 */
public class ServiceItemsInputDTO_24 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4609663500654195444L;
	
	private String productCode;//产品代码
	private String nextPayDate;//金管家开始给付时间
	private String effectedTwo;//保单生效两年后的日期
	private String productFrequency;//至祥缴费频率
	private String payType; // 给付方式
	private String payTypeDesc;// 给付方式描述
	private String payTypePara; // 给付方式参数--在pos里该属性被休了
	private String payPeriodType; // 给付周期
	private String payPeriodTypeDesc; // 给付周期描述
	private String payPeriodTypePara;// 给付周期参数
	private String frequency; // 给付频率
	private String typePeriodDesc;//综合起来怎么给付的描述
	private String productType;//产品类型  

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate
	 * (org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayTypePara() {
		return payTypePara;
	}

	public void setPayTypePara(String payTypePara) {
		this.payTypePara = payTypePara;
	}

	public String getPayPeriodType() {
		return payPeriodType;
	}

	public void setPayPeriodType(String payPeriodType) {
		this.payPeriodType = payPeriodType;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getPayPeriodTypePara() {
		return payPeriodTypePara;
	}

	public void setPayPeriodTypePara(String payPeriodTypePara) {
		this.payPeriodTypePara = payPeriodTypePara;
	}
	
	public String getPayTypeDesc() {
		return payTypeDesc;
	}

	public void setPayTypeDesc(String payTypeDesc) {
		this.payTypeDesc = payTypeDesc;
	}

	public String getPayPeriodTypeDesc() {
		return payPeriodTypeDesc;
	}

	public void setPayPeriodTypeDesc(String payPeriodTypeDesc) {
		this.payPeriodTypeDesc = payPeriodTypeDesc;
	}
	
	//综合起来怎么给付的描述
	public String getTypePeriodDesc(){
		if("U".equals(getProductType()) && StringUtils.isNotBlank(nextPayDate)){
			typePeriodDesc = nextPayDate + "开始给付";
			
		}else if(StringUtils.isEmpty(typePeriodDesc)){
			if(StringUtils.isNotEmpty(payType)){
				typePeriodDesc = payTypeDesc + (StringUtils.isEmpty(payTypePara)?"":"（"+payTypePara+"）");
			}else{
				typePeriodDesc = payPeriodTypeDesc + (StringUtils.isEmpty(payPeriodTypePara)?"":"（"+payPeriodTypePara+"）");
			}
			
			payTypePara =null;       //这几个在页面上是可选参数，置空掉，页面上若新录入了其值，就写入新值，若未录入，写入空，不能写原值
			payPeriodType = null;
			payPeriodTypePara = null;
			
			typePeriodDesc = typePeriodDesc.replaceAll("null", "");//null加出来会变成字符串"null"
		}
		
		return typePeriodDesc;
	}

	public void setTypePeriodDesc(String typePeriodDesc) {
		this.typePeriodDesc = typePeriodDesc;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	/**
	 * 险种类型，是U金管家一类的呢？还是C至祥一类的
	 */
	public String getProductType(){
		
		if("UIAN_AN0".equals(productCode)||
		   "UIAN_BN0".equals(productCode)||
		   "UBAN_AN1".equals(productCode)||
		   "UIAN_CN0".equals(productCode)||
		   "UBAN_BN1".equals(productCode)||
		   "UIAN_DN1".equals(productCode)||
		   "UIAN_EN1".equals(productCode)||
		   "UNAN_AN1".equals(productCode)||
		   "UIAN_FN0".equals(productCode)||
		   "UNAN_CN1".equals(productCode)||
		   "UEAN_EN1".equals(productCode)||
		   "UEAN_DN1".equals(productCode)||
		   "UBAN_EN0".equals(productCode)||
		   "UIAN_GN0".equals(productCode)||
		   "CBAN_DN1".equals(productCode)||
		   "UBAN_FN1".equals(productCode)||
		   "UEAN_FN1".equals(productCode)||
		   "UEAN_GN1".equals(productCode)||
		   "UEAN_HN1".equals(productCode)					   
		     //增加新险种判断
		   
		){
			productType = "U";
			
		}else if("CIAN_CP1".equals(productCode)||"CNAN_CN1".equals(productCode)){
			productType = "C";
		}
		return productType;
	}

	public String getEffectedTwo() {
		return effectedTwo;
	}

	public void setEffectedTwo(String effectedTwo) {
		this.effectedTwo = effectedTwo;
	}
	
	/**
	 * 返回保单生效日和受理申请日的大值
	 * 金管家开始领取日不得早于此
	 * 其实规则引擎有此控制，此处冗余
	 * @return
	 */
	public String getMinDate(){
		if(StringUtils.isBlank(effectedTwo)){
			return null;
		}
		return getApplyDate().compareTo(effectedTwo)>0?getApplyDate():effectedTwo;
	}

	public String getProductFrequency() {
		return productFrequency;
	}

	public void setProductFrequency(String productFrequency) {
		this.productFrequency = productFrequency;
	}

	public String getNextPayDate() {
		return nextPayDate;
	}

	public void setNextPayDate(String nextPayDate) {
		this.nextPayDate = nextPayDate;
	}
	
	/**
	 * 理财一号UBAN_BN1，金管家A款UIAN_CN0
	 * 月领变年领，在x月变更，则返回下一年的x月1号
	 * 年领变月领，在x月变更，则返回x+1月1号
	 * @return
	 */
	public String getDateTransYM(){
		String date = "";
		
		String ad = getApplyDate();
		if("2".equals(frequency)){
			date = (Integer.parseInt(ad.substring(0, 4))+1)+"-"+ad.substring(ad.indexOf("-")+1, ad.indexOf("-")+3)+"-01";			
		}
		if("5".equals(frequency)){
			if("12".equals(ad.substring(ad.indexOf("-")+1, ad.indexOf("-")+3))){
				date = (Integer.parseInt(ad.substring(0, 4))+1)+"-01-01";	
			}else {
				date = ad.substring(0, 5)+(Integer.parseInt(ad.substring(5, 7))+1)+"-01";
			}
		}
		return date;
	}
	
}
