package com.efrota.recruitingprocess.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.efrota.recruitingprocess.model.constants.JobOfferConstants;

/**
 * Entity class for job offer.
 * 
 * @author edmundofrota
 *
 */
@Entity
@Table(name = "offer")
@SuppressWarnings("serial")
public class JobOffer implements Serializable {

	@Id
	@GeneratedValue
	private int id;

	@Column(unique = true, nullable = false, length = JobOfferConstants.TITLE_LENGTH)
	private String title;

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;

	@OneToMany(targetEntity = JobApplication.class, mappedBy = "jobOffer", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	private List<JobApplication> jobApplications;

	/**
	 * Total amount of {@link JobApplication} for this offer.
	 */
	@Transient
	private long applicationAmount;

	public JobOffer() {
		super();
	}

	public JobOffer(int id, String title, Date startDate, long applicationAmount) {
		this();
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.applicationAmount = applicationAmount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public List<JobApplication> getJobApplications() {
		return jobApplications;
	}

	public void setJobApplications(List<JobApplication> jobApplications) {
		this.jobApplications = jobApplications;
	}

	public long getApplicationAmount() {
		return applicationAmount;
	}

	public void setApplicationAmount(long applicationAmount) {
		this.applicationAmount = applicationAmount;
	}
}
