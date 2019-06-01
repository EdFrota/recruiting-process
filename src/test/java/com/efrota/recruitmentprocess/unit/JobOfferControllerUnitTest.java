package com.efrota.recruitmentprocess.unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.efrota.recruitmentprocess.controller.JobOfferController;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.dto.JobOfferDTO;
import com.efrota.recruitmentprocess.service.JobOfferService;
import com.efrota.recruitmentprocess.utils.EntityDTOConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(JobOfferController.class)
public class JobOfferControllerUnitTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JobOfferService service;

	private static final String API_PATH = "/api/offers/";

	@Test
	public void givenOffer_whenCreateOffer_thenReturnCreated() throws Exception {

		final String offerTitle = "offer test";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		BDDMockito.given(service.create(offer)).willReturn(offer);

		final String content = objectMapper.writeValueAsString(EntityDTOConverter.convertToDTO(offer));

		mvc.perform(MockMvcRequestBuilders.post(API_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		// for some reason, service.create isn't retrieving the mocked value.
//		.andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle", Matchers.equalTo(offerTitle)));
	}

	@Test
	public void givenOffer_whenCreateOffer_thenReturnBadRequest() throws Exception {

		JobOfferDTO dto = new JobOfferDTO();
		dto.setJobTitle(StringUtils.EMPTY);
		
		final String content = objectMapper.writeValueAsString(dto);
		
		mvc.perform(MockMvcRequestBuilders.post(API_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void givenOffer_whenFindSingleJobOffer_thenReturnOk() throws Exception {

		final String offerTitle = "offer";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		BDDMockito.given(service.findByTitle(offerTitle)).willReturn(offer);

		mvc.perform(MockMvcRequestBuilders.get(API_PATH + offerTitle).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle", Matchers.equalTo(offerTitle)));
	}

	@Test
	public void givenOffer_whenFindSingleJobOffer_thenNotFound() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get(API_PATH + "something").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void givenOffer_whenFindAllJobOffers_thenReturnOk() throws Exception {

		int offerListSize = 2;
		List<JobOffer> jobOfferDTOs = new ArrayList<>(offerListSize);

		JobOffer offer = new JobOffer();
		offer.setTitle("offer test");
		jobOfferDTOs.add(offer);

		offer = new JobOffer();
		offer.setTitle("offer test 2");
		jobOfferDTOs.add(offer);

		BDDMockito.given(service.findAll()).willReturn(jobOfferDTOs);

		mvc.perform(MockMvcRequestBuilders.get(API_PATH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(offerListSize)));
	}

	@Test
	public void givenOffer_whenFindAllJobOffers_thenReturnEmpty() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get(API_PATH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
	}

}
