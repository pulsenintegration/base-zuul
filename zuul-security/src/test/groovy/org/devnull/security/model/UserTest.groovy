package com.confluex.security.model

import org.junit.Test
import org.junit.Before;

public class UserTest {

    User user

    @Before
    void createUser() {
        user = new User(id:1, userName:"http://fake.openid.com", firstName: "Billy", lastName: "Bob", email:"bob@aol.com")
        def roles = [new Role(id: 1, name: "a"), new Role(id: 2, name: "b"), new Role(id: 3, name: "c")]
        user.roles = roles
    }

    @Test
    void setOpenIdShouldBeImmutableOnceSet() {
        user.userName = "http://hacked.openid.com"
        assert user.userName == "http://fake.openid.com"
    }
    
    @Test
    void seIdShouldBeImmutableOnceSet() {
        user.id = 1234
        assert user.id == 1
    }
    
    @Test
    void getAuthoritiesShouldConvertRolesToGrantedAuthorities() {
        def authorities = user.authorities
        user.roles.each { role ->
            assert authorities.find { it.authority == role.name }
        }
    }

    @Test
    void addToRolesShouldNotDuplicateRoles() {
        def duplicateRole = new Role(id: 2, name: "b")
        user.addToRoles(duplicateRole)
        assert user.roles.size() == 3
        assert !user.roles.find { it.is(duplicateRole)}
    }

    @Test
    void addToRolesShouldAddNewRoles() {
        def newRole = new Role(id: 4, name: "d")
        user.addToRoles(newRole)
        assert user.roles.size() == 4
        assert user.roles.contains(newRole)
    }

    @Test
    void shouldGenerateEmailDigest() {
        def user = new User(email: "test@devnull.org")
        assert user.emailHash == "9b345d5ef6f1397f6ae1e28a5794d4c1"
    }
}
