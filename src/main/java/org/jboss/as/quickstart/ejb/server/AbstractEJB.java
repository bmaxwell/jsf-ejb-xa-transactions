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

package org.jboss.as.quickstart.ejb.server;

import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.jboss.as.quickstart.ejb.api.EJBRequest;
import org.jboss.as.quickstart.ejb.api.EJBResponse;
import org.jboss.as.quickstart.ejb.api.TestException;
import org.jboss.as.quickstart.ejb.api.TransactionEJB;
import org.jboss.as.quickstart.jpa.model.Animal;
import org.jboss.as.quickstart.jpa.model.ClearDatabases;
import org.jboss.as.quickstart.jpa.model.ListDatabases;
import org.jboss.as.quickstart.jpa.model.Place;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TransactionManagementType;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.jms.XAConnectionFactory;
import jakarta.jms.XAJMSContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

/**
 * @author bmaxwell
 *
 */
@PermitAll
public abstract class AbstractEJB extends AbstractUtilBase implements TransactionEJB {

    protected TransactionManagementType txManagementType;
    protected Logger log = Logger.getLogger(getClass().getSimpleName());
    protected static String JBOSS_NODE_NAME = System.getProperty("jboss.node.name");

    @Resource
    protected SessionContext ctx;

    @PersistenceContext(unitName = "database1")
    protected EntityManager em1;

    @PersistenceContext(unitName = "database2")
    protected EntityManager em2;

    @Resource(name = "jdbc/datasource1")
    private DataSource xaDatasource1;

    @Resource(name = "jdbc/datasource2")
    private DataSource xaDatasource2;

    @Resource(name = "jms/JmsXA")
    private XAConnectionFactory xaConnectionFactory;

    @Resource(name = "jms/Queue1")
    private Queue xaQueue1;

    @Resource(name = "jms/Queue2")
    private Queue xaQueue2;

    public AbstractEJB(TransactionManagementType txManagementType) {
        this.txManagementType = txManagementType;
    }

    protected boolean isBMT() {
        return this.txManagementType == TransactionManagementType.BEAN;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws TestException;
    }

    /**
     * Invoke the method, if it is BMT then call UTX to start/commit a tx, else, it is CMT
     *
     * @param op
     * @return
     * @throws TestException
     */
    private Object invoke(ThrowingSupplier op) throws TestException {

        log.info("invoke isBMT: " + isBMT());

        Object ret = null;

        if (isBMT()) {

            UserTransaction utx = null;
            try {
                utx = ctx.getUserTransaction();
                utx.begin();

                // do work
                ret = op.get();

            } catch (TestException te) {
                throw te;
            } catch (Throwable t) {
                t.printStackTrace();
                try {
                    if (utx != null)
                        utx.rollback();
                } catch (Throwable t2) {
                    t2.printStackTrace();
                }
                throw new TestException(String.format("invokeBMT error"), t);
            } finally {
                try {
                    if (utx != null)
                        utx.commit();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } else {
            ret = op.get();
        }

        return ret;
    }

    @Override
    public ListDatabases list() throws TestException {

        log.info("*** list invoked ***");

        ThrowingSupplier<ListDatabases> listDatabasesSupplier = new ThrowingSupplier<ListDatabases>() {
            @Override
            public ListDatabases get() throws TestException {
                ListDatabases ld = new ListDatabases();
                ld.setPlaceDatabase1(getAllFromDatabase(em1, "Place", Place.class));
                ld.setAnimalDatabase2(getAllFromDatabase(em2, "Animal", Animal.class));
                return ld;
            }
        };

        return (ListDatabases) invoke(listDatabasesSupplier);
    }

    @Override
    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws TestException {

        log.info("*** test invoked ***");

        ThrowingSupplier<EJBResponse> testSupplier = new ThrowingSupplier<EJBResponse>() {
            @Override
            public EJBResponse get() throws TestException {

                XAJMSContext xaJMSContext = null;

                EJBResponse response = new EJBResponse();

                try {

                    Place place = (placeName == null || placeName.isEmpty()) ? Place.randomPlace() : new Place(placeName);
                    Animal animal = (animalName == null || animalName.isEmpty()) ? Animal.randomAnimal()
                            : new Animal(animalName);

                    // save Place , Animal, have the MessageConsumers update something to indicate they have been processed.

                    em1.joinTransaction();
                    em2.joinTransaction();

                    em1.persist(place);
                    response.addStepCompleted("EntityManager1.persist: " + place);

                    em2.persist(animal);
                    response.addStepCompleted("EntityManager2.persist: " + animal);

                    // send the Place / Animal to the JMS Consumers

                    xaJMSContext = xaConnectionFactory.createXAContext();
                    // xaJMSContext = getXAConnectionFactory().createXAContext();
                    response.addStepCompleted("Got xaConnectionFactory xaContext: " + xaJMSContext);

                    // send jms message to xaQueue1 start
                    JMSProducer xaJMSproducer = xaJMSContext.createProducer();
                    response.addStepCompleted("Got xaJMSproducer: " + xaJMSproducer);

                    TextMessage xaJMSmessage1 = xaJMSContext
                            .createTextMessage(String.format("Send Place %s to Datasource1", place));
                    response.addStepCompleted("Created xaJMSmessage1: " + xaJMSmessage1);

                    xaJMSproducer.send(xaQueue1, xaJMSmessage1);
                    response.addStepCompleted("Send xaJMSmessage1: " + xaJMSmessage1 + " to queue: " + xaQueue1);
                    // send jms message to xaQueue1 end

                    // send jms message to xaQueue2 start
                    TextMessage xaJMSmessage2 = xaJMSContext
                            .createTextMessage(String.format("Send Place %s to Datasource2", animal));
                    response.addStepCompleted("Created xaJMSmessage2: " + xaJMSmessage2);
                    xaJMSproducer.send(xaQueue2, xaJMSmessage2);

                    response.addStepCompleted("Send xaJMSmessage2: " + xaJMSmessage2 + " to queue: " + xaQueue2);
                    // send jms message to xaQueue2 end

                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new TestException(t);
                } finally {
                    safeClose(xaJMSContext);
                    response.addStepCompleted("Closed xaJMSContext");
                }
                return response;
            }
        };

        return (EJBResponse) invoke(testSupplier);
    }

    @Override
    public ClearDatabases clearAllDatabases() throws TestException {

        log.info("*** clearAllDatabases invoked ***");

        ThrowingSupplier<ClearDatabases> clearDatabasesSupplier = new ThrowingSupplier<ClearDatabases>() {
            @Override
            public ClearDatabases get() throws TestException {
                ClearDatabases cd = new ClearDatabases();
                try {
                    cd.setPlaceDatabase1(String.format("Removed %d Places", clearDatabase(em1, "Place")));
                    cd.setAnimalDatabase2(String.format("Removed %d Animals", clearDatabase(em2, "Animal")));
                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new TestException(t);
                }
                return cd;
            }
        };

        return (ClearDatabases) invoke(clearDatabasesSupplier);
    }

    protected String getCaller() {
        return ctx.getCallerPrincipal() == null ? "null" : ctx.getCallerPrincipal().getName();
    }

    private int clearDatabase(EntityManager entityManager, String col) {
        try {
            return entityManager.createQuery(String.format("DELETE FROM %s", col)).executeUpdate();
        } catch (Throwable t) {
            throw t;
        }
    }

    public <T> List<T> getAllFromDatabase(EntityManager entityManger, String col, Class clazz) {
        try {
            return entityManger.createQuery(String.format("SELECT x FROM %s x", col), clazz).getResultList();
        } catch (Throwable t) {
            throw t;
        }
    }
}