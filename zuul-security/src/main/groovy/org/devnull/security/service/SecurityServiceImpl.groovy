package com.confluex.security.service

import com.confluex.security.config.UserLookupStrategy
import com.confluex.security.dao.RoleDao
import com.confluex.security.dao.UserDao
import com.confluex.security.model.Role
import com.confluex.security.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("securityService")
@Transactional(readOnly=true)
class SecurityServiceImpl implements SecurityService {

    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    UserLookupStrategy userLookupStrategy

    User getCurrentUser() {
        return userLookupStrategy.lookupCurrentUser()
    }

    User findByUserName(String userName) {
        return userDao.findByUserName(userName)
    }

    @Transactional(readOnly=false)
    User createNewUser(User user, List<String> roles) {
        roles.each { role ->
            log.debug("Adding user to role: {}", role)
            user.roles << roleDao.findByName(role)
        }
        userDao.save(user)
    }

    @Transactional(readOnly=false)
    User updateCurrentUser(Boolean reAuthenticate) {
        def user = currentUser
        log.info("Saving user: {}", user)
        userDao.save(user)
        if (reAuthenticate) {
            userLookupStrategy.reAuthenticateCurrentUser()
        }
        return currentUser
    }

    Role findRoleByName(String name) {
        return roleDao.findByName(name)
    }

    Long countUsers() {
        return userDao.count()
    }

    List<User> listUsers() {
        return userDao.findAll(new Sort("lastName")) as List
    }

    List<Role> listRoles() {
        return roleDao.findAll(new Sort("description")) as List
    }

    @Transactional(readOnly=false)
    User removeRoleFromUser(Integer roleId, Integer userId) {
        def user = userDao.findOne(userId)
        user.roles.removeAll { it.id == roleId }
        return userDao.save(user)
    }

    @Transactional(readOnly=false)
    User addRoleToUser(Integer roleId, Integer userId) {
        def user = userDao.findOne(userId)
        user.addToRoles(roleDao.findOne(roleId))
        return userDao.save(user)
    }

    @Transactional(readOnly=false)
    void deleteUser(Integer userId) {
        userDao.delete(userId)
    }
}
