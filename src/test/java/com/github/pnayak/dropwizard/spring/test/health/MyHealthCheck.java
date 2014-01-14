package com.github.pnayak.dropwizard.spring.test.health;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pnayak.dropwizard.spring.test.service.MyService;

@Component
public class MyHealthCheck extends HealthCheck {
	
	@Autowired
	private MyService myService;


	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}
	
	public MyService getMyService() {
		return myService;
	}

}
