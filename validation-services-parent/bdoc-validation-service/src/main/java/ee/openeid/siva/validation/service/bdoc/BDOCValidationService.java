package ee.openeid.siva.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.bdoc.report.qualified.Policy;
import ee.openeid.siva.validation.service.bdoc.report.qualified.QualifiedReport;
import ee.openeid.siva.validation.service.bdoc.report.qualified.SignatureValidationData;
import org.apache.xml.security.signature.Reference;
import org.digidoc4j.*;
import org.digidoc4j.impl.bdoc.BDocSignature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.digidoc4j.X509Cert.SubjectName.CN;

public class BDOCValidationService implements ValidationService {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Override
    public BDOCValidationResult validateDocument(ValidationDocument validationDocument) {

        // TODO: Make configuration mode configurable
        Configuration configuration = new Configuration(Configuration.Mode.PROD);

        InputStream containerInputStream = new ByteArrayInputStream(validationDocument.getBytes());

        Container container = ContainerBuilder.
                aContainer().
                withConfiguration(configuration).
                fromStream(containerInputStream).
                build();

        ValidationResult validationResult = container.validate();
        QualifiedReport qualifiedReport = constructQualifiedReport(container, validationResult, validationDocument);

        BDOCValidationResult bdocValidationResult = new BDOCValidationResult(validationResult);
        bdocValidationResult.setQualifiedReport(qualifiedReport);

        return bdocValidationResult;
    }

    private QualifiedReport constructQualifiedReport(Container container, ValidationResult validationResult, ValidationDocument validationDocument) {
        QualifiedReport qualifiedReport = new QualifiedReport();

        //policy
        qualifiedReport.setPolicy(Policy.SIVA_DEFAULT);

        //validation time
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        String validationTime = sdf.format(date);
        qualifiedReport.setValidationTime(validationTime);


        //document name
        qualifiedReport.setDocumentName(validationDocument.getName());

        //signature count
        qualifiedReport.setSignaturesCount(container.getSignatures().size());

        qualifiedReport.setSignatures(createSignaturesForReport(container));

        BDocSignature bDocSignature = (BDocSignature)container.getSignatures().get(0);
        Reference reference = bDocSignature.getOrigin().getReferences().get(0);
        reference.getURI();





        return qualifiedReport;
    }

    private List<SignatureValidationData> createSignaturesForReport(Container container) {
        return container.getSignatures().stream().map(this::createSignatureValidationData).collect(Collectors.toList());
    }

    /*
    private String id;
    private String signatureFormat; //TODO: enum?
    private String signatureLevel; //TODO: enum?
    private String signedBy;
    private String indication; //TODO: enum? also sub indication?
    private List<Error> errors;
    private List<SignatureScope> signatureScopes;
    private String claimedSigningTime;
    private List<Warning> warnings;
    private Info info;
    private AdditionalValidation additionalValidation;
    */
    private SignatureValidationData createSignatureValidationData(Signature signature) {
        SignatureValidationData signatureValidationData = new SignatureValidationData();

        BDocSignature bDocSignature = (BDocSignature) signature;

        signatureValidationData.setId(bDocSignature.getId());
        signatureValidationData.setSignatureFormat(getSignatureFormat(bDocSignature.getProfile()));
        //TODO: level
        signatureValidationData.setSignedBy(bDocSignature.getSigningCertificate().getSubjectName(CN));
        //TODO: indication



        return signatureValidationData;

    }

    private String getSignatureFormat(SignatureProfile profile) {
        String prefix = "XAdES_BASELINE_";
        return prefix + profile.name();
    }

}
