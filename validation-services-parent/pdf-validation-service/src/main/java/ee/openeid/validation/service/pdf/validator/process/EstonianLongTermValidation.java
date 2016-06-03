package ee.openeid.validation.service.pdf.validator.process;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.ProcessParameters;
import eu.europa.esig.dss.validation.policy.RuleUtils;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.policy.rules.*;
import eu.europa.esig.dss.validation.process.LongTermValidation;
import eu.europa.esig.dss.validation.process.TimestampComparator;
import eu.europa.esig.dss.validation.process.ValidationXPathQueryHolder;
import eu.europa.esig.dss.validation.process.ltv.PastSignatureValidation;
import eu.europa.esig.dss.validation.process.ltv.PastSignatureValidationConclusion;
import eu.europa.esig.dss.validation.process.subprocess.EtsiPOEExtraction;
import eu.europa.esig.dss.x509.TimestampType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class is the customized version of {@link eu.europa.esig.dss.validation.process.LongTermValidation}
 */
public class EstonianLongTermValidation {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongTermValidation.class);

    ProcessParameters params;

    private XmlDom diagnosticData;
    private XmlDom timestampValidationData;
    private XmlDom adestValidationData;
    private XmlNode signatureNode;
    private XmlNode conclusionNode;

    private EtsiPOEExtraction poe;

    private void prepareParameters(final XmlNode mainNode) {
        diagnosticData = params.getDiagnosticData();
        isInitialised(mainNode);
    }

    private void isInitialised(final XmlNode mainNode) {

        if (diagnosticData == null) {
            throw new DSSException(String.format(ExceptionMessage.EXCEPTION_TCOPPNTBI, getClass().getSimpleName(), "diagnosticData"));
        }

        if (params.getValidationPolicy() == null) {
            throw new DSSException(String.format(ExceptionMessage.EXCEPTION_TCOPPNTBI, getClass().getSimpleName(), "validationPolicy"));
        }

        if (adestValidationData == null) {
            final EstonianAdESTValidation adestValidation = new EstonianAdESTValidation();
            adestValidationData = adestValidation.run(mainNode, params);
            timestampValidationData = params.getTsData();
        }

        if (poe == null) {
            poe = new EtsiPOEExtraction();
            params.setPOE(poe);
        }
    }

    public XmlDom run(final XmlNode mainNode, final ProcessParameters params) {

        this.params = params;
        prepareParameters(mainNode);
        LOGGER.debug(this.getClass().getSimpleName() + ": start.");

        XmlNode longTermValidationData = mainNode.addChild(NodeName.LONG_TERM_VALIDATION_DATA);

        final List<XmlDom> signatures = diagnosticData.getElements("/DiagnosticData/Signature");

        for (final XmlDom signature : signatures) {

            final String signatureId = signature.getValue("./@Id");
            final String type = signature.getValue("./@Type");
            if (AttributeValue.COUNTERSIGNATURE.equals(type)) {

                params.setCurrentValidationPolicy(params.getCountersignatureValidationPolicy());
            } else {

                params.setCurrentValidationPolicy(params.getValidationPolicy());
            }
            final XmlDom signatureTimestampValidationData = timestampValidationData.getElement("./Signature[@Id='%s']", signatureId);
            final XmlDom adestSignatureValidationData = adestValidationData.getElement("/AdESTValidationData/Signature[@Id='%s']", signatureId);

            signatureNode = longTermValidationData.addChild(NodeName.SIGNATURE);
            signatureNode.setAttribute(AttributeName.ID, signatureId);

            conclusionNode = new XmlNode(NodeName.CONCLUSION);
            try {

                final boolean valid = process(params, signature, signatureTimestampValidationData, adestSignatureValidationData);
                if (valid) {

                    conclusionNode.addFirstChild(NodeName.INDICATION, Indication.VALID);
                }
            } catch (Exception e) {

                LOGGER.warn("Unexpected exception: " + e.getMessage(), e);
            }
            conclusionNode.setParent(signatureNode);
        }
        final XmlDom ltvDom = longTermValidationData.toXmlDom();
        params.setLtvData(ltvDom);
        return ltvDom;
    }

    /**
     * 9.3.4 Processing<br>
     * <p>
     * The following steps shall be performed:<br>
     *
     * @param params
     * @param signature
     * @param signatureTimestampValidationData
     * @param adestSignatureValidationData
     * @return
     */
    private boolean process(ProcessParameters params, XmlDom signature, XmlDom signatureTimestampValidationData, XmlDom adestSignatureValidationData) {

        final List<XmlDom> certificates = params.getCertPool().getElements("./Certificate");

        final XmlDom adestSignatureConclusion = adestSignatureValidationData.getElement("./Conclusion");
        final String adestSignatureIndication = adestSignatureConclusion.getValue("./Indication/text()");
        final String adestSignatureSubIndication = adestSignatureConclusion.getValue("./SubIndication/text()");

        XmlNode constraintNode = addConstraint(signatureNode, MessageTag.PSV_IATVC);

        if (Indication.VALID.equals(adestSignatureIndication)) {

            constraintNode.addChild(NodeName.STATUS, NodeValue.OK);
            final List<XmlDom> adestInfo = adestSignatureConclusion.getElements("./Info");
            constraintNode.addChildren(adestInfo);
            conclusionNode.addChildren(adestInfo);

            return true;
        }

        final boolean finalStatus = Indication.INDETERMINATE.equals(adestSignatureIndication) && (RuleUtils
                .in(adestSignatureSubIndication, SubIndication.REVOKED_NO_POE, SubIndication.REVOKED_CA_NO_POE, SubIndication.OUT_OF_BOUNDS_NO_POE, SubIndication.CRYPTO_CONSTRAINTS_FAILURE_NO_POE));
        if (!finalStatus) {

            conclusionNode.addChildrenOf(adestSignatureConclusion);
            constraintNode.addChild(NodeName.STATUS, NodeValue.KO);
            constraintNode.addChild(NodeName.INFO, adestSignatureIndication).setAttribute(AttributeName.FIELD, NodeName.INDICATION);
            constraintNode.addChild(NodeName.INFO, adestSignatureSubIndication).setAttribute(AttributeName.FIELD, NodeName.SUB_INDICATION);
            return false;
        }
        constraintNode.addChild(NodeName.STATUS, NodeValue.OK);
        constraintNode.addChild(NodeName.INFO, adestSignatureIndication).setAttribute(AttributeName.FIELD, NodeName.INDICATION);
        constraintNode.addChild(NodeName.INFO, adestSignatureSubIndication).setAttribute(AttributeName.FIELD, NodeName.SUB_INDICATION);

        final XmlNode archiveTimestampsNode = signatureNode.addChild("ArchiveTimestamps");
        final List<XmlDom> archiveTimestamps = signature.getElements("./Timestamps/Timestamp[@Type='%s']", TimestampType.ARCHIVE_TIMESTAMP);
        if (archiveTimestamps.size() > 0) {
            dealWithTimestamp(archiveTimestampsNode, signatureTimestampValidationData, archiveTimestamps);
        }

        final XmlNode refsOnlyTimestampsNode = signatureNode.addChild("RefsOnlyTimestamps");
        final List<XmlDom> refsOnlyTimestamps = signature.getElements("./Timestamps/Timestamp[@Type='%s']", TimestampType.VALIDATION_DATA_REFSONLY_TIMESTAMP);
        if (refsOnlyTimestamps.size() > 0) {
            dealWithTimestamp(refsOnlyTimestampsNode, signatureTimestampValidationData, refsOnlyTimestamps);
        }

        final XmlNode sigAndRefsTimestampsNode = signatureNode.addChild("SigAndRefsTimestamps");
        final List<XmlDom> sigAndRefsTimestamps = signature.getElements("./Timestamps/Timestamp[@Type='%s']", TimestampType.VALIDATION_DATA_TIMESTAMP);
        if (sigAndRefsTimestamps.size() > 0) {
            dealWithTimestamp(sigAndRefsTimestampsNode, signatureTimestampValidationData, sigAndRefsTimestamps);
        }

        final XmlNode timestampsNode = signatureNode.addChild("Timestamps");
        final List<XmlDom> timestamps = signature.getElements("./Timestamps/Timestamp[@Type='%s']", TimestampType.SIGNATURE_TIMESTAMP);
        if (timestamps.size() > 0) {
            dealWithTimestamp(timestampsNode, signatureTimestampValidationData, timestamps);
        }

        final PastSignatureValidation pastSignatureValidation = new PastSignatureValidation();
        final PastSignatureValidationConclusion psvConclusion = pastSignatureValidation.run(params, signature, adestSignatureConclusion, NodeName.MAIN_SIGNATURE);

        signatureNode.addChild(psvConclusion.getValidationData());

        constraintNode = addConstraint(signatureNode, MessageTag.PSV_IPSVC);

        if (!Indication.VALID.equals(psvConclusion.getIndication())) {
            constraintNode.addChild(NodeName.STATUS, NodeValue.KO);
            constraintNode.addChild(NodeName.INFO, psvConclusion.getIndication()).setAttribute(AttributeName.FIELD, NodeName.INDICATION);
            constraintNode.addChild(NodeName.INFO, psvConclusion.getSubIndication()).setAttribute(AttributeName.FIELD, NodeName.SUB_INDICATION);
            psvConclusion.infoToXmlNode(constraintNode);

            conclusionNode.addChild(NodeName.INDICATION, psvConclusion.getIndication());
            conclusionNode.addChild(NodeName.SUB_INDICATION, psvConclusion.getSubIndication());
            psvConclusion.infoToXmlNode(conclusionNode);
            return false;
        }

        constraintNode.addChild(NodeName.STATUS, NodeValue.OK);

        return true;
    }

    private void dealWithTimestamp(final XmlNode processNode, final XmlDom signatureTimestampValidationData, final List<XmlDom> timestamps) throws DSSException {

        Collections.sort(timestamps, new TimestampComparator());
        for (final XmlDom timestamp : timestamps) {

            final String timestampId = timestamp.getValue("./@Id");
            try {

                XmlNode constraintNode = addConstraint(processNode, MessageTag.ADEST_IMIVC);

                final boolean messageImprintDataIntact = timestamp.getBoolValue(ValidationXPathQueryHolder.XP_MESSAGE_IMPRINT_DATA_INTACT);
                if (!messageImprintDataIntact) {

                    constraintNode.addChild(NodeName.STATUS, NodeValue.KO);
                    XmlNode xmlNode = conclusionNode.addChild(NodeName.INFO, MessageTag.ADEST_IMIVC_ANS.getMessage());
                    xmlNode.setAttribute("Id", timestampId);
                    continue;
                }
                constraintNode.addChild(NodeName.STATUS, NodeValue.OK);

                final XmlDom timestampConclusion = signatureTimestampValidationData.getElement("./Timestamp[@Id='%s']/BasicBuildingBlocks/Conclusion", timestampId);
                final String timestampIndication = timestampConclusion.getValue("./Indication/text()");

                if (Indication.VALID.equals(timestampIndication)) {
                    processNode.addChild("POEExtraction", NodeValue.OK);
                    extractPOEs(timestamp);
                } else {

                    final PastSignatureValidation psvp = new PastSignatureValidation();
                    final PastSignatureValidationConclusion psvConclusion = psvp.run(params, timestamp, timestampConclusion, NodeName.TIMESTAMP);

                    processNode.addChild(psvConclusion.getValidationData());

                    if (Indication.VALID.equals(psvConclusion.getIndication())) {

                        final boolean couldExtract = extractPOEs(timestamp);
                        if (couldExtract) {

                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                throw new DSSException("Error for timestamp: id: " + timestampId, e);
            }
        }
    }

    /**
     * @param timestamp
     * @return
     * @throws eu.europa.esig.dss.DSSException
     */
    private boolean extractPOEs(final XmlDom timestamp) throws DSSException {
        final String digestAlgorithm = RuleUtils.canonicalizeDigestAlgo(timestamp.getValue("./SignedDataDigestAlgo/text()"));
        final Date algorithmExpirationDate = params.getCurrentValidationPolicy().getAlgorithmExpirationDate(digestAlgorithm);
        final Date timestampProductionTime = timestamp.getTimeValue("./ProductionTime/text()");
        if ((algorithmExpirationDate == null) || timestampProductionTime.before(algorithmExpirationDate)) {

            poe.addPOE(timestamp, params.getCertPool());
            return true;
        }
        return false;
    }

    private XmlNode addConstraint(final XmlNode parentNode, final MessageTag messageTag) {
        final XmlNode constraintNode = parentNode.addChild(NodeName.CONSTRAINT);
        constraintNode.addChild(NodeName.NAME, messageTag.getMessage()).setAttribute(AttributeName.NAME_ID, messageTag.name());
        return constraintNode;
    }

}
