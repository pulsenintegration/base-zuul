package com.confluex.orm.util

import com.confluex.util.pagination.SimplePagination
import com.confluex.util.pagination.Sort
import org.junit.Test
import org.springframework.data.domain.Pageable

class JpaPaginationAdapterTest {
    @Test
    void shouldConvertFromPaginationToPageable() {
        def pagination = new SimplePagination(
                page: 3, max: 10,
                sorts: [
                        new Sort(field: "foo", direction: "DESC"),
                        new Sort(field: "bar", direction: "ASC")
                ]
        )
        def adapter = new JpaPaginationAdapter(pagination)
        assert adapter instanceof Pageable
        assert adapter.sort.getOrderFor("foo").direction.toString() == "DESC"
        assert adapter.sort.getOrderFor("bar").direction.toString() == "ASC"
        assert adapter.pageNumber == 3
        assert adapter.pageSize == 10
        assert adapter.offset == 30
    }

    @Test
    void shouldNotBuildSortIfCriteriaIsNotAvailable() {
        def pagination = new SimplePagination(page: 3, max: 10)
        def adapter = new JpaPaginationAdapter(pagination)
        assert !adapter.sort
    }
}
