/*
 * Copyright 2017 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.service.signature.policy.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "constraintData")
public class ConstraintDefinedPolicy extends ValidationPolicy {
    private Resource constraintPath;
    private byte[] constraintData;

    public ConstraintDefinedPolicy(ValidationPolicy validationPolicy) {
        setName(validationPolicy.getName());
        setDescription(validationPolicy.getDescription());
        setUrl(validationPolicy.getUrl());
    }

    public InputStream getConstraintDataStream() {
        return new ByteArrayInputStream(constraintData);
    }
}
