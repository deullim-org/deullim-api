package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.deullim.api.common.BaseEntity

@Entity
@Table(
    name = "locations",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_locations_external_source",
            columnNames = ["provider", "external_id"],
        ),
    ],
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "source_type", length = 20)
abstract class Location protected constructor() : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @Embedded
    lateinit var coordinate: Coordinate
        protected set

    @Column(nullable = false, length = 200)
    var name: String = ""
        protected set

    abstract fun isVisibleTo(memberId: Long): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
