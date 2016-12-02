package se.kth.infosys.smx.alma;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Alma Component producer.
 */
public class AlmaProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(AlmaProducer.class);
    private AlmaEndpoint endpoint;

    public AlmaProducer(AlmaEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
