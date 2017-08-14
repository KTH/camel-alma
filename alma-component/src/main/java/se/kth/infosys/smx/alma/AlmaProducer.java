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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ExchangeHelper;

import se.kth.infosys.smx.alma.internal.AlmaMessage;
import se.kth.infosys.smx.alma.internal.UserServiceWrapper;

/**
 * The Alma Component producer.
 */
public class AlmaProducer extends DefaultProducer {
    private final UserServiceWrapper userService;
    private final AlmaEndpoint endpoint;

    public AlmaProducer(AlmaEndpoint endpoint) throws Exception {
        super(endpoint);
        this.endpoint = endpoint;
        this.userService = new UserServiceWrapper(endpoint.getEnvironment(), endpoint.getApiKey());
    }

    /**
     * {@inheritDoc}
     */
    public void process(Exchange exchange) throws Exception {
        final String api;

        if (endpoint.getApi() != null) {
            api = endpoint.getApi();
        } else {
            api = ExchangeHelper.getMandatoryHeader(exchange, AlmaMessage.Header.Api, String.class);
        }

        switch (api) {
        case AlmaMessage.Api.Users:
            processUsersRequest(exchange);
            break;
        default:
            throw new UnsupportedOperationException("Api: " + api + " not supported");
        }
    }

    private void processUsersRequest(Exchange exchange) throws Exception {
        final String operation;

        if (endpoint.getOperation() != null) {
            operation = endpoint.getOperation();
        } else {
            operation = ExchangeHelper.getMandatoryHeader(exchange, AlmaMessage.Header.Operation, String.class);
        }

        switch (operation) {
        case AlmaMessage.Operation.Read:
            userService.getUser(exchange);
            break;
        case AlmaMessage.Operation.Create:
            userService.createUser(exchange);
            break;
        case AlmaMessage.Operation.Update:
            userService.updateUser(exchange);
            break;
        case AlmaMessage.Operation.CreateOrUpdate:
            userService.createOrUpdateUser(exchange);
            break;
        case AlmaMessage.Operation.Delete:
            userService.deleteUser(exchange);
            break;
        default:
            throw new UnsupportedOperationException("Operation: " + operation + " not supported");
        }
    }
}
