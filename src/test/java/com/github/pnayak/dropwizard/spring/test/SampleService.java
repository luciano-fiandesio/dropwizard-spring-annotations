package com.github.pnayak.dropwizard.spring.test;

import com.github.pnayak.dropwizard.spring.AutoWiredService;

public class SampleService extends AutoWiredService<SampleServiceConfiguration> {

    public SampleService() {
        super("sample-service", "com.github.pnayak.dropwizard.spring.test");
    }
}
