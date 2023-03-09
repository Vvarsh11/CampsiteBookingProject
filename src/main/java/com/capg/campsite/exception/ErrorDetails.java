package com.capg.campsite.exception;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorDetails {
	private static final Logger logger =  LoggerFactory.getLogger(ErrorDetails.class);

	private String message;


	public ErrorDetails( String message) {
		super();
		logger.info("ErrorDetails class");

		this.message = message;

	}



	public String getMessage() {
		logger.info(message);
		return message;
	}


}

