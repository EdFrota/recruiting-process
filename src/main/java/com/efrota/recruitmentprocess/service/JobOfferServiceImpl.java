package com.efrota.recruitmentprocess.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.efrota.recruitmentprocess.controller.JobOfferController;
import com.efrota.recruitmentprocess.exception.ServiceValidationException;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.repository.JobOfferRepository;

/**
 * Service implementation of {@link JobOfferService}.
 * 
 * @author edmundofrota
 *
 */
@SuppressWarnings("serial")
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public class JobOfferServiceImpl implements JobOfferService {

	private Log log = LogFactory.getLog(JobOfferController.class);

	@Autowired
	private JobOfferRepository jobOfferRepository;

	@Override
	public JobOffer create(JobOffer jobOffer) {
		JobOffer exist = findByTitle(jobOffer.getTitle());
		if (exist == null) {
			return jobOfferRepository.save(jobOffer);
		} else {
			String message = String.format("Offer %s already exist.", jobOffer.getTitle());
			log.warn(message);
			throw new ServiceValidationException(message);
		}
	}

	@Override
	public JobOffer findByTitle(String title) {
		return jobOfferRepository.findByTitle(title);
	}

	@Override
	public List<JobOffer> findAll() {
		return jobOfferRepository.findAllFetchApplicationAmount();
	}

}
