<!--# WSDL of SOAP interface-->

See also [Interfaces](/siva/v2/interfaces) for more information about the SOAP interfaces.

## Validation web service wsdl

```xml
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xrd="http://x-road.eu/xsd/xroad.xsd" targetNamespace="http://soap.webapp.siva.openeid.ee/"
                  xmlns:external="http://dss.esig.europa.eu/validation/detailed-report"
                  name="SignatureValidationService">
    <wsdl:types>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                   targetNamespace="http://soap.webapp.siva.openeid.ee/">
            <xs:import namespace="http://x-road.eu/xsd/xroad.xsd" schemaLocation="xroad.xsd"/>
            <xs:import namespace="http://dss.esig.europa.eu/validation/detailed-report"
                       schemaLocation="DetailedReport.xsd"/>
            <xs:element name="ValidateDocument" type="tns:ValidateDocument"/>
            <xs:element name="ValidationRequest" type="tns:SoapValidationRequest"/>
            <xs:element name="ValidateDocumentResponse" type="tns:ValidateDocumentResponse"/>
            <xs:element name="ValidationReport" type="tns:ValidationReport"/>
            <xs:element name="ValidationProcess" substitutionGroup="external:DetailedReport"/>
            <xs:element name="ValidationConclusion" type="tns:ValidationConclusion"/>

            <xs:complexType name="ValidateDocument">
                <xs:sequence>
                    <xs:element ref="tns:ValidationRequest"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="SoapValidationRequest">
                <xs:sequence>
                    <xs:element name="Document" type="xs:string"/>
                    <xs:element name="Filename" type="xs:string"/>
                    <xs:element minOccurs="0" maxOccurs="1" name="ReportType" type="xs:string"/>
                    <xs:element minOccurs="0" maxOccurs="1" name="DocumentType" type="tns:DocumentType"/>
                    <xs:element minOccurs="0" name="SignaturePolicy" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="ValidateDocumentResponse">
                <xs:sequence>
                    <xs:element minOccurs="1" ref="tns:ValidationReport"/>
                    <xs:element minOccurs="0" name="ValidationReportSignature" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="ValidationReport">
                <xs:sequence>
                    <xs:element minOccurs="1" ref="tns:ValidationConclusion"/>
                    <xs:element minOccurs="0" ref="tns:ValidationProcess"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="ValidationConclusion">
                <xs:sequence>

                    <xs:element name="Policy" type="tns:Policy"/>
                    <xs:element name="ValidationTime" type="xs:string"/>
                    <xs:element name="ValidatedDocument" type="tns:ValidatedDocumentData"/>
                    <xs:element name="ValidationLevel" minOccurs="0" type="xs:string"/>
                    <xs:element name="ValidationWarnings" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="ValidationWarning"
                                            type="tns:ValidationWarning"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="SignatureForm" minOccurs="0" type="xs:string"/>
                    <xs:element name="Signatures" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="Signature"
                                            type="tns:SignatureValidationData"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="TimeStampTokens" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element minOccurs="0" maxOccurs="unbounded" name="TimeStampToken"
                                            type="tns:TimeStampTokenData"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="ValidSignaturesCount" minOccurs="0" type="xs:int"/>
                    <xs:element name="SignaturesCount" minOccurs="0" type="xs:int"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="Policy">
                <xs:sequence>
                    <xs:element name="PolicyDescription" type="xs:string"/>
                    <xs:element name="PolicyName" type="xs:string"/>
                    <xs:element name="PolicyUrl" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="TimeStampTokenData">
                <xs:sequence>
                    <xs:element name="Indication" type="tns:Indication"/>
                    <xs:element name="SignedBy" type="xs:string"/>
                    <xs:element name="SignedTime" type="xs:string"/>
                    <xs:element name="Errors" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="Error" type="tns:Error"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="ValidatedDocumentData">
                <xs:sequence>
                    <xs:element name="Filename" type="xs:string"/>
                    <xs:element name="FileHashInHex" minOccurs="0" type="xs:string"/>
                    <xs:element name="HashAlgo" minOccurs="0" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="SignatureValidationData">
                <xs:sequence>
                    <xs:element name="Id" type="xs:string"/>
                    <xs:element name="SignatureFormat" type="xs:string"/>
                    <xs:element name="SignatureLevel" minOccurs="0" type="xs:string"/>
                    <xs:element name="SignedBy" type="xs:string"/>
                    <xs:element name="Indication" type="tns:Indication"/>
                    <xs:element name="SubIndication" minOccurs="0" type="xs:string"/>
                    <xs:element name="Errors" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="Error" type="tns:Error"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="SignatureScopes" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" name="SignatureScope" type="tns:SignatureScope"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="ClaimedSigningTime" type="xs:string"/>
                    <xs:element name="Warnings" minOccurs="0">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="Warning" type="tns:Warning"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element name="Info" minOccurs="0" type="tns:Info"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="Error">
                <xs:sequence>
                    <xs:element name="Content" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="ValidationWarning">
                <xs:sequence>
                    <xs:element name="Content" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="SignatureScope">
                <xs:sequence>
                    <xs:element name="Name" minOccurs="0" type="xs:string"/>
                    <xs:element name="Scope" minOccurs="0" type="xs:string"/>
                    <xs:element name="Content" minOccurs="0" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="Warning">
                <xs:sequence>
                    <xs:element name="Content" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="Info">
                <xs:sequence>
                    <xs:element minOccurs="0" name="BestSignatureTime" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>
            <xs:simpleType name="DocumentType">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="XROAD"/>
                </xs:restriction>
            </xs:simpleType>
            <xs:simpleType name="Indication">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="TOTAL-PASSED"/>
                    <xs:enumeration value="TOTAL-FAILED"/>
                    <xs:enumeration value="INDETERMINATE"/>
                </xs:restriction>
            </xs:simpleType>
        </xs:schema>
    </wsdl:types>
    <wsdl:message name="ValidateDocument">
        <wsdl:part element="tns:ValidateDocument" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="ValidateDocumentResponse">
        <wsdl:part element="tns:ValidateDocumentResponse" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="requestHeader">
        <wsdl:part name="client" element="xrd:client"/>
        <wsdl:part name="service" element="xrd:service"/>
        <wsdl:part name="id" element="xrd:id"/>
        <wsdl:part name="userId" element="xrd:userId"/>
        <wsdl:part name="issue" element="xrd:issue"/>
        <wsdl:part name="protocolVersion" element="xrd:protocolVersion"/>
    </wsdl:message>
    <wsdl:portType name="ValidationWebService">
        <wsdl:operation name="ValidateDocument">
            <wsdl:input message="tns:ValidateDocument" name="ValidateDocument"/>
            <wsdl:output message="tns:ValidateDocumentResponse" name="ValidateDocumentResponse"/>
        </wsdl:operation>
    </wsdl:portType>
    <wsdl:binding name="SignatureValidationServiceSoapBinding" type="tns:ValidationWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="ValidateDocument">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="ValidateDocument">
                <soap:body use="literal"/>
                <soap:header message="tns:requestHeader" part="client" use="literal"/>
                <soap:header message="tns:requestHeader" part="service" use="literal"/>
                <soap:header message="tns:requestHeader" part="id" use="literal"/>
                <soap:header message="tns:requestHeader" part="userId" use="literal"/>
                <soap:header message="tns:requestHeader" part="issue" use="literal"/>
                <soap:header message="tns:requestHeader" part="protocolVersion" use="literal"/>
            </wsdl:input>
            <wsdl:output name="ValidateDocumentResponse">
                <soap:body use="literal"/>
                <soap:header message="tns:requestHeader" part="client" use="literal"/>
                <soap:header message="tns:requestHeader" part="service" use="literal"/>
                <soap:header message="tns:requestHeader" part="id" use="literal"/>
                <soap:header message="tns:requestHeader" part="userId" use="literal"/>
                <soap:header message="tns:requestHeader" part="issue" use="literal"/>
                <soap:header message="tns:requestHeader" part="protocolVersion" use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    <wsdl:service name="SignatureValidationService">
        <wsdl:port binding="tns:SignatureValidationServiceSoapBinding" name="ValidationWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/validationWebService"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>

```

## Data files web service wsdl

```xml
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xrd="http://x-road.eu/xsd/xroad.xsd" targetNamespace="http://soap.webapp.siva.openeid.ee/"
                  name="DataFilesService">
    <wsdl:types>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                   targetNamespace="http://soap.webapp.siva.openeid.ee/">
            <xs:import namespace="http://x-road.eu/xsd/xroad.xsd" schemaLocation="xroad.xsd"/>
            <xs:element name="GetDocumentDataFiles" type="tns:GetDocumentDataFiles"/>
            <xs:element name="GetDocumentDataFilesResponse" type="tns:GetDocumentDataFilesResponse"/>
            <xs:element name="DataFilesReport" type="tns:DataFilesReport"/>
            <xs:element name="DataFilesRequest" type="tns:SoapDataFilesRequest"/>
            <xs:complexType name="GetDocumentDataFiles">
                <xs:sequence>
                    <xs:element ref="tns:DataFilesRequest"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="SoapDataFilesRequest">
                <xs:sequence>
                    <xs:element name="Document" type="xs:string"/>
                    <xs:element minOccurs="0" maxOccurs="1" name="Filename" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="GetDocumentDataFilesResponse">
                <xs:sequence>
                    <xs:element minOccurs="0" ref="tns:DataFilesReport"/>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="DataFilesReport">
                <xs:sequence>
                    <xs:element name="DataFiles">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" minOccurs="0" name="DataFile" type="tns:DataFile"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                </xs:sequence>
            </xs:complexType>
            <xs:complexType name="DataFile">
                <xs:sequence>
                    <xs:element name="Base64" type="xs:string"/>
                    <xs:element name="Filename" type="xs:string"/>
                    <xs:element name="MimeType" type="xs:string"/>
                    <xs:element name="Size" type="xs:long"/>
                </xs:sequence>
            </xs:complexType>
            <xs:simpleType name="DocumentType">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="PDF"/>
                    <xs:enumeration value="XROAD"/>
                    <xs:enumeration value="BDOC"/>
                    <xs:enumeration value="DDOC"/>
                </xs:restriction>
            </xs:simpleType>
        </xs:schema>
    </wsdl:types>
    <wsdl:message name="GetDocumentDataFiles">
        <wsdl:part element="tns:GetDocumentDataFiles" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="GetDocumentDataFilesResponse">
        <wsdl:part element="tns:GetDocumentDataFilesResponse" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="requestHeader">
        <wsdl:part name="client" element="xrd:client"/>
        <wsdl:part name="service" element="xrd:service"/>
        <wsdl:part name="id" element="xrd:id"/>
        <wsdl:part name="userId" element="xrd:userId"/>
        <wsdl:part name="issue" element="xrd:issue"/>
        <wsdl:part name="protocolVersion" element="xrd:protocolVersion"/>
    </wsdl:message>
    <wsdl:portType name="DataFilesWebService">
        <wsdl:operation name="GetDocumentDataFiles">
            <wsdl:input message="tns:GetDocumentDataFiles" name="GetDocumentDataFiles"/>
            <wsdl:output message="tns:GetDocumentDataFilesResponse" name="GetDocumentDataFilesResponse"/>
        </wsdl:operation>
    </wsdl:portType>
    <wsdl:binding name="DataFilesServiceSoapBinding" type="tns:DataFilesWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="GetDocumentDataFiles">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="GetDocumentDataFiles">
                <soap:body use="literal"/>
                <soap:header message="tns:requestHeader" part="client" use="literal"/>
                <soap:header message="tns:requestHeader" part="service" use="literal"/>
                <soap:header message="tns:requestHeader" part="id" use="literal"/>
                <soap:header message="tns:requestHeader" part="userId" use="literal"/>
                <soap:header message="tns:requestHeader" part="issue" use="literal"/>
                <soap:header message="tns:requestHeader" part="protocolVersion" use="literal"/>
            </wsdl:input>
            <wsdl:output name="GetDocumentDataFilesResponse">
                <soap:body use="literal"/>
                <soap:header message="tns:requestHeader" part="client" use="literal"/>
                <soap:header message="tns:requestHeader" part="service" use="literal"/>
                <soap:header message="tns:requestHeader" part="id" use="literal"/>
                <soap:header message="tns:requestHeader" part="userId" use="literal"/>
                <soap:header message="tns:requestHeader" part="issue" use="literal"/>
                <soap:header message="tns:requestHeader" part="protocolVersion" use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    <wsdl:service name="DataFilesWebService">
        <wsdl:port binding="tns:DataFilesServiceSoapBinding" name="DataFilesWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/dataFilesWebService"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>
```
## DSS detailed report XSD

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified"
		   targetNamespace="http://dss.esig.europa.eu/validation/detailed-report"
		   xmlns="http://dss.esig.europa.eu/validation/detailed-report"
		   xmlns:xs="http://www.w3.org/2001/XMLSchema">

	<xs:element name="DetailedReport">
		<xs:complexType>
			<xs:sequence>
				<xs:element type="Signature" name="Signatures" minOccurs="0" maxOccurs="unbounded" />
				<xs:element type="BasicBuildingBlocks" name="BasicBuildingBlocks" minOccurs="0" maxOccurs="unbounded" />
				<xs:element type="QMatrixBlock" name="QMatrixBlock" minOccurs="0" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>

	<xs:complexType name="Signature">
		<xs:sequence>
			<xs:element type="ValidationProcessBasicSignatures" name="ValidationProcessBasicSignatures" minOccurs="0" />
			<xs:element type="ValidationProcessTimestamps" name="ValidationProcessTimestamps" minOccurs="0" maxOccurs="unbounded" />
			<xs:element type="ValidationProcessLongTermData" name="ValidationProcessLongTermData" minOccurs="0" />
			<xs:element type="ValidationProcessArchivalData" name="ValidationProcessArchivalData" minOccurs="0" />
		</xs:sequence>
		<xs:attribute name="Id" type="xs:string" use="required" />
		<xs:attribute name="Type" type="xs:string" use="optional" />
	</xs:complexType>

	<xs:complexType name="BasicBuildingBlocks">
		<xs:sequence>
			<xs:element type="FC" name="FC" minOccurs="0" />
			<xs:element type="ISC" name="ISC" minOccurs="0" />
			<xs:element type="VCI" name="VCI" minOccurs="0" />
			<xs:element type="CV"  name="CV"  minOccurs="0" />
			<xs:element type="SAV" name="SAV" minOccurs="0" />
			<xs:element type="XCV" name="XCV" minOccurs="0" />
			<xs:element type="PSV" name="PSV" minOccurs="0" />
			<xs:element type="PCV" name="PCV" minOccurs="0" />
			<xs:element type="VTS" name="VTS" minOccurs="0" />
			<xs:element type="Conclusion" name="Conclusion" />
		</xs:sequence>
		<xs:attribute name="Id" type="xs:string" use="required" />
		<xs:attribute name="Type" type="xs:string" use="required" />
	</xs:complexType>

	<xs:complexType name="QMatrixBlock">
		<xs:sequence>
			<xs:element type="TLAnalysis" name="TLAnalysis" minOccurs="0" maxOccurs="unbounded" />
			<xs:element type="SignatureAnalysis" name="SignatureAnalysis" minOccurs="0" maxOccurs="unbounded" />
		</xs:sequence>
	</xs:complexType>

	<xs:complexType name="TLAnalysis">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:attribute name="CountryCode" type="xs:string" use="required" />
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="SignatureAnalysis">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:attribute name="Id" type="xs:string" use="required" />
				<xs:attribute name="SignatureQualification" type="SignatureQualification" use="optional" />
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="ConstraintsConclusion">
		<xs:sequence>
			<xs:element type="Constraint" name="Constraint" minOccurs="0" maxOccurs="unbounded" />
			<xs:element type="Conclusion" name="Conclusion" />
		</xs:sequence>
	</xs:complexType>

	<xs:complexType name="ValidationProcessBasicSignatures">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="ValidationProcessTimestamps">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:attribute name="Id" type="xs:string" use="required" />
				<xs:attribute name="Type" type="xs:string" use="required" />
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="ValidationProcessLongTermData">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="ValidationProcessArchivalData">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="FC">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="ISC">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="VCI">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="RFC">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="CV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="SAV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="XCV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:sequence>
					<xs:element name="SubXCV" type="SubXCV" minOccurs="0" maxOccurs="unbounded" />
				</xs:sequence>
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="SubXCV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:sequence>
					<xs:element name="RFC" type="RFC" minOccurs="0" />
				</xs:sequence>
				<xs:attribute name="Id" type="xs:string" use="required" />
				<xs:attribute name="TrustAnchor" type="xs:boolean" />
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="VTS">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:sequence>
					<xs:element name="ControlTime" type="xs:dateTime" />
				</xs:sequence>
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="PCV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion">
				<xs:sequence>
					<xs:element name="ControlTime" type="xs:dateTime" minOccurs="0" />
				</xs:sequence>
			</xs:extension>
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="PSV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>
	<xs:complexType name="ERV">
		<xs:complexContent>
			<xs:extension base="ConstraintsConclusion" />
		</xs:complexContent>
	</xs:complexType>

	<xs:complexType name="Constraint">
		<xs:sequence>
			<xs:element type="Name" name="Name" />
			<xs:element type="Status" name="Status" />
			<xs:element type="Name" name="Error" minOccurs="0" />
			<xs:element type="Name" name="Warning" minOccurs="0" />
			<xs:element type="Name" name="Info" minOccurs="0" />
			<xs:element type="xs:string" name="AdditionalInfo" minOccurs="0" />
		</xs:sequence>
		<xs:attribute name="Id" type="xs:string" use="optional" /> <!-- In case of constraint with a BBB -->
	</xs:complexType>

	<xs:complexType name="Conclusion">
		<xs:sequence>
			<xs:element type="Indication" name="Indication" />
			<xs:element type="SubIndication" name="SubIndication" minOccurs="0" />
			<xs:element type="Name" name="Errors" minOccurs="0" maxOccurs="unbounded" />
			<xs:element type="Name" name="Warnings" minOccurs="0" maxOccurs="unbounded" />
			<xs:element type="Name" name="Infos" minOccurs="0" maxOccurs="unbounded" />
		</xs:sequence>
	</xs:complexType>

	<xs:complexType name="Name">
		<xs:simpleContent>
			<xs:extension base="xs:string">
				<xs:attribute name="NameId" type="xs:string" />
			</xs:extension>
		</xs:simpleContent>
	</xs:complexType>

	<xs:simpleType name="Status" final="restriction">
		<xs:restriction base="xs:string">
			<xs:enumeration value="OK" />
			<xs:enumeration value="NOT OK" />
			<xs:enumeration value="IGNORED" />
			<xs:enumeration value="INFORMATION" />
			<xs:enumeration value="WARNING" />
		</xs:restriction>
	</xs:simpleType>

	<xs:simpleType name="Indication" final="restriction">
		<xs:restriction base="xs:string">
			<xs:enumeration value="PASSED" />
			<xs:enumeration value="INDETERMINATE" />
			<xs:enumeration value="FAILED" />
		</xs:restriction>
	</xs:simpleType>

	<xs:simpleType name="SubIndication" final="restriction">
		<xs:restriction base="xs:string">
			<xs:enumeration value="NO_SIGNING_CERTIFICATE_FOUND" />
			<xs:enumeration value="FORMAT_FAILURE" />
			<xs:enumeration value="SIGNATURE_POLICY_NOT_AVAILABLE" />
			<xs:enumeration value="POLICY_PROCESSING_ERROR" />
			<xs:enumeration value="OUT_OF_BOUNDS_NO_POE" />
			<xs:enumeration value="NO_CERTIFICATE_CHAIN_FOUND" />
			<xs:enumeration value="TRY_LATER" />
			<xs:enumeration value="REVOKED_NO_POE" />
			<xs:enumeration value="REVOKED_CA_NO_POE" />
			<xs:enumeration value="CHAIN_CONSTRAINTS_FAILURE" />
			<xs:enumeration value="CRYPTO_CONSTRAINTS_FAILURE" />
			<xs:enumeration value="CRYPTO_CONSTRAINTS_FAILURE_NO_POE" />
			<xs:enumeration value="SIGNED_DATA_NOT_FOUND" />
			<xs:enumeration value="HASH_FAILURE" />
			<xs:enumeration value="SIG_CRYPTO_FAILURE" />
			<xs:enumeration value="SIG_CONSTRAINTS_FAILURE" />
			<xs:enumeration value="NOT_YET_VALID" />
			<xs:enumeration value="TIMESTAMP_ORDER_FAILURE" />
			<xs:enumeration value="REVOKED" />
			<xs:enumeration value="EXPIRED" />
			<xs:enumeration value="NO_POE" />
			<xs:enumeration value="CERTIFICATE_CHAIN_GENERAL_FAILURE" />
			<xs:enumeration value="UNEXPECTED_ERROR" />
		</xs:restriction>
	</xs:simpleType>

	<xs:simpleType name="SignatureQualification" final="restriction">
		<xs:restriction base="xs:string">

			<xs:enumeration value="QESig" />
			<xs:enumeration value="QESeal" />
			<xs:enumeration value="QES?" />
			<xs:enumeration value="AdESig-QC" />
			<xs:enumeration value="AdESeal-QC" />
			<xs:enumeration value="AdES?-QC" />
			<xs:enumeration value="AdESig" />
			<xs:enumeration value="AdESeal" />
			<xs:enumeration value="AdES?" />

			<xs:enumeration value="Indeterminate QESig" />
			<xs:enumeration value="Indeterminate QESeal" />
			<xs:enumeration value="Indeterminate QES?" />
			<xs:enumeration value="Indeterminate AdESig-QC" />
			<xs:enumeration value="Indeterminate AdESeal-QC" />
			<xs:enumeration value="Indeterminate AdES?-QC" />
			<xs:enumeration value="Indeterminate AdESig" />
			<xs:enumeration value="Indeterminate AdESeal" />
			<xs:enumeration value="Indeterminate AdES?" />

			<xs:enumeration value="Not AdES but QC with QSCD" />
			<xs:enumeration value="Not AdES but QC" />
			<xs:enumeration value="Not AdES" />

			<xs:enumeration value="N/A" />
		</xs:restriction>
	</xs:simpleType>

</xs:schema>

```
