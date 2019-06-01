package com.efrota.recruitingprocess.service;

import java.io.Serializable;
import java.util.List;

import com.efrota.recruitingprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitingprocess.model.JobApplication;
import com.efrota.recruitingprocess.model.JobOffer;

/**
 * Service for {@link JobApplication}.
 * 
 * @author edmundofrota
 *
 */
public interface JobApplicationService extends Serializable {

	/**
	 * Create an application.
	 * 
	 * @param jobApplication
	 *            {@link JobApplication} containg the data to be stored.
	 * @param jobOfferTitle
	 *            {@link JobOffer} title to be related.
	 * @return created {@link JobApplication}
	 */
	JobApplication create(JobApplication jobApplication, String jobOfferTitle);

	/**
	 * Update an application status for another status.
	 * 
	 * Application can only be updated if the transition from current status to
	 * next one match some criteria.
	 * 
	 * Current status <code>JobApplicationStatusEnum.APPLIED</code> can be
	 * updated to: <code>JobApplicationStatusEnum.INVITED</code>
	 * <code>JobApplicationStatusEnum.DECLINED</code>
	 * 
	 * Current status <code>JobApplicationStatusEnum.INVITED</code> can be
	 * updated to: <code>JobApplicationStatusEnum.DECLINED</code>
	 * <code>JobApplicationStatusEnum.HIRED</code>
	 * 
	 * Current status <code>JobApplicationStatusEnum.DECLINED</code> can NOT be
	 * updated.
	 * 
	 * Current status <code>JobApplicationStatusEnum.HIRED</code> can be NOT be
	 * updated.
	 * 
	 * @param jobApplicationStatusEnum
	 *            {@link JobApplicationStatusEnum} to be updated.
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 */
	void update(JobApplicationStatusEnum jobApplicationStatusEnum, String jobOfferTitle, String candidateEmail);

	/**
	 * Find a list of applications based on the offer title.
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @return {@link JobApplication} filtered by offer title.
	 */
	List<JobApplication> findByJobOfferTitle(String jobOfferTitle);

	/**
	 * Find an application based on the offer title and application email.
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 * @return {@link JobApplication} filtered by offer title and email.
	 */
	JobApplication findByJobOfferTitleCandidateEmail(String jobOfferTitle, String candidateEmail);

}
