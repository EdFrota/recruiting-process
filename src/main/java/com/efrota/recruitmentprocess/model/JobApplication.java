package com.efrota.recruitmentprocess.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.model.constants.JobApplicationConstants;

/**
 * Entity class for job application.
 * 
 * @author edmundofrota
 *
 */
@Entity
@Table(
		name = "application", 
		uniqueConstraints = @UniqueConstraint(
				name = "uk_offer_email", columnNames = { "job_offer_id", "candidate_email" }))
@SuppressWarnings("serial")
public class JobApplication implements Serializable {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "job_offer_id", referencedColumnName = "id", nullable = false)
	private JobOffer jobOffer;

	@Column(name = "candidate_email", nullable = false, length = JobApplicationConstants.CANDIDATE_EMAIL_LENGTH)
	private String candidateEmail;

	@Column(name = "resume_text", length = JobApplicationConstants.CANDIDATE_RESUME_LENGTH)
	private String resumeText;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = JobApplicationConstants.CANDIDATE_STATUS_LENGTH)
	private JobApplicationStatusEnum jobApplicationStatusEnum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JobOffer getJobOffer() {
		return jobOffer;
	}

	public void setJobOffer(JobOffer jobOffer) {
		this.jobOffer = jobOffer;
	}

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

	public JobApplicationStatusEnum getJobApplicationStatusEnum() {
		return jobApplicationStatusEnum;
	}

	public void setJobApplicationStatusEnum(JobApplicationStatusEnum jobApplicationStatusEnum) {
		this.jobApplicationStatusEnum = jobApplicationStatusEnum;
	}

}
