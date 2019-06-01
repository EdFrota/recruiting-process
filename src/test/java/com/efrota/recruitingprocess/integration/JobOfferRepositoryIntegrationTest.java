package com.efrota.recruitingprocess.integration;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.efrota.recruitingprocess.model.JobOffer;
import com.efrota.recruitingprocess.model.constants.JobOfferConstants;
import com.efrota.recruitingprocess.repository.JobOfferRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-integration.properties")
public class JobOfferRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private JobOfferRepository repository;

	private static final String EXISTING_OFFER_TITLE = "existing offer test";

	@Before
	public void createJobOffer() {
		JobOffer offer = new JobOffer();
		offer.setTitle(EXISTING_OFFER_TITLE);
		entityManager.persistAndFlush(offer);
	}

	@Test
	public void givenExistingJobOffer_whenFindByTitle_thenReturnJobOffer() {
		JobOffer found = repository.findByTitle(EXISTING_OFFER_TITLE);
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getTitle()).isEqualTo(EXISTING_OFFER_TITLE);
	}

	@Test
	public void givenNonExistingJobOffer_whenFindByTitle_thenReturnNull() {
		final String offerTitle = "non-existing offer test";
		JobOffer found = repository.findByTitle(offerTitle);
		Assertions.assertThat(found).isNull();
	}

	@Test(expected = PersistenceException.class)
	public void givenJobOfferMissingTitle_whenCreate_thenThrowPersistenceException() {
		JobOffer offer = new JobOffer();
		offer.setStartDate(DateUtil.now());

		entityManager.persistAndFlush(offer);
	}

	@Test(expected = PersistenceException.class)
	public void givenJobOfferNonUniqueTitle_whenCreate_thenThrowPersistenceException() {
		JobOffer offer = new JobOffer();
		offer.setTitle(EXISTING_OFFER_TITLE);

		entityManager.persistAndFlush(offer);
	}

	@Test(expected = PersistenceException.class)
	public void givenJobOfferLongTitle_whenCreate_thenThrowPersistenceException() {
		JobOffer offer = new JobOffer();
		offer.setTitle(StringUtils.repeat("a", JobOfferConstants.TITLE_LENGTH + 1));

		entityManager.persistAndFlush(offer);
	}
}
