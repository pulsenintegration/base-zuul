package com.confluex.util.pagination

/**
 * Utility class used for pagination of result sets
 */
interface Pagination<T> extends List<T> {

    static final String ORDER_ASC = "ASC"
    static final String ORDER_DESC = "DESC"

    /**
     * Contains a subset of the requested rows. Delegate for List.
     */
    List<T> getResults()
    void setResults(List<T> results)

    /**
     * Key value pairs used to limit reset set.
     */
    Map<String, Serializable> getFilter()

    /**
     * Total results available regardless of current results size.
     */
    Long getTotal()
    void setTotal(Long total)

    /**
     * Current page (slice) of the total results.
     *
     * Default = {@value}
     */
    Integer getPage()

    /**
     * The max number of responses that can be fetched per request.
     */
    Integer getMax()

    /**
     * The field names to sort on
     */
    List<Sort> getSorts()

}
