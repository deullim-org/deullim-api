package org.deullim.api.domain.note

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.deullim.api.common.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "notes")
class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    @Column(nullable = false, length = 100)
    var locationId: String,

    // TODO: Member 도메인 구현 후 관계 설정
    // @Column(nullable = false)
    // var memberId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: NoteStatus = NoteStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var policy: NotePolicy = NotePolicy.DAY,

    @Embedded
    var radius: Radius = Radius.DEFAULT,

    @Column(nullable = false)
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
