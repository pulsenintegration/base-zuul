package com.confluex.security.service

import com.confluex.security.config.UserLookupStrategy
import com.confluex.security.dao.RoleDao
import com.confluex.security.dao.UserDao
import com.confluex.security.model.Role
import com.confluex.security.model.User
import org.junit.Before
import org.junit.Test
import org.springframework.data.domain.Sort

import static org.mockito.Mockito.*

public class SecurityServiceImplTest {
    SecurityServiceImpl service
    User currentUser

    @Before
    void createService() {
        service = new SecurityServiceImpl(
                userDao: mock(UserDao),
                roleDao: mock(RoleDao),
                userLookupStrategy: mock(UserLookupStrategy)
        )
        currentUser = new User(id: 20314, userName: "http://test.openid.com", firstName: "John", lastName: "Doe", email: "jdoe@test.com")
        currentUser.roles = [new Role(id: 1, name: "a"), new Role(id: 2, name: "b"), new Role(id: 3, name: "c")]
        when(service.userLookupStrategy.lookupCurrentUser()).thenReturn(currentUser)
    }

    @Test
    void createNewUserShouldAddRolesToUserAndSave() {
        def mockUser = new User()
        def roles = ["a", "b"]

        when(service.userDao.save(mockUser)).thenReturn(mockUser)
        when(service.roleDao.findByName("a")).thenReturn(new Role(id: 1, name: "a"))
        when(service.roleDao.findByName("b")).thenReturn(new Role(id: 1, name: "b"))
        def result = service.createNewUser(mockUser, roles)
        assert result
        roles.each { roleName ->
            verify(service.roleDao).findByName(roleName)
            assert result.roles.find { it.name == roleName }
        }
    }

    @Test
    void findRoleByNameShouldReturnResultFromDao() {
        def expected = new Role(id: 1, name: "a")
        when(service.findRoleByName("a")).thenReturn(expected)
        def result = service.findRoleByName("a")
        assert result.is(expected)
    }

    @Test
    void updateCurrentUserShouldSaveCurrentUserAndReAuthenticate() {
        when(service.userDao.save(currentUser)).thenReturn(currentUser)
        def result = service.updateCurrentUser(true)
        verify(service.userDao).save(currentUser)
        verify(service.userLookupStrategy).reAuthenticateCurrentUser()
        assert result == currentUser
    }

    @Test
    void updateCurrentUserShouldSaveCurrentUserWithoutReauthencation() {
        when(service.userDao.save(currentUser)).thenReturn(currentUser)
        def result = service.updateCurrentUser(false)
        verify(service.userDao).save(currentUser)
        verify(service.userLookupStrategy, never()).reAuthenticateCurrentUser()
        assert result == currentUser
    }

    @Test
    void shouldListAllUsersAndSortByLastName() {
        def expected = []
        when(service.userDao.findAll(new Sort("lastName"))).thenReturn(expected)
        def results = service.listUsers()
        verify(service.userDao).findAll(new Sort("lastName"))
        assert results.is(expected)
    }

    @Test
    void shouldListAllRolesAndSortByDescription() {
        def expected = [new Role()]
        when(service.roleDao.findAll(new Sort("description"))).thenReturn(expected)
        def results = service.listRoles()
        assert results.is(expected)
    }

    @Test
    void shouldRemoveRoleFromUserAndSave() {
        def roleId = 123
        def userId = 456
        def roles = [new Role(id: -1), new Role(id: roleId)]
        def user = new User(id: userId, roles: roles)
        when(service.userDao.findOne(userId)).thenReturn(user)
        service.removeRoleFromUser(roleId, userId)
        verify(service.userDao).save(user)
        assert user.roles.size() == 1
        assert user.roles.first() != roleId
    }

    @Test
    void shouldAddRoleToUserAndSave() {
        def newRole = new Role(id:456)
        def user = new User(id: 1, roles: [new Role(id: 123)])
        when(service.userDao.findOne(user.id)).thenReturn(user)
        when(service.roleDao.findOne(newRole.id)).thenReturn(newRole)
        service.addRoleToUser(newRole.id, user.id)
        verify(service.userDao).save(user)
        assert user.roles.size() == 2
        assert user.roles.contains(newRole)
    }

    @Test
    void shouldDeleteUser() {
        service.deleteUser(1)
        verify(service.userDao).delete(1)
    }

    @Test
    void shouldCountUsers() {
        when(service.userDao.count()).thenReturn(22L)
        assert service.countUsers() == 22L
    }

}
