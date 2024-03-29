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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import se.kth.infosys.smx.alma.model.ObjectFactory;
import se.kth.infosys.smx.alma.model.User;
import se.kth.infosys.smx.alma.model.Users;

/**
 * The Alma service endpoint for users.
 */
public class AlmaUserService extends AlmaService {
    public static final String FAILED_TO_DELETE_USER = "401850";
    public static final String USER_NOT_FOUND = "401861";
    public static final String USER_WITH_ID_TYPE_NOT_FOUND = "401890";
    public static final String UTF_8 = "utf-8";

    /**
     * Initialize the Web Service target.
     * @param host The targeted ExLibris environment.
     * @param apiKey The ExLibris ALMA API key.
     * @throws Exception on SSL errors
     */
    public AlmaUserService(String host, String apiKey) throws Exception {
        super(String.format("https://%s/almaws/v1/users", host), apiKey);
    }

    /**
     * Get a User from ALMA with specified ID.
     * @param userId the user identifier
     * @return The user as found in Alma
     */
    public User getUser(final String userId) {
        return alma.path("/{user_id}")
                .resolveTemplate("user_id", userId)
                .queryParam("apikey", apiKey)
                .request()
                .accept(MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8))
                .get(User.class);
    }

    /**
     * Get all users from ALMA. Use with care if many users. Since
     * requests are batched in counts of 100, you may consume your
     * ExLibris API call quota faster than expected.
     *
     * @return A list of all users found in Alma.
     */
    public Users getUsers() {
        final Users users = new ObjectFactory().createUsers();
        final int LIMIT = 100;

        Users nextUsers;
        int offset = 0;

        do {
            nextUsers = alma.path("")
                .queryParam("apikey", apiKey)
                .queryParam("limit", LIMIT)
                .queryParam("offset", offset)
                .request()
                .accept(MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8))
                .get(Users.class);
            users.getUser().addAll(nextUsers.getUser());
            offset += LIMIT;
        } while (nextUsers.getUser().size() == LIMIT);

        return users;
    }

    /**
     * Update a User from ALMA using the primary ID.
     * @param user the ALMA user object
     * @return the user as updated in ALMA.
     */
    public User updateUser(final User user) {
        return alma.path("/{user_id}")
                .resolveTemplate("user_id", user.getPrimaryId())
                .queryParam("apikey", apiKey)
                .request()
                .accept(MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8))
                .put(Entity.entity(user, MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8)), User.class);
    }

    /**
     * Update a User from ALMA with specified ID.
     * @param user the ALMA user object
     * @param userId the user identifier
     * @return the user as updated in ALMA.
     */
    public User updateUser(final User user, String userId) {
        return alma.path("/{user_id}")
                .resolveTemplate("user_id", userId)
                .queryParam("apikey", apiKey)
                .request()
                .accept(MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8))
                .put(Entity.entity(user, MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8)), User.class);
    }

    /**
     * Create a User from ALMA.
     * @param user the ALMA user object
     * @return the user as created in ALMA.
     */
    public User createUser(final User user) {
        return alma.path("")
                .queryParam("apikey", apiKey)
                .request()
                .accept(MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8))
                .post(Entity.entity(user, MediaType.APPLICATION_XML_TYPE.withCharset(UTF_8)), User.class);
    }

    /**
     * Remove a User from ALMA.
     * @param userId the user identifier
     * @return true if User is deleted, false if not, even if not found.
     */
    public boolean deleteUser(final String userId) {
        Response res = alma.path("/{user_id}")
            .resolveTemplate("user_id", userId)
            .queryParam("apikey", apiKey)
            .request()
            .delete();

        return res.getStatus() == 204;
    }
}
