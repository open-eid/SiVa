package ee.openeid.siva.webapp.request;

import org.hibernate.validator.constraints.NotEmpty;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import lombok.Data;

/**
 * Created by Andrei on 14.03.2018.
 */

@Data
public class TimeStampTokenContent {

  @NotEmpty
  private String fileName;

  @ValidBase64String
  private String base64Value;
}
