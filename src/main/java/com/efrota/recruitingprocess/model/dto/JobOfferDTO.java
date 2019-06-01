package com.efrota.recruitingprocess.model.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;

import com.efrota.recruitingprocess.model.JobOffer;
import com.efrota.recruitingprocess.model.constants.JobOfferConstants;

/**
 * DTO class to handle and validate {@link JobOffer} entity.
 * 
 * @author edmundofrota
 *
 */
@SuppressWarnings("serial")
public class JobOfferDTO implements Serializable {

	@Size(min = 1, max = JobOfferConstants.TITLE_LENGTH, 
			message = "Job title cannot be less than 1 and greater then " + JobOfferConstants.TITLE_LENGTH)
	private String jobTitle;

	@FutureOrPresent(message = "Start date cannot be before than today")
	private Date startDate;

	private long numberApplication;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public long getNumberApplication() {
		return numberApplication;
	}

	public void setNumberApplication(long numberApplication) {
		this.numberApplication = numberApplication;
	}
}
