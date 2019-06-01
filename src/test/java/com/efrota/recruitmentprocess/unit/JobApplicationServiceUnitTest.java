package com.efrota.recruitmentprocess.unit;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.exception.NotFoundException;
import com.efrota.recruitmentprocess.exception.ServiceValidationException;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.repository.JobApplicationRepository;
import com.efrota.recruitmentprocess.repository.JobOfferRepository;
import com.efrota.recruitmentprocess.service.JobApplicationService;
import com.efrota.recruitmentprocess.service.JobApplicationServiceImpl;
import com.efrota.recruitmentprocess.service.JobOfferService;
import com.efrota.recruitmentprocess.service.JobOfferServiceImpl;

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

@RunWith(SpringRunner.class)
public class JobApplicationServiceUnitTest {

	@TestConfiguration
	static class JobApplicationServiceImplTestContextConfig {
		@Bean
		public JobApplicationService jobApplicationService() {
			return new JobApplicationServiceImpl();
		}

		@Bean
		public JobOfferService jobOfferService() {
			return new JobOfferServiceImpl();
		}
	}

	@Autowired
	private JobApplicationService jobApplicationService;
	@Autowired
	private JobOfferService jobOfferService;
	@MockBean
	private JobApplicationRepository jobApplicationRepository;
	@MockBean
	private JobOfferRepository jobOfferRepository;

	@Test
	public void givenJobApplication_whenCreate_thenReturnJobApplication() {
		final String offerTitle = "offer title";

		JobOffer jobOffer = new JobOffer();
		jobOffer.setTitle(offerTitle);
		jobOffer.setStartDate(DateUtil.now());

		Mockito.when(jobOfferService.findByTitle(offerTitle)).thenReturn(jobOffer);

		final String email = "email@email.com";
		final String resume = "resume test";
		JobApplication application = new JobApplication();

		application.setJobOffer(jobOffer);
		application.setCandidateEmail(email);
		application.setResumeText(resume);

		Mockito.when(jobApplicationService.create(application, offerTitle)).thenReturn(application);

		JobApplication created = jobApplicationService.create(application, offerTitle);

		Assertions.assertThat(created).isNotNull();
		Assertions.assertThat(created.getCandidateEmail()).isEqualTo(email);
		Assertions.assertThat(created.getJobApplicationStatusEnum()).isEqualTo(JobApplicationStatusEnum.APPLIED);
		Assertions.assertThat(created.getJobOffer()).isNotNull();
		Assertions.assertThat(created.getJobOffer().getTitle()).isEqualTo(offerTitle);
	}

	@Test(expected = NotFoundException.class)
	public void givenNonExistingOffer_whenUpdate_thenThrowNotFoundException() {
		final String offer = "some offer";
		final String email = "email@email.com";
		final JobApplicationStatusEnum status = JobApplicationStatusEnum.INVITED;

		jobApplicationService.update(status, offer, email);
	}

	private static final String OFFER_TITLE = "offer title";
	private static final String APPLICATION_EMAIL = "email@email.com";

	private JobApplication createApplication(final JobApplicationStatusEnum status) {
		JobOffer jobOffer = new JobOffer();
		jobOffer.setTitle(OFFER_TITLE);
		jobOffer.setStartDate(DateUtil.now());

		final String resume = "resume test";
		JobApplication application = new JobApplication();

		application.setJobOffer(jobOffer);
		application.setCandidateEmail(APPLICATION_EMAIL);
		application.setResumeText(resume);
		application.setJobApplicationStatusEnum(status);

		return application;
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeAppliedToHired_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.APPLIED));

		jobApplicationService.update(JobApplicationStatusEnum.HIRED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeAppliedToApplied_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.APPLIED));

		jobApplicationService.update(JobApplicationStatusEnum.APPLIED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeInvitedToApplied_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.INVITED));

		jobApplicationService.update(JobApplicationStatusEnum.APPLIED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeRejectedToApplied_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.REJECTED));

		jobApplicationService.update(JobApplicationStatusEnum.APPLIED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeRejectedToHired_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.REJECTED));

		jobApplicationService.update(JobApplicationStatusEnum.HIRED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeRejectedToRejected_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.REJECTED));

		jobApplicationService.update(JobApplicationStatusEnum.REJECTED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeHiredToHired_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.HIRED));

		jobApplicationService.update(JobApplicationStatusEnum.HIRED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeHiredToApplied_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.HIRED));

		jobApplicationService.update(JobApplicationStatusEnum.APPLIED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeHiredToRejected_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.HIRED));

		jobApplicationService.update(JobApplicationStatusEnum.REJECTED, OFFER_TITLE, APPLICATION_EMAIL);
	}

	@Test(expected = ServiceValidationException.class)
	public void givenInvalidStatusChangeHiredToInvited_whenUpdate_thenThrowServiceValidationException() {

		Mockito.when(jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, APPLICATION_EMAIL))
				.thenReturn(createApplication(JobApplicationStatusEnum.HIRED));

		jobApplicationService.update(JobApplicationStatusEnum.INVITED, OFFER_TITLE, APPLICATION_EMAIL);
	}

}
