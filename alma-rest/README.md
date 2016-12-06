# alma-rest

Stand alone simple JAX RS and JAXB based interface to the ExLibris ALMA REST API.
See https://developers.exlibrisgroup.com/alma/apis

It only implements some of the calls for user administration in the User interface,
but is easily expanded.

## Development

There is very little code here due to the high level of abstraction in the JAX RS
library. In se.kth.infosys.smx.alma.rest you find the classes wrapping the REST
interface.

In se.kth.infosys.smx.alma.model you find the auto generated JAXB model
representating the data used by the REST interface, it is not available in
the source tree but found as a build output under target after build.

The model is based on XSDs downloaded from ExLibris and can be used to 
consistently marshal/unmarshal objects to/from XML and JSON with libraries
such as Jackson.

### Testing

For testing you need to set the ExLibris environment to use and your API key, the
authentication method currently used, in src/test/resources/test.properties. There
is a template file test.properties.in for your convenvience.

### Building

There is nothing to test here that is not really calls to the actual API, so unless
you can set these properties you have to turn testing off in order to build.

E.g. `mvn install -Dmaven.test.skip=true`

