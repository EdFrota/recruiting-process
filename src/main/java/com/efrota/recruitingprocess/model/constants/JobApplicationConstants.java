package com.efrota.recruitingprocess.model.constants;

import com.efrota.recruitingprocess.model.JobApplication;

/**
 * Non-implementable class to store constants related to {@link JobApplication}.
 * 
 * @author edmundofrota
 *
 */
public class JobApplicationConstants {

	private JobApplicationConstants() {
		// empty
	}

	/**
	 * Field email max length.
	 */
	public static final int CANDIDATE_EMAIL_LENGTH = 60;
	/**
	 * Field resume max length.
	 */
	public static final int CANDIDATE_RESUME_LENGTH = 300;
	/**
	 * Field status max length.
	 */
	public static final int CANDIDATE_STATUS_LENGTH = 30;

}
