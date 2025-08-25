
# Setup

## Unzip JBoss EAP

~~~
unzip -d $JBOSS_HOME jboss-eap-7.4.0.zip
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

### Test with specified data

http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/test?place=Florida&animal=Dolphin

### List the databases' contents

[http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/list](http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/list)

### Clear the databases' contents

[http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/clear](http://localhost:8080/jsf-ejb-xa-transactions/rest/cmt/clear)

## JSF Web Interface

[http://localhost:8080/jsf-ejb-xa-transactions](http://localhost:8080/jsf-ejb-xa-transactions)