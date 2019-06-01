package com.efrota.recruitingprocess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.efrota.recruitingprocess.model.JobApplication;
import com.efrota.recruitingprocess.model.JobOffer;

/**
 * Repository for {@link JobApplication}.
 * 
 * @author edmundofrota
 *
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

	/**
	 * Find applications based on offer title.
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title.
	 * @return List of {@link JobApplication} filtered by offer title.
	 */
	@Query("select a from JobApplication a where a.jobOffer.title = ?1")
	List<JobApplication> findByJobOfferTitle(String jobOfferTitle);

	/**
	 * Find an application based on offer title and candidate email.
	 * 
	 * @param jobOfferTitle
	 *            {@link JobOffer} title.
	 * @param candidateEmail
	 *            {@link JobApplication} email.
	 * @return {@link JobApplication} filtered by offer title and application
	 *         email.
	 */
	@Query("select a from JobApplication a where a.jobOffer.title = ?1 and candidateEmail = ?2")
	JobApplication findByJobOfferTitleAndCandidateEmail(String jobOfferTitle, String candidateEmail);

}
