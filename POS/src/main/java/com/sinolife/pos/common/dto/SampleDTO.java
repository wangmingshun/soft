package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;


@Entity
public class SampleDTO implements Serializable {

	private static final long serialVersionUID = 6034211757844556728L;
	private String confirmer;
	private String resultDesc;// 备注
	private String confirmResult;// 确认意见
	private List<SampleSeqDTO> sampleList;

	public List<SampleSeqDTO> getSampleList() {
		return sampleList;
	}

	public void setSampleList(List<SampleSeqDTO> sampleList) {
		this.sampleList = sampleList;
	}

	public String getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(String confirmer) {
		this.confirmer = confirmer;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getConfirmResult() {
		return confirmResult;
	}

	public void setConfirmResult(String confirmResult) {
		this.confirmResult = confirmResult;
	}

}
