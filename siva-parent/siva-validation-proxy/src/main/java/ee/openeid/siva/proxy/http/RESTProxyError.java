package ee.openeid.siva.proxy.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RESTProxyError {
    private HttpStatus httpStatus;
    private String key;
    private String message;
}
