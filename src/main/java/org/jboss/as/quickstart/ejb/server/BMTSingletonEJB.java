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

/**
 * @author bmaxwell
 *
 */
@Singleton
//@Local(TransactionSingletonEJB.class)
@PermitAll
@TransactionManagement(TransactionManagementType.BEAN)
@Path("/bmt")
public class BMTSingletonEJB extends AbstractEJB {

    /**
     * This is EJB Method that the JSF Page can call
     */
    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws SingletonException {

        log.info("*** test invoked ***");
        return invokeBMT(request, placeName, animalName);
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