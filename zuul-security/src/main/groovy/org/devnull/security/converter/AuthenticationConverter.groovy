package com.confluex.security.converter

import org.springframework.security.core.Authentication
import com.confluex.security.model.User

interface AuthenticationConverter {
    User convert(Authentication authentication)
}
