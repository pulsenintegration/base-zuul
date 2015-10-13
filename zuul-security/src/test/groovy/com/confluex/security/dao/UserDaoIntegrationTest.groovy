package com.confluex.security.dao

import com.confluex.security.BaseSecurityIntegrationTest
import com.confluex.security.model.User
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import javax.validation.ConstraintViolationException

class UserDaoIntegrationTest extends BaseSecurityIntegrationTest {
    @Autowired
    UserDao dao

    @Autowired
    RoleDao roleDao

    @Test
    void createMissingGhostBusterShouldPersistRecord() {
        def user = new User(userName: 'http://fake.openid.com/ehudson', firstName: 'Ernie', lastName: 'Hudson', email: 'ehudson@ghostbusters.com')
        def ernie = dao.save(user)
        assert ernie.userName == 'http://fake.openid.com/ehudson'
        assert ernie.accountNonExpired
        assert ernie.accountNonLocked
        assert ernie.enabled
        assert ernie.id > 3
    }

    @Test
    void findByOpenIdShouldReturnCorrectRecord() {
        def user = dao.findByUserName("http://fake.openid.com/daykroyd")
        assert user.userName == "http://fake.openid.com/daykroyd"
    }

    @Test
    void saveShouldPersistAddedRoles() {
        def bill = dao.findByUserName("http://fake.openid.com/bmurray")
        assert bill.roles.size() == 1
        assert bill.roles.first().name == "ROLE_USER"
        bill.roles << roleDao.findByName("ROLE_SYSTEM_ADMIN")
        dao.save(bill)

        bill = dao.findByUserName("http://fake.openid.com/bmurray")
        assert bill.roles.size() == 2
        assert bill.roles.find { it.name == "ROLE_USER" }
        assert bill.roles.find { it.name == "ROLE_SYSTEM_ADMIN" }
    }

    @Test
    void saveShouldRemoveRoleWithoutCascading() {
        def bill = dao.findByUserName("http://fake.openid.com/bmurray")
        assert bill.roles.size() == 1
        assert bill.roles.first().name == "ROLE_USER"

        bill.roles.remove(0)
        dao.save(bill)

        bill = dao.findByUserName("http://fake.openid.com/bmurray")
        assert bill.roles.size() == 0
        assert roleDao.findByName("ROLE_USER")
    }

    @Test
    void deleteShouldNotCascadeToRoles() {
        def bill = dao.findByUserName("http://fake.openid.com/bmurray")
        assert bill.roles.size() == 1
        assert bill.roles.first().name == "ROLE_USER"
        dao.delete(bill.id)
        assert roleDao.findByName("ROLE_USER")
    }

    @Test
    void listAllUsersShouldFindAllRecordsInCorrectOrder() {
        def results = dao.findAll(new Sort("lastName")) as List
        assert results.size() == 3
        assert results[0].lastName == "Aykroyd"
        assert results[1].lastName == "Murray"
        assert results[2].lastName == "Ramis"
    }

    @Test(expected=ConstraintViolationException)
    void shouldRequireFormattedEmailAddress() {
        def user = new User(firstName:"Joe", lastName: "Blow", email: "bademail")
        dao.save(user)
    }
}
