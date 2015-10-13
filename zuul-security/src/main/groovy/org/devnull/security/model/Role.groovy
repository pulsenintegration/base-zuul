package com.confluex.security.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.validation.constraints.Pattern
import javax.persistence.*

@Entity
@Table(name = "SecurityRole", uniqueConstraints = [
@UniqueConstraint(columnNames = ["name"])
])
@EqualsAndHashCode
@ToString(includeNames = true)
class Role implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Pattern(regexp = "^ROLE_.*", message = "Role names must begin with ROLE_")
    String name

    String description

    @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinTable(
    name = "SecurityUserRole",
    joinColumns = @JoinColumn(name = "RoleId"),
    inverseJoinColumns = @JoinColumn(name = "UserId")
    )
    List<User> users = []
}