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
import org.deullim.api.domain.Base

@Entity
@Table(name = "locations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "source_type", length = 20)
abstract class Location protected constructor() : Base() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
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
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
