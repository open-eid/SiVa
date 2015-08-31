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
package eu.europa.esig.dss.tsl;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.x509.CertificatePool;
import eu.europa.esig.dss.x509.CertificateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This CertificateSource keep a list of trusted certificates extracted from the trusted list. To populate this list {@link
 * TrustedListsCertificateSource} class is used. This list is refreshed when the method refresh
 * is called.
 *
 *
 */

public class ReloadableTrustedListCertificateSource extends TrustedListsCertificateSource {
    private Date updateStartDate;
    private Date updateEndTime;
    private String  tslUpdateMessage;

    private static final Logger LOG = LoggerFactory.getLogger(ReloadableTrustedListCertificateSource.class);

    protected TrustedListsCertificateSource currentSource = new TrustedListsCertificateSource();

    public ReloadableTrustedListCertificateSource() {

        super();
    }

    static class Reloader implements Runnable {
        private Date started;
        private Date ended;
        private String errorMessage = "TSL Update completed successfully";

        private TrustedListsCertificateSource underlyingSource;

        Reloader(final TrustedListsCertificateSource underlyingSource) {
            this.underlyingSource = underlyingSource;
        }

        @Override
        public void run() {

            try {
                started = new Date();
                LOG.info("--> run(): START LOADING");
                underlyingSource.init();

                ended = new Date();
                LOG.info("--> run(): END LOADING");

            } catch (DSSException e) {
                makeATrace(e);
            }
        }

        private void makeATrace(final Exception e) {
            errorMessage = "TSL update failed with ERROR: " + e.getMessage();
            LOG.error(e.getMessage(), e);
        }

        protected Date getStarted() {
            return started;
        }

        protected Date getEnded() {
            return ended;
        }

        protected String getErrorMessage() {
            return errorMessage;
        }
    }

    public Date getUpdateStartDate() {
        return updateStartDate;
    }

    public void setUpdateStartDate(Date updateStartDate) {
        this.updateStartDate = updateStartDate;
    }

    public Date getUpdateEndTime() {
        return updateEndTime;
    }

    public void setUpdateEndTime(Date updateEndTime) {
        this.updateEndTime = updateEndTime;
    }

    public String getTslUpdateMessage() {
        return tslUpdateMessage;
    }

    public void setTslUpdateMessage(String tslUpdateMessage) {
        this.tslUpdateMessage = tslUpdateMessage;
    }

    public synchronized void refresh() {

        final TrustedListsCertificateSource newSource = new TrustedListsCertificateSource(this);
	    final Reloader target = new Reloader(newSource);
	    final Thread reloader = new Thread(target);

        updateStartDate = new Date();
        reloader.start();

        setTslRefreshInfo(target, reloader);

        updateEndTime = new Date();
        currentSource = newSource;
    }

    protected void setTslRefreshInfo(Reloader target, Thread reloader) {
        try {
            reloader.join();

            setUpdateEndTime(target.getEnded());
            setUpdateStartDate(target.getStarted());
            setTslUpdateMessage(target.getErrorMessage());

            LOG.info("TSL Update error occurred: {}", target.getErrorMessage());
            LOG.info("TSL loading started: {}", getUpdateStartDate());
            LOG.info("TSL Ended loading: {}", getUpdateEndTime());
        } catch (InterruptedException e) {
            LOG.info("TSL update failed: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, String> getDiagnosticInfo() {

        return currentSource.getDiagnosticInfo();
    }

    @Override
    public CertificatePool getCertificatePool() {

        return currentSource.getCertificatePool();
    }

    @Override
    /**
     * Retrieves the list of all certificate tokens from this source.
     *
     * @return
     */
    public List<CertificateToken> getCertificates() {

        return currentSource.getCertificatePool().getCertificateTokens();
    }
}
