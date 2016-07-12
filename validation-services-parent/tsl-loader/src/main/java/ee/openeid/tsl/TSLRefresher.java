package ee.openeid.tsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TSLRefresher implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TSLRefresher.class);

    private TSLLoader loader;

    @Override
    public void run() {
        LOGGER.info("Started TSL refresh process...");
        loader.loadTSL();
        LOGGER.info("Finished TSL refresh process...");
    }

    @Autowired
    public void setLoader(TSLLoader loader) {
        this.loader = loader;
    }
}
