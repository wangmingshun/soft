package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;


public class PersonalNoticeDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5320323230681060828L;
	public static class InsProductInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9118893840507903365L;
		private String company;
		private String insName;
		private BigDecimal insSum;
		private BigDecimal hospitalizationPremPerDay;
		@DateTimeFormat(pattern="yyyy-MM-dd")
		private Date effDate;
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public String getInsName() {
			return insName;
		}
		public void setInsName(String insName) {
			this.insName = insName;
		}
		public BigDecimal getInsSum() {
			return insSum;
		}
		public void setInsSum(BigDecimal insSum) {
			this.insSum = insSum;
		}
		public BigDecimal getHospitalizationPremPerDay() {
			return hospitalizationPremPerDay;
		}
		public void setHospitalizationPremPerDay(BigDecimal hospitalizationPremPerDay) {
			this.hospitalizationPremPerDay = hospitalizationPremPerDay;
		}
		public Date getEffDate() {
			return effDate;
		}
		public void setEffDate(Date effDate) {
			this.effDate = effDate;
		}
	}
	
	private List<InsProductInfo> insProductList = new ArrayList<InsProductInfo>();
	
	private String clientOption;
	
	private String itemAnswer_1;
	private String itemAnswer_2;
	private String itemAnswer_3;
	private String itemAnswer_4;
	private String itemAnswer_5;
	private String itemAnswer_6_1;
	private String itemAnswer_6_2;
	private String itemAnswer_7;
	private String itemAnswer_8;
	private String itemAnswer_9;
	private String itemAnswer_10_1;
	private String itemAnswer_10_2;
	private String itemAnswer_10_3;
	private String itemAnswer_10_4;
	private String itemAnswer_10_5;
	private String itemAnswer_10_6;
	private String itemAnswer_10_7;
	private String itemAnswer_11_1;
	private String itemAnswer_11_2;
	private String itemAnswer_12_1;
	private String itemAnswer_12_2;
	private String itemAnswer_12_3;
	private String itemAnswer_12_4;
	private String itemAnswer_13_1;
	private String itemAnswer_13_2;
	private String itemAnswer_13_3;
	private String itemAnswer_13_4;
	private String itemAnswer_13_5;
	private String itemAnswer_13_6;
	private String itemAnswer_13_7;
	private String itemAnswer_13_8;
	private String itemAnswer_13_9;
	private String itemAnswer_13_10;
	private String itemAnswer_13_11;
	private String itemAnswer_13_12;
	private String itemAnswer_13_13;
	private String itemAnswer_14;
	private String itemAnswer_15;
	private String itemAnswer_16;
	private String itemAnswer_17;
	
	private String itemRemark_2;
	private String itemRemark_3;
	private String itemRemark_4;
	private String itemRemark_5;
	private String itemRemark_6_1;
	private String itemRemark_6_2;
	private String itemRemark_7;
	private String itemRemark_8;
	private String itemRemark_9;
	private String itemRemark_10_1;
	private String itemRemark_10_2;
	private String itemRemark_10_3;
	private String itemRemark_10_4;
	private String itemRemark_10_5;
	private String itemRemark_10_6;
	private String itemRemark_10_7;
	private String itemRemark_11_1;
	private String itemRemark_11_2;
	private String itemRemark_12_1;
	private String itemRemark_12_2;
	private String itemRemark_12_3;
	private String itemRemark_12_4;
	private String itemRemark_13_1;
	private String itemRemark_13_2;
	private String itemRemark_13_3;
	private String itemRemark_13_4;
	private String itemRemark_13_5;
	private String itemRemark_13_6;
	private String itemRemark_13_7;
	private String itemRemark_13_8;
	private String itemRemark_13_9;
	private String itemRemark_13_10;
	private String itemRemark_13_11;
	private String itemRemark_13_12;
	private String itemRemark_13_13;
	private String itemRemark_14;
	private String itemRemark_15;
	private String itemRemark_16;
	private String itemRemark_17;
	private BigDecimal birthHeight;
	private BigDecimal birthWeight;
	private String birthHospital;
	private BigInteger birthStayHispitalDays;
	private BigInteger fetationWeeks;
	private BigInteger smokeYear;
	private BigInteger smokePerDay;
	private String smokeQuitReason;
	private BigInteger drinkYear;
	private String drinkType;
	private BigDecimal drinkWeightPerWeek;
	private String drinkQuitReason;
	private BigDecimal height;
	private BigDecimal weight;
	
	private Set<String> itemOption;
	
	private PosInfoDTO posInfo;
	private ClientInformationDTO clientInfo;
	//家庭单多被保险人需求 add by zhangyi.wb
	private String insuredSeq;//被保险人序号
	/**
	 * 判断健康告知中是否有是的选项
	 * @param personalNoticeDTO
	 * @return
	 */
	public boolean isAnyDeclarationYes() {
		String[] checkArr = new String[] {
			"itemAnswer_1",		"itemAnswer_2",		"itemAnswer_3",		"itemAnswer_4",		"itemAnswer_5",
			"itemAnswer_6_1",	"itemAnswer_6_2",	"itemAnswer_7",		"itemAnswer_8",		"itemAnswer_9",
			"itemAnswer_10_1",	"itemAnswer_10_2",	"itemAnswer_10_3",	"itemAnswer_10_4",	"itemAnswer_10_5","itemAnswer_10_6","itemAnswer_10_7","itemAnswer_11_1",
			"itemAnswer_11_2",	"itemAnswer_12_1",	"itemAnswer_12_2",	"itemAnswer_12_3",	"itemAnswer_12_4",
			"itemAnswer_13_1",	"itemAnswer_13_2",	"itemAnswer_13_3",	"itemAnswer_13_4",	"itemAnswer_13_5",
			"itemAnswer_13_6",	"itemAnswer_13_7",	"itemAnswer_13_8",	"itemAnswer_13_9",	"itemAnswer_13_10",
			"itemAnswer_13_11",	"itemAnswer_13_12",	"itemAnswer_13_13", "itemAnswer_14",	"itemAnswer_15" , "itemAnswer_16",	"itemAnswer_17"
		};//需要检查的属性数组，其中任何一个回答Y即返回true
		for(int i = 0; i < checkArr.length; i++) {
			String checkPropertyName = checkArr[i];
			try {
				String answer = BeanUtils.getProperty(this, checkPropertyName);
				if("Y".equals(answer)) {
					return true;
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return false;
	}
	
	public List<Boolean> getItemOptionList() {
		List<Boolean> list = new ArrayList<Boolean>();
		for(int i = 0; i < 20; i++) {
			list.add(false);
		}
		for(String str : itemOption) {
			int i = Integer.parseInt(str);
			list.set(i, true);
		}
		return list;
	}
	
	public List<InsProductInfo> getInsProductList() {
		return insProductList;
	}
	public void setInsProductList(List<InsProductInfo> insProductList) {
		this.insProductList = insProductList;
	}
	public String getItemAnswer_1() {
		return itemAnswer_1;
	}
	public void setItemAnswer_1(String itemAnswer_1) {
		this.itemAnswer_1 = itemAnswer_1;
	}
	public String getItemAnswer_2() {
		return itemAnswer_2;
	}
	public void setItemAnswer_2(String itemAnswer_2) {
		this.itemAnswer_2 = itemAnswer_2;
	}
	public String getItemAnswer_3() {
		return itemAnswer_3;
	}
	public void setItemAnswer_3(String itemAnswer_3) {
		this.itemAnswer_3 = itemAnswer_3;
	}
	public String getItemAnswer_4() {
		return itemAnswer_4;
	}
	public void setItemAnswer_4(String itemAnswer_4) {
		this.itemAnswer_4 = itemAnswer_4;
	}
	public String getItemAnswer_5() {
		return itemAnswer_5;
	}
	public void setItemAnswer_5(String itemAnswer_5) {
		this.itemAnswer_5 = itemAnswer_5;
	}
	public String getItemAnswer_6_1() {
		return itemAnswer_6_1;
	}
	public void setItemAnswer_6_1(String itemAnswer_6_1) {
		this.itemAnswer_6_1 = itemAnswer_6_1;
	}
	public String getItemAnswer_6_2() {
		return itemAnswer_6_2;
	}
	public void setItemAnswer_6_2(String itemAnswer_6_2) {
		this.itemAnswer_6_2 = itemAnswer_6_2;
	}
	public String getItemAnswer_7() {
		return itemAnswer_7;
	}
	public void setItemAnswer_7(String itemAnswer_7) {
		this.itemAnswer_7 = itemAnswer_7;
	}
	public String getItemAnswer_8() {
		return itemAnswer_8;
	}
	public void setItemAnswer_8(String itemAnswer_8) {
		this.itemAnswer_8 = itemAnswer_8;
	}
	public String getItemAnswer_9() {
		return itemAnswer_9;
	}
	public void setItemAnswer_9(String itemAnswer_9) {
		this.itemAnswer_9 = itemAnswer_9;
	}
	public String getItemAnswer_10_1() {
		return itemAnswer_10_1;
	}
	public void setItemAnswer_10_1(String itemAnswer_10_1) {
		this.itemAnswer_10_1 = itemAnswer_10_1;
	}
	public String getItemAnswer_10_2() {
		return itemAnswer_10_2;
	}
	public void setItemAnswer_10_2(String itemAnswer_10_2) {
		this.itemAnswer_10_2 = itemAnswer_10_2;
	}
	public String getItemAnswer_10_3() {
		return itemAnswer_10_3;
	}
	public void setItemAnswer_10_3(String itemAnswer_10_3) {
		this.itemAnswer_10_3 = itemAnswer_10_3;
	}
	public String getItemAnswer_10_4() {
		return itemAnswer_10_4;
	}
	public void setItemAnswer_10_4(String itemAnswer_10_4) {
		this.itemAnswer_10_4 = itemAnswer_10_4;
	}
	
	public String getItemAnswer_10_5() {
		return itemAnswer_10_5;
	}

	public void setItemAnswer_10_5(String itemAnswer_10_5) {
		this.itemAnswer_10_5 = itemAnswer_10_5;
	}

	public String getItemAnswer_10_6() {
		return itemAnswer_10_6;
	}

	public void setItemAnswer_10_6(String itemAnswer_10_6) {
		this.itemAnswer_10_6 = itemAnswer_10_6;
	}

	public String getItemAnswer_10_7() {
		return itemAnswer_10_7;
	}

	public void setItemAnswer_10_7(String itemAnswer_10_7) {
		this.itemAnswer_10_7 = itemAnswer_10_7;
	}

	public String getItemAnswer_11_1() {
		return itemAnswer_11_1;
	}
	public void setItemAnswer_11_1(String itemAnswer_11_1) {
		this.itemAnswer_11_1 = itemAnswer_11_1;
	}
	public String getItemAnswer_11_2() {
		return itemAnswer_11_2;
	}
	public void setItemAnswer_11_2(String itemAnswer_11_2) {
		this.itemAnswer_11_2 = itemAnswer_11_2;
	}
	public String getItemAnswer_12_1() {
		return itemAnswer_12_1;
	}
	public void setItemAnswer_12_1(String itemAnswer_12_1) {
		this.itemAnswer_12_1 = itemAnswer_12_1;
	}
	public String getItemAnswer_12_2() {
		return itemAnswer_12_2;
	}
	public void setItemAnswer_12_2(String itemAnswer_12_2) {
		this.itemAnswer_12_2 = itemAnswer_12_2;
	}
	public String getItemAnswer_12_3() {
		return itemAnswer_12_3;
	}
	public void setItemAnswer_12_3(String itemAnswer_12_3) {
		this.itemAnswer_12_3 = itemAnswer_12_3;
	}
	public String getItemAnswer_12_4() {
		return itemAnswer_12_4;
	}
	public void setItemAnswer_12_4(String itemAnswer_12_4) {
		this.itemAnswer_12_4 = itemAnswer_12_4;
	}
	public String getItemAnswer_13_1() {
		return itemAnswer_13_1;
	}
	public void setItemAnswer_13_1(String itemAnswer_13_1) {
		this.itemAnswer_13_1 = itemAnswer_13_1;
	}
	public String getItemAnswer_13_2() {
		return itemAnswer_13_2;
	}
	public void setItemAnswer_13_2(String itemAnswer_13_2) {
		this.itemAnswer_13_2 = itemAnswer_13_2;
	}
	public String getItemAnswer_13_3() {
		return itemAnswer_13_3;
	}
	public void setItemAnswer_13_3(String itemAnswer_13_3) {
		this.itemAnswer_13_3 = itemAnswer_13_3;
	}
	public String getItemAnswer_13_4() {
		return itemAnswer_13_4;
	}
	public void setItemAnswer_13_4(String itemAnswer_13_4) {
		this.itemAnswer_13_4 = itemAnswer_13_4;
	}
	public String getItemAnswer_13_5() {
		return itemAnswer_13_5;
	}
	public void setItemAnswer_13_5(String itemAnswer_13_5) {
		this.itemAnswer_13_5 = itemAnswer_13_5;
	}
	public String getItemAnswer_13_6() {
		return itemAnswer_13_6;
	}
	public void setItemAnswer_13_6(String itemAnswer_13_6) {
		this.itemAnswer_13_6 = itemAnswer_13_6;
	}
	public String getItemAnswer_13_7() {
		return itemAnswer_13_7;
	}
	public void setItemAnswer_13_7(String itemAnswer_13_7) {
		this.itemAnswer_13_7 = itemAnswer_13_7;
	}
	public String getItemAnswer_13_8() {
		return itemAnswer_13_8;
	}
	public void setItemAnswer_13_8(String itemAnswer_13_8) {
		this.itemAnswer_13_8 = itemAnswer_13_8;
	}
	public String getItemAnswer_13_9() {
		return itemAnswer_13_9;
	}
	public void setItemAnswer_13_9(String itemAnswer_13_9) {
		this.itemAnswer_13_9 = itemAnswer_13_9;
	}
	public String getItemAnswer_13_10() {
		return itemAnswer_13_10;
	}
	public void setItemAnswer_13_10(String itemAnswer_13_10) {
		this.itemAnswer_13_10 = itemAnswer_13_10;
	}
	public String getItemAnswer_13_11() {
		return itemAnswer_13_11;
	}
	public void setItemAnswer_13_11(String itemAnswer_13_11) {
		this.itemAnswer_13_11 = itemAnswer_13_11;
	}
	public String getItemAnswer_13_12() {
		return itemAnswer_13_12;
	}
	public void setItemAnswer_13_12(String itemAnswer_13_12) {
		this.itemAnswer_13_12 = itemAnswer_13_12;
	}
	public String getItemAnswer_14() {
		return itemAnswer_14;
	}
	public void setItemAnswer_14(String itemAnswer_14) {
		this.itemAnswer_14 = itemAnswer_14;
	}
	public String getItemAnswer_15() {
		return itemAnswer_15;
	}
	public void setItemAnswer_15(String itemAnswer_15) {
		this.itemAnswer_15 = itemAnswer_15;
	}
	public String getItemRemark_2() {
		return itemRemark_2;
	}
	public void setItemRemark_2(String itemRemark_2) {
		this.itemRemark_2 = itemRemark_2;
	}
	public String getItemRemark_3() {
		return itemRemark_3;
	}
	public void setItemRemark_3(String itemRemark_3) {
		this.itemRemark_3 = itemRemark_3;
	}
	public String getItemRemark_4() {
		return itemRemark_4;
	}
	public void setItemRemark_4(String itemRemark_4) {
		this.itemRemark_4 = itemRemark_4;
	}
	public String getItemRemark_5() {
		return itemRemark_5;
	}
	public void setItemRemark_5(String itemRemark_5) {
		this.itemRemark_5 = itemRemark_5;
	}
	public String getItemRemark_6_1() {
		return itemRemark_6_1;
	}
	public void setItemRemark_6_1(String itemRemark_6_1) {
		this.itemRemark_6_1 = itemRemark_6_1;
	}
	public String getItemRemark_6_2() {
		return itemRemark_6_2;
	}
	public void setItemRemark_6_2(String itemRemark_6_2) {
		this.itemRemark_6_2 = itemRemark_6_2;
	}
	public String getItemRemark_7() {
		return itemRemark_7;
	}
	public void setItemRemark_7(String itemRemark_7) {
		this.itemRemark_7 = itemRemark_7;
	}
	public String getItemRemark_8() {
		return itemRemark_8;
	}
	public void setItemRemark_8(String itemRemark_8) {
		this.itemRemark_8 = itemRemark_8;
	}
	public String getItemRemark_9() {
		return itemRemark_9;
	}
	public void setItemRemark_9(String itemRemark_9) {
		this.itemRemark_9 = itemRemark_9;
	}
	public String getItemRemark_10_1() {
		return itemRemark_10_1;
	}
	public void setItemRemark_10_1(String itemRemark_10_1) {
		this.itemRemark_10_1 = itemRemark_10_1;
	}
	public String getItemRemark_10_2() {
		return itemRemark_10_2;
	}
	public void setItemRemark_10_2(String itemRemark_10_2) {
		this.itemRemark_10_2 = itemRemark_10_2;
	}
	public String getItemRemark_10_3() {
		return itemRemark_10_3;
	}
	public void setItemRemark_10_3(String itemRemark_10_3) {
		this.itemRemark_10_3 = itemRemark_10_3;
	}
	public String getItemRemark_10_4() {
		return itemRemark_10_4;
	}
	public void setItemRemark_10_4(String itemRemark_10_4) {
		this.itemRemark_10_4 = itemRemark_10_4;
	}
	public String getItemRemark_11_1() {
		return itemRemark_11_1;
	}
	public void setItemRemark_11_1(String itemRemark_11_1) {
		this.itemRemark_11_1 = itemRemark_11_1;
	}
	public String getItemRemark_11_2() {
		return itemRemark_11_2;
	}
	public void setItemRemark_11_2(String itemRemark_11_2) {
		this.itemRemark_11_2 = itemRemark_11_2;
	}
	public String getItemRemark_12_1() {
		return itemRemark_12_1;
	}
	public void setItemRemark_12_1(String itemRemark_12_1) {
		this.itemRemark_12_1 = itemRemark_12_1;
	}
	public String getItemRemark_12_2() {
		return itemRemark_12_2;
	}
	public void setItemRemark_12_2(String itemRemark_12_2) {
		this.itemRemark_12_2 = itemRemark_12_2;
	}
	public String getItemRemark_12_3() {
		return itemRemark_12_3;
	}
	public void setItemRemark_12_3(String itemRemark_12_3) {
		this.itemRemark_12_3 = itemRemark_12_3;
	}
	public String getItemRemark_12_4() {
		return itemRemark_12_4;
	}
	public void setItemRemark_12_4(String itemRemark_12_4) {
		this.itemRemark_12_4 = itemRemark_12_4;
	}
	public String getItemRemark_13_1() {
		return itemRemark_13_1;
	}
	public void setItemRemark_13_1(String itemRemark_13_1) {
		this.itemRemark_13_1 = itemRemark_13_1;
	}
	public String getItemRemark_13_2() {
		return itemRemark_13_2;
	}
	public void setItemRemark_13_2(String itemRemark_13_2) {
		this.itemRemark_13_2 = itemRemark_13_2;
	}
	public String getItemRemark_13_3() {
		return itemRemark_13_3;
	}
	public void setItemRemark_13_3(String itemRemark_13_3) {
		this.itemRemark_13_3 = itemRemark_13_3;
	}
	public String getItemRemark_13_4() {
		return itemRemark_13_4;
	}
	public void setItemRemark_13_4(String itemRemark_13_4) {
		this.itemRemark_13_4 = itemRemark_13_4;
	}
	public String getItemRemark_13_5() {
		return itemRemark_13_5;
	}
	public void setItemRemark_13_5(String itemRemark_13_5) {
		this.itemRemark_13_5 = itemRemark_13_5;
	}
	public String getItemRemark_13_6() {
		return itemRemark_13_6;
	}
	public void setItemRemark_13_6(String itemRemark_13_6) {
		this.itemRemark_13_6 = itemRemark_13_6;
	}
	public String getItemRemark_13_7() {
		return itemRemark_13_7;
	}
	public void setItemRemark_13_7(String itemRemark_13_7) {
		this.itemRemark_13_7 = itemRemark_13_7;
	}
	public String getItemRemark_13_8() {
		return itemRemark_13_8;
	}
	public void setItemRemark_13_8(String itemRemark_13_8) {
		this.itemRemark_13_8 = itemRemark_13_8;
	}
	public String getItemRemark_13_9() {
		return itemRemark_13_9;
	}
	public void setItemRemark_13_9(String itemRemark_13_9) {
		this.itemRemark_13_9 = itemRemark_13_9;
	}
	public String getItemRemark_13_10() {
		return itemRemark_13_10;
	}
	public void setItemRemark_13_10(String itemRemark_13_10) {
		this.itemRemark_13_10 = itemRemark_13_10;
	}
	public String getItemRemark_13_11() {
		return itemRemark_13_11;
	}
	public void setItemRemark_13_11(String itemRemark_13_11) {
		this.itemRemark_13_11 = itemRemark_13_11;
	}
	public String getItemRemark_13_12() {
		return itemRemark_13_12;
	}
	public void setItemRemark_13_12(String itemRemark_13_12) {
		this.itemRemark_13_12 = itemRemark_13_12;
	}
	public String getItemRemark_14() {
		return itemRemark_14;
	}
	public void setItemRemark_14(String itemRemark_14) {
		this.itemRemark_14 = itemRemark_14;
	}
	public String getItemRemark_15() {
		return itemRemark_15;
	}
	public void setItemRemark_15(String itemRemark_15) {
		this.itemRemark_15 = itemRemark_15;
	}
	
	public String getItemRemark_16() {
		return itemRemark_16;
	}

	public void setItemRemark_16(String itemRemark_16) {
		this.itemRemark_16 = itemRemark_16;
	}

	public String getItemRemark_17() {
		return itemRemark_17;
	}

	public void setItemRemark_17(String itemRemark_17) {
		this.itemRemark_17 = itemRemark_17;
	}
	
	public String getItemAnswer_13_13() {
		return itemAnswer_13_13;
	}

	public void setItemAnswer_13_13(String itemAnswer_13_13) {
		this.itemAnswer_13_13 = itemAnswer_13_13;
	}

	public String getItemAnswer_16() {
		return itemAnswer_16;
	}

	public void setItemAnswer_16(String itemAnswer_16) {
		this.itemAnswer_16 = itemAnswer_16;
	}

	public String getItemAnswer_17() {
		return itemAnswer_17;
	}

	public void setItemAnswer_17(String itemAnswer_17) {
		this.itemAnswer_17 = itemAnswer_17;
	}

	public String getItemRemark_10_5() {
		return itemRemark_10_5;
	}

	public void setItemRemark_10_5(String itemRemark_10_5) {
		this.itemRemark_10_5 = itemRemark_10_5;
	}

	public String getItemRemark_10_6() {
		return itemRemark_10_6;
	}

	public void setItemRemark_10_6(String itemRemark_10_6) {
		this.itemRemark_10_6 = itemRemark_10_6;
	}

	public String getItemRemark_10_7() {
		return itemRemark_10_7;
	}

	public void setItemRemark_10_7(String itemRemark_10_7) {
		this.itemRemark_10_7 = itemRemark_10_7;
	}

	public String getItemRemark_13_13() {
		return itemRemark_13_13;
	}

	public void setItemRemark_13_13(String itemRemark_13_13) {
		this.itemRemark_13_13 = itemRemark_13_13;
	}

	public BigDecimal getBirthWeight() {
		return birthWeight;
	}
	public void setBirthWeight(BigDecimal birthWeight) {
		this.birthWeight = birthWeight;
	}
	public BigInteger getBirthStayHispitalDays() {
		return birthStayHispitalDays;
	}
	public void setBirthStayHispitalDays(BigInteger birthStayHispitalDays) {
		this.birthStayHispitalDays = birthStayHispitalDays;
	}
	public BigInteger getFetationWeeks() {
		return fetationWeeks;
	}
	public void setFetationWeeks(BigInteger fetationWeeks) {
		this.fetationWeeks = fetationWeeks;
	}
	public BigInteger getSmokeYear() {
		return smokeYear;
	}
	public void setSmokeYear(BigInteger smokeYear) {
		this.smokeYear = smokeYear;
	}
	public BigInteger getSmokePerDay() {
		return smokePerDay;
	}
	public void setSmokePerDay(BigInteger smokePerDay) {
		this.smokePerDay = smokePerDay;
	}
	public String getSmokeQuitReason() {
		return smokeQuitReason;
	}
	public void setSmokeQuitReason(String smokeQuitReason) {
		this.smokeQuitReason = smokeQuitReason;
	}
	public BigInteger getDrinkYear() {
		return drinkYear;
	}
	public void setDrinkYear(BigInteger drinkYear) {
		this.drinkYear = drinkYear;
	}
	public String getDrinkType() {
		return drinkType;
	}
	public void setDrinkType(String drinkType) {
		this.drinkType = drinkType;
	}
	public BigDecimal getDrinkWeightPerWeek() {
		return drinkWeightPerWeek;
	}
	public void setDrinkWeightPerWeek(BigDecimal drinkWeightPerWeek) {
		this.drinkWeightPerWeek = drinkWeightPerWeek;
	}
	public String getDrinkQuitReason() {
		return drinkQuitReason;
	}
	public void setDrinkQuitReason(String drinkQuitReason) {
		this.drinkQuitReason = drinkQuitReason;
	}
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getClientOption() {
		return clientOption;
	}
	public void setClientOption(String clientOption) {
		this.clientOption = clientOption;
	}
	public Set<String> getItemOption() {
		return itemOption;
	}
	public void setItemOption(Set<String> itemOption) {
		this.itemOption = itemOption;
	}
	public PosInfoDTO getPosInfo() {
		return posInfo;
	}
	public void setPosInfo(PosInfoDTO posInfo) {
		this.posInfo = posInfo;
	}
	public ClientInformationDTO getClientInfo() {
		return clientInfo;
	}
	public void setClientInfo(ClientInformationDTO clientInfo) {
		this.clientInfo = clientInfo;
	}
	public BigDecimal getBirthHeight() {
		return birthHeight;
	}
	public void setBirthHeight(BigDecimal birthHeight) {
		this.birthHeight = birthHeight;
	}
	public String getBirthHospital() {
		return birthHospital;
	}
	public void setBirthHospital(String birthHospital) {
		this.birthHospital = birthHospital;
	}

	public String getInsuredSeq() {
		return insuredSeq;
	}

	public void setInsuredSeq(String insuredSeq) {
		this.insuredSeq = insuredSeq;
	}
	
}
