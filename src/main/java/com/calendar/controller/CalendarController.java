package com.calendar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {
	
	@GetMapping("/test")
	public boolean test(){
		System.out.println("Test");
		return true;
	}
}
