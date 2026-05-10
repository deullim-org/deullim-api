package org.deullim.api.domain.note

import org.deullim.api.domain.location.Coordinate
import org.deullim.api.domain.location.Location
import org.deullim.api.domain.location.LocationSource
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

private fun testLocation(sourceId: String = "test"): Location =
    Location.of(
        source = LocationSource.NAVER,
        sourceId = sourceId,
        coordinate = Coordinate(37.5665, 126.9780),
        name = "테스트 위치",
    )

private fun Note.injectId(id: Long) {
    val field = Note::class.java.getDeclaredField("id")
    field.isAccessible = true
    field.set(this, id)
}

@DisplayName("Note 도메인 테스트")
class NoteTest {
    @Nested
    @DisplayName("Radius 테스트")
    inner class RadiusTest {
        @Test
        @DisplayName("유효한 반경으로 Radius를 생성할 수 있다")
        fun `should create radius with valid meters`() {
            val radius = Radius(100)

            assertEquals(100, radius.meters)
        }

        @Test
        @DisplayName("최소 반경(50m)으로 Radius를 생성할 수 있다")
        fun `should create radius with minimum value`() {
            val radius = Radius(Radius.MIN_RADIUS)

            assertEquals(50, radius.meters)
        }

        @Test
        @DisplayName("최대 반경(2000m)으로 Radius를 생성할 수 있다")
        fun `should create radius with maximum value`() {
            val radius = Radius(Radius.MAX_RADIUS)

            assertEquals(2000, radius.meters)
        }

        @Test
        @DisplayName("50m 미만의 반경으로 생성 시 예외가 발생한다")
        fun `should throw exception when radius is less than minimum`() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    Radius(49)
                }
            assertEquals("Radius must be between 50m and 2000m, but was 49m", exception.message)
        }

        @Test
        @DisplayName("2000m 초과의 반경으로 생성 시 예외가 발생한다")
        fun `should throw exception when radius is greater than maximum`() {
            val exception =
                assertThrows<IllegalArgumentException> {
                    Radius(2001)
                }
            assertEquals("Radius must be between 50m and 2000m, but was 2001m", exception.message)
        }

        @Test
        @DisplayName("DEFAULT 반경은 100m이다")
        fun `default radius should be 100 meters`() {
            assertEquals(100, Radius.DEFAULT.meters)
        }

        @Test
        @DisplayName("Radius는 data class로 동등성 비교가 가능하다")
        fun `radii with same values should be equal`() {
            val radius1 = Radius(100)
            val radius2 = Radius(100)
            val radius3 = Radius(200)

            assertEquals(radius1, radius2)
            assertNotEquals(radius1, radius3)
        }
    }

    @Nested
    @DisplayName("Note 엔티티 테스트")
    inner class NoteEntityTest {
        @Test
        @DisplayName("Note.create로 필수 필드만으로 생성할 수 있다")
        fun `should create note with required fields only`() {
            val title = "테스트 노트"
            val location = testLocation("loc_123")

            val note = Note.create(title = title, location = location)

            assertEquals(title, note.title)
            assertEquals(location, note.location)
            assertEquals(NoteStatus.ACTIVE, note.status)
            assertEquals(NotePolicy.DAY, note.policy)
            assertEquals(Radius.DEFAULT, note.radius)
            assertNotNull(note.activatedAt)
        }

        @Test
        @DisplayName("Note.create로 모든 필드를 지정할 수 있다")
        fun `should create note with all fields`() {
            val title = "전체 필드 테스트"
            val content = "노트 내용입니다"
            val location = testLocation("loc_456")
            val policy = NotePolicy.WEEK
            val radius = Radius(500)
            val activatedAt = Instant.parse("2025-01-01T12:00:00Z")

            val note =
                Note.create(
                    title = title,
                    location = location,
                    content = content,
                    policy = policy,
                    radius = radius,
                    activatedAt = activatedAt,
                )

            assertEquals(title, note.title)
            assertEquals(content, note.content)
            assertEquals(location, note.location)
            assertEquals(NoteStatus.ACTIVE, note.status)
            assertEquals(policy, note.policy)
            assertEquals(radius, note.radius)
            assertEquals(activatedAt, note.activatedAt)
        }

        @Test
        @DisplayName("title이 공백이면 생성 시 예외가 발생한다")
        fun `should throw when creating note with blank title`() {
            assertThrows<IllegalArgumentException> {
                Note.create(title = " ", location = testLocation())
            }
        }

        @Test
        @DisplayName("edit으로 title만 수정할 수 있다")
        fun `should edit only title`() {
            val note = Note.create(title = "원래 제목", location = testLocation())
            val newTitle = "수정된 제목"

            note.edit(title = newTitle)

            assertEquals(newTitle, note.title)
        }

        @Test
        @DisplayName("edit으로 content를 수정할 수 있다")
        fun `should edit content`() {
            val note = Note.create(title = "제목", location = testLocation())
            val newContent = "새로운 내용"

            note.edit(content = newContent)

            assertEquals(newContent, note.content)
        }

        @Test
        @DisplayName("edit으로 content를 null로 비울 수 있다")
        fun `should clear content via edit`() {
            val note = Note.create(title = "제목", content = "초기 내용", location = testLocation())

            note.edit(content = null)

            assertEquals(null, note.content)
        }

        @Test
        @DisplayName("edit으로 여러 필드를 동시에 수정할 수 있다")
        fun `should edit multiple fields at once`() {
            val note = Note.create(title = "제목", location = testLocation("loc_old"))
            val newLocation = testLocation("loc_new")
            val newActivatedAt = Instant.parse("2025-06-15T10:00:00Z")

            note.edit(
                title = "새 제목",
                location = newLocation,
                policy = NotePolicy.MONTH,
                radius = Radius(1000),
                activatedAt = newActivatedAt,
            )

            assertEquals("새 제목", note.title)
            assertEquals(newLocation, note.location)
            assertEquals(NotePolicy.MONTH, note.policy)
            assertEquals(Radius(1000), note.radius)
            assertEquals(newActivatedAt, note.activatedAt)
        }

        @Test
        @DisplayName("edit 시 title이 공백이면 예외가 발생한다")
        fun `should throw when editing with blank title`() {
            val note = Note.create(title = "제목", location = testLocation())

            assertThrows<IllegalArgumentException> {
                note.edit(title = " ")
            }
        }

        @Test
        @DisplayName("Note를 비활성화할 수 있다")
        fun `should deactivate note`() {
            val note = Note.create(title = "제목", location = testLocation())

            note.deactivate()

            assertEquals(NoteStatus.INACTIVE, note.status)
        }

        @Test
        @DisplayName("INACTIVE 상태에서 Note를 활성화할 수 있다")
        fun `should activate note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.deactivate()

            note.activate()

            assertEquals(NoteStatus.ACTIVE, note.status)
        }

        @Test
        @DisplayName("Note를 삭제할 수 있다")
        fun `should delete note`() {
            val note = Note.create(title = "제목", location = testLocation())

            note.delete()

            assertEquals(NoteStatus.DELETED, note.status)
        }

        @Test
        @DisplayName("DELETED 상태의 Note는 edit 시 예외가 발생한다")
        fun `should throw when editing deleted note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.delete()

            val exception =
                assertThrows<IllegalStateException> {
                    note.edit(title = "수정 시도")
                }
            assertEquals("Cannot edit a deleted note", exception.message)
        }

        @Test
        @DisplayName("DELETED 상태의 Note는 activate 시 예외가 발생한다")
        fun `should throw when activating deleted note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.delete()

            val exception =
                assertThrows<IllegalStateException> {
                    note.activate()
                }
            assertEquals("Cannot activate a deleted note", exception.message)
        }

        @Test
        @DisplayName("DELETED 상태의 Note는 deactivate 시 예외가 발생한다")
        fun `should throw when deactivating deleted note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.delete()

            val exception =
                assertThrows<IllegalStateException> {
                    note.deactivate()
                }
            assertEquals("Cannot deactivate a deleted note", exception.message)
        }

        @Test
        @DisplayName("delete는 멱등성이 있다")
        fun `delete should be idempotent`() {
            val note = Note.create(title = "제목", location = testLocation())

            note.delete()
            note.delete()

            assertEquals(NoteStatus.DELETED, note.status)
        }
    }

    @Nested
    @DisplayName("rescheduleAfterNotification 테스트")
    inner class RescheduleTest {
        private val baseTime: Instant = Instant.parse("2026-04-27T12:00:00Z")

        @Test
        @DisplayName("NONE 정책은 알림 후 INACTIVE로 전환된다")
        fun `NONE policy should turn note inactive after notification`() {
            val note =
                Note.create(
                    title = "일회성",
                    location = testLocation(),
                    policy = NotePolicy.NONE,
                    activatedAt = baseTime,
                )

            note.rescheduleAfterNotification(now = baseTime)

            assertEquals(NoteStatus.INACTIVE, note.status)
            assertEquals(baseTime, note.activatedAt)
        }

        @Test
        @DisplayName("DAY 정책은 알림 후 활성화 시점이 1일 뒤로 이동한다")
        fun `DAY policy should push activatedAt one day forward`() {
            val note =
                Note.create(
                    title = "매일",
                    location = testLocation(),
                    policy = NotePolicy.DAY,
                    activatedAt = baseTime,
                )

            note.rescheduleAfterNotification(now = baseTime)

            assertEquals(NoteStatus.ACTIVE, note.status)
            assertEquals(baseTime.atOffset(ZoneOffset.UTC).plusDays(1).toInstant(), note.activatedAt)
        }

        @Test
        @DisplayName("WEEK 정책은 알림 후 활성화 시점이 1주 뒤로 이동한다")
        fun `WEEK policy should push activatedAt one week forward`() {
            val note =
                Note.create(
                    title = "매주",
                    location = testLocation(),
                    policy = NotePolicy.WEEK,
                    activatedAt = baseTime,
                )

            note.rescheduleAfterNotification(now = baseTime)

            assertEquals(baseTime.atOffset(ZoneOffset.UTC).plusWeeks(1).toInstant(), note.activatedAt)
        }

        @Test
        @DisplayName("MONTH 정책은 알림 후 활성화 시점이 1개월 뒤로 이동한다")
        fun `MONTH policy should push activatedAt one month forward`() {
            val note =
                Note.create(
                    title = "매달",
                    location = testLocation(),
                    policy = NotePolicy.MONTH,
                    activatedAt = baseTime,
                )

            note.rescheduleAfterNotification(now = baseTime)

            assertEquals(baseTime.atOffset(ZoneOffset.UTC).plusMonths(1).toInstant(), note.activatedAt)
        }

        @Test
        @DisplayName("INACTIVE 상태에서는 reschedule 시 예외가 발생한다")
        fun `should throw when rescheduling inactive note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.deactivate()

            val exception =
                assertThrows<IllegalStateException> {
                    note.rescheduleAfterNotification()
                }
            assertEquals("Cannot reschedule a non-active note", exception.message)
        }

        @Test
        @DisplayName("DELETED 상태에서는 reschedule 시 예외가 발생한다")
        fun `should throw when rescheduling deleted note`() {
            val note = Note.create(title = "제목", location = testLocation())
            note.delete()

            val exception =
                assertThrows<IllegalStateException> {
                    note.rescheduleAfterNotification()
                }
            assertEquals("Cannot reschedule a non-active note", exception.message)
        }
    }

    @Nested
    @DisplayName("Note 동등성 테스트")
    inner class NoteEqualityTest {
        @Test
        @DisplayName("같은 id를 가진 영속 Note는 동등하다")
        fun `persisted notes with same id should be equal`() {
            val note1 = Note.create(title = "A", location = testLocation("loc_1"))
            val note2 = Note.create(title = "B", location = testLocation("loc_2"))
            note1.injectId(1L)
            note2.injectId(1L)

            assertEquals(note1, note2)
            assertEquals(note1.hashCode(), note2.hashCode())
        }

        @Test
        @DisplayName("transient(id=0) Note 두 개는 동일 인스턴스가 아니면 동등하지 않다")
        fun `transient notes are not equal unless same instance`() {
            val note1 = Note.create(title = "A", location = testLocation())
            val note2 = Note.create(title = "A", location = testLocation())

            assertNotEquals(note1, note2)
        }
    }
}
