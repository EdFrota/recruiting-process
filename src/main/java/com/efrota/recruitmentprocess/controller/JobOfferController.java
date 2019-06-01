package com.efrota.recruitmentprocess.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.efrota.recruitmentprocess.exception.NotFoundException;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.dto.JobOfferDTO;
import com.efrota.recruitmentprocess.service.JobOfferService;
import com.efrota.recruitmentprocess.utils.EntityDTOConverter;

/**
 * API {@link RestController} containing endpoints to handle {@link JobOffer}.
 * 
 * @author edmundofrota
 *
 */
@RestController
@RequestMapping("/api/offers")
public class JobOfferController {

	private Log log = LogFactory.getLog(JobOfferController.class);

	@Autowired
	private JobOfferService jobOfferService;

	private static final String HEADER_ACCEPT = "Accept=application/json";
	private static final String HEADER_CONTENT_TYPE = "Content-Type=application/json";

	/**
	 * Create a {@link JobOffer}.
	 * 
	 * @param jobOfferDTO
	 *            DTO containing the data to be stored.
	 * @return the created offer in {@link JobOfferDTO}
	 */
	@PostMapping(path = "/", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<JobOfferDTO> createOffer(@Valid @RequestBody JobOfferDTO jobOfferDTO) {

		JobOffer offer = jobOfferService.create(EntityDTOConverter.convertToEntity(jobOfferDTO));

		return new ResponseEntity<>(EntityDTOConverter.convertToDTO(offer), HttpStatus.CREATED);
	}

	/**
	 * Find a {@linkJobOffer} based on the title.
	 * 
	 * @param offerTitle
	 *            unique title.
	 * @return {@link JobOfferDTO} filtered by the title.
	 */
	@GetMapping(path = "/{offerTitle}", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<JobOfferDTO> findSingleJobOffer(@PathVariable String offerTitle) {

		JobOffer offer = jobOfferService.findByTitle(offerTitle);

		if (offer == null) {
			String message = String.format("Offer %s could not be found.", offerTitle);
			log.warn(message);
			throw new NotFoundException(message);
		}

		return new ResponseEntity<>(EntityDTOConverter.convertToDTO(offer), HttpStatus.OK);
	}

	/**
	 * Find all {@link JobOffer}.
	 * 
	 * @return List of offers in {@link JobOfferDTO}
	 */
	@GetMapping(path = "/", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<List<JobOfferDTO>> findAllJobOffers() {

		List<JobOffer> jobOffers = jobOfferService.findAll();

		List<JobOfferDTO> jobOfferDTOs = jobOffers.stream()
				.map(offer -> EntityDTOConverter.convertToDTO(offer)).collect(Collectors.toList());

		return new ResponseEntity<>(jobOfferDTOs, HttpStatus.OK);
	}
}
