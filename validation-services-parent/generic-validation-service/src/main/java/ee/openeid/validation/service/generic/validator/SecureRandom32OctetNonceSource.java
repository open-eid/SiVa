/*
 * Copyright 2023 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.service.NonceSource;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SecureRandom32OctetNonceSource implements NonceSource {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public byte[] getNonceValue() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    @Override
    public BigInteger getNonce() {
        return new BigInteger(getNonceValue());
    }
}
