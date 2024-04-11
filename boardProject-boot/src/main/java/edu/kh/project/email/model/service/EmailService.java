package edu.kh.project.email.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;


public interface EmailService {

	String sendEamil(String string, String email);

	
	
	
}
