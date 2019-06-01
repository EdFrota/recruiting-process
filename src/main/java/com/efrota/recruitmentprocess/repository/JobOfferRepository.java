package com.efrota.recruitmentprocess.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.efrota.recruitmentprocess.model.JobOffer;

/**
 * Repository for {@link JobOffer}.
 * 
 * @author edmundofrota
 *
 */
public interface JobOfferRepository extends JpaRepository<JobOffer, Integer> {

	/**
	 * Find an offer based on the title.
	 * <code>JobOffer::applicationAmount</code> fetched.
	 * 
	 * @param title
	 *            {@link JobOffer} title.
	 * @return {@link JobOffer} filtered by the title.
	 */
	@Query("select new com.efrota.recruitmentprocess.model.JobOffer(o.id, o.title, o.startDate, count(a)) "
			+ "from JobOffer o left join o.jobApplications a where o.title = ?1 group by o.id")
	JobOffer findByTitle(String title);

	/**
	 * Find all offers with fetched application amount.
	 * 
	 * @return List of {@link JobOffer}
	 */
	@Query("select new com.efrota.recruitmentprocess.model.JobOffer(o.id, o.title, o.startDate, count(a)) "
			+ "from JobOffer o left join o.jobApplications a group by o.id")
	List<JobOffer> findAllFetchApplicationAmount();

}
