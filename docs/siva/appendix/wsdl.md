<!--# WSDL of SOAP interface-->

See also [Interfaces](../interface_description) for more information about the SOAP interface. 

```xml
<?xml version='1.0' encoding='UTF-8'?><wsdl:definitions xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://soap.webapp.siva.openeid.ee/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:ns1="http://schemas.xmlsoap.org/soap/http" name="SignatureValidationService" targetNamespace="http://soap.webapp.siva.openeid.ee/">
  <wsdl:types>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://soap.webapp.siva.openeid.ee/" elementFormDefault="unqualified" targetNamespace="http://soap.webapp.siva.openeid.ee/" version="1.0">

  <xs:element name="ValidateDocument" type="tns:validateDocument"/>

  <xs:element name="ValidateDocumentResponse" type="tns:ValidateDocumentResponse"/>

  <xs:element name="ValidationReport" type="tns:qualifiedReport"/>

  <xs:element name="ValidationRequest" type="tns:soapValidationRequest"/>

  <xs:complexType name="validateDocument">
    <xs:sequence>
      <xs:element ref="tns:ValidationRequest"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="soapValidationRequest">
    <xs:sequence>
      <xs:element name="Document" type="xs:string"/>
      <xs:element name="Filename" type="xs:string"/>
      <xs:element name="DocumentType" type="tns:documentType"/>
      <xs:element minOccurs="0" name="SignaturePolicy" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="ValidateDocumentResponse">
    <xs:sequence>
      <xs:element minOccurs="0" ref="tns:ValidationReport"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="qualifiedReport">
    <xs:sequence>
      <xs:element name="Policy" type="tns:policy"/>
      <xs:element name="ValidationTime" type="xs:string"/>
      <xs:element name="DocumentName" type="xs:string"/>
      <xs:element name="Signatures">
        <xs:complexType>
          <xs:sequence>
            <xs:element maxOccurs="unbounded" minOccurs="0" name="Signature" type="tns:signatureValidationData"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="ValidSignaturesCount" type="xs:int"/>
      <xs:element name="SignaturesCount" type="xs:int"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="policy">
    <xs:sequence>
      <xs:element name="PolicyDescription" type="xs:string"/>
      <xs:element name="PolicyName" type="xs:string"/>
      <xs:element name="PolicyUrl" type="xs:string"/>
      <xs:element name="PolicyVersion" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="signatureValidationData">
    <xs:sequence>
      <xs:element name="Id" type="xs:string"/>
      <xs:element name="SignatureFormat" type="xs:string"/>
      <xs:element name="SignatureLevel" type="xs:string"/>
      <xs:element name="SignedBy" type="xs:string"/>
      <xs:element name="Indication" type="tns:indication"/>
      <xs:element name="SubIndication" type="xs:string"/>
      <xs:element name="Errors">
        <xs:complexType>
          <xs:sequence>
            <xs:element maxOccurs="unbounded" minOccurs="0" name="Error" type="tns:error"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="SignatureScopes">
        <xs:complexType>
          <xs:sequence>
            <xs:element maxOccurs="unbounded" name="SignatureScope" type="tns:signatureScope"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="ClaimedSigningTime" type="xs:string"/>
      <xs:element name="Warnings">
        <xs:complexType>
          <xs:sequence>
            <xs:element maxOccurs="unbounded" minOccurs="0" name="Warning" type="tns:warning"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="Info" type="tns:info"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="error">
    <xs:sequence>
      <xs:element name="NameId" type="xs:string"/>
      <xs:element name="Content" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="signatureScope">
    <xs:sequence>
      <xs:element name="Name" type="xs:string"/>
      <xs:element name="Scope" type="xs:string"/>
      <xs:element name="Content" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="warning">
    <xs:sequence>
      <xs:element name="NameId" type="xs:string"/>
      <xs:element name="Description" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="info">
    <xs:sequence>
      <xs:element minOccurs="0" name="bestSignatureTime" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>

  <xs:simpleType name="documentType">
    <xs:restriction base="xs:string">
      <xs:enumeration value="PDF"/>
      <xs:enumeration value="ASICE"/>
      <xs:enumeration value="XROAD"/>
      <xs:enumeration value="BDOC"/>
      <xs:enumeration value="DDOC"/>
    </xs:restriction>
  </xs:simpleType>

  <xs:simpleType name="indication">
    <xs:restriction base="xs:string">
      <xs:enumeration value="TOTAL_PASSED"/>
      <xs:enumeration value="TOTAL_FAILED"/>
      <xs:enumeration value="INDETERMINATE"/>
    </xs:restriction>
  </xs:simpleType>

</xs:schema>
  </wsdl:types>
  <wsdl:message name="ValidateDocument">
    <wsdl:part element="tns:ValidateDocument" name="parameters">
    </wsdl:part>
  </wsdl:message>
  <wsdl:message name="ValidateDocumentResponse">
    <wsdl:part element="tns:ValidateDocumentResponse" name="parameters">
    </wsdl:part>
  </wsdl:message>
  <wsdl:portType name="ValidationWebService">
    <wsdl:operation name="ValidateDocument">
      <wsdl:input message="tns:ValidateDocument" name="ValidateDocument">
    </wsdl:input>
      <wsdl:output message="tns:ValidateDocumentResponse" name="ValidateDocumentResponse">
    </wsdl:output>
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="SignatureValidationServiceSoapBinding" type="tns:ValidationWebService">
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
  <wsdl:service name="SignatureValidationService">
    <wsdl:port binding="tns:SignatureValidationServiceSoapBinding" name="ValidationWebServiceImplPort">
      <soap:address location="http://localhost:8080/soap/validationWebService"/>
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```