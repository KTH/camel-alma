package se.kth.infosys.alma;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public abstract class AlmaService {
    protected final Client client;
    protected final String apiKey;
    
    protected AlmaService(String apiKey) throws Exception {
        this.apiKey = apiKey;
        final SSLContext context = SSLContext.getDefault();
        this.client = ClientBuilder.newBuilder()
                .sslContext(context)
                .build();
    }
}
