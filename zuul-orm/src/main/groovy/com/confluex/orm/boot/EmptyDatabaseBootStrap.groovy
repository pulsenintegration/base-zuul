package com.confluex.orm.boot

import javax.sql.DataSource
import groovy.sql.Sql


class EmptyDatabaseBootStrap extends DataBootStrap {

    /**
     * Set the enable/disable flag based upon the results of a query. If rows are returned from
     * the query, the bootstrap flag will be set to false.
     */
    EmptyDatabaseBootStrap(DataSource dataSource, String query) {
        def sql = new Sql(dataSource)
        this.enabled = sql.rows(query).size() < 1
    }


}
