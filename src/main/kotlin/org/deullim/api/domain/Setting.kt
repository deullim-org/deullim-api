package org.deullim.api.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "settings",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_settings_member_id", columnNames = ["member_id"]),
    ],
)
class Setting protected constructor() : Base() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "radius", nullable = false)
    var radius: Double = DEFAULT_RADIUS_METER
        protected set

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    lateinit var member: Member
        protected set

    companion object {
        const val MIN_RADIUS_METER: Double = 50.0
        const val MAX_RADIUS_METER: Double = 2000.0
        const val DEFAULT_RADIUS_METER: Double = 200.0

        fun create(
            member: Member,
            radius: Double?,
        ): Setting {
            if (radius != null) {
                require(radius in MIN_RADIUS_METER..MAX_RADIUS_METER) {
                    "radius must be between $MIN_RADIUS_METER and $MAX_RADIUS_METER meters"
                }
            }
            return Setting().apply {
                this.member = member
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Setting) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
