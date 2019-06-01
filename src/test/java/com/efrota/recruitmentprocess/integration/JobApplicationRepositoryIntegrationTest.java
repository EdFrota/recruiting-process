package com.efrota.recruitmentprocess.integration;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.constants.JobApplicationConstants;
import com.efrota.recruitmentprocess.repository.JobApplicationRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-integration.properties")
public class JobApplicationRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private JobApplicationRepository repository;
	
	private static final String OFFER_TITLE = "offer test";
	private static final String CANDIDATE_EMAIL = "email@email.com";
	
	@Before
	public void persistData() {
		JobOffer offer = new JobOffer();
		offer.setTitle(OFFER_TITLE);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail(CANDIDATE_EMAIL);
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		entityManager.persist(application);
		
		application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail("another@email.com");
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		entityManager.persist(application);
		
		entityManager.flush();
	}

	@Test
	public void givenExistingJobOfferTitle_whenFindByJobOfferTitle_thenReturnJobApplications() {
		List<JobApplication> founds = repository.findByJobOfferTitle(OFFER_TITLE);
		Assertions.assertThat(founds).isNotNull();
		Assertions.assertThat(founds.size()).isEqualTo(2);
	}

	@Test
	public void givenNonExistingJobOfferTitle_whenFindByJobOfferTitle_thenReturnEmpty() {
		final String offerTitle = "non-existing offer";
		List<JobApplication> founds = repository.findByJobOfferTitle(offerTitle);
		Assertions.assertThat(founds).isEmpty();
	}
	
	
	@Test
	public void givenExistingJobOfferTitleAndCandidateEmail_whenFindByJobOfferTitleAndCandidateEmail_thenReturnJobApplication() {
		JobApplication found = repository.findByJobOfferTitleAndCandidateEmail(OFFER_TITLE, CANDIDATE_EMAIL);
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getJobOffer().getTitle()).isEqualTo(OFFER_TITLE);
		Assertions.assertThat(found.getCandidateEmail()).isEqualTo(CANDIDATE_EMAIL);
	}

	@Test
	public void givenNonExistingJobOfferTitleAndOrCandidateEmail_whenFindByJobOfferTitleAndCandidateEmail_thenReturnNull() {
		final String nonExistingOfferTitle = "non-existing offer";
		final String nonExistingEmail = "non-existing email";
		
		// both parameters do not exist
		JobApplication found = repository.findByJobOfferTitleAndCandidateEmail(nonExistingOfferTitle, nonExistingEmail);
		Assertions.assertThat(found).isNull();
		
		// offer does not exist only
		found = repository.findByJobOfferTitleAndCandidateEmail(nonExistingOfferTitle, CANDIDATE_EMAIL);
		Assertions.assertThat(found).isNull();
		
		// email does not exist only
		found = repository.findByJobOfferTitleAndCandidateEmail(OFFER_TITLE, nonExistingOfferTitle);
		Assertions.assertThat(found).isNull();
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationSameJobOfferAndEmail_whenCreate_thenThrowPersistenceException() {
		final String offerTitle = "same offer";
		final String email = "same email";
		
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail(email);
		entityManager.persist(application);
		
		application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail(email);
		entityManager.persist(application);
		
		entityManager.flush();
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationMissingEmail_whenCreate_thenThrowPersistenceException() {
		final String offerTitle = "missing email offer title";
		
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		entityManager.persistAndFlush(application);
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationMissingOffer_whenCreate_thenThrowPersistenceException() {
		JobApplication application = new JobApplication();
		application.setCandidateEmail("missingoffer@email.com");
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		entityManager.persistAndFlush(application);
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationMissingStatus_whenCreate_thenThrowPersistenceException() {
		final String offerTitle = "missing status offer title";
		
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail("missingstatus@email.com");
		entityManager.persistAndFlush(application);
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationLongEmail_whenCreate_thenThrowPersistenceException() {
		final String offerTitle = "long email offer title";
		
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setCandidateEmail(
				StringUtils.repeat("a", JobApplicationConstants.CANDIDATE_EMAIL_LENGTH + 1));
		application.setJobOffer(offer);
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		
		entityManager.persistAndFlush(application);
	}
	
	@Test(expected = PersistenceException.class)
	public void givenJobApplicationLongResume_whenCreate_thenThrowPersistenceException() {
		final String offerTitle = "long resume offer title";
		
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);
		entityManager.persist(offer);
		
		JobApplication application = new JobApplication();
		application.setResumeText(
				StringUtils.repeat("a", JobApplicationConstants.CANDIDATE_RESUME_LENGTH + 1));
		application.setJobOffer(offer);
		application.setCandidateEmail("longresume@email.com");
		application.setJobApplicationStatusEnum(JobApplicationStatusEnum.APPLIED);
		
		entityManager.persistAndFlush(application);
	}
	
}
