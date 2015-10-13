package com.confluex.orm.dao

import com.confluex.orm.model.Author
import org.springframework.data.repository.PagingAndSortingRepository

interface AuthorDao extends PagingAndSortingRepository<Author, Long> {
    List<Author> findByLastName(String lastName);
}
