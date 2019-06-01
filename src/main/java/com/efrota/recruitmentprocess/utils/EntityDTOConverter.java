package com.efrota.recruitmentprocess.utils;

import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.dto.JobApplicationDTO;
import com.efrota.recruitmentprocess.model.dto.JobOfferDTO;

/**
 * Non-implementable utility class to convert entity-DTO and DTO-entity.
 * 
 * @author edmundofrota
 *
 */
public class EntityDTOConverter {

	private EntityDTOConverter() {
		// empty
	}

	/**
	 * Convert application DTO-entity.
	 * 
	 * @param dto
	 *            {@link JobApplicationDTO}
	 * @return {@link JobApplication}
	 */
	public static JobApplication convertToEntity(JobApplicationDTO dto) {
		if (dto == null) {
			return null;
		}

		JobApplication entity = new JobApplication();

		entity.setCandidateEmail(dto.getCandidateEmail());
		entity.setResumeText(dto.getResumeText());
		if (dto.getStatus() != null) {
			entity.setJobApplicationStatusEnum(dto.getStatus());
		}

		return entity;
	}

	/**
	 * Convert application entity-DTO.
	 * 
	 * @param entity
	 *            {@link JobApplication}
	 * @return {@link JobApplicationDTO}
	 */
	public static JobApplicationDTO convertToDTO(JobApplication entity) {
		if (entity == null) {
			return null;
		}

		JobApplicationDTO dto = new JobApplicationDTO();

		dto.setCandidateEmail(entity.getCandidateEmail());
		dto.setJobOffer(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null);
		dto.setResumeText(entity.getResumeText());
		dto.setStatus(entity.getJobApplicationStatusEnum());

		return dto;
	}

	/**
	 * Convert offer DTO-entity.
	 * 
	 * @param dto
	 *            {@link JobOfferDTO}
	 * @return {@link JobOffer}
	 */
	public static JobOffer convertToEntity(JobOfferDTO dto) {
		if (dto == null) {
			return null;
		}

		JobOffer entity = new JobOffer();

		entity.setTitle(dto.getJobTitle());
		entity.setStartDate(dto.getStartDate());

		return entity;
	}

	/**
	 * Convert offer DTO-entity.
	 * 
	 * @param dto
	 *            {@link JobOffer}
	 * @return {@link JobOfferDTO}
	 */
	public static JobOfferDTO convertToDTO(JobOffer entity) {
		if (entity == null) {
			return null;
		}

		JobOfferDTO dto = new JobOfferDTO();

		dto.setJobTitle(entity.getTitle());
		dto.setStartDate(entity.getStartDate());
		dto.setNumberApplication(entity.getApplicationAmount());

		return dto;
	}
}
