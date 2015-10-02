package ee.sk.pdf.validator.monitoring;

import ee.sk.pdf.validator.monitoring.status.StatusRepository;
import ee.sk.pdf.validator.monitoring.status.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MonitoringApplication {

    @Autowired
    private StatusRepository statusRepository;

    @RequestMapping("/")
    @ResponseBody
    private StatusResponse index() {
        return statusRepository.getSystemStatus();
    }

    public static void main(String... args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

}