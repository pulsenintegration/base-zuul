package com.confluex.orm.model

import groovy.transform.EqualsAndHashCode
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@EqualsAndHashCode
class Book implements Serializable {

    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @ManyToOne
    Author author

    String title

    String toString() {
        return "${title} - ${author}"
    }
}
