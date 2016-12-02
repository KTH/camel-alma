package se.kth.infosys.smx.alma;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

/**
 * Represents the component that manages {@link Alma ComponentEndpoint}.
 */
public class AlmaComponent extends UriEndpointComponent {
    
    public AlmaComponent() {
        super(AlmaEndpoint.class);
    }

    public AlmaComponent(CamelContext context) {
        super(context, AlmaEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new AlmaEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
