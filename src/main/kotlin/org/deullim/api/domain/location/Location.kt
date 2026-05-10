package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.deullim.api.common.BaseEntity

@Entity
@Table(
    name = "locations",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_locations_external_source",
            columnNames = ["source", "source_id"],
        ),
    ],
)
class Location protected constructor() : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @Embedded
    lateinit var externalSource: ExternalSource
        protected set

    @Embedded
    lateinit var coordinate: Coordinate
        protected set

    @Column(nullable = false, length = 200)
    var name: String = ""
        protected set

    // TODO: Member 도메인 구현 후 관계 설정
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id")
    // lateinit var member: Member
    //     protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    companion object {
        fun of(
            source: LocationSource,
            sourceId: String,
            coordinate: Coordinate,
            name: String,
        ): Location {
            require(name.isNotBlank()) { "name must not be blank" }
            return Location().apply {
                this.externalSource = ExternalSource(source, sourceId)
                this.coordinate = coordinate
                this.name = name
            }
        }
    }
}
