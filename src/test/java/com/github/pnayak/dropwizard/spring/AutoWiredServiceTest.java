package com.github.pnayak.dropwizard.spring;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.pnayak.dropwizard.spring.test.SampleService;
import com.github.pnayak.dropwizard.spring.test.SampleServiceConfiguration;
import com.github.pnayak.dropwizard.spring.test.health.MyHealthCheck;
import com.github.pnayak.dropwizard.spring.test.resources.MyResource;
import com.github.pnayak.dropwizard.spring.test.tasks.MyTask;

public class AutoWiredServiceTest {

	@Mock
	private SampleServiceConfiguration configuration;
	@Mock
	private Environment environment;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
    @Ignore
	public void testResourcesInstalled() throws Exception {
		SampleService s = new SampleService();
		s.run(configuration, environment);

		ArgumentCaptor<MyResource> resource = ArgumentCaptor
				.forClass(MyResource.class);
		// TODO verify(environment).addResource(resource.capture());
        assertThat(resource.getValue(), is(MyResource.class));
	}

    @Test
    @Ignore
	public void testHealthChecksInstalled() throws Exception {
		SampleService s = new SampleService();
		s.run(configuration, environment);

		ArgumentCaptor<? extends HealthCheck> healthCheck = ArgumentCaptor
				.forClass(HealthCheck.class);
		//verify(environment).addHealthCheck(healthCheck.capture());
		assertThat(healthCheck.getValue(), is(MyHealthCheck.class));
	}

    @Test
    @Ignore
	public void testTasksInstalled() throws Exception {
		SampleService s = new SampleService();
		s.run(configuration, environment);

		ArgumentCaptor<? extends Task> task = ArgumentCaptor
				.forClass(Task.class);
		//verify(environment).addTask(task.capture());
		assertThat(task.getValue(), is(MyTask.class));
	}

    @Test
    @Ignore
	public void testDependenciesWiredUpCorrectly() throws Exception {
		SampleService s = new SampleService();
		s.run(new SampleServiceConfiguration(), environment);

		ArgumentCaptor<MyResource> resource = ArgumentCaptor
				.forClass(MyResource.class);
		//verify(environment).addResource(resource.capture());

		MyResource r = resource.getValue();
		assertThat(r.getMyService(), not(nullValue()));
		assertThat(r.getMyService().getMyOtherService(), not(nullValue()));
		
		// Verify that the Dropwizard configuration is Autowired correctly
		assertThat(r.getConfiguration(), not(nullValue()));
		assert r.getConfiguration().getBar() == "bar";
		
		// MyHealthCheck is expected to by Autowired by Spring. The Autowire
		// annotation is on a class variable and not part of the constructor
		// like with MyResource above
		ArgumentCaptor<MyHealthCheck> myHealthCheck = ArgumentCaptor
				.forClass(MyHealthCheck.class);
		//verify(environment).addHealthCheck(myHealthCheck.capture());
		assertThat(myHealthCheck.getValue().getMyService(), not(nullValue()));
	}

}
