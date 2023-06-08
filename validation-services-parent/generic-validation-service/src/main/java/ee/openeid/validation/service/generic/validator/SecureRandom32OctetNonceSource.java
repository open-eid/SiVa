package ee.openeid.validation.service.generic.validator;

import eu.europa.esig.dss.service.NonceSource;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SecureRandom32OctetNonceSource implements NonceSource {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public BigInteger getNonce() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return new BigInteger(bytes);
    }
}
