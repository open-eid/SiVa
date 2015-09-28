package ee.sk.pdf.validator.monitoring.status;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "status")
public class StatusRepository {
    private StatusResponse statusResponse;

    @Cacheable
    public StatusResponse getSystemStatus() {
        return statusResponse;
    }

    @CacheEvict(cacheNames = "status", allEntries = true)
    public void setStatusResponse(StatusResponse statusResponse) {
        this.statusResponse = statusResponse;
    }
}