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
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.as.quickstart.ejb.api.EJBRequest;
import org.jboss.as.quickstart.ejb.api.EJBResponse;
import org.jboss.as.quickstart.ejb.api.TestException;

/**
 * @author bmaxwell
 *
 */
@Stateless
// @Local(TransactionSingletonEJB.class)
@PermitAll
@TransactionManagement(TransactionManagementType.BEAN)
public class BMTSingletonEJB extends AbstractEJB {

    /**
     * This is EJB Method that the JSF Page can call
     */
    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws TestException {

        log.info("*** test invoked ***");
        return invokeBMT(request, placeName, animalName);
    }
}