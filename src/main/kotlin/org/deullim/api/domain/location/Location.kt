package org.deullim.api.domain.location

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.deullim.api.common.BaseTimeEntity

@Entity
class Location(
    @Id val id: String,
    val coordinate: Coordinate,
    val source: LocationSource,
    val sourceId: String,
    val name: String

    // TODO: Member 도메인 구현 후 관계 설정
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id")
    // val member: Member? = null
) : BaseTimeEntity()
