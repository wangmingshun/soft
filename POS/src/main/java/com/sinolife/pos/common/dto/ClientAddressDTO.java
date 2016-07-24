package com.sinolife.pos.common.dto;

import org.apache.commons.lang.StringUtils;

/**
 * 客户地址明细DTO
 */
public class ClientAddressDTO extends PosComDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String addressSeq;		//地址序号
	private String addressType;		//地址类型
	private String addressTypeDesc;	//地址类型描述
	private String clientNo;		//客户号
	private String countryCode;		//国籍代码
	private String countryDesc;		//国籍名
	private String provinceCode;	//省代码
	private String provinceDesc;	//省名称
	private String cityCode;		//市代码
	private String cityDesc;		//市名称
	private String areaCode;		//区代码
	private String areaDesc;		//区名称
	private String detailAddress;	//详细地址
	private String postalcode;		//邮政编码
	private String fullAddress;		//完整地址
	private String addressStatus;	//地址状态
	private String webMobilePhoneChecked;
	private String webMobilePhoneNo;		//官网加挂的手机号码
	
	public String getWebMobilePhoneNo() {
		return webMobilePhoneNo;
	}

	public void setWebMobilePhoneNo(String webMobilePhoneNo) {
		this.webMobilePhoneNo = webMobilePhoneNo;
	}

	public String getWebMobilePhoneChecked() {
		return webMobilePhoneChecked;
	}

	public void setWebMobilePhoneChecked(String webMobilePhoneChecked) {
		this.webMobilePhoneChecked = webMobilePhoneChecked;
	}

	private String checked;			//页面是否选中

	public String getAddressSeq() {
		return addressSeq;
	}

	public void setAddressSeq(String addressSeq) {
		this.addressSeq = addressSeq;
	}

	public String getAddressType() {
		if(StringUtils.isBlank(addressType)){
			addressType="3";//保全这里都是联系地址
		}
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAddressTypeDesc() {
		return addressTypeDesc;
	}

	public void setAddressTypeDesc(String addressTypeDesc) {
		this.addressTypeDesc = addressTypeDesc;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDesc() {
		return countryDesc;
	}

	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceDesc() {
		return provinceDesc;
	}

	public void setProvinceDesc(String provinceDesc) {
		this.provinceDesc = provinceDesc;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityDesc() {
		return cityDesc;
	}

	public void setCityDesc(String cityDesc) {
		this.cityDesc = cityDesc;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaDesc() {
		return areaDesc;
	}

	public void setAreaDesc(String areaDesc) {
		this.areaDesc = areaDesc;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	public String getTypeAndAddress(){
		return addressTypeDesc + "：" + fullAddress;
	}

	public String getAddressStatus() {
		return addressStatus;
	}

	public void setAddressStatus(String addressStatus) {
		this.addressStatus = addressStatus;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
	
	/**
	 * 得到虚拟的一条主键标识
	 * 要是有多条ClientAddressDTO记录（如项目20），必须在页面上获取这个值了
	 * 项目19只有一条ClientAddressDTO记录，在这里获取比较简单准确
	 */
	public String getFakeSeq(){
		String city = StringUtils.isBlank(this.cityCode)||"null".equalsIgnoreCase(this.cityCode)?"":this.cityCode;
		String area = StringUtils.isBlank(this.areaCode)||"null".equalsIgnoreCase(this.areaCode)?"":this.areaCode;
		return "FAKE_"+this.provinceCode+"-"+city+"-"+area+"-"+this.detailAddress;
	}
}
