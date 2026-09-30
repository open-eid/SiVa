/*
 * Copyright 2025 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.helper.matcher;

import ee.openeid.siva.validation.document.report.Certificate;
import ee.openeid.siva.validation.document.report.CertificateType;
import ee.openeid.siva.validation.helper.Generators;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class IsCertificate extends TypeSafeDiagnosingMatcher<Certificate> {

    private final Matcher<String> commonNameMatcher;
    private final Matcher<String> contentMatcher;
    private final Matcher<Certificate> issuerMatcher;
    private final Matcher<CertificateType> typeMatcher;

    @Override
    protected boolean matchesSafely(Certificate item, Description mismatchDescription) {
        Supplier<String> conjunctionGenerator = Generators.infinite("Certificate ", " and ");
        boolean result = true;

        if (commonNameMatcher != null && !commonNameMatcher.matches(item.getCommonName())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("common name ");
            commonNameMatcher.describeMismatch(item.getCommonName(), mismatchDescription);
            result = false;
        }

        if (contentMatcher != null && !contentMatcher.matches(item.getContent())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("content ");
            contentMatcher.describeMismatch(item.getContent(), mismatchDescription);
            result = false;
        }

        if (issuerMatcher != null && !issuerMatcher.matches(item.getIssuer())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("issuer ");
            issuerMatcher.describeMismatch(item.getIssuer(), mismatchDescription);
            result = false;
        }

        if (typeMatcher != null && !typeMatcher.matches(item.getType())) {
            mismatchDescription.appendText(conjunctionGenerator.get()).appendText("type ");
            typeMatcher.describeMismatch(item.getType(), mismatchDescription);
            result = false;
        }

        return result;
    }

    @Override
    public void describeTo(Description description) {
        Supplier<String> conjunctionGenerator = Generators.infinite(" with ", " and ");
        description.appendText("Certificate");

        if (commonNameMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("common name ").appendDescriptionOf(commonNameMatcher);
        }
        if (contentMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("content ").appendDescriptionOf(contentMatcher);
        }
        if (issuerMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("issuer ").appendDescriptionOf(issuerMatcher);
        }
        if (typeMatcher != null) {
            description.appendText(conjunctionGenerator.get()).appendText("type ").appendDescriptionOf(typeMatcher);
        }
    }

    public static Matcher<Certificate> isCertificateWith(
            String commonName,
            String content,
            Matcher<Certificate> issuerMatcher,
            CertificateType type
    ) {
        return isCertificateWith(
                asStringMatcher(commonName),
                asStringMatcher(content),
                issuerMatcher,
                asCertificateTypeMatcher(type)
        );
    }

    public static Matcher<Certificate> isCertificateWith(
            Matcher<String> commonNameMatcher,
            Matcher<String> contentMatcher,
            Matcher<Certificate> issuerMatcher,
            Matcher<CertificateType> typeMatcher
    ) {
        return new IsCertificate(commonNameMatcher, contentMatcher, issuerMatcher, typeMatcher);
    }

    public static Matcher<Certificate> isCertificateWithoutIssuer(
            String commonName,
            String content,
            CertificateType type
    ) {
        return isCertificateWithoutIssuer(
                asStringMatcher(commonName),
                asStringMatcher(content),
                asCertificateTypeMatcher(type)
        );
    }

    public static Matcher<Certificate> isCertificateWithoutIssuer(
            Matcher<String> commonNameMatcher,
            Matcher<String> contentMatcher,
            Matcher<CertificateType> typeMatcher
    ) {
        return new IsCertificate(
                commonNameMatcher,
                contentMatcher,
                Matchers.nullValue(Certificate.class),
                typeMatcher
        );
    }

    public static Matcher<Certificate> isCertificateWithoutType(
            String commonName,
            String content,
            Matcher<Certificate> issuerMatcher
    ) {
        return isCertificateWithoutType(
                asStringMatcher(commonName),
                asStringMatcher(content),
                issuerMatcher
        );
    }

    public static Matcher<Certificate> isCertificateWithoutType(
            Matcher<String> commonNameMatcher,
            Matcher<String> contentMatcher,
            Matcher<Certificate> issuerMatcher
    ) {
        return new IsCertificate(
                commonNameMatcher,
                contentMatcher,
                issuerMatcher,
                Matchers.nullValue(CertificateType.class)
        );
    }

    public static Matcher<Certificate> isCertificateWithoutIssuerNorType(
            String commonName,
            String content
    ) {
        return isCertificateWithoutIssuerNorType(
                asStringMatcher(commonName),
                asStringMatcher(content)
        );
    }

    public static Matcher<Certificate> isCertificateWithoutIssuerNorType(
            Matcher<String> commonNameMatcher,
            Matcher<String> contentMatcher
    ) {
        return new IsCertificate(
                commonNameMatcher,
                contentMatcher,
                Matchers.nullValue(Certificate.class),
                Matchers.nullValue(CertificateType.class)
        );
    }

    public static Matcher<Certificate> isCertificateEqualTo(Certificate certificate) {
        return isCertificateWith(
                certificate.getCommonName(),
                certificate.getContent(),
                Optional
                        .ofNullable(certificate.getIssuer())
                        .map(IsCertificate::isCertificateEqualTo)
                        .orElseGet(() -> Matchers.nullValue(Certificate.class)),
                certificate.getType()
        );
    }

    private static Matcher<CertificateType> asCertificateTypeMatcher(CertificateType type) {
        return Optional
                .ofNullable(type)
                .map(Matchers::sameInstance)
                .orElseGet(() -> Matchers.nullValue(CertificateType.class));
    }

    private static Matcher<String> asStringMatcher(String value) {
        return Optional
                .ofNullable(value)
                .map(Matchers::equalTo)
                .orElseGet(() -> Matchers.nullValue(String.class));
    }

}
