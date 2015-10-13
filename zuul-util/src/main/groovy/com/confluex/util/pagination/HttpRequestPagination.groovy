package com.confluex.util.pagination

import javax.servlet.http.HttpServletRequest
import com.confluex.util.pagination.SimplePagination
import com.confluex.util.pagination.Sort

/**
 * Helps bind http request parameters to pagination fields. See the PARAM constants.
 */
class HttpRequestPagination<T extends Serializable> extends SimplePagination<T> {

    static final String PARAM_MAX = "max"
    static final String PARAM_PAGE = "page"
    static final String PARAM_SORT = "sort"
    static final String PARAM_DIRECTION = "dir"

    HttpRequestPagination(HttpServletRequest request) {
        def max = request.getParameter(PARAM_MAX)
        def page = request.getParameter(PARAM_PAGE)
        if (max) this.max = max.toInteger()
        if (page) this.page = page.toInteger()
        this.sorts = parseSortParams(request)
    }

    /**
     * Uses request parameters {@value PARAM_SORT} and {@value PARAM_DIRECTION} to build Sort criteria.
     *
     * Examples:
     *
     * ?{@value PARAM_SORT}=lastName&{@value PARAM_DIRECTION}=DESC
     * Sort(field: "lastName", direction: "DESC")
     *
     * ?{@value PARAM_SORT}=lastName&?{@value PARAM_DIRECTION}=age&{@value PARAM_DIRECTION}=DESC
     * [Sort(field: "lastName", direction: "DESC"), Sort(field: "age", direction: "DESC")]
     *
     * You can scope directions to fields by appending the direction after the field value. For instance:
     *
     * ?{@value PARAM_SORT}=lastName&?{@value PARAM_DIRECTION}=age&{@value PARAM_DIRECTION}=DESC&age.{@value PARAM_DIRECTION}=ASC
     * [Sort(field: "lastName", direction: "DESC"), Sort(field: "age", direction: "ASC")]
     *
     */
    static List<Sort> parseSortParams(HttpServletRequest request) {
        def sortParams = request.getParameterValues(PARAM_SORT)
        def directionParam = request.getParameter(PARAM_DIRECTION)
        return sortParams.collect { field ->
            def fieldDirection = request.getParameter("${field}.${PARAM_DIRECTION}") ?: directionParam
            def sort = new Sort(field: field)
            if (fieldDirection) sort.direction = fieldDirection
            return sort
        }
    }
}
