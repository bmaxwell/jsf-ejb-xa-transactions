# jsf-ejb-xa-transactions

- This is a Jakarta EE 10 example application, which provides some REST endpoints which insert into 2 different XA database datasources and sends a JMS Message to 2 different Queues to test XA.  It has a list method that returns the values in the databases and methods to remove the entries from the database.

The example uses standard portable Jakarta EE APIs, the only JBoss specifics are:

- ./src/main/webapp/WEB-INF/web.xml
-- The lookup-name configuration for datasources, JMS XA Connection factory and JMS Queues are defined in the EAP profile configuration (standalone*.xml or domain.xml).  If deploying on a different application server, these would need to be updated in the web.xml and use the corresponding values for the other application server.

~~~
        <lookup-name>java:jboss/datasources/OracleXADS1</lookup-name>
        ...
        <lookup-name>java:jboss/datasources/OracleXADS2</lookup-name>
        ...
        <lookup-name>java:/JmsXA</lookup-name>
        ...
        <lookup-name>java:/jms/queue/Queue1</lookup-name>
        ...
        <lookup-name>java:/jms/queue/Queue2</lookup-name>
~~~


- ./src/main/resources/META-INF/persistence.xml
-- The example uses JPA to insert values & query values from the database, and these EAP JPA specific configuration for Hibernate (EAP JPA) is used to create the database schema automatically using the hibernate.hbm2ddl.auto.  If run on another application server, the corresponding configuration would be needed, or the database schema would need to be created prior to running the application.

~~~
        <properties>
            <!-- Properties for Hibernate -->
            <property name="hibernate.hbm2ddl.auto"
                value="update"/>
            <property name="hibernate.show_sql" value="false"/>
            <property name="hibernate.archive.autodetect"
                value="class"/>
            <property name="hibernate.flushMode"
                value="FLUSH_AUTO"/>
        </properties>
~~~


# Setup

## Requirements

- JBoss EAP 8.1
- Oracle JDK 17 or 21 / OpenJDK 17 or 21
- Oracle JDBC driver jar
- Oracle Database

## Configure Database for XA Recovery

For EAP XA Recovery to run, the permissions below need to be granted to the Database user that is used when EAP connects.  (This can be the username in the XA datasource configuration or you can specify a different user for recovery in the EAP datasource configuration).

[https://docs.redhat.com/en/documentation/red_hat_jboss_enterprise_application_platform/7.4/html/configuration_guide/datasource_management#example_oracle_xa_datasource](https://docs.redhat.com/en/documentation/red_hat_jboss_enterprise_application_platform/7.4/html/configuration_guide/datasource_management#example_oracle_xa_datasource)

~~~
GRANT SELECT ON sys.dba_pending_transactions TO user;
GRANT SELECT ON sys.pending_trans$ TO user;
GRANT SELECT ON sys.dba_2pc_pending TO user;
GRANT EXECUTE ON sys.dbms_xa TO user;
~~~

## Unzip JBoss EAP

~~~
unzip -d $JBOSS_HOME jboss-eap-8.1.0.zip
~~~

## Configure JBoss EAP

### Add the JDBC Driver

- Put the JDBC driver (ojdbc8.jar) into the jsf-ejb-xa-transactions directory

### Configure the setup.properties

- edit the setup.properties and set the db, username, password that EAP should use to connect to the database

~~~
serverConfig=standalone-full.xml

db1.username=sys
db1.password=oracleDBpassword
db1.url="jdbc:oracle:thin:@localhost:1521:FREE"

db2.username=sys
db2.password=oracleDBpassword
db2.url="jdbc:oracle:thin:@localhost:1521:FREE"
~~~

### Run setup.sh

~~~
./setup.sh $JBOSS_HOME
~~~

## Build the Application

With Java 8/11/17, build the application:

~~~
mvn package
~~~

Deploy the application into $JBOSS_HOME
cp target/jsf-ejb-xa-transactions.war $JBOSS_HOME/standalone/deployments/


## Start JBoss EAP

~~~
$JBOSS_HOME/bin/standalone.sh -c standalone-full.xml
~~~

# Testing

## Rest Endpoints

### Test with random data

[http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/test](http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/test)

- This inserts some random values into the 2 datasources and sends a JMS Message to the 2 JMS Queues

### Test with specified data

http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/test?place=Florida&animal=Dolphin

- This inserts these values into the 2 datasources and sends a JMS Message to the 2 JMS Queues

### List the databases' contents

[http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/list](http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/list)

### Clear the databases' contents

[http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/clear](http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/clear)

## JSF Web Interface

[http://localhost:8080/jsf-ejb-xa-transactions](http://localhost:8080/jsf-ejb-xa-transactions)