package org.deullim.api.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "settings")
class Setting protected constructor() : Base() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "radius", nullable = false)
    var radius: Double = DEFAULT_RADIUS_METER
        protected set

    // TODO: 연관처리
    @Column(name = "member_id", nullable = false, updatable = false)
    var memberId: Long = 0
        protected set

    companion object {
        const val MIN_RADIUS_METER: Double = 50.0
        const val MAX_RADIUS_METER: Double = 2000.0
        const val DEFAULT_RADIUS_METER: Double = 200.0

        fun create(memberId: Long, radius: Double?): Setting {
            require(memberId > 0) { "memberId must be positive" }
            if (radius != null) {
                require(radius in MIN_RADIUS_METER..MAX_RADIUS_METER) {
                    "radius must be between $MIN_RADIUS_METER and $MAX_RADIUS_METER meters"
                }
            }
            return Setting().apply {
                this.memberId = memberId
                this.radius = radius ?: DEFAULT_RADIUS_METER
            }
        }
    }

    fun changeRadius(newRadiusMeter: Double) {
        require(newRadiusMeter in MIN_RADIUS_METER..MAX_RADIUS_METER) {
            "radius must be between $MIN_RADIUS_METER and $MAX_RADIUS_METER meters"
        }
        this.radius = newRadiusMeter
    }
}
