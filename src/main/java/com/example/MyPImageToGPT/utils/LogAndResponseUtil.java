package com.example.MyPImageToGPT.utils;


import com.example.MyPImageToGPT.responses.ResponseHandler;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class LogAndResponseUtil
{

	public ResponseEntity<Object> generate(Logger logger, String methodName, String errorMessage, HttpStatus status, Object responseObject) {
		logger.info(methodName + errorMessage);
		return ResponseHandler.generateResponse(errorMessage, status, responseObject);
	}


}
