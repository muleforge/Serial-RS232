#(default = "org/mule/module")



WELCOME
=======
Congratulations you have just created a new Mule module!

This wizard created a number of new classes and resources useful for new Mule module.  Each of the created files
contains documentation and _todo_ items where necessary.  Here is an overview of what was created.

./pom.xml:
A maven project descriptor that describes how to build this module.  If you enabled this project for the
MuleForge, this file will contain additional information about the project on MuleForge.

./assembly.xml:
A maven assembly descriptor that defines how this module will be packaged when you make a release.

./LICENSE.txt:
The open source license text for this project.

-----------------------------------------------------------------
./src/main/java/default/comm/i18n/CommMessages.java:

The CommMessages java class contains methods for access i18n messages embedded in your java code.

-----------------------------------------------------------------
./src/main/resources/META-INF/services/org/mule/i18n/comm-messages.properties

These message properties contain i18n strings used by CommMessages.java.

    
    -----------------------------------------------------------------
./src/main/resources/META-INF/mule-comm.xsd

The configuration schema file for this module. All configuration elements should be defined in this schema.

-----------------------------------------------------------------
./src/main/resources/META-INF/spring.schemas

Contains a mapping of the Namespace URI for this projects schema.

-----------------------------------------------------------------
./src/main/resources/META-INF/spring.handlers

Contains a mapping of the namespace handler to use for the schema in this project.

-----------------------------------------------------------------
./src/main/java/default/comm/config/CommNamespaceHandler.java

The implmentation of the namespace handler used to parse elements defined in mule-comm.xsd.
    
TESTING
=======

This  project also contains test classes that can be run as part of a test suite.

-----------------------------------------------------------------
./src/test/java/default/comm/CommTestCase.java

This is an example functional test case.  The test will work as is, but you need to configure it to actually test your
code.  For more information about testing see: http://www.mulesource.org/display/MULE2USER/Functional+Testing.

-----------------------------------------------------------------
./src/test/resources/comm-functional-test-config.xml

Defines the Mule configuration for the CommTestCase.java.

    -----------------------------------------------------------------
./src/test/java/default/comm/CommNamespaceHandlerTestCase.java

A test case that is used to test each of the configuration elements inside your mule-comm.xsd schema file.

-----------------------------------------------------------------
./src/test/resources/comm-namespace-config.xml

The configuration file for the CommNamespaceHandlerTestCase.java testcase.
    
ADDITIONAL RESOURCES
====================
Everything you need to know about getting started with Mule can be found here:
http://www.mulesource.org/display/MULE2INTRO/Home

For information about working with Mule inside and IDE with maven can be found here:
http://www.mulesource.org/display/MULE2INTRO/Setting+Up+Eclipse

Remember if you get stuck you can try getting help on the Mule user list:
http://www.mulesource.org/display/MULE/Mailing+Lists

Also, MuleSource, the company behind Mule, offers 24x7 support options:
http://www.mulesource.com/services/subscriptions.php

Enjoy your Mule ride!

The Mule Team

--------------------------------------------------------------------
This project was auto-genrated by the mule-project-archetype.

artifactId=mule-module-comm
description=Comm Transport
muleVersion=2.1.2
hasCustomSchema=y
hasBootstrap=n
projectType=${projectType}
packagePath=default
forgeProject=y

version=1.0-SNAPSHOT
groupId=default
basedir=C:\Java\mule-2.1.2
--------------------------------------------------------------------