package com.confluex.security.converter

import com.confluex.security.model.User
import org.springframework.security.core.Authentication
import org.springframework.security.openid.OpenIDAuthenticationToken
import org.springframework.security.openid.OpenIDAuthenticationStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException

class OpenIdAuthenticationTokenConverter implements AuthenticationConverter {

    static final String ATTR_EMAIL = "email"
    static final String ATTR_FIRST_NAME = "firstName"
    static final String ATTR_LAST_NAME = "lastName"

    User convert(Authentication authentication) {
        if (authentication instanceof OpenIDAuthenticationToken) {
            def token = authentication as OpenIDAuthenticationToken
            if (token.status != OpenIDAuthenticationStatus.SUCCESS) {
                throw new BadCredentialsException("Invalid token status: ${token.status}, messsage: ${token.message}")
            }
            def user = new User(userName: token.identityUrl)
            user.email = token.attributes.find { it.name == ATTR_EMAIL }?.values?.first()
            user.firstName = token.attributes.find { it.name == ATTR_FIRST_NAME }?.values?.first()
            user.lastName = token.attributes.find { it.name == ATTR_LAST_NAME }?.values?.first()
            // TODO support more attributes
            return user
        }
        else {
            throw new InsufficientAuthenticationException("Authentication type ${authentication?.class} is not supported")
        }
    }
}
