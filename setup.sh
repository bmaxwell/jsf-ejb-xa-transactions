##!/bin/bash

jbossHome=$1
config=$2

if [ "x${jbossHome}y" == "xy" ]; then

	echo "Usage setup.sh /path/to/jboss"
	exit 0

fi

addJBossUsers() {

	$jbossHome/bin/add-user.sh -a -u ejbuser -p redhat1! -g ejbrole
	$jbossHome/bin/add-user.sh -a -u webuser -g webrole,ejbrole -p redhat1!
	$jbossHome/bin/add-user.sh -u admin -p redhat1!

}

configure() {
  # You can also specify -D instead of setup.properties when calling jboss-cli.sh" 
	#-Dusername="system" -Dpassword="dbpassword" -Ddb="FREE" -DserverConfig=standalone-full.xml --properties=cli.properties 

	echo "Using config properties from setup.properties"
	$jbossHome/bin/jboss-cli.sh --resolve-parameter-values --file=setup.cli --properties=setup.properties


}


addJBossUsers
configure
