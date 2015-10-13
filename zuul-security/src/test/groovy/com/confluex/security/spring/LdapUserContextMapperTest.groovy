package com.confluex.security.spring

import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static org.mockito.Mockito.*

class LdapUserContextMapperTest {
    LdapUserContextMapper mapper

    @Before
    void createStrategy() {
        mapper = new LdapUserContextMapper(
                [
                        "cn=Single Role User,dc=devnull,dc=org": [
                                "ROLE_USER_SINGLE"
                        ],
                        "cn=Multi Roled User,dc=devnull,dc=org": [
                                "ROLE_USER_A",
                                "ROLE_USER_B",
                        ]
                ]
        )

        mapper.securityService = mock(SecurityService)
    }

    @Test
    void shouldNotBeCaseSensitive() {
        assert mapper.groupToRolesMapping["CN=Single Role User,DC=devnull,DC=org"] == ["ROLE_USER_SINGLE"]
    }

    @Test
    void shouldNotCreateUserIfRecordAlreadyExists() {
        def username = "testUser"
        def context = mock(DirContextOperations)
        def groups = convertToAuthorities(["cn=Single Role User,dc=devnull,dc=org"])
        def user = new User(id: 1, userName: username)
        when(mapper.securityService.findByUserName(username)).thenReturn(user)
        def result = mapper.mapUserFromContext(context, username, groups)
        verify(mapper.securityService, never()).createNewUser(Matchers.any(User), Matchers.any(List))
        assert result == user
    }

    @Test
    void shouldCreateUserWithMatchingMappedRoles() {
        def context = mock(DirContextOperations)

        def groups = convertToAuthorities(["cn=Single Role User,dc=devnull,dc=org"])
        mapper.mapUserFromContext(context, "testUser", groups)
        verify(mapper.securityService).createNewUser(Matchers.any(User), eq(["ROLE_USER_SINGLE"]))

        groups = convertToAuthorities(["cn=Multi Roled User,dc=devnull,dc=org"])
        mapper.mapUserFromContext(context, "testUser", groups)
        verify(mapper.securityService).createNewUser(Matchers.any(User), eq(["ROLE_USER_A", "ROLE_USER_B"]))

        groups = convertToAuthorities(["cn=Multi Roled User,dc=devnull,dc=org", "cn=Single Role User,dc=devnull,dc=org"])
        mapper.mapUserFromContext(context, "testUser", groups)
        verify(mapper.securityService).createNewUser(Matchers.any(User), eq(["ROLE_USER_A", "ROLE_USER_B", "ROLE_USER_SINGLE"]))
    }

    @Test
    void shouldCreateUserWithCorrectProperties() {
        def username = "testUser"
        def context = mock(DirContextOperations)
        def groups = convertToAuthorities(["cn=Single Role User,dc=devnull,dc=org"])
        def user = new User(email: "testUser@devnull.org", firstName: "Test", lastName: "User", userName: username)
        when(context.getStringAttribute(mapper.attributesToPropertyNames.email)).thenReturn(user.email)
        when(context.getStringAttribute(mapper.attributesToPropertyNames.firstName)).thenReturn(user.firstName)
        when(context.getStringAttribute(mapper.attributesToPropertyNames.lastName)).thenReturn(user.lastName)
        when(mapper.securityService.createNewUser(user, ["ROLE_USER_SINGLE"])).thenReturn(user)
        def result = mapper.mapUserFromContext(context, username, groups)
        verify(mapper.securityService).createNewUser(user, ["ROLE_USER_SINGLE"])
        assert result == user
    }

    @Test(expected = DisabledException)
    void shouldErrorIfUserIsNotMappedToRoles() {
        def context = mock(DirContextOperations)
        def groups = convertToAuthorities(["cn=Not Found,dc=devnull,dc=org"])
        mapper.mapUserFromContext(context, "testUser", groups)
    }

    @Test(expected = DisabledException)
    void shouldErrorIfUserHasNoGroups() {
        def context = mock(DirContextOperations)
        def groups = convertToAuthorities([])
        mapper.mapUserFromContext(context, "testUser", groups)
    }

    @Test(expected = DisabledException)
    void shouldErrorIfUserHasNullGroups() {
        def context = mock(DirContextOperations)
        mapper.mapUserFromContext(context, "testUser", null)
    }

    protected Set convertToAuthorities(List<String> groups) {
        return groups.collect { new SimpleGrantedAuthority(it) } as Set
    }
}
