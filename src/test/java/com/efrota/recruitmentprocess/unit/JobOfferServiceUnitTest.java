package com.efrota.recruitmentprocess.unit;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.repository.JobOfferRepository;
import com.efrota.recruitmentprocess.service.JobOfferService;
import com.efrota.recruitmentprocess.service.JobOfferServiceImpl;

@RunWith(SpringRunner.class)
public class JobOfferServiceUnitTest {

	@TestConfiguration
	static class JobOfferServiceImplTestContextConfig {
		@Bean
		public JobOfferService jobOfferService() {
			return new JobOfferServiceImpl();
		}
	}

	@MockBean
	private JobOfferRepository jobOfferRepository;

	@Autowired
	private JobOfferService jobOfferService;

	private static final String OFFER_TITLE = "offer title";
	private static final Date START_DATE = DateUtil.now();

	@Test
	public void givenOffer_whenCreate_thenReturnOffer() {
		JobOffer offer = new JobOffer();
		offer.setTitle(OFFER_TITLE);
		offer.setStartDate(START_DATE);

		Mockito.when(jobOfferService.create(offer)).thenReturn(offer);

		JobOffer created = jobOfferService.create(offer);

		Assertions.assertThat(created).isNotNull();
		Assertions.assertThat(created.getTitle()).isEqualTo(OFFER_TITLE);
		Assertions.assertThat(created.getStartDate()).isEqualTo(START_DATE);
	}

	@Test
	public void givenOfferMissingAttribute_whenCreate_thenReturnOffer() {
		JobOffer created = jobOfferService.create(new JobOffer());

		Assertions.assertThat(created).isNull();
	}

}
