package com.efrota.recruitmentprocess.service;

import java.io.Serializable;
import java.util.List;

import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;

/**
 * Service for {@link JobOffer}.
 * 
 * @author edmundofrota
 *
 */
public interface JobOfferService extends Serializable {

	/**
	 * Create an application.
	 * 
	 * @param jobOffer
	 *            {@link JobApplication} containing the data to be stored.
	 * @return created {@link JobOffer}
	 */
	JobOffer create(JobOffer jobOffer);

	/**
	 * Find an offer based on the title.
	 * 
	 * @param title
	 *            {@link JobOffer} title used as filter.
	 * @return {@link JobOffer} filtered by title.
	 */
	JobOffer findByTitle(String title);

	/**
	 * List of offers with fetched application amount.
	 * 
	 * @return List of {@link JobOffer}.
	 */
	List<JobOffer> findAll();

}
