package com.confluex.security.spring

import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import org.junit.Before
import org.junit.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException
import static org.mockito.Mockito.*

class OpenIdUserDetailsServiceTest {

    OpenIdUserDetailsService service

    @Before
    void createService() {
        service = new OpenIdUserDetailsService(securityService: mock(SecurityService))
    }


    @Test
    void loadUserByNameShouldConstructUser() {
        def mockUser = new User(userName: 'abc')
        when(service.securityService.findByUserName('abc')).thenReturn(mockUser)
        def user = service.loadUserByUsername('abc')
        verify(service.securityService).findByUserName('abc')
        assert user.is(mockUser)
    }

    @Test(expected = UsernameNotFoundException)
    void loadUserByNameShouldErrorIfUserNotFound() {
        when(service.securityService.findByUserName('abc')).thenReturn(null)
        service.loadUserByUsername('abc')
    }
}
