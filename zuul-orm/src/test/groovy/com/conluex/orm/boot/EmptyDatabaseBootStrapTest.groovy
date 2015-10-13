package com.confluex.orm.boot

import groovy.mock.interceptor.MockFor
import groovy.sql.Sql
import org.junit.Test

import javax.sql.DataSource

import static org.mockito.Mockito.*

class EmptyDatabaseBootStrapTest {

    @Test
    void shouldNotBeEnabledIfQueryReturnsResults() {
        def mockSql = new MockFor(Sql)
        mockSql.demand.rows {
            return [
                    [a: 1, b: 2]
            ]
        }
        mockSql.use {
            def bootStrap = new EmptyDatabaseBootStrap(mock(DataSource), "i'm a query")
            assert !bootStrap.enabled
        }
    }

    @Test
    void shouldBeEnabledIfQueryDoesNotReturnResults() {
        def mockSql = new MockFor(Sql)
        mockSql.demand.rows {
            return [ ]
        }
        mockSql.use {
            def bootStrap = new EmptyDatabaseBootStrap(mock(DataSource), "i'm a query")
            assert bootStrap.enabled
        }
    }
}
