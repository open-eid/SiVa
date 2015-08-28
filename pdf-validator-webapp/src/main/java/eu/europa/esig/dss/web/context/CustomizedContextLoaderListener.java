package eu.europa.esig.dss.web.context;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomizedContextLoaderListener extends ContextLoaderListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomizedContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	String profile = System.getProperty("spring.profiles.active");
        if (profile != null && profile.equals("development")) {
            LOG.warn(" =========================================================================");
            LOG.warn("||                                                                       ||");
            LOG.warn("||                               WARNING!                                ||");
            LOG.warn("||                                                                       ||");
            LOG.warn("||      You are running the application with development profile!        ||");
            LOG.warn("||                                                                       ||");
            LOG.warn("||                                                                       ||");
            LOG.warn(" =========================================================================");
        }
        super.contextInitialized(sce);
    }

}
