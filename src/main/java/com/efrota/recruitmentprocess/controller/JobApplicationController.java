package com.efrota.recruitmentprocess.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.efrota.recruitmentprocess.controller.handler.ResponseDetails;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.dto.JobApplicationDTO;
import com.efrota.recruitmentprocess.service.JobApplicationService;
import com.efrota.recruitmentprocess.utils.EntityDTOConverter;

/**
 * API {@link RestController} containing endpoints to handle
 * {@link JobApplication}.
 * 
 * @author edmundofrota
 *
 */
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

	private static final String HEADER_ACCEPT = "Accept=application/json";
	private static final String HEADER_CONTENT_TYPE = "Content-Type=application/json";

	@Autowired
	private JobApplicationService jobApplicationService;

	/**
	 * Create a {@link JobApplication} related to a {@link JobOffer}.
	 * 
	 * @param jobApplicationDTO
	 *            DTO containing the data to be stored.
	 * @return the created application in {@link JobApplicationDTO}
	 */
	@PostMapping(path = "/", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<JobApplicationDTO> apply(
			@Valid @RequestBody JobApplicationDTO jobApplicationDTO) {

		JobApplication application = jobApplicationService
				.create(EntityDTOConverter.convertToEntity(jobApplicationDTO), jobApplicationDTO.getJobOffer());

		return new ResponseEntity<>(EntityDTOConverter.convertToDTO(application), HttpStatus.CREATED);
	}

	/**
	 * Find a {@link JobApplication} based on offer title and candidate email.
	 * 
	 * @param offerTitle
	 *            {@link JobOffer} title
	 * @param candidateEmail
	 *            application email
	 * @return {@link JobApplicationDTO} filtered by offer title and email.
	 */
	@GetMapping(path = "/{offerTitle}/{candidateEmail}", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<JobApplicationDTO> findJobApplicationByJobOfferAndCandidateEmail(
			@PathVariable String offerTitle, @PathVariable String candidateEmail) {

		JobApplication application = jobApplicationService
				.findByJobOfferTitleCandidateEmail(offerTitle, candidateEmail);

		ResponseEntity<JobApplicationDTO> responseEntity;

		if (application != null) {
			responseEntity = new ResponseEntity<>(EntityDTOConverter.convertToDTO(application), HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return responseEntity;
	}

	/**
	 * Find a list of {@link JobApplication} based on offer title.
	 * 
	 * @param offerTitle
	 *            {@link JobOffer} title.
	 * @return {@link JobApplicationDTO} filtered by offer title.
	 */
	@GetMapping(path = "/{offerTitle}", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<List<JobApplicationDTO>> findAllJobApplicationByJobOffer(
			@PathVariable String offerTitle) {

		List<JobApplication> jobApplications = jobApplicationService.findByJobOfferTitle(offerTitle);

		ResponseEntity<List<JobApplicationDTO>> responseEntity;

		if (!jobApplications.isEmpty()) {
			responseEntity = new ResponseEntity<>(jobApplications.stream()
					.map(appl -> EntityDTOConverter.convertToDTO(appl)).collect(Collectors.toList()), HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return responseEntity;
	}

	/**
	 * Update a {@link JobApplication} based on offer title and application
	 * email.
	 * 
	 * @param jobApplicationDTO
	 *            {@link JobApplicationDTO} containing the data to be updated.
	 * @return message of the update result.
	 */
	@PutMapping(path = "/", headers = { HEADER_ACCEPT, HEADER_CONTENT_TYPE })
	public @ResponseBody ResponseEntity<ResponseDetails> updateCandidateStatus(
			@Valid @RequestBody JobApplicationDTO jobApplicationDTO, WebRequest request) {

		jobApplicationService.update(jobApplicationDTO.getStatus(), jobApplicationDTO.getJobOffer(),
				jobApplicationDTO.getCandidateEmail());

		String message = String.format("Candidate %s changed status to %s for the offer %s.",
				jobApplicationDTO.getJobOffer(), jobApplicationDTO.getStatus(), jobApplicationDTO.getCandidateEmail());

		return new ResponseEntity<>(new ResponseDetails(message, request.getDescription(false)), HttpStatus.OK);
	}

}
