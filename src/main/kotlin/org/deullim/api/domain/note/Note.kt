package org.deullim.api.domain.note

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.deullim.api.common.BaseTimeEntity
import java.time.LocalDateTime

@Entity
class Note(
    @Id val id: Long = 0L,
    var title: String,
    var content: String? = null,
    var locationId: String,

    // TODO: Member 도메인 구현 후 관계 설정
    // @Column(nullable = false)
    // var memberId: Long,

    var status: NoteStatus = NoteStatus.ACTIVE,
    var policy: NotePolicy = NotePolicy.DAY,
    var radius: Radius = Radius.DEFAULT,
    var activatedAt: LocalDateTime = LocalDateTime.now()
) : BaseTimeEntity() {

    fun updateTitle(newTitle: String) {
        this.title = newTitle
    }

    fun updateContent(newContent: String?) {
        this.content = newContent
    }

    fun updateLocationId(newLocationId: String) {
        this.locationId = newLocationId
    }

    fun updatePolicy(newPolicy: NotePolicy) {
        this.policy = newPolicy
    }

    fun updateRadius(newRadius: Radius) {
        this.radius = newRadius
    }

    fun updateActivatedAt(newActivatedAt: LocalDateTime) {
        this.activatedAt = newActivatedAt
    }

    fun activate() {
        this.status = NoteStatus.ACTIVE
    }

    fun deactivate() {
        this.status = NoteStatus.INACTIVE
    }

    fun delete() {
        this.status = NoteStatus.DELETED
    }
}
