/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.tsl.keystore;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DSSKeyStoreFactoryBean extends AbstractFactoryBean<KeyStoreCertificateSource> {
    private static final Logger KEY_STORE_LOGGER = LoggerFactory.getLogger(DSSKeyStoreFactoryBean.class);
    private static final String ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER = "DSS_DATA_FOLDER";

    private String keyStoreType;
    private String keyStoreFilename;
    private String keyStorePassword;

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setKeyStoreFilename(String keyStoreFilename) {
        this.keyStoreFilename = keyStoreFilename;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    protected KeyStoreCertificateSource createInstance() throws Exception {
        File keystoreFile = getKeyStoreFile();
        if (keystoreFile.exists()) {
            KEY_STORE_LOGGER.info("Keystore file found (" + keystoreFile.getAbsolutePath() + ")");
        } else {
            KEY_STORE_LOGGER.info("Keystore file not found on server");
            KEY_STORE_LOGGER.info("Copying keystore file from the war");

            InputStream is = null;
            OutputStream os = null;
            try {
                is = DSSKeyStoreFactoryBean.class.getResourceAsStream("/" + keyStoreFilename);
                os = new FileOutputStream(keystoreFile);
                IOUtils.copy(is, os);
            } catch (Exception e) {
                throw new DSSException("Unable to create the keystore on the server : " + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(os);
            }
        }
        return new KeyStoreCertificateSource(keystoreFile, keyStoreType, keyStorePassword);
    }

    @Override
    public Class<?> getObjectType() {
        return KeyStoreCertificateSource.class;
    }

    private File getKeyStoreFile() {
        String finalDataFolder = getDssDataFolder();

        File folder = new File(finalDataFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }

        String finalAbsoluteKeystoreFilepath = finalDataFolder + File.separatorChar + keyStoreFilename;
        File keystoreFile = new File(finalAbsoluteKeystoreFilepath);
        return keystoreFile;
    }

    private String getDssDataFolder() {
        String dssDataFolder = System.getProperty(ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER);
        if (StringUtils.isNotEmpty(dssDataFolder)) {
            KEY_STORE_LOGGER.info(ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER + " found as system property : " + dssDataFolder);
            return dssDataFolder;
        }

        dssDataFolder = System.getenv(ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER);
        if (StringUtils.isNotEmpty(dssDataFolder)) {
            KEY_STORE_LOGGER.info(ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER + " found as environment variable : " + dssDataFolder);
            return dssDataFolder;
        }

        KEY_STORE_LOGGER.warn(ENVIRONMENT_VARIABLE_DSS_DATA_FOLDER + " not defined (returns 'etc')");
        return "etc";
    }

}
