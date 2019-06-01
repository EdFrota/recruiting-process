package com.efrota.recruitmentprocess.unit;

import com.efrota.recruitmentprocess.enums.JobApplicationStatusEnum;
import com.efrota.recruitmentprocess.model.JobApplication;
import com.efrota.recruitmentprocess.model.JobOffer;
import com.efrota.recruitmentprocess.model.dto.JobApplicationDTO;
import com.efrota.recruitmentprocess.model.dto.JobOfferDTO;
import com.efrota.recruitmentprocess.utils.EntityDTOConverter;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
public class EntityDTOConverterUnitTest {

	@Test
	public void jobApplicationConvertToDTO() {
		String email = "email@email.com";
		String resume = "resume";
		JobApplicationStatusEnum status = JobApplicationStatusEnum.HIRED;
		String offerTitle = "offer";

		JobOffer offer = new JobOffer();
		offer.setTitle(offerTitle);

		JobApplication entity = new JobApplication();

		entity.setJobOffer(offer);
		entity.setCandidateEmail(email);
		entity.setResumeText(resume);
		entity.setJobApplicationStatusEnum(status);

		JobApplicationDTO dto = EntityDTOConverter.convertToDTO(entity);

		Assertions.assertThat(dto).isNotNull();
		Assertions.assertThat(dto.getJobOffer()).isEqualTo(offerTitle);
		Assertions.assertThat(dto.getCandidateEmail()).isEqualTo(email);
		Assertions.assertThat(dto.getResumeText()).isEqualTo(resume);
		Assertions.assertThat(dto.getStatus()).isEqualTo(status);
	}

	@Test
	public void jobApplicationConvertToEntity() {
		String email = "email@email.com";
		String resume = "resume";
		JobApplicationStatusEnum status = JobApplicationStatusEnum.HIRED;
		String offerTitle = "offer";

		JobApplicationDTO dto = new JobApplicationDTO();

		dto.setJobOffer(offerTitle);
		dto.setCandidateEmail(email);
		dto.setResumeText(resume);
		dto.setStatus(status);

		JobApplication entity = EntityDTOConverter.convertToEntity(dto);

		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getJobOffer()).isNull();
		Assertions.assertThat(entity.getCandidateEmail()).isEqualTo(email);
		Assertions.assertThat(entity.getResumeText()).isEqualTo(resume);
		Assertions.assertThat(entity.getJobApplicationStatusEnum()).isEqualTo(status);
	}

	@Test
	public void jobOfferConvertToDTO() {
		String offerTitle = "offer";
		Date startDate = DateUtil.now();

		JobOffer entity = new JobOffer();

		entity.setTitle(offerTitle);
		entity.setStartDate(startDate);

		JobOfferDTO dto = EntityDTOConverter.convertToDTO(entity);

		Assertions.assertThat(dto).isNotNull();
		Assertions.assertThat(dto.getJobTitle()).isEqualTo(offerTitle);
		Assertions.assertThat(dto.getStartDate()).isEqualTo(startDate);
	}

	@Test
	public void jobOfferConvertToEntity() {
		String offerTitle = "offer";
		Date startDate = DateUtil.now();

		JobOfferDTO dto = new JobOfferDTO();

		dto.setJobTitle(offerTitle);
		dto.setStartDate(startDate);

		JobOffer entity = EntityDTOConverter.convertToEntity(dto);

		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStartDate()).isEqualTo(startDate);
		Assertions.assertThat(entity.getStartDate()).isEqualTo(startDate);
	}

}
