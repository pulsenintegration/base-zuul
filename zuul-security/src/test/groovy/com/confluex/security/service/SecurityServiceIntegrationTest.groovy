package com.confluex.security.service

import com.confluex.security.BaseSecurityIntegrationTest
import com.confluex.security.model.User

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

public class SecurityServiceIntegrationTest extends BaseSecurityIntegrationTest {

    @Autowired
    SecurityService service

    @Test
    void findUserByOpenIdShouldFindDanAykroyd() {
        def dan = service.findByUserName('http://fake.openid.com/daykroyd') as User
        assert dan.firstName == "Dan"
        assert dan.lastName == "Aykroyd"
        assert dan.email == "daykroyd@ghostbusters.com"
        assert dan.accountNonExpired
        assert dan.accountNonLocked
        assert dan.enabled
    }

    @Test()
    void findUserByOpenIdShouldNotFindRickMoranis() {
        def rick = service.findByUserName('http://fake.openid.com/rmoranis')
        assert !rick
    }

}
