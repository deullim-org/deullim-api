package org.deullim.api.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SettingTest {
    private fun newMember(): Member = Member.create("nick", Device(token = "token", os = "ios"), SourceType.GUEST)

    @Test
    @DisplayName("radius가 null이면 기본값으로 생성된다")
    fun `create setting with default radius`() {
        val setting = Setting.create(member = newMember(), radius = null)

        assertEquals(Setting.DEFAULT_RADIUS_METER, setting.radius)
    }

    @Test
    @DisplayName("유효한 radius로 Setting을 생성한다")
    fun `create setting`() {
        val setting = Setting.create(member = newMember(), radius = 500.0)

        assertEquals(500.0, setting.radius)
    }

    @Test
    @DisplayName("radius가 최소값이면 생성된다")
    fun `create setting at min`() {
        val setting = Setting.create(member = newMember(), radius = Setting.MIN_RADIUS_METER)

        assertEquals(Setting.MIN_RADIUS_METER, setting.radius)
    }

    @Test
    @DisplayName("radius가 최대값이면 생성된다")
    fun `create setting at max`() {
        val setting = Setting.create(member = newMember(), radius = Setting.MAX_RADIUS_METER)

        assertEquals(Setting.MAX_RADIUS_METER, setting.radius)
    }

    @Test
    @DisplayName("radius가 최소값보다 작으면 예외가 발생한다")
    fun `throw when radius below min`() {
        assertFailsWith<IllegalArgumentException> {
            Setting.create(member = newMember(), radius = Setting.MIN_RADIUS_METER - 0.1)
        }
    }

    @Test
    @DisplayName("radius가 최대값보다 크면 예외가 발생한다")
    fun `throw when radius above max`() {
        assertFailsWith<IllegalArgumentException> {
            Setting.create(member = newMember(), radius = Setting.MAX_RADIUS_METER + 0.1)
        }
    }

    @Test
    @DisplayName("radius를 변경한다")
    fun `change radius`() {
        val setting = Setting.create(member = newMember(), radius = null)

        setting.changeRadius(300.0)

        assertEquals(300.0, setting.radius)
    }

    @Test
    @DisplayName("radius를 최소값으로 변경한다")
    fun `change radius at min`() {
        val setting = Setting.create(member = newMember(), radius = null)

        setting.changeRadius(Setting.MIN_RADIUS_METER)

        assertEquals(Setting.MIN_RADIUS_METER, setting.radius)
    }

    @Test
    @DisplayName("radius를 최대값으로 변경한다")
    fun `change radius at max`() {
        val setting = Setting.create(member = newMember(), radius = null)

        setting.changeRadius(Setting.MAX_RADIUS_METER)

        assertEquals(Setting.MAX_RADIUS_METER, setting.radius)
    }

    @Test
    @DisplayName("변경할 radius가 최소값보다 작으면 예외가 발생한다")
    fun `throw when radius below min on change`() {
        val setting = Setting.create(member = newMember(), radius = null)

        assertFailsWith<IllegalArgumentException> {
            setting.changeRadius(Setting.MIN_RADIUS_METER - 0.1)
        }
    }

    @Test
    @DisplayName("변경할 radius가 최대값보다 크면 예외가 발생한다")
    fun `throw when radius above max on change`() {
        val setting = Setting.create(member = newMember(), radius = null)

        assertFailsWith<IllegalArgumentException> {
            setting.changeRadius(Setting.MAX_RADIUS_METER + 0.1)
        }
    }
}
