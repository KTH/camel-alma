# camel-alma

An Apache Camel component to integrate with ExLibris ALMA. So far it only
supports basic functionality for creation and maintenance of users in the
system. This functionality is easily expanded, but we currently have no need
for it.

## Project overview

The camel-alma project consists of three sub modules. See the respective
README.md of these for details about each.

### alma-rest Standalone REST interface

The alma-rest maven module contains a standalone library for communicating 
with the REST API. It is based on JAX RS and uses JAXB to generate a Java
model from the XSDs provided by ExLibris for the API interface. The library
is packaged as an OSGi bundle.

The model is used to consistently marshal/unmarshal Alma objects to/from
XML and JSON with libraries such as Jackson.

### alma-component Camel component

Exposes the functionality of the REST interface as a Camel component to 
use in Camel routes. See the [./alma-component/README.md](README.md).

### alma-feature Karaf feature

A Karaf feature definition to manage deploys of the above modules in an OSGi
environment such as Apache ServiceMix/RedHat Fuse. We are currently not using
this, but it is included for completeness.
