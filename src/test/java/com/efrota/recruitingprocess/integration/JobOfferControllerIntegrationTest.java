package com.efrota.recruitingprocess.integration;

import com.efrota.recruitingprocess.model.JobOffer;
import com.efrota.recruitingprocess.service.JobOfferService;

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
public class JobOfferControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JobOfferService service;

	@Test
	public void givenOffer_whenCreate_thenReturnCreated() throws Exception {
		String offerTitle = "some offer title";
		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		mvc.perform(MockMvcRequestBuilders.post("/api/offers/")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.format("{\"jobTitle\": \"%s\"}", offerTitle)))
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle", Matchers.equalTo(offerTitle)));
	}

	@Test
	public void givenOffer_whenCreateOffer_thenReturnBadRequest() throws Exception {

		mvc.perform(MockMvcRequestBuilders.post("/api/offers/")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"jobTitle\": \"\"}"))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	private static final String OFFER_TITLE = "find single offer";

	@Test
	public void givenOffer_whenFindSingleJobOffer_thenReturnOk() throws Exception {

		JobOffer offer = new JobOffer();
		offer.setTitle(OFFER_TITLE);

		service.create(offer);

		mvc.perform(MockMvcRequestBuilders.get("/api/offers/" + OFFER_TITLE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle", Matchers.equalTo(OFFER_TITLE)));
	}

	@Test
	public void givenOffer_whenFindSingleJobOffer_thenNotFound() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get("/api/offers/something")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void givenOffer_whenFindAllJobOffers_thenReturnOk() throws Exception {
		JobOffer offer = new JobOffer();
		offer.setTitle("offer test 2");
		service.create(offer);

		offer = new JobOffer();
		offer.setTitle("offer test 3");
		service.create(offer);

		mvc.perform(MockMvcRequestBuilders.get("/api/offers/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(service.findAll().size())));
	}

}
