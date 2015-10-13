package com.confluex.orm.dao

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import com.confluex.orm.BaseIntegrationTest
import com.confluex.orm.model.Author
import javax.validation.ConstraintViolationException

class JpaAuthorDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AuthorDao authorDao

    
    @Test
    void findOneFindsRecordWithCorrectId() {
        def author = authorDao.findOne(2)
        assert author.id == 2
        assert author.firstName == "David"
        assert author.lastName == "Chang"
    }

    @Test
    void findAllWithSorting() {
        def results = authorDao.findAll(new Sort(Direction.DESC, "lastName", "firstName"))
        def lastNames = results.collect { it.lastName }
        assert lastNames == ["McGee", "Keller", "Chang"]
    }

    @Test
    void findAllWithPaging() {
        def results = authorDao.findAll(new PageRequest(1, 2, Direction.DESC, "lastName"))
        println results
        assert results.content.size() == 1
        assert results.content.first().lastName == "Chang"
    }

    @Test
    void findByLastName() {
        def results = authorDao.findByLastName("Chang")
        assert results.size() == 1
        assert results.first().lastName == "Chang"
    }

    @Test(expected=ConstraintViolationException)
    void shouldRequireLastNameWithMinLength() {
        def author = new Author(firstName: "Joel", lastName: "R")
        authorDao.save(author)
    }

}
