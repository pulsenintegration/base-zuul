package com.confluex.security.spring

import com.confluex.security.converter.AuthenticationConverter
import com.confluex.security.converter.OpenIdAuthenticationTokenConverter
import com.confluex.security.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OpenIdRegistrationHandler extends SimpleUrlAuthenticationFailureHandler {

    final def log = LoggerFactory.getLogger(this.class)

    String registrationUrl = "/account/register"
    List<String> defaultRoles
    List<String> firstUserRoles

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    SecurityService securityService

    AuthenticationConverter authenticationConverter = new OpenIdAuthenticationTokenConverter()

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        log.info("Creating new account unregistered user for auth: {}", e.authentication)
        try {
            def user = authenticationConverter.convert(e.authentication)
            def roles = securityService.countUsers() ? defaultRoles : firstUserRoles
            securityService.createNewUser(user, roles)
            reAuthenticate(e.authentication)
            new DefaultRedirectStrategy().sendRedirect(request, response, registrationUrl)
        } catch (AuthenticationException ae) {
            super.onAuthenticationFailure(request, response, ae)
        }
    }

    protected void reAuthenticate(Authentication authentication) {
        log.info("Re-Authenticating..")
        def response = authenticationManager.authenticate(authentication)
        SecurityContextHolder.getContext().setAuthentication(response);
        log.info("Re-Authentication response: {}", response)
    }


}