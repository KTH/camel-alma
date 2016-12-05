/*
 * MIT License
 *
 * Copyright (c) 2016 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.infosys.smx.alma;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;

/**
 * Represents a Alma Component endpoint.
 */
@UriEndpoint(scheme = "alma", title = "Alma Component", syntax="alma://apikey@environment[/api][/operation]", consumerClass = AlmaConsumer.class, label = "Alma Component")
public class AlmaEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String host;
    @UriPath @Metadata(required = "true")
    private String apiKey;

    private String api;
    private String operation;

    public AlmaEndpoint() {
    }

    public AlmaEndpoint(String uri, AlmaComponent component) {
        super(uri, component);
    }

    /**
     * {@inheritDoc}
     */
    public Producer createProducer() throws Exception {
        return new AlmaProducer(this);
    }

    /**
     * {@inheritDoc}
     */
    public Consumer createConsumer(Processor processor) throws Exception {
        return new AlmaConsumer(this, processor);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Set environment for ExLibris ALMA api calls, e.g. api-eu.hosted.exlibrisgroup.com
     * @param host the FQDN for the environment.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the ExLibris ALMA environment.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Set the API to use for authentication with ExLibris ALMA.
     * @param apiKey the API key.
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the API key for the ExLibris ALMA environment.
     */
    public String getApiKey() {
        return this.apiKey;
    }

    /**
     * Set the api to use with this endpoint.
     * @param api the ALMA api to use.
     */
    public void setApi(String api) {
        this.api = api;
    }

    /**
     * Get the api to use with this endpoint.
     * @return the api.
     */
    public String getApi() {
        return this.api;
    }

    /**
     * Set the API operation to use for this endpoint.
     * @param operation the ALMA api operation to use.
     */
    public void setOperation(String operation) {
        this.operation = operation;
        
    }

    /**
     * Get the operation to use with this endpoint.
     * @return the operation.
     */
    public String getOperation() {
        return this.operation;
    }
}
