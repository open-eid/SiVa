/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.generic.report;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.generic.validator.report.GenericValidationReportBuilder;
import eu.europa.esig.dss.jaxb.diagnostic.*;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;


@RunWith(MockitoJUnitRunner.class)
public class GenericValidationReportBuilderTest {

    @Test
    public void totalPassedIndicationReportBuild() {
        Reports reports = new GenericValidationReportBuilder(getDssReports(), ValidationLevel.ARCHIVAL_DATA, getValidationDocument(), getValidationPolicy(), false).build();
        Assert.assertEquals(new Integer(1), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("TOTAL-PASSED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
    }

    @Test
    public void totalFailedIndicationReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports();
        dssReports.getSimpleReportJaxb().getSignature().get(0).setIndication(Indication.TOTAL_FAILED);
        dssReports.getSimpleReportJaxb().getSignature().get(0).getErrors().add("Something is wrong");
        Reports reports = new GenericValidationReportBuilder(dssReports, ValidationLevel.ARCHIVAL_DATA, getValidationDocument(), getValidationPolicy(), false).build();
        Assert.assertEquals(new Integer(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("TOTAL-FAILED", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
        Assert.assertEquals("Something is wrong", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getErrors().get(0).getContent());
    }

    @Test
    public void indeterminateIndicationReportBuild() {
        eu.europa.esig.dss.validation.reports.Reports dssReports = getDssReports();
        dssReports.getSimpleReportJaxb().getSignature().get(0).setIndication(Indication.INDETERMINATE);
        Reports reports = new GenericValidationReportBuilder(dssReports, ValidationLevel.ARCHIVAL_DATA, getValidationDocument(), getValidationPolicy(), false).build();
        Assert.assertEquals(new Integer(0), reports.getSimpleReport().getValidationConclusion().getValidSignaturesCount());
        Assert.assertEquals("INDETERMINATE", reports.getSimpleReport().getValidationConclusion().getSignatures().get(0).getIndication());
    }

    private ValidationDocument getValidationDocument() {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName("filename.bdoc");
        validationDocument.setBytes("dGVzdA==".getBytes());
        return validationDocument;
    }

    private ConstraintDefinedPolicy getValidationPolicy() {
        ValidationPolicy validationPolicy = new ValidationPolicy();
        validationPolicy.setName("POLv3");
        validationPolicy.setDescription("description");
        validationPolicy.setUrl("localhost");
        return new ConstraintDefinedPolicy(validationPolicy);
    }

    private eu.europa.esig.dss.validation.reports.Reports getDssReports() {
        return new eu.europa.esig.dss.validation.reports.Reports(getDiagnosticDataJaxb(), null, getSimpleReport());
    }

    private DiagnosticData getDiagnosticDataJaxb() {
        DiagnosticData diagnosticData = new DiagnosticData();
        XmlContainerInfo xmlContainerInfo = new XmlContainerInfo();
        xmlContainerInfo.setContainerType("ASIC-E");
        diagnosticData.setContainerInfo(xmlContainerInfo);
        XmlSignature xmlSignature = new XmlSignature();
        XmlSignatureScope xmlSignatureScope = new XmlSignatureScope();
        xmlSignature.setSignatureScopes(Collections.singletonList(xmlSignatureScope));
        xmlSignature.setId("SIG-id");
        diagnosticData.setSignatures(Collections.singletonList(xmlSignature));
        return diagnosticData;
    }

    private eu.europa.esig.dss.jaxb.simplereport.SimpleReport getSimpleReport() {
        eu.europa.esig.dss.jaxb.simplereport.SimpleReport simpleReport = new eu.europa.esig.dss.jaxb.simplereport.SimpleReport();
        XmlSigningCertificate xmlSigningCertificate = new XmlSigningCertificate();
        xmlSigningCertificate.setId("CERT-id");

        eu.europa.esig.dss.jaxb.simplereport.XmlSignature xmlSignature = new eu.europa.esig.dss.jaxb.simplereport.XmlSignature();
        xmlSignature.setId("SIG-id");
        xmlSignature.setIndication(Indication.TOTAL_PASSED);
        xmlSignature.setSignatureFormat("XAdES-BASELINE-LT");
        simpleReport.getSignature().add(xmlSignature);

        return simpleReport;
    }
}
