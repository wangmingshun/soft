package com.sinolife.pos.common.dto.serviceitems;

import com.sinolife.pos.common.dto.PolicyDTO;

/**
 * 16 补充告知
 * @author wangkui
 */

public class ServiceItemsInputDTO_16 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = 5916952952537980008L;
	private String informObject;		//补充告知对象
	private String informMatter;		//补充告知事项
	private String startDate;			//补充告知事项起点时间yyyy-mm-dd
	private String matterDesc;			//补充告知事项描述
	private String relationship;		//投被保人关系
	private String relationshipDesc;	//投被保人其他关系描述
	private PolicyDTO policyInfo;		//保单层信息
	
	private String informMatterA;				//a.未如实告知健康情况
	private String informMatterB;				//b.保全项目未如实告知
	private String informMatterC;				//c.变更或取消特别约定
	private String informMatterD;				//d.职业未如实告知
	private String informMatterE;				//e.投被保人关系告知
	private String informMatterF;				//f.新年龄超投保范围
	private String informMatterG;				//g.其他投保前未如实告知情况
	private String matterBeforeDesc;		//补充告知投保前事项描述
	private String matterAfterDesc;		//补充告知投保后事项描述
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#onPropertyChange()
	 */
	@Override
	protected void onPropertyChange() {
		super.onPropertyChange();
		if(!"5".equals(informMatterE)) {
			relationship = null;
			relationshipDesc = null;
		} else if(!"99".equals(relationship)) {
			relationshipDesc = null;
		}
	}

	public String getInformMatterA() {
		return informMatterA;
	}

	public void setInformMatterA(String informMatterA) {
		this.informMatterA = informMatterA;
	}

	public String getInformMatterB() {
		return informMatterB;
	}

	public void setInformMatterB(String informMatterB) {
		this.informMatterB = informMatterB;
	}

	public String getInformMatterC() {
		return informMatterC;
	}

	public void setInformMatterC(String informMatterC) {
		this.informMatterC = informMatterC;
	}

	public String getInformMatterD() {
		return informMatterD;
	}

	public void setInformMatterD(String informMatterD) {
		this.informMatterD = informMatterD;
	}

	public String getInformMatterE() {
		return informMatterE;
	}

	public void setInformMatterE(String informMatterE) {
		this.informMatterE = informMatterE;
	}

	public String getInformMatterF() {
		return informMatterF;
	}

	public void setInformMatterF(String informMatterF) {
		this.informMatterF = informMatterF;
	}

	public String getInformMatterG() {
		return informMatterG;
	}

	public void setInformMatterG(String informMatterG) {
		this.informMatterG = informMatterG;
	}

	public String getMatterBeforeDesc() {
		return matterBeforeDesc;
	}

	public void setMatterBeforeDesc(String matterBeforeDesc) {
		this.matterBeforeDesc = matterBeforeDesc;
	}

	public String getMatterAfterDesc() {
		return matterAfterDesc;
	}

	public void setMatterAfterDesc(String matterAfterDesc) {
		this.matterAfterDesc = matterAfterDesc;
	}

	public String getInformObject() {
		return informObject;
	}

	public void setInformObject(String informObject) {
		this.informObject = informObject;
	}

	public String getInformMatter() {
		return informMatter;
	}

	public void setInformMatter(String informMatter) {
		this.informMatter = informMatter;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getMatterDesc() {
		return matterDesc;
	}

	public void setMatterDesc(String matterDesc) {
		this.matterDesc = matterDesc;
	}

	public PolicyDTO getPolicyInfo() {
		return policyInfo;
	}

	public void setPolicyInfo(PolicyDTO policyInfo) {
		this.policyInfo = policyInfo;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getRelationshipDesc() {
		return relationshipDesc;
	}

	public void setRelationshipDesc(String relationshipDesc) {
		this.relationshipDesc = relationshipDesc;
	}
	
}
