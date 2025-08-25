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

import javax.annotation.security.PermitAll;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.as.quickstart.ejb.api.EJBRequest;
import org.jboss.as.quickstart.ejb.api.EJBResponse;
import org.jboss.as.quickstart.ejb.api.SingletonException;
import org.jboss.ejb3.annotation.SecurityDomain;

/**
 * @author bmaxwell
 *
 */
@Singleton
// @Local(TransactionSingletonEJB.class)
@SecurityDomain("other")
@PermitAll
@TransactionManagement(TransactionManagementType.CONTAINER)
@Path("/cmt")
public class CMTSingletonEJB extends AbstractEJB {

    // ./bin/standalone.sh -c standalone-full-ha.xml
    // http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/test

    // module add --name=com.oracle --resources=ojdbc8.jar
    // --dependencies=javaee.api,sun.jdk,ibm.jdk,javax.api,javax.transaction.api

    // /subsystem=datasources/jdbc-driver=oracle:add(driver-name=oracle,driver-module-name=com.oracle,driver-xa-datasource-class-name=oracle.jdbc.xa.client.OracleXADataSource)
    // xa-data-source add --name=OracleXADS --jndi-name=java:jboss/OracleXADS --driver-name=oracle --user-name=admin
    // --password=admin --validate-on-match=true --background-validation=false
    // --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker
    // --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter
    // --same-rm-override=false --xa-datasource-properties={"URL"=>"jdbc:oracle:thin:@oracleHostName:1521:orcl"}

    // /subsystem=messaging-activemq/server=default/jms-queue=Queue1:add(entries=["java:/jms/queue/Queue1"])
    // /subsystem=messaging-activemq/server=default/jms-queue=Queue2:add(entries=["java:/jms/queue/Queue2"])

    /*
     * https://docs.redhat.com/en/documentation/red_hat_jboss_enterprise_application_platform/7.4/html/configuration_guide/
     * datasource_management#example_oracle_xa_datasource GRANT SELECT ON sys.dba_pending_transactions TO user; GRANT SELECT ON
     * sys.pending_trans$ TO user; GRANT SELECT ON sys.dba_2pc_pending TO user; GRANT EXECUTE ON sys.dbms_xa TO user;
     */

    /**
     * This is EJB Method that the JSF Page can call
     */
    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws SingletonException {

        log.info("*** test invoked ***");
        return invokeCMT(request, placeName, animalName);
    }

    /**
     * This is the REST Call if invoked using the REST interface
     *
     * @param placeName
     * @param animalName
     * @return
     */
    @GET
    @Path("/test")
    @Produces({ "application/json", "text/plain" })
    public Response testREST(@QueryParam("place") String placeName, @QueryParam("animal") String animalName) {

        log.info("*** testREST invoked ***");
        EJBRequest request = new EJBRequest();

        try {
            EJBResponse response = test(request, placeName, animalName);
            return Response.ok().entity(response).build();
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.serverError().entity(t).build();
        }
    }
}