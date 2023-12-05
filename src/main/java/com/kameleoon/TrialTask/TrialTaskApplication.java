package com.kameleoon.TrialTask;

import com.kameleoon.TrialTask.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class TrialTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrialTaskApplication.class, args);
	}

}
