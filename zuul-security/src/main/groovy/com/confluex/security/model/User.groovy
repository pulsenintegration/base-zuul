package com.confluex.security.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.FetchType
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.constraints.NotNull

import javax.persistence.UniqueConstraint
import org.apache.commons.lang.StringUtils
import java.security.MessageDigest
import org.apache.commons.codec.digest.DigestUtils

@Entity
@Table(name = "SecurityUser", uniqueConstraints = [
    @UniqueConstraint(columnNames=["userName"])
])
@EqualsAndHashCode(excludes = "roles")
@ToString(excludes = "roles", includeNames = true)
class User implements Serializable, UserDetails {

    static final def log = LoggerFactory.getLogger(User)
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST], fetch=FetchType.EAGER)
    @JoinTable(
    name = "SecurityUserRole",
    joinColumns = @JoinColumn(name = "UserId"),
    inverseJoinColumns = @JoinColumn(name = "RoleId")
    )
    List<Role> roles = []

    String userName


    @Pattern(regexp=".{1,50}\\@.{1,100}", message="Email must be properly formatted")
    String email

    @Size(min=1, message="First name cannot be empty")
    String firstName

    @Size(min=1, message="Last name cannot be empty")
    String lastName

    @NotNull
    Boolean enabled = true

    /**
     * Immutable once set
     * @param id
     */
    void setId(Long id) {
        if (this.id) {
            log.warn("attempt to alter domain id from: {}, to: {}", this.id, id)
            return
        }
        this.id = id
    }

    /**
     * Immutable once set
     * @param id
     */
    void setUserName(String openId) {
        if (this.userName) {
            log.warn("attempt to alter openId from: {}, to: {}", this.userName, openId)
            return
        }
        this.userName = openId
    }

    Collection<GrantedAuthority> getAuthorities() {
        return roles.collect { new SimpleGrantedAuthority(it.name) }
    }

    String getPassword() {
        return "********"
    }

    String getUsername() {
        return userName
    }

    boolean isAccountNonExpired() {
        return true
    }

    boolean isAccountNonLocked() {
        return true
    }

    boolean isCredentialsNonExpired() {
        return true
    }

    boolean isEnabled() {
        return enabled
    }

    void addToRoles(Role role) {
        if (!roles.contains(role)) {
            roles.add(role)
        }
    }

    /**
     * MD5 Hash the email which is can be used for Gravatar profile requests.
     * @return
     */
    String getEmailHash() {
        if (!email) return ""
        return DigestUtils.md5Hex(email.toLowerCase().trim())
    }

}
