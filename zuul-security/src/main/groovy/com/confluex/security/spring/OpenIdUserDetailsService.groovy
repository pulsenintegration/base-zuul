package com.confluex.security.spring

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

import org.springframework.security.core.userdetails.UsernameNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import org.slf4j.LoggerFactory
import com.confluex.security.service.SecurityService

@Service("openIdUserDetailsService")
class OpenIdUserDetailsService implements UserDetailsService {

    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    SecurityService securityService

    UserDetails loadUserByUsername(String openIdToken) {
        log.debug("Attempting to load user by token {}", openIdToken)
        def user = securityService.findByUserName(openIdToken)
        log.debug("User={}", user)
        if (!user) {
            throw new UsernameNotFoundException("Unable to locate user with open id token :${openIdToken}")
        }
        return user
    }
}
