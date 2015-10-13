package com.confluex.orm.util

import com.confluex.util.pagination.Pagination
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Adapts DevNull Pagination to spring-data-jpa Pageable to prevent coupling from data layer to web layers.
 */
class JpaPaginationAdapter<T> implements Pageable {

    @Delegate
    PageRequest pageRequest

    Pagination<T> pagination

    JpaPaginationAdapter(Pagination<T> pagination) {
        this.pagination = pagination
        def orders = pagination.sorts.collect {
            new Sort.Order(Sort.Direction.fromString(it.direction), it.field)
        }
        def sort = orders.size() > 0 ? new Sort(orders) : null
        this.pageRequest = new PageRequest(pagination.page, pagination.max, sort)
    }
}
