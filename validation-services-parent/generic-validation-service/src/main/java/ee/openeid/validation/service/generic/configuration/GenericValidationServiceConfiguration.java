/*
 * Copyright 2016 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.configuration;

import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.util.DistinguishedNameUtil;
import ee.openeid.tsl.annotation.LoadableTsl;
import ee.openeid.tsl.configuration.AlwaysFailingOCSPSource;
import ee.openeid.validation.service.generic.configuration.properties.GenericSignaturePolicyProperties;
import ee.openeid.validation.service.generic.configuration.properties.TLevelSignatureFilterProperties;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidator;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidatorFactory;
import ee.openeid.validation.service.generic.validator.TLevelSignatureOfNonListedCountryPredicate;
import ee.openeid.validation.service.generic.validator.container.AsicContainerDataFileSizeValidator;
import ee.openeid.validation.service.generic.validator.container.ContainerValidator;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.container.ZipBasedContainerValidator;
import ee.openeid.validation.service.generic.validator.filter.AllowedCountriesFilter;
import ee.openeid.validation.service.generic.validator.filter.CountryFilter;
import ee.openeid.validation.service.generic.validator.filter.NotAllowedCountriesFilter;
import ee.openeid.validation.service.generic.validator.ocsp.CompositeOCSPSource;
import ee.openeid.validation.service.generic.validator.ocsp.LoggingOSCPSourceWrapper;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPRequestPredicate;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.validation.reports.AbstractReports;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_POLICY_SERVICE_BEAN_NAME;
import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME;
import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_TSL_NAME;
import static ee.openeid.validation.service.generic.PolicyUtil.getTLevelSignatures;

@Configuration
@EnableConfigurationProperties(GenericSignaturePolicyProperties.class)
public class GenericValidationServiceConfiguration {

    @Bean(name = GENERIC_POLICY_SERVICE_BEAN_NAME)
    public ConstraintLoadingSignaturePolicyService signaturePolicyService(GenericSignaturePolicyProperties properties) {
        return new ConstraintLoadingSignaturePolicyService(properties);
    }

    @LoadableTsl(name = GENERIC_TSL_NAME)
    @Bean(name = GENERIC_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME)
    public TrustedListsCertificateSource genericTrustedListsCertificateSource() {
        return new TrustedListsCertificateSource();
    }

    @Bean
    public ContainerValidatorFactory containerValidatorFactory() {
        return (validationReports, validationDocument) -> isAsicContainer(validationReports)
                ? new ZipBasedContainerValidator(validationDocument, new AsicContainerDataFileSizeValidator(validationReports))
                : ContainerValidator.NO_OP_INSTANCE;
    }

    @Bean
    @ConditionalOnBean(TLevelSignatureFilterProperties.class)
    public CountryFilter countryFilter(TLevelSignatureFilterProperties properties) {
        List<String> countries = properties.getCountries();

        switch (properties.getFilterType()) {
            case ALLOWED_COUNTRIES:
                return new AllowedCountriesFilter(countries);
            case NOT_ALLOWED_COUNTRIES:
                return new NotAllowedCountriesFilter(countries);
            default:
                throw new IllegalStateException("Unexpected country filter type: " + properties.getFilterType());
        }
    }

    @Bean
    public OCSPSourceFactory ocspSourceFactory(@Autowired(required = false) CountryFilter countryFilter) {
        if (countryFilter != null) {
            BiPredicate<CertificateToken, CertificateToken> predicate = new OCSPRequestPredicate(
                    DistinguishedNameUtil::getSubjectDistinguishedNameValueByOid, countryFilter
            );
            final LoggingOSCPSourceWrapper loggingOSCPSourceWrapper = new LoggingOSCPSourceWrapper(createOnlineOCSPSource());
            final OCSPSource ocspSource = new CompositeOCSPSource(loggingOSCPSourceWrapper, predicate);
            return () -> ocspSource;
        } else {
            final OCSPSource ocspSource = new AlwaysFailingOCSPSource();
            return () -> ocspSource;
        }
    }

    @Bean
    public RevocationFreshnessValidatorFactory revocationFreshnessValidatorFactory(
            @Autowired(required = false) CountryFilter countryFilter) {
        if (countryFilter != null) {
            return (reports, policy) -> new RevocationFreshnessValidator(
                    reports, new TLevelSignatureOfNonListedCountryPredicate(countryFilter, getTLevelSignatures(policy))
            );
        } else {
            final Predicate<SignatureWrapper> predicate = signatureWrapper -> false;
            return (reports, policy) -> new RevocationFreshnessValidator(reports, predicate);
        }
    }

    private static boolean isAsicContainer(Reports validationReports) {
        return Optional.ofNullable(validationReports)
                .map(AbstractReports::getDiagnosticData)
                .map(DiagnosticData::getContainerType)
                .filter(ASiCContainerType.class::isInstance)
                .isPresent();
    }

    private static OnlineOCSPSource createOnlineOCSPSource() {
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource(new OCSPDataLoader());
        onlineOCSPSource.setNonceSource(new SecureRandomNonceSource());
        onlineOCSPSource.setCertIDDigestAlgorithm(DigestAlgorithm.SHA1);
        return onlineOCSPSource;
    }

}
