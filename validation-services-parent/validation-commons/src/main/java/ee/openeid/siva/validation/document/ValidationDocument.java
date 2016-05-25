/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ee.openeid.siva.validation.document;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.MimeType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Container for any kind of document that is to be transferred to and from web service endpoints.
 *
 *
 */

public class ValidationDocument {

    protected MimeType mimeType;
    protected ValidationDocument nextDocument;
    private byte[] bytes;
    private String name;
    private String absolutePath = "WSDocument";

    /**
     * This constructor is used by Spring in the web-app..
     */
    public ValidationDocument() {

    }

    /**
     * The default constructor for WSDocument.
     *
     * @param dssDocument
     * @throws DSSException
     */
    public ValidationDocument(final DSSDocument dssDocument) throws DSSException {

        final byte[] bytes = dssDocument.getBytes();
        this.bytes = Arrays.copyOf(bytes, bytes.length);
        mimeType = dssDocument.getMimeType();
        name = dssDocument.getName();
        absolutePath = dssDocument.getAbsolutePath();

        final DSSDocument nextDssDocument = dssDocument.getNextDocument();
        if (nextDssDocument != null) {
            nextDocument = new ValidationDocument(nextDssDocument);
        }
    }

    /**
     * This method is used by web services
     *
     * @return the bytes
     */
    public byte[] getBytes() {

        return bytes;
    }

    /**
     * This method is used by web services
     *
     * @param bytes the bytes to set
     */
    public void setBytes(byte[] bytes) {

        this.bytes = bytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(final MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public InputStream openStream() throws DSSException {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return byteArrayInputStream;
    }

    public ValidationDocument getNextDocument() {
        return nextDocument;
    }

    public void setNextDocument(ValidationDocument nextDocument) {
        this.nextDocument = nextDocument;
    }

    @Override
    public String toString() {

        final StringWriter stringWriter = new StringWriter();
        stringWriter.append("Name: " + getName()).append(" / ").append("mime-type=" + (mimeType == null ? "null" : mimeType.getMimeTypeString())).append(" / ")
                .append(" / AbsolutePath [").append(getAbsolutePath()).append("] / nextDocument [").append(nextDocument.toString()).append("]");
        final String string = stringWriter.toString();
        return string;
    }
}