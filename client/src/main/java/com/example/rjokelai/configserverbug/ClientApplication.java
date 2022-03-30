package com.example.rjokelai.configserverbug;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClientTestConfigurationProperties.class)
public class ClientApplication {

	/**
	 * Launch with --spring.profiles.active=remote to load from configserver
	 * Launch with --spring.profiles.active=local to load from local env
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Autowired
	public void logConfigProps(ClientTestConfigurationProperties props) throws JsonProcessingException {
		System.out.println("=============");
		System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(props));
		System.out.println("=============");
	}

}
