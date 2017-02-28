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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.ws.rs.BadRequestException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.util.ExchangeHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.alma.AlmaUserService;
import se.kth.infosys.smx.alma.model.User;
import se.kth.infosys.smx.alma.model.UserIdentifier;
import se.kth.infosys.smx.alma.model.UserIdentifiers;
import se.kth.infosys.smx.alma.model.WebServiceResult;

/**
 * Wrapper class translating exchanges to AlmaUserService requests.
 */
public class UserServiceWrapper {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AlmaUserService userService;
    private final HashSet<String> NO_UPDATE_PROPERTIES = new HashSet<String>(Arrays.asList(new String[]{"externalId"}));

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
        final Message in = exchange.getIn();
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Failed);
        User user = exchange.getIn().getMandatoryBody(User.class);

        User currentUser = getUserByUser(user);
        copyPropertiesFromTo(currentUser, user);
        log.debug("Updating user with id {} in ALMA", currentUser.getPrimaryId());
        in.setBody(userService.updateUser(user, currentUser.getPrimaryId()));
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Ok);
    }

    private void copyPropertiesFromTo(User from, User to) throws Exception {
        for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(User.class)) {
            if (NO_UPDATE_PROPERTIES.contains(property.getName())) {
                log.debug("Copying property {}", property.getName());
                Method read = property.getReadMethod();
                Method write = property.getWriteMethod();
                if (read.invoke(from) != null) {
                    write.invoke(to, read.invoke(from));
                }
            }
        }
    }

    /**
     * Update user if exists, otherwise create it.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void createOrUpdateUser(final Exchange exchange) throws Exception {
        try {
            updateUser(exchange);
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

            log.debug("Could not update non-existing user in Alma, creating instead.");
            createUser(exchange);
        }
    }

    /**
     * Get result from AlmaUserService.createUser() and store in exchange.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void createUser(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Failed);
        User user = in.getMandatoryBody(User.class);

        try {
            log.debug("Creating user with id {} in ALMA", user.getPrimaryId());
            in.setBody(userService.createUser(user));
            in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Ok);
        } catch (BadRequestException e) {
            if (e.getResponse().getStatus() != 400) {
                log.error("Failed to create user", e);
            } else {
                WebServiceResult res = e.getResponse().readEntity(WebServiceResult.class);
                String code = res.getErrorList().getError().get(0).getErrorCode();
                String message = res.getErrorList().getError().get(0).getErrorMessage();
                log.error("Code: " + code + ", message: " + message, e);
                e.getResponse().close();
            }
            throw e;
        }
    }

    /**
     * Get result from AlmaUserService.getUser() and store in exchange.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void getUser(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Failed);

        if (in.getHeader(AlmaMessage.Header.UserId) != null) {
            in.setBody(getUserById(in.getHeader(AlmaMessage.Header.UserId, String.class)));
        } else {
            in.setBody(getUserByUser(in.getMandatoryBody(User.class)));
        }
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Ok);
    }

    private User getUserByUser(User user) {
        UserIdentifiers identifiers = user.getUserIdentifiers();
        Iterator<UserIdentifier> iterator = identifiers.getUserIdentifier().iterator();
        while (iterator.hasNext()) {
            UserIdentifier identifier = (UserIdentifier) iterator.next();
            try {
                log.debug("Finding user by identifer: {}: {}",
                        identifier.getIdType().getValue(),
                        identifier.getValue());
                return userService.getUser(identifier.getValue());
            } catch (Exception e) {}
        }
        return userService.getUser(user.getPrimaryId());
    }

    private User getUserById(String userId) {
        log.debug("Getting user with id {} from ALMA", userId);
        return userService.getUser(userId);
    }

    /**
     * Run AlmaUserService.deleteUser(), produces no output.
     * @param exchange the Camel exchange.
     * @throws Exception on errors.
     */
    public void deleteUser(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Failed);
        String userId = ExchangeHelper.getMandatoryHeader(exchange, AlmaMessage.Header.UserId, String.class);

        log.debug("Getting user with id {} from ALMA", userId);
        if (userService.deleteUser(userId)) {
            log.debug("User with id {} deleted from Alma.", userId);
            in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Ok);
        } else {
            log.debug("User with id {} NOT deleted from Alma, maybe not found.", userId);
            in.setHeader(AlmaMessage.Header.Status, AlmaMessage.Status.Failed);
        }
    }
}