package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class ExternalSource(
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    val provider: ExternalProvider,
    @Column(name = "external_id", length = 100)
    val externalId: String,
) {
    init {
        require(externalId.isNotBlank()) { "externalId must not be blank" }
    }
}
