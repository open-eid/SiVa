package ee.sk.pdf.validator.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class MonitoringApplication {
    private  String message = "Hello World";

    @RequestMapping("/")
    @ResponseBody
    private String index() {
        return message;
    }

    public static void main(String... args) {
        SpringApplication.run(MonitoringApplication.class);
    }
}
