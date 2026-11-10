/*
 * Copyright 2026 Riigi Infosüsteemi Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.statistics.ContainerTypeResolver;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.tsl.TSLLoader;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_TSL_NAME;
import static ee.openeid.validation.service.timemark.TimemarkValidationConstants.TM_TSL_NAME;

// The stages of a validation request as Micrometer observations: a timer per stage with the low-cardinality keys
// as labels, and one log line per stage with every key (siva-monitoring's LoggingObservationHandler), so a request's
// record in the log says which validator ran against which trust-list state. Values a client chooses or that are
// unbounded (policy, counts, the snapshot id) are high-cardinality keys: log only, never a label. Nothing here
// names the file or the signer.
@Component
@RequiredArgsConstructor
public class ValidationObservations {

    public static final String VALIDATION = "siva.validation";
    public static final String VALIDATION_SERVICE = "siva.validation.service";
    static final String NONE = "none";

    private final @NonNull ObservationRegistry observationRegistry;
    // Every trust-list loader; the service stage reports the one its validator reads.
    private final @NonNull Set<TSLLoader> tslLoaders;

    // reportType is the type the proxy produces, which is not always the one the request asks for.
    SimpleReport validation(ProxyRequest proxyRequest, @NonNull ReportType reportType,
                            Supplier<SimpleReport> validation) {
        Observation observation = Observation.createNotStarted(VALIDATION, observationRegistry)
                .lowCardinalityKeyValue("request", proxyRequest instanceof ProxyDocument ? "container" : "hashcode")
                .lowCardinalityKeyValue("report.type", reportType.name())
                .highCardinalityKeyValue("policy", valueOr(proxyRequest.getSignaturePolicy(), "default"));
        return observation.observe(() -> {
            SimpleReport report = validation.get();
            describe(observation, report);
            return report;
        });
    }

    Reports service(String validatorName, Supplier<Reports> validation) {
        String validator = StringUtils.removeEnd(validatorName, ValidationProxy.SERVICE_BEAN_NAME_POSTFIX);
        Observation observation = Observation.createNotStarted(VALIDATION_SERVICE, observationRegistry)
                .lowCardinalityKeyValue("validator", validator);
        loaderFor(validator).ifPresent(loader -> {
            TSLLoader.State state = loader.getState();
            observation.lowCardinalityKeyValue("tsl.loader", loader.getTslName())
                    .highCardinalityKeyValue("tsl.snapshot", valueOr(state.snapshotId(), NONE))
                    .highCardinalityKeyValue("tsl.anchors", String.valueOf(state.numberOfCertificates()));
        });
        return observation.observe(validation);
    }

    // The timemark service validates against its own trusted list, every other validator against the generic
    // one: the holder each service is wired to (its qualifier) says the same.
    private Optional<TSLLoader> loaderFor(String validator) {
        String tslName = ContainerValidationProxy.TIMEMARK_CONTAINER_SERVICE.equals(validator)
                ? TM_TSL_NAME : GENERIC_TSL_NAME;
        return tslLoaders.stream()
                .filter(loader -> tslName.equals(loader.getTslName()))
                .findFirst();
    }

    private static void describe(Observation observation, SimpleReport report) {
        ValidationConclusion conclusion = report == null ? null : report.getValidationConclusion();
        if (conclusion == null) {
            return;
        }
        // The statistics record's container type, so the two vocabularies are one. Absent when the validation
        // failed: there is no conclusion to read it from.
        observation.lowCardinalityKeyValue("container.type", ContainerTypeResolver.resolveContainerType(conclusion))
                .highCardinalityKeyValue("signatures", valueOr(conclusion.getSignaturesCount(), "0"))
                .highCardinalityKeyValue("valid.signatures", valueOr(conclusion.getValidSignaturesCount(), "0"));
    }

    private static String valueOr(Object value, String fallback) {
        return value == null || StringUtils.isBlank(Objects.toString(value)) ? fallback : value.toString();
    }

}
