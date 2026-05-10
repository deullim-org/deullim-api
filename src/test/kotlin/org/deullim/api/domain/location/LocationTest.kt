package org.deullim.api.domain.location

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private fun Location.injectId(id: Long) {
    val field = Location::class.java.getDeclaredField("id")
    field.isAccessible = true
    field.set(this, id)
}

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
    @DisplayName("ExternalSource 테스트")
    inner class ExternalSourceTest {
        @Test
        @DisplayName("ExternalSource 생성 시 source와 sourceId가 저장된다")
        fun `should create external source with source and sourceId`() {
            val external = ExternalSource(LocationSource.NAVER, "12345")

            assertEquals(LocationSource.NAVER, external.source)
            assertEquals("12345", external.sourceId)
        }

        @Test
        @DisplayName("sourceId가 공백이면 예외가 발생한다")
        fun `should throw when sourceId is blank`() {
            assertThrows<IllegalArgumentException> {
                ExternalSource(LocationSource.NAVER, " ")
            }
            assertThrows<IllegalArgumentException> {
                ExternalSource(LocationSource.NAVER, "")
            }
        }

        @Test
        @DisplayName("같은 source/sourceId를 가지면 동등하다")
        fun `external sources with same values should be equal`() {
            val a = ExternalSource(LocationSource.NAVER, "1")
            val b = ExternalSource(LocationSource.NAVER, "1")
            val c = ExternalSource(LocationSource.USER, "1")
            val d = ExternalSource(LocationSource.NAVER, "2")

            assertEquals(a, b)
            assertNotEquals(a, c)
            assertNotEquals(a, d)
        }
    }

    @Nested
    @DisplayName("Location 엔티티 테스트")
    inner class LocationEntityTest {
        @Test
        @DisplayName("Location.of로 모든 필드를 지정해 생성할 수 있다")
        fun `should create location with all fields via factory`() {
            val coordinate = Coordinate(37.5665, 126.9780)
            val source = LocationSource.NAVER
            val sourceId = "12345"
            val name = "서울시청"

            val location =
                Location.of(
                    source = source,
                    sourceId = sourceId,
                    coordinate = coordinate,
                    name = name,
                )

            assertEquals(ExternalSource(source, sourceId), location.externalSource)
            assertEquals(coordinate, location.coordinate)
            assertEquals(name, location.name)
            assertEquals(0L, location.id)
        }

        @Test
        @DisplayName("USER 소스로 Location을 생성할 수 있다")
        fun `should create location with USER source`() {
            val location =
                Location.of(
                    source = LocationSource.USER,
                    sourceId = "custom_location",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "사용자 지정 위치",
                )

            assertEquals(LocationSource.USER, location.externalSource.source)
            assertEquals("custom_location", location.externalSource.sourceId)
        }

        @Test
        @DisplayName("Location의 좌표를 통해 다른 Location과의 거리를 계산할 수 있다")
        fun `should calculate distance between two locations`() {
            val location1 =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "서울시청",
                )
            val location2 =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "2",
                    coordinate = Coordinate(37.4979, 127.0276),
                    name = "강남역",
                )

            val distance = location1.coordinate.distanceTo(location2.coordinate)

            assertTrue(distance > 8500 && distance < 9500)
        }

        @Test
        @DisplayName("같은 id를 가진 영속 Location은 동등하다")
        fun `persisted locations with same id should be equal`() {
            val a =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )
            val b =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "2",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "B",
                )
            a.injectId(1L)
            b.injectId(1L)

            assertEquals(a, b)
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("transient(id=0) Location 두 개는 동일 인스턴스가 아니면 동등하지 않다")
        fun `transient locations are not equal unless same instance`() {
            val a =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )
            val b =
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )

            assertNotEquals(a, b)
        }

        @Test
        @DisplayName("sourceId가 공백이면 예외가 발생한다")
        fun `should throw when sourceId is blank`() {
            assertThrows<IllegalArgumentException> {
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = " ",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "이름",
                )
            }
        }

        @Test
        @DisplayName("name이 공백이면 예외가 발생한다")
        fun `should throw when name is blank`() {
            assertThrows<IllegalArgumentException> {
                Location.of(
                    source = LocationSource.NAVER,
                    sourceId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "",
                )
            }
        }
    }
}
