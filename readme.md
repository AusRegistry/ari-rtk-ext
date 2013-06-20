## Downloads

The latest ari-rtk-addon is available for download. [ari-rtk-addon v1.0.0](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0.jar) ([sources](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0-sources.jar) | [javadoc](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0-javadoc.jar))

For more information, please read [Installation and Setup](#installation-and-setup).

## Building

To build the ari-toolkit, you must have the Java Development Kit (JDK) v6.0 or above installed. The project can be built with the command:

    `gradlew build`

Toolkit jar can be obtained directly as mentioned [here](#direct-download), or it can be built with the command

    `gradlew jar`

## Introduction

ARI’s Universal RTK Toolkit Add-On extends the Universal RTK Toolkit, by providing support for proprietary extensions implemented in Registries developed by ARI.

## Installation and Setup

### How to get the toolkit

#### Direct download

Obtain the latest toolkit here: [Toolkit v1.0.0](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0.jar) ([sources](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0-sources.jar) | [javadoc](http://ausregistry.github.com/repo/au/com/ausregistry/ari-rtk-addon/1.0.0/ari-rtk-addon-1.0.0-javadoc.jar))

#### Dependency Management

Use your build's dependency management tool to automatically download the toolkit from our repository.

* Repository: `http://ausregistry.github.com/repo/`
* groupId: `au.com.ausregistry`
* artifactId: `ari-rtk-addon`
* version: `1.0.0`

For example (using Maven):

    <repositories>
       <repository>
          <id>ausregistry.com.au</id>
          <url>http://ausregistry.github.com/repo</url>
       </repository>
    </repositories>

    <dependencies>
       <dependency>
          <groupId>au.com.ausregistry</groupId>
          <artifactId>ari-rtk-addon</artifactId>
          <version>1.0.0</version>
       </dependency>
    </dependencies>


#### Contribute

You can view the source on [GitHub/AusRegistry/ari-rtk-addon](http://github.com/ausregistry/ari-rtk-ext). Contributions via pull requests are welcome.

### Development documentation

The javadoc is available online: [ARI-RTK-Addon Javadoc](http://ausregistry.github.com/javadoc/ari-rtk-addon/index.html)

### Environment

The following environment specifics are required:

#### Java 6

The Toolkit has been developed against the standard Java 6 API.

Confirm the version of Java installed using:

`java –version`

#### UTF-8 Encoding

The Toolkit uses the Java VM default character set for character encoding. Consequently, the default character set must be UTF-8 to properly parse and encode UTF-8 characters in sent and received EPP messages. For English Windows machines, the default character set is typically Cp1252, and can be changed to UTF-8 by setting the system property:

    `file.encoding` to UTF-8.

This can be done on the command line with the syntax:

    java -Dfile.encoding=UTF-8 ...

#### UTC Date

Date objects must be set to UTC time instead of local time.

### Configuration

#### Properties

The Universal RTK Toolkit needs to be configured to access an ARI Domain Name Registry System (DNRS).This involves setting properties in two properties files present in the RTK toolkit:

* etc/rtk.properties
* ssl/ssl.properties

##### rtk.properties

The property in the rtk.properties file that needs to be set is *rtk.transport*. You will need to set:

    rtk.transport to ari.dnrs.rtk.addon.transport.EPPTransportTCPTLS.

Using this setting also includes a fix to send the correct length of the XML when using Unicode characters.

##### ssl.properties

The properties that need to be set in the ssl.properties file are:

    ssl.keystore.file=
    ssl.truststore.location=
    ssl.truststore.pass=

These properties identify the trust store to be used. Thus enabling changing the trust store independent of the JVM base trust store. The values of these properties are dependent on the Registrar. Values will be supplied by the Registry operator.

## Code Examples

Code examples of how to use the ARI proprietary extensions with the Universal Registry/Registrar Toolkit can be found at [Code Examples](https://github.com/AusRegistry/ari-rtk-ext/tree/master/src/main/java/ari/dnrs/rtk/addon/examples)








