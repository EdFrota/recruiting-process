package com.efrota.recruitmentprocess.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.exception.NotFoundException;
import com.efrota.recruitmentprocess.exception.ServiceValidationException;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.repository.JobApplicationRepository;

/**
 * Service implementation of {@link JobApplicationService}.
 * 
 * @author edmundofrota
 *
 */
@SuppressWarnings("serial")
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public class JobApplicationServiceImpl implements JobApplicationService {

	private Log log = LogFactory.getLog(JobApplicationServiceImpl.class);

	@Autowired
	private JobApplicationRepository jobApplicationRepository;
	@Autowired
	private JobOfferService jobOfferService;

	@Override
	public JobApplication create(JobApplication jobApplication, String jobOfferTitle) {
		JobOffer jobOffer = jobOfferService.findByTitle(jobOfferTitle);

		// validate offer title existence
		if (jobOffer == null) {
			String message = String.format("Offer %s could not be found.", jobOfferTitle);
			log.warn(message);
			throw new NotFoundException(message);
		}

		JobApplication sameEmailApp = findByJobOfferTitleCandidateEmail(jobOfferTitle,
				jobApplication.getCandidateEmail());

		// validate unique email
		if (sameEmailApp != null) {
			String message = String.format("Email %s already exist for the offer %s.",
					jobApplication.getCandidateEmail(), jobOfferTitle);
			log.warn(message);
			throw new ServiceValidationException(message);
		}

		jobApplication.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		jobApplication.setJobOffer(jobOffer);
		return jobApplicationRepository.save(jobApplication);
	}

	@Override
	public List<JobApplication> findByJobOfferTitle(String jobOfferTitle) {
		return jobApplicationRepository.findByJobOfferTitle(jobOfferTitle);
	}

	@Override
	public JobApplication findByJobOfferTitleCandidateEmail(String jobOfferTitle, String candidateEmail) {
		return jobApplicationRepository.findByJobOfferTitleAndCandidateEmail(jobOfferTitle, candidateEmail);
	}

	/**
	 * Update application to status
	 * <code>JobApplicationStatusEnum.INVITED</code>.
	 * 
	 * {@link ServiceValidationException} in case of current status is different
	 * of: <code>JobApplicationStatusEnum.APPLIED</code>
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 */
	private void inviteCandidate(String jobOfferTitle, String candidateEmail) {
		JobApplication jobApplication = findExistingJobApplication(jobOfferTitle, candidateEmail);

		if (jobApplication.getJobApplicationStatusEnum() != JobApplicationStatusEnum.APPLIED) {
			String message = String.format("Candidate %s cannot be invited for the offer %s.", candidateEmail,
					jobOfferTitle);
			log.warn(message);
			throw new ServiceValidationException(message);
		}

		updateJobApplication(jobApplication, JobApplicationStatusEnum.INVITED);
	}

	/**
	 * Update application to status
	 * <code>JobApplicationStatusEnum.REJECTED</code>.
	 * 
	 * {@link ServiceValidationException} in case of current status is different
	 * of: <code>JobApplicationStatusEnum.APPLIED</code>
	 * <code>JobApplicationStatusEnum.INVITED</code>
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 */
	private void rejectCandidate(String jobOfferTitle, String candidateEmail) {
		JobApplication jobApplication = findExistingJobApplication(jobOfferTitle, candidateEmail);

		if (jobApplication.getJobApplicationStatusEnum() != JobApplicationStatusEnum.APPLIED
				&& jobApplication.getJobApplicationStatusEnum() != JobApplicationStatusEnum.INVITED) {
			String message = String.format("Candidate %s cannot be rejected for the offer %s.", candidateEmail,
					jobOfferTitle);
			log.warn(message);
			throw new ServiceValidationException(message);
		}

		updateJobApplication(jobApplication, JobApplicationStatusEnum.REJECTED);
	}

	/**
	 * Update application to status <code>JobApplicationStatusEnum.HIRED</code>.
	 * 
	 * {@link ServiceValidationException} in case of current status is different
	 * of: <code>JobApplicationStatusEnum.INVITED</code>
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 */
	private void hireCandidate(String jobOfferTitle, String candidateEmail) {
		JobApplication jobApplication = findExistingJobApplication(jobOfferTitle, candidateEmail);

		if (jobApplication.getJobApplicationStatusEnum() != JobApplicationStatusEnum.INVITED) {
			String message = String.format("Candidate %s cannot be hired for the offer %s.", candidateEmail,
					jobOfferTitle);
			log.warn(message);
			throw new ServiceValidationException(message);
		}

		updateJobApplication(jobApplication, JobApplicationStatusEnum.HIRED);
	}

	/**
	 * Find existing application.
	 * 
	 * In case of not found, {@link NotFoundException} is thrown.
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title used as filter.
	 * @param candidateEmail
	 *            {@link JobApplication} email used as filter.
	 * @return {@link JobApplication} filtered by offer title and email.
	 */
	private JobApplication findExistingJobApplication(String jobOfferTitle, String candidateEmail) {
		JobApplication jobApplication = findByJobOfferTitleCandidateEmail(jobOfferTitle, candidateEmail);

		if (jobApplication == null) {
			String message = String.format(
					"Aplication not found for offer %s email %s.", jobOfferTitle, candidateEmail);
			log.warn(message);
			throw new NotFoundException(message);
		}

		return jobApplication;
	}

	/**
	 * Update the application status to the passed status as parameter.
	 * 
	 * @param jobApplication
	 *            {@link JobApplication} to be updated.
	 * @param status
	 *            {@link JobApplicationStatusEnum} new status of the
	 *            application.
	 */
	private void updateJobApplication(JobApplication jobApplication, JobApplicationStatusEnum status) {

		jobApplication.setJobApplicationStatusEnum(status);

		log.info(String.format("Sending %s email to candidate %s...", 
				jobApplication.getJobApplicationStatusEnum(), jobApplication.getCandidateEmail()));

		jobApplicationRepository.save(jobApplication);
	}

	@Override
	public void update(JobApplicationStatusEnum jobApplicationStatusEnum, String jobOfferTitle, 
			String candidateEmail) {
		switch (jobApplicationStatusEnum) {
			case APPLIED:
				throw new ServiceValidationException(
						String.format("Candidate %s cannot have status changed back to %s for the offer %s.",
								candidateEmail, jobApplicationStatusEnum, jobOfferTitle));
			case INVITED:
				inviteCandidate(jobOfferTitle, candidateEmail);
				break;
			case REJECTED:
				rejectCandidate(jobOfferTitle, candidateEmail);
				break;
			case HIRED:
				hireCandidate(jobOfferTitle, candidateEmail);
				break;
		}
	}

}
