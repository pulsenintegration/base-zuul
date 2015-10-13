package com.confluex.util.pagination

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames=true)
class Sort implements Serializable {

    static final long serialVersionUID = 1L;

    /**
     * Ascending direction: {@value}
     */
    static final String ASC = "ASC"

    /**
     * Descending direction: {@value}
     */
    static final String DESC = "DESC"

    /**
     * The field name to sort on
     */
    String field

    /**
     * The direction to sort in
     *
     * default = {@value}
     */
    String direction = ASC
}
