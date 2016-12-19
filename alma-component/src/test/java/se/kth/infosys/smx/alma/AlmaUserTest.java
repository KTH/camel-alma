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

import java.util.Properties;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import se.kth.infosys.smx.alma.internal.AlmaMessage;
import se.kth.infosys.smx.alma.model.User;

public class AlmaUserTest extends CamelTestSupport {
    private static final Properties properties = new Properties();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));
    }

    @Test
    public void testAlmaComponent() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        MockEndpoint mock2 = getMockEndpoint("mock:result2");
        mock2.expectedMinimumMessageCount(1);

        MockEndpoint mock3 = getMockEndpoint("mock:result3");
        mock3.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();

        Message in;
        User user;

        in = mock.getExchanges().get(0).getIn();
        user = in.getBody(User.class);
        assertEquals(AlmaMessage.Status.Ok, in.getHeader(AlmaMessage.Header.Status));
        assertEquals(properties.getProperty("test.data.user.first_name"), user.getFirstName());
        assertEquals(properties.getProperty("test.data.user.last_name"), user.getLastName());

        in = mock2.getExchanges().get(0).getIn();
        user = in.getBody(User.class);
        assertEquals(AlmaMessage.Status.Ok, in.getHeader(AlmaMessage.Header.Status));
        assertEquals("Magnus", user.getFirstName());
        assertEquals(properties.getProperty("test.data.user.last_name"), user.getLastName());

        in = mock3.getExchanges().get(0).getIn();
        user = in.getBody(User.class);
        assertEquals(AlmaMessage.Status.Ok, in.getHeader(AlmaMessage.Header.Status));
        assertEquals(properties.getProperty("test.data.user.first_name"), user.getFirstName());
        assertEquals(properties.getProperty("test.data.user.last_name"), user.getLastName());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                PropertiesComponent pc = (PropertiesComponent) context.getComponent("properties");
                pc.setLocation("classpath:test.properties");

                JaxbDataFormat jaxb = new JaxbDataFormat(false);
                jaxb.setContextPath("se.kth.infosys.smx.alma.model");

                from("timer:once?repeatCount=1")
                  .setHeader("almaUserId")
                  .simple("fjo@kth.se")
                  .to("alma://apikey:{{alma.apikey}}@{{alma.host}}/users/read")
                  .to("log:test1")
                  .to("mock:result")

                  .marshal(jaxb)
                  .to("log:test2")
                  .setHeader("first_name")
                  .simple("Magnus")
                  .to("xslt:replace-firstname.xslt")
                  .unmarshal(jaxb)
                  .to("alma://apikey:{{alma.apikey}}@{{alma.host}}/users/update")
                  .to("mock:result2")

                  .marshal(jaxb)
                  .to("log:test3")
                  .setHeader("first_name")
                  .simple("properties:test.data.user.first_name")
                  .to("xslt:replace-firstname.xslt")
                  .unmarshal(jaxb)
                  .to("alma://apikey:{{alma.apikey}}@{{alma.host}}/users/createOrUpdate")

                  .to("mock:result3")
                  .marshal(jaxb)
                  .to("log:test4");
            }
        };
    }
}
