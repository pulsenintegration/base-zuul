package com.confluex.security.service

import com.confluex.security.model.Role
import com.confluex.security.model.User

public interface SecurityService {
    /**
     * Locates the current logged in user.
     * @return
     */
    User getCurrentUser()

    /**
     * Find a user with the matching username
     *
     * @param userName
     * @return persisted version of the user
     */
    User findByUserName(String userName)

    /**
     * Create a new user with the given roles
     * @param user transient user
     * @param roles starting roles for the user
     * @return persisted version of the user
     */
    User createNewUser(User user, List<String> roles)

    /**
     * Save any changes to the authenticated user and optionally re-authenticate
     *
     * @param reAuthenticate if true, user's security context will be re-authenticated
     */
    User updateCurrentUser(Boolean reAuthenticate)

    /**
     * Lookup an existing role by name
     */
    Role findRoleByName(String name)

    /**
     * Count all of the users in the system
     */
    Long countUsers()

    /**
     * Find all users in the system.
     */
    List<User> listUsers()

    /**
     * Find all of the roles in the system.
     */
    List<Role> listRoles()

    User addRoleToUser(Integer roleId, Integer userId)

    /**
     * Remove the given role from the given user's role collection.
     */
    User removeRoleFromUser(Integer roleId, Integer userId)

    /**
     * Delete the user from the system
     */
    void deleteUser(Integer userId)
}