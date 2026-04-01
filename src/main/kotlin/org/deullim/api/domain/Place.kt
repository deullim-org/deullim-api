package org.deullim.api.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "places")
class Place protected constructor() : Base() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "alias", nullable = true)
    var alias: String? = null
        protected set

    @Column(name = "icon", nullable = true)
    var icon: String? = null
        protected set

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "location_id", nullable = false, updatable = false)
//    lateinit var location: Location
//        protected set

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    lateinit var member: Member
        protected set

    companion object {
        fun create(
            member: Member,
//            location: Location,
            alias: String? = null,
            icon: String? = null,
        ): Place = Place().apply {
            this.member = member
//            this.location = location
            this.alias = alias
            this.icon = icon
        }
    }

    fun changeAlias(newAlias: String) {
        require(newAlias.isNotBlank()) { "alias must not be blank" }
        alias = newAlias.trim()
    }

    fun changeIcon(newIcon: String) {
        require(newIcon.isNotBlank()) { "icon must not be blank" }
        icon = newIcon.trim()
    }
}
