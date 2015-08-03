package ee.sk.testserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class DeployProperties {
    private static final String PREFIX = "testserver.";
    private static final String DEPLOY_PREFIX = "DEPLOY";

    private Map<DeployPropertyKey, String> deployProperties = new HashMap<>();

    public DeployProperties() throws IOException {
        setDeployPropertiesValues();
    }

    public String getProperty(DeployPropertyKey key) {
        String keyValue = deployProperties.get(key);
        if (isDeployKeyName(key)) {
            keyValue = deployProperties.get(DeployPropertyKey.DEPLOY_BASE_DIRECTORY) + keyValue;
        }

        return keyValue;
    }

    private static boolean isDeployKeyName(DeployPropertyKey key) {
        String keyName = key.toString();
        return keyName.startsWith(DEPLOY_PREFIX) && !keyName.equals(DeployPropertyKey.DEPLOY_BASE_DIRECTORY.toString());
    }

    private void setDeployPropertiesValues() throws IOException {
        Properties testServerProperties = getTestServerProperties();

        for (DeployPropertyKey deployPropertyKey : DeployPropertyKey.values()) {
            String propertyKey = PREFIX + deployPropertyKey.toString().toLowerCase().replace('_', '.');
            String deployPropertyValue = testServerProperties.getProperty(propertyKey);

            deployProperties.put(deployPropertyKey, deployPropertyValue);
        }
    }

    private static Properties getTestServerProperties() throws IOException {
        Path propertiesPath = getConfigPropertiesPath();
        return loadProperties(propertiesPath);
    }

    private static Properties loadProperties(Path propertiesPath) throws IOException {
        InputStream fileInputStream = new FileInputStream(propertiesPath.toFile());
        Properties properties = new Properties();
        properties.load(fileInputStream);

        fileInputStream.close();
        return properties;
    }

    private static Path getConfigPropertiesPath() throws IOException {
        String configPropertiesFile = "/pdf-validator-test/src/main/config/tests.properties";
        return Paths.get(new File(".").getCanonicalPath() + configPropertiesFile);
    }
}
