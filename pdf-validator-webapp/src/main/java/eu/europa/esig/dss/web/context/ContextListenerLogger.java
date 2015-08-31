package eu.europa.esig.dss.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextListenerLogger implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContextListenerLogger.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String profile = System.getProperty("spring.profiles.active");
        if ("development".equals(profile)) {
            LOG.warn(" =========================================================================");
            LOG.warn("||                                                                       ||");
            LOG.warn("||                               WARNING!                                ||");
            LOG.warn("||                                                                       ||");
            LOG.warn("||      You are running the application with development profile!        ||");
            LOG.warn("||                                                                       ||");
            LOG.warn("||                                                                       ||");
            LOG.warn(" =========================================================================");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
