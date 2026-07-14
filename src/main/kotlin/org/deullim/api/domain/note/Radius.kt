package org.deullim.api.domain.note

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Radius(
    @Column(nullable = false)
    val meters: Int,
) {
    init {
        require(meters in MIN_RADIUS..MAX_RADIUS) {
            "Radius must be between ${MIN_RADIUS}m and ${MAX_RADIUS}m, but was ${meters}m"
        }
    }

    companion object {
        const val MIN_RADIUS = 50
        const val MAX_RADIUS = 2000

        val DEFAULT = Radius(100)
    }
}
