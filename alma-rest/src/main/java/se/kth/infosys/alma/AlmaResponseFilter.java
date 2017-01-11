package se.kth.infosys.alma;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmaResponseFilter implements ClientResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AlmaResponseFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        LOG.trace("Response status: {} {}", responseContext.getStatus(), responseContext.getStatusInfo().toString());
        LOG.trace("Response headers: {}", responseContext.getHeaders());
    }
}
