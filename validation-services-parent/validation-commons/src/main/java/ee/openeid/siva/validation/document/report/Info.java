/*
 * Copyright 2020 - 2026 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.document.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Info {

    private String bestSignatureTime;
    private String ocspResponseCreationTime;
    private String timestampCreationTime;
    private String timeAssertionMessageImprint;
    private String signingReason;
    private List<SignerRole> signerRole;
    private SignatureProductionPlace signatureProductionPlace;
    private List<ArchiveTimeStamp> archiveTimeStamps;

    public static class InfoBuilder {
        public Info build() {
            boolean hasMeaningfulData = StringUtils.isNotEmpty(bestSignatureTime)
                    || StringUtils.isNotEmpty(ocspResponseCreationTime)
                    || StringUtils.isNotEmpty(timestampCreationTime)
                    || StringUtils.isNotEmpty(timeAssertionMessageImprint)
                    || StringUtils.isNotEmpty(signingReason)
                    || CollectionUtils.isNotEmpty(signerRole)
                    || Objects.nonNull(signatureProductionPlace)
                    || CollectionUtils.isNotEmpty(archiveTimeStamps);

            return hasMeaningfulData ? new Info(
                    bestSignatureTime,
                    ocspResponseCreationTime,
                    timestampCreationTime,
                    timeAssertionMessageImprint,
                    signingReason,
                    signerRole,
                    signatureProductionPlace,
                    archiveTimeStamps
            ) : null;
        }
    }
}
