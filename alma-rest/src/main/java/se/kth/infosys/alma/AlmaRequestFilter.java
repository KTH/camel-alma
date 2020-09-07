package se.kth.infosys.alma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class AlmaRequestFilter implements ClientRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AlmaRequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Target: {}", requestContext.getUri().toString());
            LOG.trace("Method: {}", requestContext.getMethod());
            LOG.trace("Headers: {}", requestContext.getStringHeaders());
            if (requestContext.getEntity() != null) {
                LOG.trace(requestContext.getEntity().toString());
            }
        }
    }
}
