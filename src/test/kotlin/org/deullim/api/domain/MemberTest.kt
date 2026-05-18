package org.deullim.api.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MemberTest {
    private val validDevice = Device(token = "token", os = "ios")

    private fun newMember(): Member = Member.create("nickname", validDevice, SourceType.GUEST)

    @Test
    @DisplayName("Member를 생성한다")
    fun `create member`() {
        val member = Member.create("nickname", validDevice, SourceType.APPLE)

        assertEquals("nickname", member.nickname)
        assertEquals(listOf(validDevice), member.devices)
        assertEquals(SourceType.APPLE, member.source)
        assertEquals(Status.ACTIVE, member.status)
    }

    @Test
    @DisplayName("nickname이 공백이면 예외가 발생한다")
    fun `throw when blank nickname`() {
        assertFailsWith<IllegalArgumentException> {
            Member.create("  ", validDevice, SourceType.GUEST)
        }
    }

    @Test
    @DisplayName("device 토큰이 공백이면 예외가 발생한다")
    fun `throw when blank token`() {
        assertFailsWith<IllegalArgumentException> {
            Member.create("nickname", Device(token = "", os = "ios"), SourceType.GUEST)
        }
    }

    @Test
    @DisplayName("device os가 공백이면 예외가 발생한다")
    fun `throw when blank os`() {
        assertFailsWith<IllegalArgumentException> {
            Member.create("nickname", Device(token = "token", os = ""), SourceType.GUEST)
        }
    }

    @Test
    @DisplayName("INACTIVE 상태에서 활성화하면 ACTIVE가 된다")
    fun `active member`() {
        val member = newMember()
        member.deactivate()

        member.activate()

        assertEquals(Status.ACTIVE, member.status)
    }

    @Test
    @DisplayName("이미 ACTIVE 상태면 활성화 시 예외가 발생한다")
    fun `throw when already active`() {
        val member = newMember()

        assertFailsWith<IllegalArgumentException> { member.activate() }
    }

    @Test
    @DisplayName("ACTIVE 상태에서 비활성화하면 INACTIVE가 된다")
    fun `deactive member`() {
        val member = newMember()

        member.deactivate()

        assertEquals(Status.INACTIVE, member.status)
    }

    @Test
    @DisplayName("이미 INACTIVE 상태면 비활성화 시 예외가 발생한다")
    fun `throw when already inactive`() {
        val member = newMember()
        member.deactivate()

        assertFailsWith<IllegalArgumentException> { member.deactivate() }
    }

    @Test
    @DisplayName("멤버를 삭제하면 DELETED가 된다")
    fun `delete member`() {
        val member = newMember()

        member.delete()

        assertEquals(Status.DELETED, member.status)
    }

    @Test
    @DisplayName("이미 DELETED 상태면 삭제 시 예외가 발생한다")
    fun `throw when already deleted`() {
        val member = newMember()
        member.delete()

        assertFailsWith<IllegalArgumentException> { member.delete() }
    }

    @Test
    @DisplayName("nickname을 변경한다")
    fun `change nickname`() {
        val member = newMember()

        member.changeNickname("new-nickname")

        assertEquals("new-nickname", member.nickname)
    }

    @Test
    @DisplayName("변경할 nickname이 공백이면 예외가 발생한다")
    fun `throw when blank nickname on change`() {
        val member = newMember()

        assertFailsWith<IllegalArgumentException> { member.changeNickname("  ") }
    }

    @Test
    @DisplayName("새 device를 추가한다")
    fun `add device`() {
        val member = newMember()
        val newDevice = Device(token = "token-2", os = "android")

        member.addDevice(newDevice)

        assertEquals(2, member.devices.size)
        assertTrue(member.devices.contains(validDevice))
        assertTrue(member.devices.contains(newDevice))
    }

    @Test
    @DisplayName("추가할 device 토큰이 공백이면 예외가 발생한다")
    fun `throw when blank token on add`() {
        val member = newMember()

        assertFailsWith<IllegalArgumentException> {
            member.addDevice(Device(token = "", os = "ios"))
        }
    }

    @Test
    @DisplayName("추가할 device os가 공백이면 예외가 발생한다")
    fun `throw when blank os on add`() {
        val member = newMember()

        assertFailsWith<IllegalArgumentException> {
            member.addDevice(Device(token = "token-2", os = ""))
        }
    }

    @Test
    @DisplayName("동일한 token과 os를 가진 device를 제거한다")
    fun `remove device`() {
        val member = newMember()
        val other = Device(token = "token", os = "android")
        member.addDevice(other)

        member.removeDevice(validDevice)

        assertEquals(listOf(other), member.devices)
    }

    @Test
    @DisplayName("매칭되는 device가 없으면 변경되지 않는다")
    fun `remove unknown device`() {
        val member = newMember()
        val before = member.devices

        member.removeDevice(Device(token = "unknown", os = "ios"))

        assertEquals(before, member.devices)
    }
}
