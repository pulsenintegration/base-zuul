package com.confluex.security.config

import com.confluex.security.model.User

public interface UserLookupStrategy {
    User lookupCurrentUser()

    void reAuthenticateCurrentUser()
}