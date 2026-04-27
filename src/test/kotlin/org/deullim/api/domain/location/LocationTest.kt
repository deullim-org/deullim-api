package org.deullim.api.domain.location

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
            val latitude = 37.5665
            val longitude = 126.9780

            val coordinate = Coordinate(latitude, longitude)

            assertEquals(latitude, coordinate.latitude)
            assertEquals(longitude, coordinate.longitude)
        }

        @Test
        @DisplayName("동일한 좌표 간 거리는 0이다")
        fun `distance between same coordinates should be zero`() {
            val coordinate = Coordinate(37.5665, 126.9780)

            val distance = coordinate.distanceTo(coordinate)

            assertEquals(0.0, distance, 0.001)
        }

        @Test
        @DisplayName("서울시청과 강남역 간 거리는 약 9km이다")
        fun `distance between Seoul City Hall and Gangnam Station should be approximately 9km`() {
            val seoulCityHall = Coordinate(37.5665, 126.9780)
            val gangnamStation = Coordinate(37.4979, 127.0276)

            val distance = seoulCityHall.distanceTo(gangnamStation)

            assertTrue(
                distance > 8500 && distance < 9500,
                "Distance should be approximately 9km, but was $distance meters",
            )
        }

        @Test
        @DisplayName("Coordinate는 data class로 동등성 비교가 가능하다")
        fun `coordinates with same values should be equal`() {
            val coordinate1 = Coordinate(37.5665, 126.9780)
            val coordinate2 = Coordinate(37.5665, 126.9780)
            val coordinate3 = Coordinate(37.4979, 127.0276)

            assertEquals(coordinate1, coordinate2)
            assertNotEquals(coordinate1, coordinate3)
        }

        @Test
        @DisplayName("위도가 범위를 벗어나면 예외가 발생한다")
        fun `should throw when latitude is out of range`() {
            assertThrows<IllegalArgumentException> { Coordinate(90.1, 0.0) }
            assertThrows<IllegalArgumentException> { Coordinate(-90.1, 0.0) }
        }

        @Test
        @DisplayName("경도가 범위를 벗어나면 예외가 발생한다")
        fun `should throw when longitude is out of range`() {
            assertThrows<IllegalArgumentException> { Coordinate(0.0, 180.1) }
            assertThrows<IllegalArgumentException> { Coordinate(0.0, -180.1) }
        }

        @Test
        @DisplayName("위/경도 경계값으로 Coordinate를 생성할 수 있다")
        fun `should accept boundary lat lon values`() {
            Coordinate(Coordinate.MAX_LATITUDE, Coordinate.MAX_LONGITUDE)
            Coordinate(Coordinate.MIN_LATITUDE, Coordinate.MIN_LONGITUDE)
        }
    }

    @Nested
    @DisplayName("Location 엔티티 테스트")
    inner class LocationEntityTest {
        @Test
        @DisplayName("Location 생성 시 모든 필드가 올바르게 저장된다")
        fun `should create location with all fields`() {
            val id = "naver_12345"
            val coordinate = Coordinate(37.5665, 126.9780)
            val source = LocationSource.NAVER
            val sourceId = "12345"
            val name = "서울시청"

            val location =
                Location(
                    id = id,
                    coordinate = coordinate,
                    source = source,
                    sourceId = sourceId,
                    name = name,
                )

            assertEquals(id, location.id)
            assertEquals(coordinate, location.coordinate)
            assertEquals(source, location.source)
            assertEquals(sourceId, location.sourceId)
            assertEquals(name, location.name)
        }

        @Test
        @DisplayName("USER 소스로 Location을 생성할 수 있다")
        fun `should create location with USER source`() {
            val source = LocationSource.USER

            val location =
                Location(
                    id = "user_custom_location",
                    coordinate = Coordinate(37.5665, 126.9780),
                    source = source,
                    sourceId = "custom_location",
                    name = "사용자 지정 위치",
                )

            assertEquals(LocationSource.USER, location.source)
        }

        @Test
        @DisplayName("Location의 좌표를 통해 다른 Location과의 거리를 계산할 수 있다")
        fun `should calculate distance between two locations`() {
            val location1 =
                Location(
                    id = "loc1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    name = "서울시청",
                )
            val location2 =
                Location(
                    id = "loc2",
                    coordinate = Coordinate(37.4979, 127.0276),
                    source = LocationSource.NAVER,
                    sourceId = "2",
                    name = "강남역",
                )

            val distance = location1.coordinate.distanceTo(location2.coordinate)

            assertTrue(distance > 8500 && distance < 9500)
        }

        @Test
        @DisplayName("Location.of 팩토리는 source와 sourceId로 id를 생성한다")
        fun `factory should compose id from source and sourceId`() {
            val location =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "12345",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "서울시청",
                )

            assertEquals("naver_12345", location.id)
            assertEquals(LocationSource.NAVER, location.source)
            assertEquals("12345", location.sourceId)
        }

        @Test
        @DisplayName("같은 id를 가진 Location은 동등하다")
        fun `locations with same id should be equal`() {
            val coord = Coordinate(37.5665, 126.9780)
            val a =
                Location(
                    id = "naver_1",
                    coordinate = coord,
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    name = "A",
                )
            val b =
                Location(
                    id = "naver_1",
                    coordinate = coord,
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    name = "B",
                )

            assertEquals(a, b)
            assertEquals(a.hashCode(), b.hashCode())
        }
    }
}
