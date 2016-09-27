/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
public class XROADValidationExceptionHandlerTest {

    private XROADValidationApplication validationController;

    private ValidationService validationService;

    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Before
    public void SetUp() {
        validationController = new XROADValidationApplication();
        validationService = mock(ValidationService.class);
        validationController.setValidationService(validationService);
        XROADValidationExceptionHandler validationExceptionHandler = new XROADValidationExceptionHandler();
        validationExceptionHandler.setMessageSource(messageSource);
        mockMvc = standaloneSetup(validationController).setControllerAdvice(validationExceptionHandler).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMalformedDocumentExceptionOnValidationExceptionHandler() throws Exception {
        when(validationService.validateDocument(any(ValidationDocument.class))).thenThrow(MalformedDocumentException.class);
        MvcResult result = mockMvc.perform(post("/xroad-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("{\"key\":\"document\",\"message\":\"Document malformed or not matching documentType\"}", content);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidPolicyExceptionOnValidationExceptionHandler() throws Exception {
        when(validationService.validateDocument(any(ValidationDocument.class))).thenThrow(new InvalidPolicyException("some message"));
        MvcResult result = mockMvc.perform(post("/xroad-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequest().toString().getBytes()))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("{\"key\":\"signaturePolicy\",\"message\":\"some message\"}", content);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidationServiceExceptionOnValidationExceptionHandler() throws Exception {
        when(validationService.validateDocument(any(ValidationDocument.class))).thenThrow(ValidationServiceException.class);
        MvcResult result = mockMvc.perform(post("/xroad-validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequest().toString().getBytes()))
                .andExpect(status().isInternalServerError())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Unfortunately there was an error validating your document\"}", content);
    }

    private JSONObject mockRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bytes", "");
        jsonObject.put("name", "");
        jsonObject.put("signaturePolicy", "");
        jsonObject.put("dataBase64Encoded", "");
        return jsonObject;
    }
}
