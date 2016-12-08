package se.kth.infosys.alma;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmaRequestFilter implements ClientRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AlmaRequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        LOG.debug("Headers: {}", requestContext.getStringHeaders());
        if (requestContext.getEntity() != null) {
            LOG.debug(requestContext.getEntity().toString());
        }
    }
}
