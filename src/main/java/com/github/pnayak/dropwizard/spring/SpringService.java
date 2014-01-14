package com.github.pnayak.dropwizard.spring;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.dropwizard.Application;
import io.dropwizard.Configuration;


public abstract class SpringService<T extends Configuration> extends Application<T> {

	protected AnnotationConfigApplicationContext appContext;

	private String serviceName;

	protected SpringService(String name) {
		super();
		this.serviceName = name;
		this.appContext = createSpringApplicationContext();
	}

	protected AnnotationConfigApplicationContext createSpringApplicationContext() {
		return new AnnotationConfigApplicationContext();
	}

    @Override
    public String getName() {
        return serviceName;
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yammer.dropwizard.Service#initialize(com.yammer.dropwizard.config
	 * .Bootstrap)
	 */
	@Override
	public void initialize(Bootstrap<T> bootstrap) {
		//bootstrap.setName(serviceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yammer.dropwizard.Service#run(com.yammer.dropwizard.config.Configuration
	 * , com.yammer.dropwizard.config.Environment)
	 */
	@Override
	public void run(T configuration, Environment environment) throws Exception {
		runWithAppContext(configuration, environment, appContext);
	}

	/**
	 * @param configuration
	 * @param environment
	 * @param appContext
	 * @throws Exception
	 */
	protected abstract void runWithAppContext(T configuration,
			Environment environment,
			AnnotationConfigApplicationContext appContext) throws Exception;

}
