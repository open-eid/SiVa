<!--# WSDL of SOAP interface-->

See also [Interfaces](/siva3/interfaces) for more information about the SOAP interfaces.

## Validation web service wsdl

```xml
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xrd="http://x-road.eu/xsd/xroad.xsd" targetNamespace="http://soap.webapp.siva.openeid.ee/"
                  xmlns:response="http://soap.webapp.siva.openeid.ee/response/"
                  name="SignatureValidationService">
    <wsdl:types>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                   targetNamespace="http://soap.webapp.siva.openeid.ee/">
            <xs:import namespace="http://x-road.eu/xsd/xroad.xsd" schemaLocation="xroad.xsd"/>
            <xs:import namespace="http://soap.webapp.siva.openeid.ee/response/" schemaLocation="ValidationResponse.xsd"/>

            <xs:element name="ValidateDocument" type="tns:ValidateDocument"/>
            <xs:element name="ValidationRequest" type="tns:SoapValidationRequest"/>
            <xs:element name="ValidateDocumentResponse" type="response:ValidateDocumentResponse"/>

            <xs:complexType name="ValidateDocument">
                <xs:sequence>
                    <xs:element ref="tns:ValidationRequest"/>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="SoapValidationRequest">
                <xs:sequence>
                    <xs:element name="Document" type="xs:string"/>
                    <xs:element name="Filename" type="xs:string"/>
                    <xs:element minOccurs="0" name="ReportType" type="xs:string"/>
                    <xs:element minOccurs="0" name="DocumentType" type="tns:DocumentType"/>
                    <xs:element minOccurs="0" name="SignaturePolicy" type="xs:string"/>
                </xs:sequence>
            </xs:complexType>

            <xs:simpleType name="DocumentType">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="XROAD"/>
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

    <wsdl:binding name="ValidationWebServiceSoapBinding" type="tns:ValidationWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="ValidateDocument">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="ValidateDocument">
                <soap:body use="literal"/>
            </wsdl:input>
            <wsdl:output name="ValidateDocumentResponse">
                <soap:body use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>

    <wsdl:binding name="XRoadValidationWebServiceSoapBinding" type="tns:ValidationWebService">
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

    <wsdl:service name="ValidationWebServiceService">
        <wsdl:port binding="tns:ValidationWebServiceSoapBinding" name="ValidationWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/validationWebService"/>
        </wsdl:port>
    </wsdl:service>

    <wsdl:service name="XRoadValidationWebService">
        <wsdl:port binding="tns:XRoadValidationWebServiceSoapBinding" name="XRoadValidationWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/validationWebService"/>
        </wsdl:port>
    </wsdl:service>

</wsdl:definitions>
```

## Hashcode validation web service wsdl

```xml
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema"
                  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xrd="http://x-road.eu/xsd/xroad.xsd"
                  xmlns:response="http://soap.webapp.siva.openeid.ee/response/"
                  targetNamespace="http://soap.webapp.siva.openeid.ee/"
                  name="SignatureHashcodeValidationService">

    <wsdl:types>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                   xmlns:tns="http://soap.webapp.siva.openeid.ee/"
                   targetNamespace="http://soap.webapp.siva.openeid.ee/">

            <xs:import namespace="http://x-road.eu/xsd/xroad.xsd" schemaLocation="xroad.xsd"/>
            <xs:import namespace="http://soap.webapp.siva.openeid.ee/response/" schemaLocation="ValidationResponse.xsd"/>

            <xs:element name="HashcodeValidationDocument" type="tns:HashcodeValidationDocument"/>
            <xs:element name="HashcodeValidationRequest" type="tns:SoapHashcodeValidationRequest"/>
            <xs:element name="HashcodeValidationResponse" type="response:ValidateDocumentResponse"/>

            <xs:complexType name="HashcodeValidationDocument">
                <xs:sequence>
                    <xs:element ref="tns:HashcodeValidationRequest"/>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="SoapHashcodeValidationRequest">
                <xs:sequence>
                    <xs:element name="SignatureFiles">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" name="SignatureFile" type="tns:SignatureFile"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                    <xs:element minOccurs="0" name="ReportType" type="tns:ReportType" default="SIMPLE"/>
                    <xs:element minOccurs="0" name="SignaturePolicy" type="tns:SignaturePolicy" default="POLv4"/>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="SignatureFile">
                <xs:sequence>
                    <xs:element name="Signature" type="tns:NotEmptyString"/>
                    <xs:element minOccurs="0" name="DataFiles">
                        <xs:complexType>
                            <xs:sequence>
                                <xs:element maxOccurs="unbounded" name="DataFile" type="tns:HashDataFile"/>
                            </xs:sequence>
                        </xs:complexType>
                    </xs:element>
                </xs:sequence>
            </xs:complexType>

            <xs:complexType name="HashDataFile">
                <xs:sequence>
                    <xs:element name="Filename" type="tns:Filename"/>
                    <xs:element name="HashAlgo" type="tns:HashAlgorithm"/>
                    <xs:element name="Hash" type="tns:NotEmptyString"/>
                </xs:sequence>
            </xs:complexType>

            <xs:simpleType name="NotEmptyString">
                <xs:restriction base="xs:string">
                    <xs:minLength value="1"/>
                    <xs:pattern value="\S+"/>
                </xs:restriction>
            </xs:simpleType>

            <xs:simpleType name="Filename">
                <xs:restriction base="xs:string">
                    <xs:minLength value="1"/>
                    <xs:maxLength value="260"/>
                </xs:restriction>
            </xs:simpleType>

            <xs:simpleType name="ReportType">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="SIMPLE" />
                    <xs:enumeration value="DETAILED" />
                    <xs:enumeration value="DIAGNOSTIC" />
                </xs:restriction>
            </xs:simpleType>

            <xs:simpleType name="SignaturePolicy">
                <xs:restriction base="xs:string">
                    <xs:minLength value="1"/>
                    <xs:maxLength value="100"/>
                    <xs:pattern value="[A-Za-z0-9_ -]*"/>
                </xs:restriction>
            </xs:simpleType>

            <xs:simpleType name="HashAlgorithm">
                <xs:restriction base="xs:string">
                    <xs:enumeration value="SHA1" />
                    <xs:enumeration value="SHA224" />
                    <xs:enumeration value="SHA256" />
                    <xs:enumeration value="SHA384" />
                    <xs:enumeration value="SHA512" />
                    <xs:enumeration value="RIPEMD160" />
                    <xs:enumeration value="MD2" />
                    <xs:enumeration value="MD5" />
                </xs:restriction>
            </xs:simpleType>
        </xs:schema>
    </wsdl:types>

    <wsdl:message name="HashcodeValidationDocument">
        <wsdl:part element="tns:HashcodeValidationDocument" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="HashcodeValidationResponse">
        <wsdl:part element="tns:HashcodeValidationResponse" name="parameters"/>
    </wsdl:message>

    <wsdl:message name="requestHeader">
        <wsdl:part name="client" element="xrd:client"/>
        <wsdl:part name="service" element="xrd:service"/>
        <wsdl:part name="id" element="xrd:id"/>
        <wsdl:part name="userId" element="xrd:userId"/>
        <wsdl:part name="issue" element="xrd:issue"/>
        <wsdl:part name="protocolVersion" element="xrd:protocolVersion"/>
    </wsdl:message>

    <wsdl:portType name="HashcodeValidationWebService">
        <wsdl:operation name="HashcodeValidationDocument">
            <wsdl:input message="tns:HashcodeValidationDocument" name="HashcodeValidationDocument"/>
            <wsdl:output message="tns:HashcodeValidationResponse" name="HashcodeValidationResponse"/>
        </wsdl:operation>
    </wsdl:portType>

    <wsdl:binding name="HashcodeValidationWebServiceSoapBinding" type="tns:HashcodeValidationWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="HashcodeValidationDocument">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="HashcodeValidationDocument">
                <soap:body use="literal"/>
            </wsdl:input>
            <wsdl:output name="HashcodeValidationResponse">
                <soap:body use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>

    <wsdl:binding name="XRoadHashcodeValidationWebServiceSoapBinding" type="tns:HashcodeValidationWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="HashcodeValidationDocument">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="HashcodeValidationDocument">
                <soap:body use="literal"/>
                <soap:header message="tns:requestHeader" part="client" use="literal"/>
                <soap:header message="tns:requestHeader" part="service" use="literal"/>
                <soap:header message="tns:requestHeader" part="id" use="literal"/>
                <soap:header message="tns:requestHeader" part="userId" use="literal"/>
                <soap:header message="tns:requestHeader" part="issue" use="literal"/>
                <soap:header message="tns:requestHeader" part="protocolVersion" use="literal"/>
            </wsdl:input>
            <wsdl:output name="HashcodeValidationResponse">
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

    <wsdl:service name="HashcodeValidationWebServiceService">
        <wsdl:port binding="tns:HashcodeValidationWebServiceSoapBinding" name="HashcodeValidationWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/hashcodeValidationWebService"/>
        </wsdl:port>
    </wsdl:service>

    <wsdl:service name="XRoadHashcodeValidationWebService">
        <wsdl:port binding="tns:XRoadHashcodeValidationWebServiceSoapBinding" name="XRoadHashcodeValidationWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/hashcodeValidationWebService"/>
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
                    <xs:element name="Filename" type="xs:string"/>
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

    <wsdl:binding name="DataFilesWebServiceSoapBinding" type="tns:DataFilesWebService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="GetDocumentDataFiles">
            <soap:operation soapAction="" style="document"/>
            <wsdl:input name="GetDocumentDataFiles">
                <soap:body use="literal"/>
            </wsdl:input>
            <wsdl:output name="GetDocumentDataFilesResponse">
                <soap:body use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>

    <wsdl:binding name="XRoadDataFilesWebServiceSoapBinding" type="tns:DataFilesWebService">
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

    <wsdl:service name="DataFilesWebServiceService">
        <wsdl:port binding="tns:DataFilesWebServiceSoapBinding" name="DataFilesWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/dataFilesWebService"/>
        </wsdl:port>
    </wsdl:service>

    <wsdl:service name="XRoadDataFilesWebService">
        <wsdl:port binding="tns:XRoadDataFilesWebServiceSoapBinding" name="XRoadDataFilesWebServiceImplPort">
            <soap:address location="http://localhost:8080/soap/dataFilesWebService"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>
```

## Validation response XSD

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema elementFormDefault="qualified"
           targetNamespace="http://soap.webapp.siva.openeid.ee/response/"
           xmlns="http://soap.webapp.siva.openeid.ee/response/"
           xmlns:tns="http://soap.webapp.siva.openeid.ee/response/"
           xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:detailed="http://dss.esig.europa.eu/validation/detailed-report"
           xmlns:diagnostic="http://dss.esig.europa.eu/validation/diagnostic">

    <xs:import namespace="http://dss.esig.europa.eu/validation/detailed-report" schemaLocation="DetailedReport.xsd"/>
    <xs:import namespace="http://dss.esig.europa.eu/validation/diagnostic" schemaLocation="DiagnosticData.xsd"/>

    <xs:element name="ValidationReport" type="tns:ValidationReport"/>

    <xs:element name="ValidationConclusion" type="tns:ValidationConclusion"/>
    <xs:element name="ValidationProcess" substitutionGroup="detailed:DetailedReport"/>
    <xs:element name="DiagnosticData" substitutionGroup="diagnostic:DiagnosticData"/>

    <xs:complexType name="ValidateDocumentResponse">
        <xs:sequence>
            <xs:element ref="tns:ValidationReport"/>
            <xs:element minOccurs="0" name="ValidationReportSignature" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="ValidationReport">
        <xs:sequence>
            <xs:element ref="tns:ValidationConclusion"/>
            <xs:element minOccurs="0" ref="tns:ValidationProcess"/>
            <xs:element minOccurs="0" ref="tns:DiagnosticData"/>
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

    <xs:complexType name="SignatureValidationData">
        <xs:sequence>
            <xs:element name="Id" type="xs:string"/>
            <xs:element name="SignatureFormat" type="xs:string"/>
            <xs:element name="SignatureLevel" minOccurs="0" type="xs:string"/>
            <xs:element name="SignedBy" type="xs:string"/>
            <xs:element name="SubjectDistinguishedName" minOccurs="0"  type="tns:SubjectDistinguishedName"/>
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
            <xs:element name="HashAlgo" minOccurs="0" type="xs:string"/>
            <xs:element name="Hash" minOccurs="0" type="xs:string"/>
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

    <xs:complexType name="SubjectDistinguishedName">
        <xs:sequence>
            <xs:element name="SerialNumber" type="xs:string"/>
            <xs:element name="CommonName" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>

    <xs:simpleType name="Indication">
        <xs:restriction base="xs:string">
            <xs:enumeration value="TOTAL-PASSED"/>
            <xs:enumeration value="TOTAL-FAILED"/>
            <xs:enumeration value="INDETERMINATE"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:complexType name="ValidatedDocumentData">
        <xs:sequence>
            <xs:element name="Filename" type="xs:string"/>
            <xs:element name="FileHash" minOccurs="0" type="xs:string"/>
            <xs:element name="HashAlgo" minOccurs="0" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
</xs:schema>
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
                <xs:element type="Signature" name="Signatures" minOccurs="0" maxOccurs="unbounded"/>
                <xs:element type="BasicBuildingBlocks" name="BasicBuildingBlocks" minOccurs="0" maxOccurs="unbounded"/>
                <xs:element type="TLAnalysis" name="TLAnalysis" minOccurs="0" maxOccurs="unbounded"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:complexType name="Signature">
        <xs:sequence>
            <xs:element type="ValidationProcessBasicSignatures" name="ValidationProcessBasicSignatures" minOccurs="0"/>
            <xs:element type="ValidationProcessTimestamps" name="ValidationProcessTimestamps" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element type="ValidationProcessLongTermData" name="ValidationProcessLongTermData" minOccurs="0"/>
            <xs:element type="ValidationProcessArchivalData" name="ValidationProcessArchivalData" minOccurs="0"/>
            <xs:element type="ValidationSignatureQualification" name="ValidationSignatureQualification" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required"/>
        <xs:attribute name="CounterSignature" type="xs:boolean" use="optional"/>
    </xs:complexType>

    <xs:complexType name="BasicBuildingBlocks">
        <xs:sequence>
            <xs:element type="FC" name="FC" minOccurs="0"/>
            <xs:element type="ISC" name="ISC" minOccurs="0"/>
            <xs:element type="VCI" name="VCI" minOccurs="0"/>
            <xs:element type="CV" name="CV" minOccurs="0"/>
            <xs:element type="SAV" name="SAV" minOccurs="0"/>
            <xs:element type="XCV" name="XCV" minOccurs="0"/>
            <xs:element type="PSV" name="PSV" minOccurs="0"/>
            <xs:element type="PCV" name="PCV" minOccurs="0"/>
            <xs:element type="VTS" name="VTS" minOccurs="0"/>
            <xs:element name="CertificateChain" type="CertificateChain"/>
            <xs:element type="Conclusion" name="Conclusion"/>
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required"/>
        <xs:attribute name="Type" type="Context" use="required"/>
    </xs:complexType>

    <xs:complexType name="TLAnalysis">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:attribute name="CountryCode" type="xs:string" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ValidationSignatureQualification">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:attribute name="Id" type="xs:string" use="required"/>
                <xs:attribute name="SignatureQualification" type="SignatureQualification" use="optional"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ConstraintsConclusion">
        <xs:sequence>
            <xs:element type="Constraint" name="Constraint" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element type="Conclusion" name="Conclusion"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="ValidationProcessBasicSignatures">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ValidationProcessTimestamps">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:attribute name="Id" type="xs:string" use="required"/>
                <xs:attribute name="Type" type="xs:string" use="required"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ValidationProcessLongTermData">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="ValidationProcessArchivalData">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="FC">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="ISC">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:sequence>
                    <xs:element name="CertificateChain" type="CertificateChain"/>
                </xs:sequence>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="VCI">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="RFC">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="CV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="SAV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="XCV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:sequence>
                    <xs:element name="SubXCV" type="SubXCV" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="SubXCV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:sequence>
                    <xs:element name="RFC" type="RFC" minOccurs="0"/>
                </xs:sequence>
                <xs:attribute name="Id" type="xs:string" use="required"/>
                <xs:attribute name="TrustAnchor" type="xs:boolean"/>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="VTS">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:sequence>
                    <xs:element name="ControlTime" type="xs:dateTime"/>
                </xs:sequence>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="PCV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion">
                <xs:sequence>
                    <xs:element name="ControlTime" type="xs:dateTime" minOccurs="0"/>
                </xs:sequence>
            </xs:extension>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="PSV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>
    <xs:complexType name="ERV">
        <xs:complexContent>
            <xs:extension base="ConstraintsConclusion"/>
        </xs:complexContent>
    </xs:complexType>

    <xs:complexType name="Constraint">
        <xs:sequence>
            <xs:element type="Name" name="Name"/>
            <xs:element type="Status" name="Status"/>
            <xs:element type="Name" name="Error" minOccurs="0"/>
            <xs:element type="Name" name="Warning" minOccurs="0"/>
            <xs:element type="Name" name="Info" minOccurs="0"/>
            <xs:element type="xs:string" name="AdditionalInfo" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="optional"/> <!-- In case of constraint with a BBB -->
    </xs:complexType>

    <xs:complexType name="Conclusion">
        <xs:sequence>
            <xs:element type="Indication" name="Indication"/>
            <xs:element type="SubIndication" name="SubIndication" minOccurs="0"/>
            <xs:element type="Name" name="Errors" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element type="Name" name="Warnings" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element type="Name" name="Infos" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="Name">
        <xs:simpleContent>
            <xs:extension base="xs:string">
                <xs:attribute name="NameId" type="xs:string"/>
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>

    <xs:simpleType name="Status" final="restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="OK"/>
            <xs:enumeration value="NOT OK"/>
            <xs:enumeration value="IGNORED"/>
            <xs:enumeration value="INFORMATION"/>
            <xs:enumeration value="WARNING"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="Indication" final="restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="PASSED"/>
            <xs:enumeration value="INDETERMINATE"/>
            <xs:enumeration value="FAILED"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="SubIndication" final="restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="NO_SIGNING_CERTIFICATE_FOUND"/>
            <xs:enumeration value="FORMAT_FAILURE"/>
            <xs:enumeration value="SIGNATURE_POLICY_NOT_AVAILABLE"/>
            <xs:enumeration value="POLICY_PROCESSING_ERROR"/>
            <xs:enumeration value="OUT_OF_BOUNDS_NO_POE"/>
            <xs:enumeration value="NO_CERTIFICATE_CHAIN_FOUND"/>
            <xs:enumeration value="TRY_LATER"/>
            <xs:enumeration value="REVOKED_NO_POE"/>
            <xs:enumeration value="REVOKED_CA_NO_POE"/>
            <xs:enumeration value="CHAIN_CONSTRAINTS_FAILURE"/>
            <xs:enumeration value="CRYPTO_CONSTRAINTS_FAILURE"/>
            <xs:enumeration value="CRYPTO_CONSTRAINTS_FAILURE_NO_POE"/>
            <xs:enumeration value="SIGNED_DATA_NOT_FOUND"/>
            <xs:enumeration value="HASH_FAILURE"/>
            <xs:enumeration value="SIG_CRYPTO_FAILURE"/>
            <xs:enumeration value="SIG_CONSTRAINTS_FAILURE"/>
            <xs:enumeration value="NOT_YET_VALID"/>
            <xs:enumeration value="TIMESTAMP_ORDER_FAILURE"/>
            <xs:enumeration value="REVOKED"/>
            <xs:enumeration value="EXPIRED"/>
            <xs:enumeration value="NO_POE"/>
            <xs:enumeration value="CERTIFICATE_CHAIN_GENERAL_FAILURE"/>
            <xs:enumeration value="UNEXPECTED_ERROR"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="SignatureQualification" final="restriction">
        <xs:restriction base="xs:string">

            <xs:enumeration value="QESig"/>
            <xs:enumeration value="QESeal"/>
            <xs:enumeration value="QES?"/>
            <xs:enumeration value="AdESig-QC"/>
            <xs:enumeration value="AdESeal-QC"/>
            <xs:enumeration value="AdES?-QC"/>
            <xs:enumeration value="AdESig"/>
            <xs:enumeration value="AdESeal"/>
            <xs:enumeration value="AdES?"/>

            <xs:enumeration value="Indeterminate QESig"/>
            <xs:enumeration value="Indeterminate QESeal"/>
            <xs:enumeration value="Indeterminate QES?"/>
            <xs:enumeration value="Indeterminate AdESig-QC"/>
            <xs:enumeration value="Indeterminate AdESeal-QC"/>
            <xs:enumeration value="Indeterminate AdES?-QC"/>
            <xs:enumeration value="Indeterminate AdESig"/>
            <xs:enumeration value="Indeterminate AdESeal"/>
            <xs:enumeration value="Indeterminate AdES?"/>

            <xs:enumeration value="Not AdES but QC with QSCD"/>
            <xs:enumeration value="Not AdES but QC"/>
            <xs:enumeration value="Not AdES"/>

            <xs:enumeration value="N/A"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="Context" final="restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="SIGNATURE"/>
            <xs:enumeration value="COUNTER_SIGNATURE"/>
            <xs:enumeration value="TIMESTAMP"/>
            <xs:enumeration value="REVOCATION"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:complexType name="CertificateChain">
        <xs:sequence>
            <xs:element name="ChainItem" minOccurs="0" maxOccurs="unbounded">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Source" type="xs:string"/>
                    </xs:sequence>
                    <xs:attribute name="Id" type="xs:string" use="required"/>
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

</xs:schema>
```

## DSS diagnostic data report XSD

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema elementFormDefault="qualified"
           targetNamespace="http://dss.esig.europa.eu/validation/diagnostic"
           xmlns="http://dss.esig.europa.eu/validation/diagnostic"
           xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xs:element name="DiagnosticData">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="DocumentName" type="xs:string" />
                <xs:element name="ValidationDate" type="xs:dateTime" />
                <xs:element name="ContainerInfo" type="ContainerInfo" minOccurs="0" />
                <xs:element name="Signatures" minOccurs="0">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="Signature" type="Signature" minOccurs="0" maxOccurs="unbounded" />
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
                <xs:element name="UsedCertificates" minOccurs="0">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="Certificate" type="Certificate" minOccurs="0" maxOccurs="unbounded" />
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>

                <xs:element name="TrustedLists" minOccurs="0">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="TrustedList" type="TrustedList" minOccurs="0" maxOccurs="unbounded" />
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
                <xs:element name="ListOfTrustedLists" type="TrustedList" minOccurs="0" />

            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:complexType name="ContainerInfo">
        <xs:sequence>
            <xs:element name="ContainerType" type="xs:string" minOccurs="0" />
            <xs:element name="ZipComment" type="xs:string" minOccurs="0" />
            <xs:element name="MimeTypeFilePresent" type="xs:boolean" minOccurs="0" />
            <xs:element name="MimeTypeContent" type="xs:string" minOccurs="0" />

            <xs:element name="ManifestFiles" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="ManifestFile" type="ManifestFile" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="ContentFiles" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="ContentFile" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="ManifestFile">
        <xs:sequence>
            <xs:element name="Filename" type="xs:string" minOccurs="0" />
            <xs:element name="SignatureFilename" type="xs:string" minOccurs="0" />
            <xs:element name="Entries" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Entry" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="Signature">
        <xs:sequence>
            <xs:element name="SignatureFilename" type="xs:string" />
            <xs:element name="ParentId" type="xs:string" minOccurs="0" />
            <xs:element name="ErrorMessage" type="xs:string" minOccurs="0" />
            <xs:element name="DateTime" type="xs:dateTime" minOccurs="0" />
            <xs:element name="SignatureFormat" type="xs:string" />

            <xs:element name="StructuralValidation"	type="StructuralValidation" minOccurs="0" />
            <xs:element name="BasicSignature" type="BasicSignature" />
            <xs:element name="SigningCertificate" type="SigningCertificate" minOccurs="0" />
            <xs:element name="CertificateChain"	type="CertificateChain" minOccurs="0" />

            <xs:element name="ContentType" type="xs:string" minOccurs="0" />
            <xs:element name="ContentIdentifier" type="xs:string" minOccurs="0" />
            <xs:element name="ContentHints" type="xs:string" minOccurs="0" />
            <xs:element name="SignatureProductionPlace" type="SignatureProductionPlace" minOccurs="0" />
            <xs:element name="CommitmentTypeIndication" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Indication" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="ClaimedRoles" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="ClaimedRoles" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="CertifiedRoles" type="CertifiedRole" minOccurs="0" maxOccurs="unbounded" />
            <xs:element name="Policy" type="Policy" minOccurs="0" />

            <xs:element name="Timestamps" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Timestamp" type="Timestamp" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="SignatureScopes" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="SignatureScope" type="SignatureScope" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required" />
        <xs:attribute name="CounterSignature" type="xs:boolean"/>
    </xs:complexType>

    <xs:complexType name="SignatureProductionPlace">
        <xs:sequence>
            <xs:element name="Address" type="xs:string" minOccurs="0" />
            <xs:element name="City" type="xs:string" minOccurs="0" />
            <xs:element name="StateOrProvince" type="xs:string" minOccurs="0" />
            <xs:element name="PostalCode" type="xs:string" minOccurs="0" />
            <xs:element name="CountryName" type="xs:string" minOccurs="0" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="Policy">
        <xs:sequence>
            <xs:element name="Id" type="xs:string" />
            <xs:element name="Url" type="xs:string" minOccurs="0" />
            <xs:element name="Notice" type="xs:string" minOccurs="0" />
            <xs:element name="DigestAlgoAndValue" type="DigestAlgoAndValue" minOccurs="0" />
            <xs:element name="Asn1Processable" type="xs:boolean" minOccurs="0" />
            <xs:element name="Identified" type="xs:boolean" minOccurs="0" />
            <xs:element name="Status" type="xs:boolean" minOccurs="0" />
            <xs:element name="ProcessingError" type="xs:string" minOccurs="0" />
            <xs:element name="DigestAlgorithmsEqual" type="xs:boolean" minOccurs="0" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="Certificate">
        <xs:sequence>
            <xs:element name="SubjectDistinguishedName" type="DistinguishedName" maxOccurs="unbounded" />
            <xs:element name="IssuerDistinguishedName" type="DistinguishedName" maxOccurs="unbounded" />
            <xs:element name="SerialNumber" type="xs:integer" />
            <xs:element name="CommonName" type="xs:string" minOccurs="0"/>
            <xs:element name="CountryName" type="xs:string" minOccurs="0"/>
            <xs:element name="OrganizationName" type="xs:string" minOccurs="0"/>
            <xs:element name="GivenName" type="xs:string" minOccurs="0"/>
            <xs:element name="OrganizationalUnit" type="xs:string" minOccurs="0"/>
            <xs:element name="Surname" type="xs:string" minOccurs="0"/>
            <xs:element name="Pseudonym" type="xs:string" minOccurs="0"/>

            <xs:element name="AuthorityInformationAccessUrls" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Url" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="CRLDistributionPoints" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Url" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="OCSPAccessUrls" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Url" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="DigestAlgoAndValues" type="DigestAlgoAndValues" minOccurs="0" />
            <xs:element name="NotAfter" type="xs:dateTime" />
            <xs:element name="NotBefore" type="xs:dateTime" />
            <xs:element name="PublicKeySize" type="xs:int" />
            <xs:element name="PublicKeyEncryptionAlgo" type="xs:string" />

            <xs:element name="KeyUsageBits" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="KeyUsage" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="IdKpOCSPSigning" type="xs:boolean" minOccurs="0" />
            <xs:element name="IdPkixOcspNoCheck" type="xs:boolean" minOccurs="0" />

            <xs:element name="BasicSignature" type="BasicSignature" />
            <xs:element name="SigningCertificate" type="SigningCertificate" minOccurs="0" />
            <xs:element name="CertificateChain" type="CertificateChain" minOccurs="0" />

            <xs:element name="Trusted" type="xs:boolean" />
            <xs:element name="SelfSigned" type="xs:boolean" />

            <xs:element name="CertificatePolicyIds" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="oid" type="OID" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="QCStatementIds" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="oid" type="OID" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
            <xs:element name="QCTypes" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="oid" type="OID" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="TrustedServiceProviders">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="TrustedServiceProvider" type="TrustedServiceProvider" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="Revocations" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Revocation" type="Revocation" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="Info" type="InfoType" minOccurs="0" />
            <xs:element name="Base64Encoded" type="xs:base64Binary" minOccurs="0" />
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required" />
    </xs:complexType>

    <xs:complexType name="OID">
        <xs:simpleContent>
            <xs:extension base="xs:string">
                <xs:attribute name="Description" type="xs:string"/>
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>

    <xs:complexType name="DistinguishedName">
        <xs:simpleContent>
            <xs:extension base="xs:string">
                <xs:attribute name="Format" type="xs:string"/>
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>

    <xs:complexType name="SignatureScope">
        <xs:simpleContent>
            <xs:extension base="xs:string">
                <xs:attribute name="name" type="xs:string" use="required" />
                <xs:attribute name="scope" type="xs:string" use="required" />
            </xs:extension>
        </xs:simpleContent>
    </xs:complexType>

    <xs:complexType name="StructuralValidation">
        <xs:sequence>
            <xs:element name="Valid" type="xs:boolean" minOccurs="0" />
            <xs:element name="Message" type="xs:string" minOccurs="0" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="BasicSignature">
        <xs:sequence>
            <xs:element name="EncryptionAlgoUsedToSignThisToken" type="xs:string" minOccurs="0" />
            <xs:element name="KeyLengthUsedToSignThisToken" type="xs:string" minOccurs="0" />
            <xs:element name="DigestAlgoUsedToSignThisToken" type="xs:string" minOccurs="0"  />
            <xs:element name="MaskGenerationFunctionUsedToSignThisToken" type="xs:string" minOccurs="0"  />
            <xs:element name="ReferenceDataFound" type="xs:boolean" />
            <xs:element name="ReferenceDataIntact" type="xs:boolean" />
            <xs:element name="SignatureIntact" type="xs:boolean" />
            <xs:element name="SignatureValid" type="xs:boolean" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="SigningCertificate">
        <xs:sequence>
            <xs:element name="AttributePresent" type="xs:boolean" minOccurs="0" />
            <xs:element name="DigestValuePresent" type="xs:boolean" minOccurs="0" />
            <xs:element name="DigestValueMatch" type="xs:boolean" minOccurs="0" />
            <xs:element name="IssuerSerialMatch" type="xs:boolean" minOccurs="0" />
            <xs:element name="Signed" type="xs:string" minOccurs="0" />
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required" />
    </xs:complexType>

    <xs:complexType name="InfoType">
        <xs:sequence>
            <xs:element name="Message" maxOccurs="unbounded" minOccurs="0">
                <xs:complexType>
                    <xs:simpleContent>
                        <xs:extension base="xs:string">
                            <xs:attribute name="Id" type="xs:int" use="required" />
                        </xs:extension>
                    </xs:simpleContent>
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="TimestampedObjects">
        <xs:sequence>
            <xs:element name="TimestampedObject" type="TimestampedObject" minOccurs="0" maxOccurs="unbounded" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="TimestampedObject">
        <xs:sequence>
            <xs:element name="DigestAlgoAndValue" type="DigestAlgoAndValue" minOccurs="0"/>
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string"/>
        <xs:attribute name="Category" type="TimestampedObjectType" use="required" />
    </xs:complexType>

    <xs:simpleType name="TimestampedObjectType" final="restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="CERTIFICATE" />
            <xs:enumeration value="SIGNATURE" />
            <xs:enumeration value="TIMESTAMP" />
            <xs:enumeration value="REVOCATION" />
        </xs:restriction>
    </xs:simpleType>

    <xs:complexType name="CertificateChain">
        <xs:sequence>
            <xs:element name="ChainItem" minOccurs="0" maxOccurs="unbounded">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Source" type="xs:string" />
                    </xs:sequence>
                    <xs:attribute name="Id" type="xs:string" use="required" />
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="CertifiedRole">
        <xs:sequence>
            <xs:element name="CertifiedRole" type="xs:string" />
            <xs:element name="NotAfter" type="xs:dateTime" />
            <xs:element name="NotBefore" type="xs:dateTime" />
        </xs:sequence>
        <xs:attribute name="Category" type="xs:string" use="required" />
    </xs:complexType>

    <xs:complexType name="DigestAlgoAndValue">
        <xs:sequence>
            <xs:element name="DigestMethod" type="xs:string" />
            <xs:element name="DigestValue" type="xs:string" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="Timestamp">
        <xs:sequence>
            <xs:element name="ProductionTime" type="xs:dateTime" />
            <xs:element name="SignedDataDigestAlgo" type="xs:string" />
            <xs:element name="EncodedSignedDataDigestValue" type="xs:string" />
            <xs:element name="MessageImprintDataFound" type="xs:boolean" />
            <xs:element name="MessageImprintDataIntact" type="xs:boolean" />
            <xs:element name="CanonicalizationMethod" type="xs:string" minOccurs="0" />

            <xs:element name="BasicSignature" type="BasicSignature" />
            <xs:element name="SigningCertificate" type="SigningCertificate" minOccurs="0" />
            <xs:element name="CertificateChain" type="CertificateChain" minOccurs="0" />

            <xs:element name="TimestampedObjects" type="TimestampedObjects" minOccurs="0" />
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required" />
        <xs:attribute name="Type" type="xs:string" use="required" />
    </xs:complexType>

    <xs:complexType name="Revocation">
        <xs:sequence>
            <xs:element name="Origin" type="xs:string" />
            <xs:element name="Source" type="xs:string" />
            <xs:element name="SourceAddress" type="xs:string" minOccurs="0" />
            <xs:element name="Available" type="xs:boolean" minOccurs="0" />
            <xs:element name="Status" type="xs:boolean" />
            <xs:element name="Reason" type="xs:string" minOccurs="0" />
            <xs:element name="ProductionDate" type="xs:dateTime" minOccurs="0" /> <!-- Can be null in case of no SUCCESSFUL response of OCSP -->
            <xs:element name="ThisUpdate" type="xs:dateTime" minOccurs="0" />
            <xs:element name="NextUpdate" type="xs:dateTime" minOccurs="0" />
            <xs:element name="RevocationDate" type="xs:dateTime" minOccurs="0" />
            <xs:element name="ExpiredCertsOnCRL" type="xs:dateTime" minOccurs="0" /> <!-- CRL -->
            <xs:element name="ArchiveCutOff" type="xs:dateTime" minOccurs="0" /> <!-- OCSP -->

            <xs:element name="DigestAlgoAndValues" type="DigestAlgoAndValues" minOccurs="0" />

            <xs:element name="BasicSignature" type="BasicSignature" minOccurs="0" /> <!-- Can be null in case of no SUCCESSFUL response of OCSP -->
            <xs:element name="SigningCertificate" type="SigningCertificate" minOccurs="0" />
            <xs:element name="CertificateChain" type="CertificateChain" minOccurs="0" />

            <xs:element name="Info" type="InfoType" minOccurs="0" />
        </xs:sequence>
        <xs:attribute name="Id" type="xs:string" use="required" />
    </xs:complexType>

    <xs:complexType name="TrustedList">
        <xs:sequence>
            <xs:element name="CountryCode" type="xs:string" />
            <xs:element name="Url" type="xs:string" />
            <xs:element name="SequenceNumber" type="xs:int" minOccurs="0" />
            <xs:element name="Version" type="xs:int" minOccurs="0" />
            <xs:element name="LastLoading" type="xs:dateTime" minOccurs="0" />
            <xs:element name="IssueDate" type="xs:dateTime" minOccurs="0" />
            <xs:element name="NextUpdate" type="xs:dateTime" minOccurs="0" />
            <xs:element name="WellSigned" type="xs:boolean" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="TrustedServiceProvider">
        <xs:sequence>
            <xs:element name="TSPName" type="xs:string" />
            <xs:element name="TSPServiceName" type="xs:string" />
            <xs:element name="CountryCode" type="xs:string" />

            <xs:element name="TrustedServices">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="TrustedService" type="TrustedService" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="TrustedService">
        <xs:sequence>
            <xs:element name="ServiceType" type="xs:string" />
            <xs:element name="Status" type="xs:string" />
            <xs:element name="StartDate" type="xs:dateTime" />
            <xs:element name="EndDate" type="xs:dateTime" minOccurs="0" />

            <xs:element name="CapturedQualifiers" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Qualifier" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="AdditionalServiceInfoUris" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="URI" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="ServiceSupplyPoints" minOccurs="0">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="URI" type="xs:string" minOccurs="0" maxOccurs="unbounded" />
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="expiredCertsRevocationInfo" type="xs:dateTime" minOccurs="0" />
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="DigestAlgoAndValues">
        <xs:sequence>
            <xs:element name="DigestAlgoAndValue" type="DigestAlgoAndValue" minOccurs="0" maxOccurs="unbounded" />
        </xs:sequence>
    </xs:complexType>

</xs:schema>
```