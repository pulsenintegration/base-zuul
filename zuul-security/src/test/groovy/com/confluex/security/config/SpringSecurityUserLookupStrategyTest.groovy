package com.confluex.security.config

import com.confluex.security.model.User
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

import static org.mockito.Mockito.*
import org.springframework.security.core.Authentication

class SpringSecurityUserLookupStrategyTest {

  SpringSecurityUserLookupStrategy strategy

  @Before
  void createStrategy() {
    strategy = new SpringSecurityUserLookupStrategy(authenticationManager: mock(AuthenticationManager))
  }

  @Test
  void shouldLookupUserFromSpringContext() {
    def user = new User(id: 1)
    currentUser = user
    assert strategy.lookupCurrentUser() == user
  }

  @Test
  void shouldReAuthenticateUserWithSpringAuthenticationManager() {
    def authentication = mock(Authentication)
    SecurityContextHolder.context?.authentication = authentication
    strategy.reAuthenticateCurrentUser()
    verify(strategy.authenticationManager).authenticate(authentication)
  }

  protected void setCurrentUser(User user) {
    SecurityContextHolder.context?.authentication = new TestingAuthenticationToken(user, "*********")
  }
}
