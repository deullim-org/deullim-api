package org.deullim.api.domain.location

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@DisplayName("Location 도메인 테스트")
class LocationTest {

    @Nested
    @DisplayName("Coordinate 테스트")
    inner class CoordinateTest {

        @Test
        @DisplayName("Coordinate 생성 시 위도와 경도가 올바르게 저장된다")
        fun `should create coordinate with latitude and longitude`() {
            // given
            val latitude = 37.5665
            val longitude = 126.9780

            // when
            val coordinate = Coordinate(latitude, longitude)

            // then
            assertEquals(latitude, coordinate.latitude)
            assertEquals(longitude, coordinate.longitude)
        }

        @Test
        @DisplayName("동일한 좌표 간 거리는 0이다")
        fun `distance between same coordinates should be zero`() {
            // given
            val coordinate = Coordinate(37.5665, 126.9780)

            // when
            val distance = coordinate.distanceTo(coordinate)

            // then
            assertEquals(0.0, distance, 0.001)
        }

        @Test
        @DisplayName("서울시청과 강남역 간 거리는 약 9km이다")
        fun `distance between Seoul City Hall and Gangnam Station should be approximately 9km`() {
            // given
            val seoulCityHall = Coordinate(37.5665, 126.9780) // 서울시청
            val gangnamStation = Coordinate(37.4979, 127.0276) // 강남역

            // when
            val distance = seoulCityHall.distanceTo(gangnamStation)

            // then
            assertTrue(distance > 8500 && distance < 9500, "Distance should be approximately 9km, but was $distance meters")
        }

        @Test
        @DisplayName("Coordinate는 data class로 동등성 비교가 가능하다")
        fun `coordinates with same values should be equal`() {
            // given
            val coordinate1 = Coordinate(37.5665, 126.9780)
            val coordinate2 = Coordinate(37.5665, 126.9780)
            val coordinate3 = Coordinate(37.4979, 127.0276)

            // then
            assertEquals(coordinate1, coordinate2)
            assertNotEquals(coordinate1, coordinate3)
        }
    }

    @Nested
    @DisplayName("Location 엔티티 테스트")
    inner class LocationEntityTest {

        @Test
        @DisplayName("Location 생성 시 모든 필드가 올바르게 저장된다")
        fun `should create location with all fields`() {
            // given
            val id = "naver_12345"
            val coordinate = Coordinate(37.5665, 126.9780)
            val source = LocationSource.NAVER
            val sourceId = "12345"
            val name = "서울시청"

            // when
            val location = Location(
                id = id,
                coordinate = coordinate,
                source = source,
                sourceId = sourceId,
                name = name
            )

            // then
            assertEquals(id, location.id)
            assertEquals(coordinate, location.coordinate)
            assertEquals(source, location.source)
            assertEquals(sourceId, location.sourceId)
            assertEquals(name, location.name)
        }

        @Test
        @DisplayName("USER 소스로 Location을 생성할 수 있다")
        fun `should create location with USER source`() {
            // given
            val source = LocationSource.USER

            // when
            val location = Location(
                id = "user_custom_location",
                coordinate = Coordinate(37.5665, 126.9780),
                source = source,
                sourceId = "custom_location",
                name = "사용자 지정 위치"
            )

            // then
            assertEquals(LocationSource.USER, location.source)
        }

        @Test
        @DisplayName("Location의 좌표를 통해 다른 Location과의 거리를 계산할 수 있다")
        fun `should calculate distance between two locations`() {
            // given
            val location1 = Location(
                id = "loc1",
                coordinate = Coordinate(37.5665, 126.9780),
                source = LocationSource.NAVER,
                sourceId = "1",
                name = "서울시청"
            )
            val location2 = Location(
                id = "loc2",
                coordinate = Coordinate(37.4979, 127.0276),
                source = LocationSource.NAVER,
                sourceId = "2",
                name = "강남역"
            )

            // when
            val distance = location1.coordinate.distanceTo(location2.coordinate)

            // then
            assertTrue(distance > 8500 && distance < 9500)
        }
    }
}
