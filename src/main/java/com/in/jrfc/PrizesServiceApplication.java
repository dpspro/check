package com.in.jrfc;

import com.in.jrfc.configuration.AsynchronousConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
public class PrizesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrizesServiceApplication.class, args);
	}

}
