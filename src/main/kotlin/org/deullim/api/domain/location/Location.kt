package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.deullim.api.common.BaseEntity

@Entity
@Table(name = "locations")
class Location protected constructor() : BaseEntity() {
    @Id
    @Column(length = 100)
    var id: String = ""
        protected set

    @Embedded
    lateinit var coordinate: Coordinate
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    lateinit var source: LocationSource
        protected set

    @Column(nullable = false, length = 100)
    var sourceId: String = ""
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
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun of(
            source: LocationSource,
            sourceId: String,
            coordinate: Coordinate,
            name: String,
        ): Location {
            require(sourceId.isNotBlank()) { "sourceId must not be blank" }
            require(name.isNotBlank()) { "name must not be blank" }
            return Location().apply {
                this.id = "${source.name.lowercase()}_$sourceId"
                this.source = source
                this.sourceId = sourceId
                this.coordinate = coordinate
                this.name = name
            }
        }
    }
}
