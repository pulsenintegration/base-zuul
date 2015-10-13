package com.confluex.util.pagination.adapter

import com.confluex.util.pagination.Pagination
import com.confluex.util.pagination.Sort
import org.displaytag.pagination.PaginatedList
import org.displaytag.properties.SortOrderEnum
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class DisplayTagPaginatedListAdapterTest {
    PaginatedList tagList
    Pagination pagination

    @Before
    void createList() {
        pagination = mock(Pagination)
        tagList = new DisplayTagPaginatedListAdapter(pagination)
    }

    @Test
    void shouldContainCorrectListContents() {
        when(pagination.results).thenReturn([1, 2, 3])
        assert tagList.list == [1, 2, 3]

    }

    @Test
    void shouldHaveCorrectFullListSize() {
        when(pagination.total).thenReturn(10L)
        assert tagList.fullListSize == 10
    }

    @Test
    void shouldUseMaxIntegerSizeWhenTotalsAreLargerThanCapacity() {
        when(pagination.total).thenReturn(Integer.MAX_VALUE + 17L)
        assert tagList.fullListSize == Integer.MAX_VALUE
    }

    @Test
    void shouldHaveCorrectObjectsPerPage() {
        when(pagination.max).thenReturn(5)
        assert tagList.objectsPerPage == 5
    }

    @Test
    void shouldHaveCorrectPageNumber() {
        when(pagination.page).thenReturn(2)
        assert tagList.pageNumber == 3
    }

    @Test
    void shouldHaveNoSortCriterionIfNotContainedInPagination() {
        assert tagList.sortCriterion == null
    }

    @Test
    void shouldUseFirstSortObjectForCriterionWhenMultipleSortsExistInPagination() {
        def sorts = [new Sort(field: "name"), new Sort(field: "count", direction: Sort.DESC)]
        when(pagination.sorts).thenReturn(sorts)
        assert tagList.sortCriterion == "name"
        assert tagList.sortDirection == SortOrderEnum.ASCENDING
    }

    @Test
    void shouldConvertAscendingOrderPropertyCorrectly() {
        def sorts = [new Sort(field: "count", direction: Sort.ASC)]
        when(pagination.sorts).thenReturn(sorts)
        assert tagList.sortDirection == SortOrderEnum.ASCENDING
    }

    @Test
    void shouldConvertDescendingOrderPropertyCorrectly() {
        def sorts = [new Sort(field: "count", direction: Sort.DESC)]
        when(pagination.sorts).thenReturn(sorts)
        assert tagList.sortDirection == SortOrderEnum.DESCENDING
    }
    
    @Test
    void shouldConvertToAscendingOrderWhenValueIsNotSupported() {
        def sorts = [new Sort(field: "count", direction: "updownleftright")]
        when(pagination.sorts).thenReturn(sorts)
        assert tagList.sortDirection == SortOrderEnum.ASCENDING
    }

}
