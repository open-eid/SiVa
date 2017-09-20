package ee.openeid.siva.webapp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import ee.openeid.siva.singature.XadesSignatureService;
import ee.openeid.siva.singature.SignatureService;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.webapp.response.ValidationResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

/**
 * Alters the response by creating a signature from the existing response's body and adding the signature into the body.
 */
@RestControllerAdvice
public class ReportSignatureInterceptor implements ResponseBodyAdvice<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSignatureInterceptor.class);

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private SignatureService signatureService = new XadesSignatureService();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object responseObject, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            if (responseObject instanceof ValidationResponse) {
                ValidationResponse validationResponse = (ValidationResponse) responseObject;
                String validationReportJsonString = jacksonObjectMapper.writeValueAsString(validationResponse.getValidationReport());
                byte[] reportSignatureBytes = signatureService.getSignature(validationReportJsonString.getBytes());
                validationResponse.setValidationReportSignature(Base64.encodeBase64String(reportSignatureBytes));
                return validationResponse;
            }
        } catch (IOException e) {
            LOGGER.error("Error producing report singature", e);
            return responseObject;
        }
        return responseObject;
    }

}
