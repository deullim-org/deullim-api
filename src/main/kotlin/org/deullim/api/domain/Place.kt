package org.deullim.api.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    // TODO: 연관 처리
    @Column(name = "location_id", nullable = false, updatable = false)
    var locationId: Long = 0
        protected set

    // TODO: 연관 처리
    @Column(name = "member_id", nullable = false, updatable = false)
    var memberId: Long = 0
        protected set

    companion object {
        fun create(
            memberId: Long,
            locationId: Long,
            alias: String? = null,
            icon: String? = null,
        ): Place = Place().apply {
            require(memberId > 0) { "memberId must be positive" }
            require(locationId > 0) { "locationId must be positive" }

            this.memberId = memberId
            this.locationId = locationId
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
