/*
 * Copyright 2021 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.monitoring.util;

import com.jcabi.manifests.Manifests;
import com.jcabi.manifests.ServletMfs;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;

@Slf4j
public class ManifestReader {

    public ManifestReader(ServletContext servletContext) {
        try {
            Manifests.DEFAULT.append(new ServletMfs(servletContext));
        } catch (Exception e) {
            log.error("Failed to register manifests: " + e.getMessage(), e);
        }
    }

    public String readFromManifest(String parameterName) {
        try {
            return Manifests.read(parameterName);
        } catch (Exception e) {
            log.warn("Failed to fetch parameter '" + parameterName + "' from manifest file! Either you are not running the application as a jar/war package or there is a problem with the build configuration. " + e.getMessage());
            return null;
        }
    }

}
