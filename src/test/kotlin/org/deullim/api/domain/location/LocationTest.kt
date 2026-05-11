package org.deullim.api.domain.location

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private fun ExternalLocation.injectId(id: Long) {
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
        @DisplayName("ExternalSource 생성 시 provider와 externalId가 저장된다")
        fun `should create external source with provider and externalId`() {
            val external = ExternalSource(ExternalProvider.NAVER, "12345")

            assertEquals(ExternalProvider.NAVER, external.provider)
            assertEquals("12345", external.externalId)
        }

        @Test
        @DisplayName("externalId가 공백이면 예외가 발생한다")
        fun `should throw when externalId is blank`() {
            assertThrows<IllegalArgumentException> {
                ExternalSource(ExternalProvider.NAVER, " ")
            }
            assertThrows<IllegalArgumentException> {
                ExternalSource(ExternalProvider.NAVER, "")
            }
        }

        @Test
        @DisplayName("같은 provider/externalId를 가지면 동등하다")
        fun `external sources with same values should be equal`() {
            val a = ExternalSource(ExternalProvider.NAVER, "1")
            val b = ExternalSource(ExternalProvider.NAVER, "1")
            val c = ExternalSource(ExternalProvider.NAVER, "2")

            assertEquals(a, b)
            assertNotEquals(a, c)
        }
    }

    @Nested
    @DisplayName("ExternalLocation 엔티티 테스트")
    inner class ExternalLocationTest {
        @Test
        @DisplayName("ExternalLocation.of로 모든 필드를 지정해 생성할 수 있다")
        fun `should create external location with all fields via factory`() {
            val coordinate = Coordinate(37.5665, 126.9780)
            val name = "서울시청"
            val externalSource = ExternalSource(ExternalProvider.NAVER, "12345")

            val location =
                ExternalLocation.of(
                    externalSource = externalSource,
                    coordinate = coordinate,
                    name = name,
                )

            assertEquals(externalSource, location.externalSource)
            assertEquals(coordinate, location.coordinate)
            assertEquals(name, location.name)
            assertEquals(0L, location.id)
        }

        @Test
        @DisplayName("provider/externalId 오버로드 팩토리도 동일하게 동작한다")
        fun `provider externalId overload factory should work the same`() {
            val location =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "12345",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "서울시청",
                )

            assertEquals(ExternalProvider.NAVER, location.externalSource.provider)
            assertEquals("12345", location.externalSource.externalId)
        }

        @Test
        @DisplayName("ExternalLocation은 모든 사용자에게 공개된다")
        fun `external location should be visible to all members`() {
            val location =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "서울시청",
                )

            assertTrue(location.isVisibleTo(memberId = 1L))
            assertTrue(location.isVisibleTo(memberId = 999L))
        }

        @Test
        @DisplayName("ExternalLocation의 좌표를 통해 다른 Location과의 거리를 계산할 수 있다")
        fun `should calculate distance between two locations`() {
            val location1 =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "서울시청",
                )
            val location2 =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "2",
                    coordinate = Coordinate(37.4979, 127.0276),
                    name = "강남역",
                )

            val distance = location1.coordinate.distanceTo(location2.coordinate)

            assertTrue(distance > 8500 && distance < 9500)
        }

        @Test
        @DisplayName("같은 id를 가진 영속 ExternalLocation은 동등하다")
        fun `persisted external locations with same id should be equal`() {
            val a =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )
            val b =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "2",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "B",
                )
            a.injectId(1L)
            b.injectId(1L)

            assertEquals<Location>(a, b)
            assertEquals(a.hashCode(), b.hashCode())
        }

        @Test
        @DisplayName("transient(id=0) ExternalLocation 두 개는 동일 인스턴스가 아니면 동등하지 않다")
        fun `transient external locations are not equal unless same instance`() {
            val a =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )
            val b =
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "A",
                )

            assertFalse(a == b)
        }

        @Test
        @DisplayName("externalId가 공백이면 예외가 발생한다")
        fun `should throw when externalId is blank`() {
            assertThrows<IllegalArgumentException> {
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = " ",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "이름",
                )
            }
        }

        @Test
        @DisplayName("name이 공백이면 예외가 발생한다")
        fun `should throw when name is blank`() {
            assertThrows<IllegalArgumentException> {
                ExternalLocation.of(
                    provider = ExternalProvider.NAVER,
                    externalId = "1",
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "",
                )
            }
        }
    }

    @Nested
    @DisplayName("UserLocation 엔티티 테스트")
    inner class UserLocationTest {
        @Test
        @DisplayName("UserLocation.of로 모든 필드를 지정해 생성할 수 있다")
        fun `should create user location with all fields via factory`() {
            val coordinate = Coordinate(37.5665, 126.9780)
            val memberId = 42L
            val name = "할머니댁"

            val location =
                UserLocation.of(
                    memberId = memberId,
                    coordinate = coordinate,
                    name = name,
                )

            assertEquals(memberId, location.memberId)
            assertEquals(coordinate, location.coordinate)
            assertEquals(name, location.name)
            assertEquals(0L, location.id)
        }

        @Test
        @DisplayName("UserLocation은 소유한 멤버에게만 공개된다")
        fun `user location should be visible only to owner`() {
            val location =
                UserLocation.of(
                    memberId = 1L,
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "내 위치",
                )

            assertTrue(location.isVisibleTo(memberId = 1L))
            assertFalse(location.isVisibleTo(memberId = 2L))
        }

        @Test
        @DisplayName("memberId가 0 이하이면 예외가 발생한다")
        fun `should throw when memberId is non-positive`() {
            assertThrows<IllegalArgumentException> {
                UserLocation.of(
                    memberId = 0L,
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "이름",
                )
            }
            assertThrows<IllegalArgumentException> {
                UserLocation.of(
                    memberId = -1L,
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "이름",
                )
            }
        }

        @Test
        @DisplayName("name이 공백이면 예외가 발생한다")
        fun `should throw when name is blank`() {
            assertThrows<IllegalArgumentException> {
                UserLocation.of(
                    memberId = 1L,
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "",
                )
            }
        }

        @Test
        @DisplayName("Location 타입으로도 polymorphic하게 다룰 수 있다")
        fun `should be usable polymorphically as Location`() {
            val location: Location =
                UserLocation.of(
                    memberId = 1L,
                    coordinate = Coordinate(37.5665, 126.9780),
                    name = "내 위치",
                )

            assertTrue(location.isVisibleTo(memberId = 1L))
            assertFalse(location.isVisibleTo(memberId = 2L))
        }
    }
}
