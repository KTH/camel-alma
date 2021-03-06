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
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * Represents a Alma Component endpoint.
 */
@ManagedResource
@UriEndpoint(scheme = "alma", title = "Alma Component", syntax="alma://apikey@environment[/api][/operation]")
public class AlmaEndpoint extends DefaultEndpoint {
    @UriPath(label="producer", description = "ExLibris Alma target environment") @Metadata(required = true)
    private String environment;

    @UriPath(label="producer", description = "API key to use with target environment") @Metadata(required = true)
    private String apiKey;

    @UriPath(label="producer", description = "Target API to use (only /users supported)")
    private String api;

    @UriPath(label="producer", description = "API operation")
    private String operation;

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
    public Consumer createConsumer(Processor processor) {
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
     * @param environment the FQDN for the environment.
     */
    @ManagedAttribute(description = "ExLibris ALMA target environment")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @return the ExLibris ALMA environment.
     */
    @ManagedAttribute(description = "ExLibris ALMA target environment")
    public String getEnvironment() {
        return this.environment;
    }

    /**
     * Set the API key to use for authentication with ExLibris ALMA.
     * @param apiKey the API key.
     */
    @ManagedAttribute(description = "API key to use for authentication with ExLibris ALMA")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the API key for the ExLibris ALMA environment.
     */
    @ManagedAttribute(description = "API key to use for authentication with ExLibris ALMA")
    public String getApiKey() {
        return this.apiKey;
    }

    /**
     * Set the api to use with this endpoint.
     * @param api the ALMA api to use.
     */
    @ManagedAttribute(description = "API to use with this ExLibris ALMA endpoint")
    public void setApi(String api) {
        this.api = api;
    }

    /**
     * Get the api to use with this endpoint.
     * @return the api.
     */
    @ManagedAttribute(description = "API to use with this ExLibris ALMA endpoint")
    public String getApi() {
        return this.api;
    }

    /**
     * Set the API operation to use for this endpoint.
     * @param operation the ALMA api operation to use.
     */
    @ManagedAttribute(description = "API operation to use with this ExLibris ALMA endpoint")
    public void setOperation(String operation) {
        this.operation = operation;

    }

    /**
     * Get the operation to use with this endpoint.
     * @return the operation.
     */
    @ManagedAttribute(description = "API operation to use with this ExLibris ALMA endpoint")
    public String getOperation() {
        return this.operation;
    }
}
