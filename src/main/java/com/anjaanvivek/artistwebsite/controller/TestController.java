package com.anjaanvivek.artistwebsite.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	    @GetMapping("/hello")
	    public String hello() {
	        return "Hello, Spring Boot is working!";
	    }

	    @GetMapping("/ping")
	    public String ping() {
	        return "Pong!";
	    }
}