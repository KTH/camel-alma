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
package se.kth.infosys.smx.alma.internal;

import javax.ws.rs.BadRequestException;

import org.apache.camel.Exchange;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.alma.AlmaUserService;
import se.kth.infosys.smx.alma.model.User;
import se.kth.infosys.smx.alma.model.WebServiceResult;

/**
 * Wrapper class translating exchanges to AlmaUserService requests.
 */
public class UserServiceWrapper {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AlmaUserService userService;

    /**
     * Constructor
     * @param host the Alma service environment.
     * @param apiKey the API key to use.
     * @throws Exception on errors.
     */
    public UserServiceWrapper(final String host, final String apiKey) throws Exception {
        userService = new AlmaUserService(host, apiKey);
    }

    /**
     * Get result from AlmaUserService.updateUser() and store in exchange.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void updateUser(final Exchange exchange) throws Exception {
        User user = exchange.getIn().getMandatoryBody(User.class);

        log.debug("Updating user with id {} in ALMA", user.getPrimaryId());
        exchange.getOut().setBody(userService.updateUser(user));
    }

    /**
     * Update user if exists, otherwise create it.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void createOrUpdateUser(final Exchange exchange) throws Exception {
        User user = exchange.getIn().getMandatoryBody(User.class);
        String userId = exchange.getIn().getHeader(AlmaMessage.Header.UserId, String.class);

        if (userId == null) {
            userId = user.getPrimaryId();
        }

        try {
            log.debug("Updating user {} with id {} in ALMA", user, userId);
            exchange.getIn().setBody(userService.updateUser(user, userId));
        } catch (BadRequestException e) {
            if (e.getResponse().getStatus() != 400) {
                log.error("Failed to update user", e);
                throw e;
            }
            WebServiceResult res = e.getResponse().readEntity(WebServiceResult.class);
            if (! AlmaUserService.USER_NOT_FOUND.equals(res.getErrorList().getError().get(0).getErrorCode())) {
                log.error("Failed to update user", e);
                throw e;
            }
            e.getResponse().close();

            log.debug("User not found, creating user with id {} in ALMA", user.getPrimaryId());
            exchange.getIn().setBody(userService.createUser(user));
        }
    }

    /**
     * Get result from AlmaUserService.createUser() and store in exchange.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void createUser(final Exchange exchange) throws Exception {
        User user = exchange.getIn().getMandatoryBody(User.class);

        log.debug("Creating user with id {} in ALMA", user.getPrimaryId());
        exchange.getIn().setBody(userService.createUser(user));
    }

    /**
     * Get result from AlmaUserService.getUser() and store in exchange.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void getUser(final Exchange exchange) throws Exception {
        String userId = ExchangeHelper.getMandatoryHeader(exchange, AlmaMessage.Header.UserId, String.class);

        log.debug("Getting user with id {} from ALMA", userId);
        exchange.getIn().setBody(userService.getUser(userId));
    }

    /**
     * Run AlmaUserService.deleteUser(), produces no output.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void deleteUser(final Exchange exchange) throws Exception {
        String userId = ExchangeHelper.getMandatoryHeader(exchange, AlmaMessage.Header.UserId, String.class);

        log.debug("Getting user with id {} from ALMA", userId);
        userService.deleteUser(userId);
    }
}