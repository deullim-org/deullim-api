package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class ExternalSource(
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    val source: LocationSource,
    @Column(name = "source_id", nullable = false, length = 100)
    val sourceId: String,
) {
    init {
        require(sourceId.isNotBlank()) { "sourceId must not be blank" }
    }
}
