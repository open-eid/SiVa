/*
 * Copyright 2023 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import eu.europa.esig.dss.exception.IllegalInputException;
import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import jakarta.xml.bind.JAXBException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.esig.dss.policy.ValidationPolicyFacade.newFacade;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PolicyUtil {

    public static List<String> getTLevelSignatures(ConstraintDefinedPolicy policy) {
        List<String> signatureLevels = getValidationPolicy(policy).getSignatureConstraints().getAcceptableFormats().getId();

        return signatureLevels.stream()
                .filter(string -> string.endsWith("BASELINE-T"))
                .collect(Collectors.toList());
    }

    public static ValidationPolicy getValidationPolicy(ConstraintDefinedPolicy policy) {
        ValidationPolicyFacade validationPolicyFacade = newFacade();
        try {
            return validationPolicyFacade.getValidationPolicy(policy.getConstraintDataStream());
        } catch (JAXBException | XMLStreamException | IOException | SAXException e) {
            throw new IllegalInputException("Unable to load the policy", e);
        }
    }
}
