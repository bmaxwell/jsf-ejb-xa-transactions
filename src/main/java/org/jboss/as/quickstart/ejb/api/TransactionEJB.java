package org.jboss.as.quickstart.ejb.api;

import org.jboss.as.quickstart.jpa.model.ClearDatabases;
import org.jboss.as.quickstart.jpa.model.ListDatabases;

public interface TransactionEJB {

    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws TestException;

    public ListDatabases list() throws TestException;

    public ClearDatabases clearAllDatabases() throws TestException;

}