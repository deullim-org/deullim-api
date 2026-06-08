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
import org.deullim.api.domain.Base
import org.deullim.api.domain.location.Location
import java.time.Instant
import java.time.ZoneOffset

@Entity
@Table(name = "notes")
class Note protected constructor() : Base() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false, length = 200)
    var title: String = ""
        protected set

    @Column(columnDefinition = "TEXT")
    var content: String? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    lateinit var location: Location
        protected set

    // TODO: Member 도메인 구현 후 관계 설정
    // @Column(nullable = false)
    // var memberId: Long = 0L
    //     protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: NoteStatus = NoteStatus.ACTIVE
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var policy: NotePolicy = NotePolicy.DAY
        protected set

    @Embedded
    var radius: Radius = Radius.DEFAULT
        protected set

    @Column(nullable = false)
    var activatedAt: Instant = Instant.now()
        protected set

    fun edit(
        title: String = this.title,
        content: String? = this.content,
        location: Location = this.location,
        policy: NotePolicy = this.policy,
        radius: Radius = this.radius,
        activatedAt: Instant = this.activatedAt,
    ) {
        check(status != NoteStatus.DELETED) { "Cannot edit a deleted note" }
        require(title.isNotBlank()) { "title must not be blank" }
        this.title = title
        this.content = content
        this.location = location
        this.policy = policy
        this.radius = radius
        this.activatedAt = activatedAt
    }

    fun activate() {
        check(status != NoteStatus.DELETED) { "Cannot activate a deleted note" }
        check(status != NoteStatus.ACTIVE) { "Note is already active" }
        this.status = NoteStatus.ACTIVE
    }

    fun deactivate() {
        check(status != NoteStatus.DELETED) { "Cannot deactivate a deleted note" }
        check(status != NoteStatus.INACTIVE) { "Note is already inactive" }
        this.status = NoteStatus.INACTIVE
    }

    fun delete() {
        check(status != NoteStatus.DELETED) { "Note is already deleted" }
        this.status = NoteStatus.DELETED
    }

    fun reschedule(now: Instant = Instant.now()) {
        check(status == NoteStatus.ACTIVE) { "Cannot reschedule a non-active note" }
        val nowUtc = now.atOffset(ZoneOffset.UTC)
        when (policy) {
            NotePolicy.NONE -> status = NoteStatus.INACTIVE
            NotePolicy.DAY -> activatedAt = nowUtc.plusDays(1).toInstant()
            NotePolicy.WEEK -> activatedAt = nowUtc.plusWeeks(1).toInstant()
            NotePolicy.MONTH -> activatedAt = nowUtc.plusMonths(1).toInstant()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    companion object {
        fun create(
            title: String,
            location: Location,
            content: String? = null,
            policy: NotePolicy = NotePolicy.DAY,
            radius: Radius = Radius.DEFAULT,
            activatedAt: Instant = Instant.now(),
        ): Note {
            require(title.isNotBlank()) { "title must not be blank" }
            return Note().apply {
                this.title = title
                this.location = location
                this.content = content
                this.policy = policy
                this.radius = radius
                this.activatedAt = activatedAt
            }
        }
    }
}
