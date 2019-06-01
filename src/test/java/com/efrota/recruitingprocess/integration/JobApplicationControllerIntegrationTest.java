package com.efrota.recruitingprocess.integration;

import com.efrota.recruitingprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitingprocess.exception.ServiceValidationException;
import com.efrota.recruitingprocess.model.JobApplication;
import com.efrota.recruitingprocess.model.JobOffer;
import com.efrota.recruitingprocess.model.dto.JobApplicationDTO;
import com.efrota.recruitingprocess.service.JobApplicationService;
import com.efrota.recruitingprocess.service.JobOfferService;
import com.efrota.recruitingprocess.utils.EntityDTOConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration.properties")
public class JobApplicationControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private JobApplicationService jobApplicationService;
	@Autowired
	private JobOfferService jobOfferService;
	@Autowired
	private ObjectMapper objectMapper;

	private static final String OFFER_TITLE = "offer title test";

	private static final String API_PATH = "/api/applications/";

	@Test
	public void givenApplication_whenApply_thenReturnCreated() throws Exception {

		JobOffer offer = new JobOffer();
		offer.setTitle(OFFER_TITLE);

		jobOfferService.create(offer);

		final String email = "createApp@email.com";
		final String resume = "my resume";
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail(email);
		application.setResumeText(resume);

		final String content = objectMapper.writeValueAsString(EntityDTOConverter.convertToDTO(application));

		mvc.perform(MockMvcRequestBuilders.post(API_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.jobOffer", Matchers.equalTo(OFFER_TITLE)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.candidateEmail", Matchers.equalTo(email)))
		.andExpect(MockMvcResultMatchers.jsonPath("$.resumeText", Matchers.equalTo(resume)));
	}

	@Test(expected = ServiceValidationException.class)
	public void givenApplicationExistingEmail_whenApply_thenReturnBadRequest() throws Exception {

		final String offerTitle = "offer same email";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		jobOfferService.create(offer);

		final String email = "email@email.com";
		final String resume = "my resume";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);
		application.setResumeText(resume);

		jobApplicationService.create(application, offerTitle);

		final String content = objectMapper.writeValueAsString(EntityDTOConverter.convertToDTO(application));

		mvc.perform(MockMvcRequestBuilders.post(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplicationToInvite_whenUpdate_thenChangeStatusInvited() throws Exception {

		final String offerTitle = "offer same email";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		jobOfferService.create(offer);

		final String email = "email@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, offerTitle);

		final JobApplicationStatusEnum status = JobApplicationStatusEnum.INVITED;

		JobApplicationDTO appDto = EntityDTOConverter.convertToDTO(application);
		appDto.setStatus(status);
		final String content = objectMapper.writeValueAsString(appDto);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
		.andExpect(MockMvcResultMatchers.status().isOk());

		application = jobApplicationService.findByJobOfferTitleCandidateEmail(offerTitle, email);

		Assertions.assertThat(application).isNotNull();
		Assertions.assertThat(application.getJobApplicationStatusEnum()).isEqualTo(status);
	}

	@Test
	public void givenApplicationToRejectAfterApply_whenUpdate_thenChangeStatusRejected() throws Exception {

		final String email = "rejectAfterApply@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		JobApplicationStatusEnum status = JobApplicationStatusEnum.REJECTED;
		JobApplicationDTO appDto = EntityDTOConverter.convertToDTO(application);
		appDto.setStatus(status);
		final String content = objectMapper.writeValueAsString(appDto);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		JobApplication rejectedApplication = jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE,
				email);

		Assertions.assertThat(rejectedApplication).isNotNull();
		Assertions.assertThat(rejectedApplication.getJobApplicationStatusEnum()).isEqualTo(status);
	}

	@Test
	public void givenApplicationToHireAfterInvited_whenUpdate_thenChangeStatusHired() throws Exception {

		final String email = "hireAfterInvited@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		JobApplicationDTO appDto = EntityDTOConverter.convertToDTO(application);
		appDto.setStatus(JobApplicationStatusEnum.INVITED);
		String content = objectMapper.writeValueAsString(appDto);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.HIRED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		JobApplication hiredApplication = jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE, email);

		Assertions.assertThat(hiredApplication).isNotNull();
		Assertions.assertThat(hiredApplication.getJobApplicationStatusEnum()).isEqualTo(JobApplicationStatusEnum.HIRED);
	}

	@Test
	public void givenApplicationToHireAfterHired_whenUpdate_thenBadRequest() throws Exception {

		final String email = "hireAfterHired@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.INVITED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.HIRED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplicationToRejectAfterRejected_whenUpdate_thenBadRequest() throws Exception {

		final String email = "rejectAfterRejected@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		final String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.REJECTED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplicationToInviteAfterInvited_whenUpdate_thenBadRequest() throws Exception {

		final String email = "inviteAfterInvited@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		final String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.INVITED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplicationToRejectAfterInvited_whenUpdate_thenChangeStatusRejected() throws Exception {

		final String email = "rejectAfterInvited@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.INVITED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.REJECTED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		JobApplication rejectedApplication = jobApplicationService.findByJobOfferTitleCandidateEmail(OFFER_TITLE,
				email);

		Assertions.assertThat(rejectedApplication).isNotNull();
		Assertions.assertThat(rejectedApplication.getJobApplicationStatusEnum())
				.isEqualTo(JobApplicationStatusEnum.REJECTED);
	}

	@Test
	public void givenApplicationToRejectAfterHired_whenUpdate_thenReturnBadRequest() throws Exception {

		final String email = "rejectAfterHired@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.INVITED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.HIRED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.REJECTED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplicationToHireAfterRejected_whenUpdate_thenReturnBadRequest() throws Exception {

		final String email = "hireAfterRejected@email.com";
		JobApplication application = new JobApplication();
		application.setCandidateEmail(email);

		jobApplicationService.create(application, OFFER_TITLE);

		String content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}",
				OFFER_TITLE, email, JobApplicationStatusEnum.INVITED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.REJECTED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk());

		content = String.format("{\"jobOffer\": \"%s\", \"candidateEmail\": \"%s\", \"status\": \"%s\"}", OFFER_TITLE,
				email, JobApplicationStatusEnum.HIRED);

		mvc.perform(MockMvcRequestBuilders.put(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplication_whenCreateOffer_thenReturnBadRequest() throws Exception {

		mvc.perform(MockMvcRequestBuilders.post(API_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"jobOffer\": \"\", \"candidateEmail\": \"\", \"resumeText\": \"\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenApplication_whenFindJobApplicationByJobOfferAndCandidateEmail_thenReturnOk() throws Exception {

		final String offerTitle = "offer find by offer and email";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		jobOfferService.create(offer);

		final String email = "email@email.com";
		JobApplication application = new JobApplication();
		application.setJobOffer(offer);
		application.setCandidateEmail(email);

		jobApplicationService.create(application, offerTitle);

		mvc.perform(MockMvcRequestBuilders.get(String.format("%s%s/%s", API_PATH, offerTitle, email))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.jobOffer", Matchers.equalTo(offerTitle)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.candidateEmail", Matchers.equalTo(email)));
	}

	@Test
	public void givenApplication_whenFindJobApplicationByJobOfferAndCandidateEmail_thenNotFound() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get(API_PATH + "offer/email").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void givenApplication_whenFindAllJobApplicationByJobOffer_thenReturnOk() throws Exception {

		final String offerTitle = "offer find all";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		jobOfferService.create(offer);

		final String resume = "my resume";

		JobApplication application = new JobApplication();
		application.setCandidateEmail("a@email.com");
		application.setResumeText(resume);
		jobApplicationService.create(application, offerTitle);

		application = new JobApplication();
		application.setCandidateEmail("b@email.com");
		application.setResumeText(resume);
		jobApplicationService.create(application, offerTitle);

		mvc.perform(
				MockMvcRequestBuilders.get(API_PATH + offerTitle).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
	}

	@Test
	public void givenApplication_whenFindAllJobApplicationByJobOffer_thenReturnNoContent() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get(API_PATH + "something").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
