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

package org.jboss.as.quickstart.ejb.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author bmaxwell
 *
 */
public class EJBResponse implements Serializable {

    private EJBRequest request;
    private String serverName;
    private String caller;
    private String methodName;

    @JsonbProperty("stepsCompleted")
    private List<String> stepsCompleted = new ArrayList<String>();

    public EJBResponse() {
    }

    public EJBResponse(EJBRequest request, String serverName, String caller, String methodName) {
        this.serverName = serverName;
        this.caller = caller;
        this.methodName = methodName;
        this.request = request;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public EJBRequest getRequest() {
        return request;
    }

    public void setRequest(EJBRequest request) {
        this.request = request;
    }

    public List<String> getStepsCompleted() {
        return stepsCompleted;
    }

    public void setStepsCompleted(List<String> stepsCompleted) {
        this.stepsCompleted = stepsCompleted;
    }

    public void addStepCompleted(String step, Object... args) {
        this.stepsCompleted.add(String.format(step, args));
    }

    @Override
    public String toString() {
        return String.format("caller: %s called server: %s methodName: %s", caller, serverName, methodName);
    }
}