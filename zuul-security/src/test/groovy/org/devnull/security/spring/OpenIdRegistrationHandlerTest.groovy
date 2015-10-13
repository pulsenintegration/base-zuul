package com.confluex.security.spring

import com.confluex.security.converter.AuthenticationConverter
import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.openid.OpenIDAttribute
import org.springframework.security.openid.OpenIDAuthenticationStatus
import org.springframework.security.openid.OpenIDAuthenticationToken

import static org.mockito.Mockito.*

public class OpenIdRegistrationHandlerTest {
    OpenIdRegistrationHandler handler
    MockHttpServletRequest mockRequest
    MockHttpServletResponse mockResponse

    @Before
    void createHandler() {
        mockRequest = new MockHttpServletRequest()
        mockResponse = new MockHttpServletResponse()
        handler = new OpenIdRegistrationHandler(
                authenticationConverter: mock(AuthenticationConverter),
                authenticationManager: mock(AuthenticationManager),
                securityService: mock(SecurityService),
                firstUserRoles: ["ROLE_FIRST_USER"],
                defaultRoles: ["ROLE_NORMAL_USER"]
        )
    }


    @Test
    void shouldDenyWhenConverterThrowsException() {
        def exception = mock(AuthenticationException)
        when(handler.authenticationConverter.convert(Matchers.any(Authentication))).thenThrow(new InsufficientAuthenticationException("test error"))
        handler.onAuthenticationFailure(mockRequest, mockResponse, exception)
        verify(handler.authenticationConverter).convert(exception.authentication)
        assert mockResponse.status == 401
    }

    @Test
    void shouldSaveAndRedirectWhenConverterCreatesUser() {
        def mockUser = new User()
        def exception = mock(AuthenticationException)

        when(handler.authenticationConverter.convert(Matchers.any(Authentication))).thenReturn(mockUser)
        when(handler.securityService.createNewUser(mockUser, handler.defaultRoles)).thenReturn(mockUser)
        when(handler.securityService.countUsers()).thenReturn(1L)
        handler.onAuthenticationFailure(mockRequest, mockResponse, exception)
        verify(handler.securityService).createNewUser(mockUser,  handler.defaultRoles)
        verify(handler.authenticationManager).authenticate(exception.authentication)

        assert mockResponse.status < 400
        assert mockResponse.redirectedUrl == handler.registrationUrl
    }

    @Test
    void shouldUseFirstUserRolesWhenNoUsersExist() {
        def mockUser = new User()
        def exception = mock(AuthenticationException)

        when(handler.authenticationConverter.convert(Matchers.any(Authentication))).thenReturn(mockUser)
        when(handler.securityService.createNewUser(mockUser, handler.defaultRoles)).thenReturn(mockUser)
        when(handler.securityService.countUsers()).thenReturn(0L)
        handler.onAuthenticationFailure(mockRequest, mockResponse, exception)
        verify(handler.securityService).createNewUser(mockUser, handler.firstUserRoles)
        verify(handler.authenticationManager).authenticate(exception.authentication)

        assert mockResponse.status < 400
        assert mockResponse.redirectedUrl == handler.registrationUrl
    }



    protected OpenIDAuthenticationToken createToken(OpenIDAuthenticationStatus status, List<OpenIDAttribute> attributes = []) {
        return new OpenIDAuthenticationToken(status, "http://fake.openid.com", "test error", attributes)
    }
}
