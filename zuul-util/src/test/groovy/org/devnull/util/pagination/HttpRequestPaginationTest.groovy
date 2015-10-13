package com.confluex.util.pagination

import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest

class HttpRequestPaginationTest {

    @Test
    void shouldFindMaxParamFromRequestIfPresent() {
        def request = new MockHttpServletRequest()
        request.setParameter(HttpRequestPagination.PARAM_MAX, "12")
        def pagination = new HttpRequestPagination(request)
        assert pagination.max == 12
    }

    @Test
    void shouldUseDefaultMaxParamIfNotPresent() {
        def pagination = new HttpRequestPagination(new MockHttpServletRequest())
        assert pagination.max == new SimplePagination().max
    }


    @Test
    void shouldFindOffsetParamFromRequestIfPresent() {
        def request = new MockHttpServletRequest()
        request.setParameter(HttpRequestPagination.PARAM_PAGE, "2")
        def pagination = new HttpRequestPagination(request)
        assert pagination.page == 2
    }

    @Test
    void shouldUseDefaultOffsetParamIfNotPresent() {
        def pagination = new HttpRequestPagination(new MockHttpServletRequest())
        assert pagination.page == new SimplePagination().page
    }


    @Test
    void shouldFindSortParamsFromRequestIfPresent() {
        def request = new MockHttpServletRequest()
        request.setParameter(HttpRequestPagination.PARAM_SORT, ["firstName", "age"] as String[])
        request.setParameter(HttpRequestPagination.PARAM_DIRECTION, "ASC")
        def pagination = new HttpRequestPagination(request)
        assert pagination.sorts.size() == 2
        assert pagination.sorts[0].field == "firstName"
        assert pagination.sorts[0].direction == "ASC"
        assert pagination.sorts[1].field == "age"
        assert pagination.sorts[1].direction == "ASC"
    }

    @Test
    void shouldUseDefaultSortParamsIfNotPresent() {
        def pagination = new HttpRequestPagination(new MockHttpServletRequest())
        assert pagination.sorts == new SimplePagination().sorts
    }

    @Test
    void shouldFindScopedSortDirections() {
        def request = new MockHttpServletRequest()
        request.setParameter(HttpRequestPagination.PARAM_SORT, ["firstName", "age"] as String[])
        request.setParameter(HttpRequestPagination.PARAM_DIRECTION, "ASC")
        request.setParameter("age.${HttpRequestPagination.PARAM_DIRECTION}", "DESC")
        def pagination = new HttpRequestPagination(request)
        assert pagination.sorts.size() == 2
        assert pagination.sorts[0].field == "firstName"
        assert pagination.sorts[0].direction == "ASC"
        assert pagination.sorts[1].field == "age"
        assert pagination.sorts[1].direction == "DESC"
    }
}
