package com.confluex.orm.model

import groovy.transform.EqualsAndHashCode
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Size

@Entity
@EqualsAndHashCode(excludes = 'books')
class Author implements Serializable {

    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Size(min=2)
    String firstName

    @Size(min=2)
    String lastName

    @OneToMany(mappedBy = 'author', cascade = CascadeType.ALL, orphanRemoval = true)
    List<Book> books = []

    String toString() {
        return "${firstName} ${lastName}"
    }
}
