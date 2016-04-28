/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ee.openeid.siva.tsl;

import eu.europa.esig.jaxb.tsl.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Current entry of the Service for the TrustedList
 *
 *
 */

class CurrentTrustService extends AbstractTrustService {

	private TSPServiceType service;

	/**
	 * The default constructor for TrustService.
	 *
	 * @param service
	 */
	CurrentTrustService(TSPServiceType service) {

		this.service = service;
	}

	@Override
	List<ExtensionType> getExtensions() {

		final TSPServiceInformationType serviceInfo = service.getServiceInformation();
		if (serviceInfo != null) {

			final ExtensionsListType extensionsList = serviceInfo.getServiceInformationExtensions();
			if (extensionsList != null) {
				return extensionsList.getExtension();
			}
		}
		return Collections.emptyList();
	}



	@Override
	Set<String> getCertificateUrls() {
		Set<String> result = new HashSet<String>();
		TSPServiceInformationType serviceInformation = service.getServiceInformation();
		if ((serviceInformation !=null) && (serviceInformation.getSchemeServiceDefinitionURI() !=null) && CollectionUtils.isNotEmpty(serviceInformation.getSchemeServiceDefinitionURI().getURI())) {
			List<NonEmptyMultiLangURIType> uris = serviceInformation.getSchemeServiceDefinitionURI().getURI();
			for (NonEmptyMultiLangURIType uri : uris) {
				String value = uri.getValue();
				if (isCertificateURI(value)){
					result.add(value);
				}
			}
		}
		return result;
	}

	private boolean isCertificateURI(String value) {
		return StringUtils.endsWithIgnoreCase(value, ".crt");
	}

	@Override
	DigitalIdentityListType getServiceDigitalIdentity() {
		return service.getServiceInformation().getServiceDigitalIdentity();
	}

	@Override
	String getStatus() {
		return service.getServiceInformation().getServiceStatus();
	}

	@Override
	Date getStatusStartDate() {

		if ((service.getServiceInformation() != null) && (service.getServiceInformation().getStatusStartingTime() != null)) {
			return service.getServiceInformation().getStatusStartingTime().toGregorianCalendar().getTime();
		} else {
			return null;
		}
	}

	@Override
	Date getStatusEndDate() {

		return null;
	}

	@Override
	String getType() {

		return service.getServiceInformation().getServiceTypeIdentifier();
	}

	@Override
	String getServiceName() {

		/* Return the english name or the first name */
		InternationalNamesType names = service.getServiceInformation().getServiceName();
		for (MultiLangNormStringType s : names.getName()) {
			if ("en".equalsIgnoreCase(s.getLang())) {
				return s.getValue();
			}
		}
		return names.getName().get(0).getValue();
	}
}
