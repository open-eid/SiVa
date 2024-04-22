package ee.openeid.validation.service.timemark.util;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.Warning;
import lombok.experimental.UtilityClass;
import org.digidoc4j.exceptions.DigiDoc4JException;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static ee.openeid.siva.validation.document.report.builder.ReportBuilderUtils.emptyWhenNull;

@UtilityClass
public class ValidationErrorMapper {
  public static List<Warning> getWarnings(Stream<List<DigiDoc4JException>> warnings) {
    return mapExceptions(warnings, ValidationErrorMapper::mapDigidoc4JWarning);
  }

  public static List<Error> getErrors(Stream<List<DigiDoc4JException>> errors) {
    return mapExceptions(errors, ValidationErrorMapper::mapDigidoc4JException);
  }

  private static <T> List<T> mapExceptions(Stream<List<DigiDoc4JException>> exceptions,
                                           Function<DigiDoc4JException, T> mapper) {
    return exceptions.flatMap(Collection::stream)
      .distinct()
      .map(mapper)
      .toList();
  }

  private static Warning mapDigidoc4JWarning(DigiDoc4JException digiDoc4JException) {
    Warning warning = new Warning();
    warning.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
    return warning;
  }

  private static Error mapDigidoc4JException(DigiDoc4JException digiDoc4JException) {
    Error error = new Error();
    error.setContent(emptyWhenNull(digiDoc4JException.getMessage()));
    return error;
  }
}
