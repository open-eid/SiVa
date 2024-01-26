/*
 * Copyright 2018 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import ee.openeid.siva.webapp.request.validation.annotations.ValidFilename;
import ee.openeid.siva.webapp.request.validation.annotations.ValidHashAlgo;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class Datafile {

    @ValidFilename
    private String filename;

    @ValidHashAlgo
    private String hashAlgo;

    @ValidBase64String
    @Size(min = 1, max = 1000)
    private String hash;

}
