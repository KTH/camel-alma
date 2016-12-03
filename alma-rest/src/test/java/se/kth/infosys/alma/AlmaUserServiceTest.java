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
package se.kth.infosys.alma;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.ws.rs.BadRequestException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.exlibrisgroup.alma.User;
import com.exlibrisgroup.alma.WebServiceResult;

public class AlmaUserServiceTest {
    private static final Logger logger = Logger.getLogger(AlmaUserServiceTest.class);
    private AlmaUserService alma;
    private Properties properties = new Properties();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("alma.host");
        String apiKey = properties.getProperty("alma.apikey");
        alma = new AlmaUserService(host, apiKey);
    }

    @Test
    public void testGetUser() {
        User user = alma.getUser("fjo@kth.se");
        assertEquals("fjo@kth.se", user.getPrimaryId());
        assertEquals("Fredrik", user.getFirstName());
        assertEquals("Jönsson", user.getLastName());
    }

//    @Test
//    public void testGetUserKthid() {
//        try {
//            User user = alma.getUser("u1fjolle");
//            assertEquals("fjo@kth.se", user.getPrimaryId());
//            assertEquals("Fredrik", user.getFirstName());
//            assertEquals("Jönsson", user.getLastName());
//        } catch (BadRequestException e) {
//            WebServiceResult res = e.getResponse().readEntity(WebServiceResult.class);
//            logger.error(res.getErrorList().getErrors().get(0).getErrorMessage());
//            assert(false);
//        }
//     }

    @Test
    public void testGetNotExistingUser() {
        try {
            alma.getUser("fjokasjdlkjasldjald@kth.se");
            assert(false);
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
            WebServiceResult res = e.getResponse().readEntity(WebServiceResult.class);
            assertEquals(AlmaUserService.USER_NOT_FOUND,
                    res.getErrorList().getErrors().get(0).getErrorCode());
        }
    }

    @Test
    public void testUpdateUser() {
        try {
            User user = alma.getUser("fjo@kth.se");

            user.setFirstName("Magnus");    
            user = alma.updateUser(user);
            assertEquals("Magnus", user.getFirstName());
    
            user.setFirstName("Fredrik");
            user = alma.updateUser(user);
            assertEquals("Fredrik", user.getFirstName());
        } catch (BadRequestException e) {
            WebServiceResult res = e.getResponse().readEntity(WebServiceResult.class);
            logger.error(res.getErrorList().getErrors().get(0).getErrorMessage());
            assert(false);
        }
    }
}
