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

package ee.openeid.siva.sample.cache;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Service
@CacheConfig(cacheNames = "file")
public class AbstractUploadFileCacheService implements UploadFileCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUploadFileCacheService.class);

    @Override
    @CachePut(key = "#timestamp")
    public UploadedFile addUploadedFile(long timestamp, MultipartFile file, String encodedFilename) throws IOException {
        final UploadedFile uploadedFile = new UploadedFile();
        setFilename(encodedFilename, uploadedFile);
        setFileContents(file, uploadedFile);

        uploadedFile.setTimestamp(timestamp);
        return uploadedFile;
    }

    private static void setFileContents(MultipartFile file, UploadedFile uploadedFile) throws IOException {
        final String fileContents = file == null ? null : Base64.encodeBase64String(file.getBytes());
        uploadedFile.setEncodedFile(fileContents);
    }

    private static void setFilename(String file, UploadedFile uploadedFile) throws UnsupportedEncodingException {
        final String filename = file  == null ? "" : URLDecoder.decode(file, Charsets.UTF_8.displayName());
        uploadedFile.setFilename(filename);
    }

    @Override
    @Cacheable(key = "#timestamp")
    public UploadedFile getUploadedFile(long timestamp) {
        return new UploadedFile();
    }

    @Override
    @CacheEvict(key = "#timestamp")
    public void deleteUploadedFile(long timestamp) {
        LOGGER.info("Remove file with timestamp {} form cache", timestamp);
    }
}
