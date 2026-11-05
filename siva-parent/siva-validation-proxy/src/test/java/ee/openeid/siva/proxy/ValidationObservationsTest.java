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
import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.tsl.TSLLoader;
import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationObservationsTest {

    private static final TSLLoader.State LOADED = new TSLLoader.State(Instant.parse("2026-11-05T08:00:00Z"), 1187,
            Map.of("EE", 1187), Map.of(), null, "20261105T073000Z", null, Map.of(), Map.of(), null);

    private final ObservationRegistry registry = ObservationRegistry.create();
    private final List<Observation.Context> stopped = new ArrayList<>();

    @Mock
    private TSLLoader genericLoader;
    @Mock
    private TSLLoader timemarkLoader;

    private ValidationObservations observations;

    @BeforeEach
    void setUp() {
        registry.observationConfig().observationHandler(new ObservationHandler<Observation.Context>() {
            @Override
            public boolean supportsContext(Observation.Context context) {
                return true;
            }

            @Override
            public void onStop(Observation.Context context) {
                stopped.add(context);
            }
        });
        // Insertion order, timemark first: Set.of iterates in an order that changes per run, and
        // the lookup stops at the first loader whose name matches.
        observations = new ValidationObservations(registry,
                new LinkedHashSet<>(List.of(timemarkLoader, genericLoader)));
    }

    @Test
    void containerValidationIsDescribedFromRequestAndReport() {
        ProxyDocument document = new ProxyDocument();
        document.setReportType(ReportType.DETAILED);
        document.setSignaturePolicy("POLv4");
        SimpleReport report = report("ASiC-E", 2, 1);

        SimpleReport result = observations.validation(document, ReportType.DETAILED, () -> report);

        assertThat(result, sameInstance(report));
        assertThat(stopped, hasSize(1));
        Observation.Context context = stopped.get(0);
        assertThat(context.getName(), equalTo("siva.validation"));
        assertThat(context.getLowCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("request", "container"),
                KeyValue.of("report.type", "DETAILED"),
                KeyValue.of("container.type", "ASiC-E")));
        assertThat(context.getHighCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("policy", "POLv4"),
                KeyValue.of("signatures", "2"),
                KeyValue.of("valid.signatures", "1")));
    }

    @Test
    void hashcodeValidationWithDefaultsFallsBackToDefaultPolicyAndNoContainer() {
        ProxyHashcodeDataSet dataSet = new ProxyHashcodeDataSet();

        observations.validation(dataSet, ReportType.SIMPLE, () -> report(null, 0, 0));

        Observation.Context context = stopped.get(0);
        assertThat(context.getLowCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("request", "hashcode"),
                KeyValue.of("report.type", "SIMPLE"),
                KeyValue.of("container.type", "N/A")));
        assertThat(context.getHighCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("policy", "default"),
                KeyValue.of("signatures", "0"),
                KeyValue.of("valid.signatures", "0")));
    }

    @Test
    void hashcodeValidationReportsTheProducedTypeNotTheRequestedOne() {
        ProxyHashcodeDataSet dataSet = new ProxyHashcodeDataSet();
        dataSet.setReportType(ReportType.DETAILED);

        observations.validation(dataSet, ReportType.SIMPLE, () -> report(null, 1, 1));

        assertThat(stopped.get(0).getLowCardinalityKeyValues(), hasItem(KeyValue.of("report.type", "SIMPLE")));
    }

    @Test
    void failedValidationRecordsTheErrorAndRethrows() {
        ProxyDocument document = new ProxyDocument();

        assertThrows(IllegalStateException.class, () -> observations.validation(document, ReportType.SIMPLE, () -> {
            throw new IllegalStateException("boom");
        }));

        assertThat(stopped, hasSize(1));
        assertThat(stopped.get(0).getError(), instanceOf(IllegalStateException.class));
        assertThat(stopped.get(0).getLowCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("request", "container"),
                KeyValue.of("report.type", "SIMPLE")));
    }

    @Test
    void serviceStageCarriesTheGenericTrustListState() {
        when(genericLoader.getTslName()).thenReturn("generic");
        when(genericLoader.getState()).thenReturn(LOADED);
        when(timemarkLoader.getTslName()).thenReturn("timemark");
        Reports reports = new Reports();

        Reports result = observations.service("genericValidationService", () -> reports);

        assertThat(result, sameInstance(reports));
        Observation.Context context = stopped.get(0);
        assertThat(context.getName(), equalTo("siva.validation.service"));
        assertThat(context.getLowCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("validator", "generic"),
                KeyValue.of("tsl.loader", "generic")));
        assertThat(context.getHighCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("tsl.snapshot", "20261105T073000Z"),
                KeyValue.of("tsl.anchors", "1187")));
    }

    @Test
    void timemarkServiceStageCarriesTheTimemarkTrustListState() {
        when(timemarkLoader.getTslName()).thenReturn("timemark");
        when(timemarkLoader.getState()).thenReturn(TSLLoader.State.NONE);

        observations.service("timemarkContainerValidationService", Reports::new);

        Observation.Context context = stopped.get(0);
        assertThat(context.getLowCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("validator", "timemarkContainer"),
                KeyValue.of("tsl.loader", "timemark")));
        assertThat(context.getHighCardinalityKeyValues(), containsInAnyOrder(
                KeyValue.of("tsl.snapshot", "none"),
                KeyValue.of("tsl.anchors", "0")));
    }

    @Test
    void serviceStageWithoutALoaderNamesTheValidatorOnly() {
        observations = new ValidationObservations(registry, Set.of());

        observations.service("hashcodeGenericValidationService", Reports::new);

        Observation.Context context = stopped.get(0);
        assertThat(context.getLowCardinalityKeyValues(), contains(KeyValue.of("validator", "hashcodeGeneric")));
        assertThat(context.getHighCardinalityKeyValues(), emptyIterable());
    }

    private static SimpleReport report(String signatureForm, int signatures, int validSignatures) {
        ValidationConclusion conclusion = new ValidationConclusion();
        conclusion.setSignatureForm(signatureForm);
        conclusion.setSignaturesCount(signatures);
        conclusion.setValidSignaturesCount(validSignatures);
        SimpleReport report = new SimpleReport();
        report.setValidationConclusion(conclusion);
        return report;
    }

}
