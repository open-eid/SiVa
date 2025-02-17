/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.tsl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;

@Slf4j
@Component
public class TSLRefresher implements Runnable {

    private Set<TSLLoader> loaders;

    @PostConstruct
    public void init() {
        run();
    }

    @Override
    public void run() {
        log.info("Started TSL refresh process...");
        loaders.forEach(TSLLoader::loadTSL);
        log.info("Finished TSL refresh process...");
    }

    @Autowired
    public void setLoaders(Set<TSLLoader> loaders) {
        this.loaders = loaders;
    }

}
