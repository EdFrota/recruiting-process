package com.efrota.recruitmentprocess.model.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.constants.JobApplicationConstants;
import com.efrota.recruitmentprocess.model.constants.JobOfferConstants;

/**
 * DTO class to handle and validate {@link JobApplication} entity.
 * 
 * @author edmundofrota
 *
 */
@SuppressWarnings("serial")
public class JobApplicationDTO implements Serializable {

	@Size(min = 1, max = JobOfferConstants.TITLE_LENGTH, 
			message = "Job offer cannot be less than 1 and greater then " + JobOfferConstants.TITLE_LENGTH)
	private String jobOffer;
	
	@Size(min = 1, max = JobApplicationConstants.CANDIDATE_EMAIL_LENGTH, 
			message = "Candidate email cannot be less than 1 and greater then " + JobApplicationConstants.CANDIDATE_EMAIL_LENGTH)
	private String candidateEmail;
	
	@Size(min = 1, max = JobApplicationConstants.CANDIDATE_RESUME_LENGTH, 
			message = "Resume text cannot be less than 1 and greater then " + JobApplicationConstants.CANDIDATE_RESUME_LENGTH)
	private String resumeText;
	
	private JobApplicationStatusEnum status;

	public String getCandidateEmail() {
		return candidateEmail;
	}

	public void setCandidateEmail(String candidateEmail) {
		this.candidateEmail = candidateEmail;
	}

	public String getResumeText() {
		return resumeText;
	}

	public void setResumeText(String resumeText) {
		this.resumeText = resumeText;
	}

	public JobApplicationStatusEnum getStatus() {
		return status;
	}

	public void setStatus(JobApplicationStatusEnum status) {
		this.status = status;
	}

	public String getJobOffer() {
		return jobOffer;
	}

	public void setJobOffer(String jobOffer) {
		this.jobOffer = jobOffer;
	}
	
}
