package com.confluex.security.dao

import com.confluex.security.model.Role
import org.springframework.data.repository.PagingAndSortingRepository

interface RoleDao extends PagingAndSortingRepository<Role, Integer>  {
    Role findByName(String name)
}