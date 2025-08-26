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

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.SessionContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.XAConnectionFactory;
import javax.jms.XAJMSContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.as.quickstart.ejb.api.EJBRequest;
import org.jboss.as.quickstart.ejb.api.EJBResponse;
import org.jboss.as.quickstart.ejb.api.TestException;
import org.jboss.as.quickstart.jpa.model.Animal;
import org.jboss.as.quickstart.jpa.model.ClearDatabases;
import org.jboss.as.quickstart.jpa.model.ListDatabases;
import org.jboss.as.quickstart.jpa.model.Place;

/**
 * @author bmaxwell
 *
 */
@PermitAll
public abstract class AbstractEJB extends AbstractUtilBase {

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

    /** REST Methods shared by CMT & BMTStatelessEJBs **/
    /***************************************************/
    @GET
    @Path("/list")
    @Produces({ "application/json", "text/plain" })
    public Response listREST() {

        log.info("*** listREST invoked ***");

        try {
            return Response.ok().entity(list()).build();
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.serverError().entity(t).build();
        }
    }

    @GET
    @Path("/clear")
    @Produces({ "application/json", "text/plain" })
    public Response clearAllDatabasesREST() {

        log.info("*** clearAllDatabasesREST invoked ***");

        try {
            return Response.ok().entity(clearAllDatabases()).build();
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.serverError().entity(t).build();
        }
    }

    public ListDatabases list() throws Exception {

        log.info("*** list invoked ***");

        try {
            ListDatabases ld = new ListDatabases();
            ld.setPlaceDatabase1(getAllPlacesDatabase1());
            ld.setAnimalDatabase2(getAllAnimalsDatabase2());
            return ld;
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    public ClearDatabases clearAllDatabases() throws Exception {

        log.info("*** clearAllDatabases invoked ***");

        ClearDatabases cd = new ClearDatabases();
        try {
            cd.setPlaceDatabase1(String.format("Removed %d Places", clearDatabase1()));
            cd.setAnimalDatabase2(String.format("Removed %d Animals", clearDatabase2()));
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
        return cd;
    }

    protected String getCaller() {
        return ctx.getCallerPrincipal() == null ? "null" : ctx.getCallerPrincipal().getName();
    }

    private EJBResponse testInternal(EJBRequest request, String placeName, String animalName) throws TestException {

        log.info("*** test invoked ***");

        XAJMSContext xaJMSContext = null;

        EJBResponse response = new EJBResponse();

        try {

            Place place = (placeName == null || placeName.isEmpty()) ? Place.randomPlace() : new Place(placeName);
            Animal animal = (animalName == null || animalName.isEmpty()) ? Animal.randomAnimal() : new Animal(animalName);

            // save Place , Animal, have the MessageConsumers update something to indicate they have been processed.

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

            TextMessage xaJMSmessage1 = xaJMSContext.createTextMessage(String.format("Send Place %s to Datasource1", place));
            response.addStepCompleted("Created xaJMSmessage1: " + xaJMSmessage1);

            xaJMSproducer.send(xaQueue1, xaJMSmessage1);
            response.addStepCompleted("Send xaJMSmessage1: " + xaJMSmessage1 + " to queue: " + xaQueue1);
            // send jms message to xaQueue1 end

            // send jms message to xaQueue2 start
            TextMessage xaJMSmessage2 = xaJMSContext.createTextMessage(String.format("Send Place %s to Datasource2", animal));
            response.addStepCompleted("Created xaJMSmessage2: " + xaJMSmessage2);
            xaJMSproducer.send(xaQueue2, xaJMSmessage2);

            response.addStepCompleted("Send xaJMSmessage2: " + xaJMSmessage2 + " to queue: " + xaQueue2);
            // send jms message to xaQueue2 end

        } catch (Throwable t) {
            t.printStackTrace();
            throw new TestException(t);
        } finally {

            // Switched to use JPA which manages the connections
            // safeClose(conn1);
            // response.addStepCompleted("Closed conn1");

            // safeClose(conn2);
            // response.addStepCompleted("Closed conn2");

            safeClose(xaJMSContext);
            response.addStepCompleted("Closed xaJMSContext");
        }

        return response;
    }

    public EJBResponse invokeCMT(EJBRequest request, String placeName, String animalName) throws TestException {
        return testInternal(request, placeName, animalName);
    }

    public EJBResponse invokeBMT(EJBRequest request, String placeName, String animalName) throws TestException {

        EJBResponse response = null;
        UserTransaction utx = null;
        try {
            utx = ctx.getUserTransaction();
            utx.setTransactionTimeout(request.getTxTimeoutSeconds());
            utx.begin();

            response = testInternal(request, placeName, animalName);

            utx.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                utx.rollback();
            } catch (Throwable t2) {
                t2.printStackTrace();
            }
            throw new TestException(String.format("invokeBMT error running: %s", request), t);
        } finally {
        }

        return response;
    }

    public int clearDatabase1() {
        try {
            return em1.createQuery("DELETE FROM Place").executeUpdate();
        } catch (Throwable t) {
            throw t;
        }
    }

    public int clearDatabase2() {
        try {
            return em2.createQuery("DELETE FROM Animal").executeUpdate();
        } catch (Throwable t) {
            throw t;
        }
    }

    public List<Place> getAllPlacesDatabase1() {
        try {
            return em1.createQuery("FROM Place", Place.class).getResultList();
        } catch (Throwable t) {
            throw t;
        }
    }

    public List<Animal> getAllAnimalsDatabase2() {
        try {
            return em2.createQuery("FROM Animal", Animal.class).getResultList();
        } catch (Throwable t) {
            throw t;
        }
    }

    protected EJBResponse invokeSleep(String methodName, EJBRequest request) throws TestException {

        log.info(methodName + " start " + request.getSleepSeconds() + " sec sleep");
        sleep(request.getSleepSeconds());
        log.info(methodName + " finished " + request.getSleepSeconds() + " sec sleep");

        return createResopnse(request, methodName);
    }

    private EJBResponse createResopnse(EJBRequest request, String methodName) {
        return new EJBResponse(request, JBOSS_NODE_NAME, getCaller(), methodName);
    }

}