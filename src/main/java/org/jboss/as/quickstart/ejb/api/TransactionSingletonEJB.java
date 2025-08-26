package org.jboss.as.quickstart.ejb.api;

public interface TransactionSingletonEJB {

    public EJBResponse test(EJBRequest request, String placeName, String animalName) throws TestException;

}