package com.anjaanvivek.artistwebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ArtistWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtistWebsiteApplication.class, args);
	}
	
}