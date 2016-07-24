package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class SampleSeqDTO implements Serializable {
	
	private static final long serialVersionUID = 6034677757844503728L;
    private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private String sampleSeq;
	private BigDecimal reviewSum;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date reviewDate; // 复核应领日期

	public String getSampleSeq() {
		return sampleSeq;
	}

	public void setSampleSeq(String sampleSeq) {
		this.sampleSeq = sampleSeq;
	}

	public BigDecimal getReviewSum() {
		return reviewSum;
	}

	public void setReviewSum(BigDecimal reviewSum) {
		this.reviewSum = reviewSum;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
}
