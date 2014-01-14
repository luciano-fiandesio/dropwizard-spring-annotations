package com.github.pnayak.dropwizard.spring.test.tasks;

import java.io.PrintWriter;

import io.dropwizard.servlets.tasks.Task;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMultimap;

@Component
public class MyTask extends Task {

    public MyTask(String name) {
        super("my-task");
    }

    @Override
	public void execute(ImmutableMultimap<String, String> parameters,
			PrintWriter output) throws Exception {

		output.println("my task complete.");
	}

}
