# alma-component

Camel component wrapper for the alma-rest Java interface to the ExLibris ALMA API.

Only the producer part is implemented. There is no consumer. The Alma functionality
coverage is the same as in the alma-rest module.

## Using

To include the Maven dependency in the project, use:

```
<depencency>
  <groupId>se.kth.infosys.smx</groupId>
  <artifactId>alma-component</artifactId>
  <version>0.1.0</version>
</dependency>
```

Though not currently used by our selves, the alma-component is also available for
install as a Karaf feature in e.g. Apache ServiceMix:

```
> feature:repo-add mvn:se.kth.infosys.smx/alma-feature/0.1.0/xml/features
> feature:install [-v] alma-feature
```

### URI format

```
alma://apikey:<your_key>@<exlibris-environment>[/<api>][/<operation>]?options
```

Where `your_key` is the API key you get set in the ExLibris Alma portal: 
https://developers.exlibrisgroup.com/alma/apis#logging

The `exlibris-environment` is any of the hosted ExLibris environments:
https://developers.exlibrisgroup.com/alma/apis#calling, without the protocol part of
the URI, e.g.: api-eu.hosted.exlibrisgroup.com

`api` and `operation` are optional and can be set in message headers instead, see below.
If set, the only api currently supported is '/users'. If these are set in the URI
any value of a corresponding header in the message is ignored.

### Message headers

| Header | Description |
|--------|-------------|
| almaApi | Corresponds to /<api> in the URI, the only supported value is 'users' |
| almaOperation | Corresponds to /<operation> in the URI |
| almaUserId | An ALMA unique user ID. We currently match against all unique ID types. Make sure they don't overlap. |

| Operations | Description | Body |
|------------|-------------|------|
| create | Create a user. | ALMA model User Java object. |
| read | Get a user, user id must be specified with `almaUserId`. | none |
| update | Update the user | ALMA model User Java object. |
| delete | Delete a user, user id must be specified with `almaUserId`. | none |
| createOrUpdate | Will create a user, or try to update if it already exists. | ALMA model User Java object. |

## Development

### Testing

For testing you need to set the ExLibris environment to use and your API key, the
authentication method currently used, in src/test/resources/test.properties. There
is a template file test.properties.in for your convenvience.

### Building

There is nothing to test here that is not really calls to the actual API, so unless
you can set these properties you have to turn testing off in order to build.

E.g. `mvn install -Dmaven.test.skip=true`

