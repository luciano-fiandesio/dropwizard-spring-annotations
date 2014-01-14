package com.github.pnayak.dropwizard.spring;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.ws.rs.Path;
import java.util.Set;

/**
 * Service which automatically adds items to the service environment, including
 * health checks, resources using Spring @Configuration and @Component annotated
 * classes
 *
 * @author pnayak
 */
public abstract class AutoWiredService<T extends Configuration> extends
        SpringService<T> {

    private static final Logger LOG = LoggerFactory
            .getLogger(AutoWiredService.class);

    private Reflections reflections;

    protected AutoWiredService(String name, String... basePackages) {

        super(name);

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        FilterBuilder filterBuilder = new FilterBuilder();
        for (String basePkg : basePackages) {
            configBuilder.addUrls(ClasspathHelper.forPackage(basePkg));
            filterBuilder.include(FilterBuilder.prefix(basePkg));
        }

        configBuilder.filterInputsBy(filterBuilder).setScanners(
                new SubTypesScanner(), new TypeAnnotationsScanner());
        this.reflections = new Reflections(configBuilder);

        this.appContext.scan(basePackages); // let Spring scan for
        // @Configuration classes
        // this.appContext.refresh();
    }

    protected AutoWiredService(String basePackage) {
        this(null, basePackage);
    }

    protected AutoWiredService() {
        super(null);
        this.reflections = new Reflections(getClass().getPackage().getName(),
                new SubTypesScanner(), new TypeAnnotationsScanner());
    }

    @Override
    protected void runWithAppContext(T configuration, Environment environment,
                                     AnnotationConfigApplicationContext appContext) throws Exception {

        // Make the Dropwizard configuration available for @Autowired
        this.appContext.getBeanFactory().registerSingleton("Configuration",
                configuration);
        this.appContext.refresh();

        addResources(environment, appContext);
        addHealthChecks(environment, appContext);
        addTasks(environment, appContext);
        addManaged(environment, appContext);
        addProviders(environment, appContext);
    }

    private void addResources(Environment environment,
                              AnnotationConfigApplicationContext appContext) {
        Set<Class<?>> resourceClasses = reflections
                .getTypesAnnotatedWith(Path.class);
        for (Class<?> resource : resourceClasses) {
            if (!resource.getName().equals("com.edappify.resources.websocket.WebSocketResource")) { // UH?
                environment.jersey().register(appContext.getBean(resource));
                LOG.info("Added resource class: " + resource);
            } else {
                LOG.info("EXCLUDED resource class: " + resource);
            }
        }
    }

    private void addManaged(Environment environment,
                            AnnotationConfigApplicationContext appContext) {
        Set<Class<? extends Managed>> managedClasses = reflections
                .getSubTypesOf(Managed.class);
        for (Class<? extends Managed> managed : managedClasses) {

            // TODO environment.manage(appContext.getBean(managed));
            environment.lifecycle().manage(appContext.getBean(managed));
            LOG.info("Added managed: " + managed);
        }
    }

    private void addTasks(Environment environment,
                          AnnotationConfigApplicationContext appContext) {
        Set<Class<? extends Task>> taskClasses = reflections
                .getSubTypesOf(Task.class);
        for (Class<? extends Task> task : taskClasses) {
            // environment.addTask(appContext.getBean(task));
            environment.admin().addTask(appContext.getBean(task));
            LOG.info("Added task: " + task);
        }
    }

    private void addProviders(Environment environment,
                              AnnotationConfigApplicationContext appContext) {
        Set<Class<?>> providerClasses = reflections
                .getTypesAnnotatedWith(DWProvider.class);
        // LOG.info("Found providers: " + providerClasses);
        for (Class<?> provider : providerClasses) {
            // TODO environment.addProvider(appContext.getBean(provider));
            environment.jersey().register(appContext.getBean(provider));
            LOG.info("Added provider: " + provider);
        }
    }

    private void addHealthChecks(Environment environment,
                                 AnnotationConfigApplicationContext appContext) {
        Set<Class<? extends HealthCheck>> healthCheckClasses = reflections
                .getSubTypesOf(HealthCheck.class);
        for (Class<? extends HealthCheck> healthCheck : healthCheckClasses) {
            //environment.addHealthCheck(appContext.getBean(healthCheck));
            environment.healthChecks().register(appContext.getBean(healthCheck).getClass().getName()
                    , appContext.getBean(healthCheck));
            LOG.info("Added healthCheck: " + healthCheck);
        }
    }
}
