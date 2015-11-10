Appendix 6 - ValidationService WSDL
===================================

```xml
<?xml version='1.0' encoding='UTF-8'?><wsdl:definitions xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:ns1="http://ws.dss.esig.europa.eu/" name="ValidationService" targetNamespace="http://ws.dss.esig.europa.eu/">
  <wsdl:types>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://ws.dss.esig.europa.eu/" attributeFormDefault="unqualified" elementFormDefault="unqualified" targetNamespace="http://ws.dss.esig.europa.eu/">
  <xs:element name="validateDocument" type="tns:validateDocument"/>
  <xs:element name="validateDocumentResponse" type="tns:validateDocumentResponse"/>
  <xs:complexType name="validateDocument">
    <xs:sequence>
      <xs:element minOccurs="0" name="document" type="tns:wsDocument"/>
      <xs:element minOccurs="0" name="detachedContent" type="tns:wsDocument"/>
      <xs:element minOccurs="0" name="policy" type="tns:wsDocument"/>
      <xs:element name="diagnosticDataToBeReturned" type="xs:boolean"/>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="wsDocument">
    <xs:sequence>
      <xs:element minOccurs="0" name="absolutePath" type="xs:string"/>
      <xs:element minOccurs="0" name="bytes" type="xs:base64Binary"/>
      <xs:element minOccurs="0" name="mimeType" type="tns:mimeType"/>
      <xs:element minOccurs="0" name="name" type="xs:string"/>
      <xs:element minOccurs="0" name="nextDocument" type="tns:wsDocument"/>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="mimeType">
    <xs:sequence>
      <xs:element minOccurs="0" name="mimeTypeString" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="validateDocumentResponse">
    <xs:sequence>
      <xs:element minOccurs="0" name="response" type="tns:wsValidationReport"/>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="wsValidationReport">
    <xs:sequence>
      <xs:element minOccurs="0" name="xmlDetailedReport" type="xs:string"/>
      <xs:element minOccurs="0" name="xmlDiagnosticData" type="xs:string"/>
      <xs:element minOccurs="0" name="xmlSimpleReport" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>
  <xs:element name="DSSException" type="tns:DSSException"/>
  <xs:complexType name="DSSException">
    <xs:sequence>
      <xs:element minOccurs="0" name="message" type="xs:string"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
  </wsdl:types>
  <wsdl:message name="DSSException">
    <wsdl:part element="ns1:DSSException" name="DSSException">
    </wsdl:part>
  </wsdl:message>
  <wsdl:message name="validateDocumentResponse">
    <wsdl:part element="ns1:validateDocumentResponse" name="parameters">
    </wsdl:part>
  </wsdl:message>
  <wsdl:message name="validateDocument">
    <wsdl:part element="ns1:validateDocument" name="parameters">
    </wsdl:part>
  </wsdl:message>
  <wsdl:portType name="ValidationService">
<wsdl:documentation>The validation web service allows to validate any kind of signature form. The validation process is based on ETSI TS 102853 and is driven by the validation policy containing a set of rules. As result three states can be returned: VALID, INDETERMINATE OR INVALID. These states are accompanied by sub-indications and some other information. The result is represented through three reports: simple validation report with the final result, detailed validation report with the result of each rule and the diagnostic data.</wsdl:documentation>
    <wsdl:operation name="validateDocument">
<wsdl:documentation>This method validates the document containing the signature(s). It takes four parameters: document with signature(s), the signed document in case of detached signature (can be null), the document containing the specific validation policy (default policy is used when null) and a flag to say if diagnostic data must be returned by the method.</wsdl:documentation>
      <wsdl:input message="ns1:validateDocument" name="validateDocument">
    </wsdl:input>
      <wsdl:output message="ns1:validateDocumentResponse" name="validateDocumentResponse">
    </wsdl:output>
      <wsdl:fault message="ns1:DSSException" name="DSSException">
    </wsdl:fault>
    </wsdl:operation>
  </wsdl:portType>
</wsdl:definitions>
```
