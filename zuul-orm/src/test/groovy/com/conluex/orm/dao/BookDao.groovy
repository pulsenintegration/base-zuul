package com.confluex.orm.dao

import com.confluex.orm.model.Book
import org.springframework.data.repository.CrudRepository

interface BookDao extends CrudRepository<Book, Long> {
}
