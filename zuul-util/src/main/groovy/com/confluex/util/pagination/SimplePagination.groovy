package com.confluex.util.pagination

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Utility class used for pagination of result sets
 */

@EqualsAndHashCode
@ToString(includeNames = true)
class SimplePagination<T extends Serializable> implements Pagination<T>, Serializable {

    static final long serialVersionUID = 1L;

    /**
     * Default = {@value}
     */
    @Delegate
    List<T> results = []

    /**
     * Default = {@value}
     */
    Map<String, Serializable> filter = [:]

    /**
     * Default = {@value}
     */
    Long total = 0

    /**
     * Default = {@value}
     */
    Integer page = 0

    /**
     * Default = {@value}
     */
    Integer max = 25

    /**
     * Default = {@value}
     */
    List<Sort> sorts = []
}
