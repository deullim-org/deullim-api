package org.deullim.api.domain.location

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embedded
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("EXTERNAL")
class ExternalLocation protected constructor() : Location() {
    @Embedded
    lateinit var externalSource: ExternalSource
        protected set

    override fun isVisibleTo(memberId: Long): Boolean = true

    companion object {
        fun of(
            externalSource: ExternalSource,
            coordinate: Coordinate,
            name: String,
        ): ExternalLocation {
            require(name.isNotBlank()) { "name must not be blank" }
            return ExternalLocation().apply {
                this.externalSource = externalSource
                this.coordinate = coordinate
                this.name = name
            }
        }

        fun of(
            provider: ExternalProvider,
            externalId: String,
            coordinate: Coordinate,
            name: String,
        ): ExternalLocation = of(ExternalSource(provider, externalId), coordinate, name)
    }
}
