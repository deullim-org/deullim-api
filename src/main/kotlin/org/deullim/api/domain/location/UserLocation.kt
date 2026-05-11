package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("USER")
class UserLocation protected constructor() : Location() {
    @Column(name = "member_id")
    var memberId: Long = 0L
        protected set

    override fun isVisibleTo(memberId: Long): Boolean = this.memberId == memberId

    companion object {
        fun of(
            memberId: Long,
            coordinate: Coordinate,
            name: String,
        ): UserLocation {
            require(memberId > 0) { "memberId must be positive" }
            require(name.isNotBlank()) { "name must not be blank" }
            return UserLocation().apply {
                this.memberId = memberId
                this.coordinate = coordinate
                this.name = name
            }
        }
    }
}
