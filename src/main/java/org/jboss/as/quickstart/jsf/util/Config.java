/*
 * Copyright 2022 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstart.jsf.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import jakarta.ejb.TransactionManagementType;
import jakarta.faces.model.SelectItem;

/**
 * @author bmaxwell
 *
 */
public class Config implements Serializable, Comparable<Config> {

    private static Set<SelectItem> transactionManagementTypes = new TreeSet<SelectItem>(new Comparator<SelectItem>() {
        @Override
        public int compare(SelectItem o1, SelectItem o2) {
            return o1.getLabel().compareTo(o2.getLabel());
        }
    });

    private static Set<SelectItem> txTimeouts = new TreeSet<SelectItem>(new Comparator<SelectItem>() {
        @Override
        public int compare(SelectItem o1, SelectItem o2) {
            return ((Integer) o1.getValue()).compareTo(((Integer) o2.getValue()));
        }
    });

    static {
        transactionManagementTypes.add(new SelectItem(TransactionManagementType.BEAN, TransactionManagementType.BEAN.name()));
        transactionManagementTypes
                .add(new SelectItem(TransactionManagementType.CONTAINER, TransactionManagementType.CONTAINER.name()));

        txTimeouts.add(new SelectItem(10));
        txTimeouts.add(new SelectItem(20));
        txTimeouts.add(new SelectItem(30));
        txTimeouts.add(new SelectItem(45));
        txTimeouts.add(new SelectItem(60));
        txTimeouts.add(new SelectItem(90));
        txTimeouts.add(new SelectItem(120));
        txTimeouts.add(new SelectItem(290));
        txTimeouts.add(new SelectItem(300));
        txTimeouts.add(new SelectItem(330));
    }

    private static String BMT_EJB_NAME = "BMTEJB";
    private static String CMT_EJB_NAME = "CMTEJB";

    private boolean remote = false;
    private String host = "localhost";
    private Integer port = 8080;
    private String username = "ejbuser";
    private String password = "redhat1!";

    // private int bmtTxTimeoutSeconds = 300;
    // private Integer cmtTxTimeout = 300;
    private Integer txTimeoutSeconds;

    private int sleepSeconds = 1;

    private TransactionManagementType transactionManagementType = TransactionManagementType.CONTAINER;

    public Config() {
    }

    public Config(Config config) {
        this.host = config.host;
        this.port = config.port;
        this.username = config.username;
        this.password = config.password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TransactionManagementType getTransactionManagementType() {
        return transactionManagementType;
    }

    public void setTransactionManagementType(TransactionManagementType transactionManagementType) {
        this.transactionManagementType = transactionManagementType;
    }

    public Set<SelectItem> getTransactionManagementTypes() {
        return transactionManagementTypes;
    }

    public int getSleepSeconds() {
        return sleepSeconds;
    }

    public void setSleepSeconds(int sleepSeconds) {
        this.sleepSeconds = sleepSeconds;
    }

    public String getEJBName() {
        switch (transactionManagementType) {
            case CONTAINER:
                return CMT_EJB_NAME;
            case BEAN:
                return BMT_EJB_NAME;
            default:
                throw new RuntimeException("transactionManagementType is not valid: " + transactionManagementType);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(String.format("txType: %s txTimeoutSeconds: %d sleepSeconds: %d",
                transactionManagementType, txTimeoutSeconds, sleepSeconds));

        if (remote)
            sb.append(String.format(" host: %s port: %d username: %s password: %s ", host, port, username, password));

        return sb.toString();
    }

    @Override
    public int compareTo(Config o) {
        if (o == null)
            return -1;
        return o.toString().compareTo(toString());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Config)
            return ((Config) obj).toString().equals(toString());
        return false;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public Set<SelectItem> getTxTimeouts() {
        return txTimeouts;
    }

    public Integer getTxTimeoutSeconds() {
        return txTimeoutSeconds;
    }

    public void setTxTimeoutSeconds(Integer txTimeout) {
        this.txTimeoutSeconds = txTimeout;
    }
}