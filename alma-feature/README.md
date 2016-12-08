# alma-feature

Feature packaging of the ExLibris ALMA endpoint with dependencies and additional
resources. Just an XML-file (feature.xml) and build procedure.

Though not currently used by our selves, this makes it possible to install the
alma-component and dependencies as a Karaf feature in e.g. Apache ServiceMix:

```
> feature:repo-add mvn:se.kth.infosys.smx.alma/alma-feature/0.1.0/xml/features
> feature:install [-v] alma-feature
```

See top-level [toplevel README.md](../README.md) for more information.
