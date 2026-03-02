package org.deullim.api.domain.note

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@DisplayName("Note 도메인 테스트")
class NoteTest {

    @Nested
    @DisplayName("Radius 테스트")
    inner class RadiusTest {

        @Test
        @DisplayName("유효한 반경으로 Radius를 생성할 수 있다")
        fun `should create radius with valid meters`() {
            // given & when
            val radius = Radius(100)

            // then
            assertEquals(100, radius.meters)
        }

        @Test
        @DisplayName("최소 반경(50m)으로 Radius를 생성할 수 있다")
        fun `should create radius with minimum value`() {
            // given & when
            val radius = Radius(Radius.MIN_RADIUS)

            // then
            assertEquals(50, radius.meters)
        }

        @Test
        @DisplayName("최대 반경(2000m)으로 Radius를 생성할 수 있다")
        fun `should create radius with maximum value`() {
            // given & when
            val radius = Radius(Radius.MAX_RADIUS)

            // then
            assertEquals(2000, radius.meters)
        }

        @Test
        @DisplayName("50m 미만의 반경으로 생성 시 예외가 발생한다")
        fun `should throw exception when radius is less than minimum`() {
            // given & when & then
            val exception = assertThrows<IllegalArgumentException> {
                Radius(49)
            }
            assertEquals("Radius must be between 50m and 2000m, but was 49m", exception.message)
        }

        @Test
        @DisplayName("2000m 초과의 반경으로 생성 시 예외가 발생한다")
        fun `should throw exception when radius is greater than maximum`() {
            // given & when & then
            val exception = assertThrows<IllegalArgumentException> {
                Radius(2001)
            }
            assertEquals("Radius must be between 50m and 2000m, but was 2001m", exception.message)
        }

        @Test
        @DisplayName("DEFAULT 반경은 100m이다")
        fun `default radius should be 100 meters`() {
            // given & when & then
            assertEquals(100, Radius.DEFAULT.meters)
        }

        @Test
        @DisplayName("Radius는 data class로 동등성 비교가 가능하다")
        fun `radii with same values should be equal`() {
            // given
            val radius1 = Radius(100)
            val radius2 = Radius(100)
            val radius3 = Radius(200)

            // then
            assertEquals(radius1, radius2)
            assertNotEquals(radius1, radius3)
        }
    }

    @Nested
    @DisplayName("Note 엔티티 테스트")
    inner class NoteEntityTest {

        @Test
        @DisplayName("Note 생성 시 필수 필드만으로 생성할 수 있다")
        fun `should create note with required fields only`() {
            // given
            val title = "테스트 노트"
            val locationId = "loc_123"

            // when
            val note = Note(
                title = title,
                locationId = locationId
            )

            // then
            assertEquals(title, note.title)
            assertEquals(locationId, note.locationId)
            assertEquals(NoteStatus.ACTIVE, note.status)
            assertEquals(NotePolicy.DAY, note.policy)
            assertEquals(Radius.DEFAULT, note.radius)
            assertNotNull(note.activatedAt)
        }

        @Test
        @DisplayName("Note 생성 시 모든 필드를 지정할 수 있다")
        fun `should create note with all fields`() {
            // given
            val title = "전체 필드 테스트"
            val content = "노트 내용입니다"
            val locationId = "loc_456"
            val status = NoteStatus.INACTIVE
            val policy = NotePolicy.WEEK
            val radius = Radius(500)
            val activatedAt = LocalDateTime.of(2025, 1, 1, 12, 0)

            // when
            val note = Note(
                title = title,
                content = content,
                locationId = locationId,
                status = status,
                policy = policy,
                radius = radius,
                activatedAt = activatedAt
            )

            // then
            assertEquals(title, note.title)
            assertEquals(content, note.content)
            assertEquals(locationId, note.locationId)
            assertEquals(status, note.status)
            assertEquals(policy, note.policy)
            assertEquals(radius, note.radius)
            assertEquals(activatedAt, note.activatedAt)
        }

        @Test
        @DisplayName("Note의 title을 수정할 수 있다")
        fun `should update note title`() {
            // given
            val note = Note(title = "원래 제목", locationId = "loc_123")
            val newTitle = "수정된 제목"

            // when
            note.updateTitle(newTitle)

            // then
            assertEquals(newTitle, note.title)
        }

        @Test
        @DisplayName("Note의 content를 수정할 수 있다")
        fun `should update note content`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")
            val newContent = "새로운 내용"

            // when
            note.updateContent(newContent)

            // then
            assertEquals(newContent, note.content)
        }

        @Test
        @DisplayName("Note를 비활성화할 수 있다")
        fun `should deactivate note`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")

            // when
            note.deactivate()

            // then
            assertEquals(NoteStatus.INACTIVE, note.status)
        }

        @Test
        @DisplayName("Note를 활성화할 수 있다")
        fun `should activate note`() {
            // given
            val note = Note(
                title = "제목",
                locationId = "loc_123",
                status = NoteStatus.INACTIVE
            )

            // when
            note.activate()

            // then
            assertEquals(NoteStatus.ACTIVE, note.status)
        }

        @Test
        @DisplayName("Note를 삭제할 수 있다")
        fun `should delete note`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")

            // when
            note.delete()

            // then
            assertEquals(NoteStatus.DELETED, note.status)
        }

        @Test
        @DisplayName("Note의 policy를 수정할 수 있다")
        fun `should update note policy`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")

            // when
            note.updatePolicy(NotePolicy.MONTH)

            // then
            assertEquals(NotePolicy.MONTH, note.policy)
        }

        @Test
        @DisplayName("Note의 activatedAt을 수정할 수 있다")
        fun `should update note activatedAt`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")
            val newActivatedAt = LocalDateTime.of(2025, 6, 15, 10, 0)

            // when
            note.updateActivatedAt(newActivatedAt)

            // then
            assertEquals(newActivatedAt, note.activatedAt)
        }

        @Test
        @DisplayName("Note의 locationId를 수정할 수 있다")
        fun `should update note locationId`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")
            val newLocationId = "loc_456"

            // when
            note.updateLocationId(newLocationId)

            // then
            assertEquals(newLocationId, note.locationId)
        }

        @Test
        @DisplayName("Note의 radius를 수정할 수 있다")
        fun `should update note radius`() {
            // given
            val note = Note(title = "제목", locationId = "loc_123")
            val newRadius = Radius(1000)

            // when
            note.updateRadius(newRadius)

            // then
            assertEquals(newRadius, note.radius)
        }
    }
}
