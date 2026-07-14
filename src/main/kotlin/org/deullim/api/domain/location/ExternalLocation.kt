package org.deullim.api.domain.location

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "external_locations",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_external_locations_source",
            columnNames = ["provider", "external_id"],
        ),
    ],
)
@DiscriminatorValue("EXTERNAL")
class ExternalLocation protected constructor() : Location() {
    @Embedded
    lateinit var externalSource: ExternalSource
        protected set

    override fun isVisibleTo(memberId: Long): Boolean = true

    companion object {
        fun create(
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

        fun create(
            provider: ExternalProvider,
            externalId: String,
            coordinate: Coordinate,
            name: String,
        ): ExternalLocation = create(ExternalSource(provider, externalId), coordinate, name)
    }
}
