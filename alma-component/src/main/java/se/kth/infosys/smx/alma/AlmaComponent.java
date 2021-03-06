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

import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

/**
 * Represents the component that manages {@link AlmaEndpoint}.
 */
public class AlmaComponent extends DefaultComponent {
    private static final Pattern PATH_PATTERN = Pattern.compile("(/(?<api>users))+(/(?<operation>create|read|update|delete|createOrUpdate))+");

    /**
     * {@inheritDoc}
     */
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        AlmaEndpoint endpoint = new AlmaEndpoint(uri, this);
        setProperties(endpoint, parameters);

        URI formattedUri = new URI(uri);
        endpoint.setEnvironment(formattedUri.getHost());
        if (formattedUri.getUserInfo() != null) {
            String[] auth = formattedUri.getUserInfo().split(":");
            assert("apikey".equals(auth[0]));
            endpoint.setApiKey(auth[1]);
        }

        Matcher matcher = PATH_PATTERN.matcher(formattedUri.getPath());
        if (matcher.matches()) {
            endpoint.setApi(matcher.group("api"));
            endpoint.setOperation(matcher.group("operation"));
        }
        return endpoint;
    }
}
