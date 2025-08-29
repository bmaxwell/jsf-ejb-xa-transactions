/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstart.jsf.controllers;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.security.Principal;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.as.quickstart.ejb.api.EJBRequest;
import org.jboss.as.quickstart.ejb.api.EJBResponse;
import org.jboss.as.quickstart.ejb.api.TransactionEJB;
import org.jboss.as.quickstart.jpa.model.ClearDatabases;
import org.jboss.as.quickstart.jpa.model.ListDatabases;
import org.jboss.as.quickstart.jsf.util.Config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.faces.context.FacesContext;

/**
 * The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an EL name
 */
@RequestScoped
@Model
public class Controller implements Serializable {

    protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @EJB(beanName = "CMTEJB")
    private TransactionEJB cmtEJB;

    @EJB(beanName = "BMTEJB")
    private TransactionEJB bmtEJB;

    private Config config;
    private String error;
    private Config currentConfig;
    private EJBRequest request = new EJBRequest();
    private EJBResponse response = null;
    private ListDatabases listDatabases = null;
    private ClearDatabases clearDatabases = null;

    // @Inject
    private FacesContext facesContext;

    @PostConstruct
    public void init() {
        this.config = new Config();
    }

    public String getUser() {
        Principal p = facesContext.getExternalContext().getUserPrincipal();
        return p == null ? null : p.getName();
    }

    /**
     * CMT or BMT
     *
     * @return
     */
    public String test() {

        log.info("invoke config: " + this.config);

        try {
            switch (this.config.getTransactionManagementType()) {
                case BEAN:
                    response = bmtEJB.test(request, null, null);
                case CONTAINER:
                    response = cmtEJB.test(request, null, null);
            }
        } catch (Exception e) {
            this.error = getException(e);
        }

        return "";
    }

    public String list() {

        log.info("list config: " + this.config);

        try {
            switch (this.config.getTransactionManagementType()) {
                case BEAN:
                    listDatabases = bmtEJB.list();
                case CONTAINER:
                    listDatabases = cmtEJB.list();
            }
        } catch (Exception e) {
            this.error = getException(e);
        }

        return "";
    }

    public String clear() {

        log.info("clear config: " + this.config);

        try {
            switch (this.config.getTransactionManagementType()) {
                case BEAN:
                    clearDatabases = bmtEJB.clearAllDatabases();
                case CONTAINER:
                    clearDatabases = cmtEJB.clearAllDatabases();
            }
        } catch (Exception e) {
            this.error = getException(e);
        }

        return "";
    }

    public String getInfo() {

        StringBuilder sb = new StringBuilder();

        if (config != null && config.getTxTimeoutSeconds() != null) {

            if (config.getSleepSeconds() > getDefaultTransactionTimeout()) {
                sb.append(String.format("Sleep (%d) > Default Timeout (%d)\n", config.getSleepSeconds(),
                        getDefaultTransactionTimeout()));
            }
            if (config.getSleepSeconds() > config.getTxTimeoutSeconds()) {
                sb.append(String.format("Sleep (%d) > Tx Timeout (%d)\n", config.getSleepSeconds(),
                        config.getTxTimeoutSeconds()));
            }
            if (sb.length() > 0)
                return sb.toString();
        }
        return null;
    }

    private String getException(Throwable t) {

        t.printStackTrace();

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        t.printStackTrace(writer);
        return out.toString();

        // StringBuilder sb = new StringBuilder();
        // sb.append(String.format("%s: %s", t.getClass().getName(), t.getMessage()));
        // if (t.getCause() != null)
        // sb.append(String.format(" Caused By: %s: %s", t.getCause().getClass().getName(), t.getCause().getMessage()));
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }

    public Config getConfig() {
        return config;
    }

    public String getError() {
        return error;
    }

    public EJBResponse getResponse() {
        return response;
    }

    public Integer getDefaultTransactionTimeout() {

        try {
            ObjectName objectName = new ObjectName("jboss.as:subsystem=transactions");
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            Object defaultTimeout = mbeanServer.getAttribute(objectName, "defaultTimeout");
            return (Integer) defaultTimeout;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public ListDatabases getListDatabases() {
        return listDatabases;
    }

    public void setListDatabases(ListDatabases listDatabases) {
        this.listDatabases = listDatabases;
    }

    public ClearDatabases getClearDatabases() {
        return clearDatabases;
    }

    public void setClearDatabases(ClearDatabases clearDatabases) {
        this.clearDatabases = clearDatabases;
    }
}