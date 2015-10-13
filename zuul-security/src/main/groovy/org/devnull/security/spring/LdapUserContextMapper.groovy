package com.confluex.security.spring

import groovy.util.logging.Slf4j
import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper
import org.apache.commons.collections.map.CaseInsensitiveMap

@Slf4j
class LdapUserContextMapper implements UserDetailsContextMapper {

    Map<String, List<String>> groupToRolesMapping

    @Autowired
    SecurityService securityService

    Map attributesToPropertyNames = [
            email: "mail",
            lastName: "sn",
            firstName: "givenName"
    ]

    LdapUserContextMapper(Map<String, List<String>> groupToRolesMapping) {
        this.groupToRolesMapping = new CaseInsensitiveMap(groupToRolesMapping)
    }

    UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                   Collection<? extends GrantedAuthority> authorities) {
        log.info("Mapping LDAP user to custom User: username={}, authorities={}", username, authorities)
        def user = securityService.findByUserName(username)
        if (!user) {
            def email = ctx.getStringAttribute(attributesToPropertyNames.email)
            def lastName = ctx.getStringAttribute(attributesToPropertyNames.lastName)
            def firstName = ctx.getStringAttribute(attributesToPropertyNames.firstName)
            log.info("Creating new user: userName={}, email={}, firstName={}, lastName={}", username, email, firstName, lastName)
            user = addNewUser(
                    new User(firstName: firstName, lastName: lastName, email: email, userName: username),
                    authorities.collect { it.authority }
            )

        }
        return user
    }

    void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        log.warn("LDAP create/update of users is not supported by this implementation")
    }

    protected User addNewUser(User user, List<String> externalAuthorities) {
        def roles = []
        externalAuthorities.each {
            log.debug("Checking groupsToRolesMapping for group={}", it)
            def mappings = groupToRolesMapping[it]
            if (mappings) {
                roles += mappings
            }
        }
        if (!roles) {
            // TODO better exception.. this isn't the intended use of this exception but it's a close fit for now
            log.warn("No matching groups found for user. user={}, groups={}, groupToRolesMapping={}", user, externalAuthorities, groupToRolesMapping)
            throw new DisabledException("User does not belong to any of the configured roles. Check log for details.")
        }
        return securityService.createNewUser(user, roles)
    }

}
