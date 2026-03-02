package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.deullim.api.common.BaseTimeEntity

@Entity
@Table(name = "locations")
class Location(
    @Id
    @Column(length = 100)
    val id: String,

    @Embedded
    val coordinate: Coordinate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val source: LocationSource,

    @Column(nullable = false, length = 100)
    val sourceId: String,

    @Column(nullable = false, length = 200)
    val name: String

    // TODO: Member 도메인 구현 후 관계 설정
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id")
    // val member: Member? = null
) : BaseTimeEntity()
