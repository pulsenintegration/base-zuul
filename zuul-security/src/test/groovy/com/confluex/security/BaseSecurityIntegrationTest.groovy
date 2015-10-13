package com.confluex.security

import com.confluex.security.model.User
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ['classpath:test-security-context.xml'])
@Transactional('transactionManager')
abstract class BaseSecurityIntegrationTest {

  @Autowired
  AuthenticationManager authenticationManager

  protected void loginAsUser(User user) {
    def testUser = new TestingAuthenticationToken(user, "fake", user.authorities as List)
    def response = authenticationManager.authenticate(testUser)
    assert response.principal == user
    SecurityContextHolder.context.authentication = response
  }
}
