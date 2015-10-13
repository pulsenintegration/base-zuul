package com.confluex.orm.dao

import com.confluex.orm.BaseIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class JpaBookDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    BookDao dao

    @Test
    void findOneShouldReturnCorrectRecord() {
        def book = dao.findOne(4)
        assert book.id == 4
        assert book.title == "The French Laundry Cookbook"
        assert book.author.lastName == "Keller"
    }


}
