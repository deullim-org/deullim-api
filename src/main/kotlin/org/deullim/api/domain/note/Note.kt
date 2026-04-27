package org.deullim.api.domain.note

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.deullim.api.common.BaseTimeEntity
import org.deullim.api.domain.location.Location
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    var location: Location,
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
    var activatedAt: LocalDateTime = LocalDateTime.now(),
) : BaseTimeEntity() {
    fun edit(
        title: String = this.title,
        content: String? = this.content,
        location: Location = this.location,
        policy: NotePolicy = this.policy,
        radius: Radius = this.radius,
        activatedAt: LocalDateTime = this.activatedAt,
    ) {
        check(status != NoteStatus.DELETED) { "Cannot edit a deleted note" }
        this.title = title
        this.content = content
        this.location = location
        this.policy = policy
        this.radius = radius
        this.activatedAt = activatedAt
    }

    fun activate() {
        check(status != NoteStatus.DELETED) { "Cannot activate a deleted note" }
        this.status = NoteStatus.ACTIVE
    }

    fun deactivate() {
        check(status != NoteStatus.DELETED) { "Cannot deactivate a deleted note" }
        this.status = NoteStatus.INACTIVE
    }

    fun delete() {
        this.status = NoteStatus.DELETED
    }

    fun rescheduleAfterNotification(now: LocalDateTime = LocalDateTime.now()) {
        check(status == NoteStatus.ACTIVE) { "Cannot reschedule a non-active note" }
        when (policy) {
            NotePolicy.NONE -> status = NoteStatus.INACTIVE
            NotePolicy.DAY -> activatedAt = now.plusDays(1)
            NotePolicy.WEEK -> activatedAt = now.plusWeeks(1)
            NotePolicy.MONTH -> activatedAt = now.plusMonths(1)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
