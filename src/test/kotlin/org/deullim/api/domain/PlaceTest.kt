package org.deullim.api.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class PlaceTest {
    private fun newMember(): Member = Member.create("nick", Device(token = "token", os = "ios"), SourceType.GUEST)

    @Test
    @DisplayName("Place를 생성한다")
    fun `create place`() {
        val member = newMember()

        val place = Place.create(member = member, alias = "home", icon = "house")

        assertSame(member, place.member)
        assertEquals("home", place.alias)
        assertEquals("house", place.icon)
    }

    @Test
    @DisplayName("alias와 icon을 생략하면 null이 된다")
    fun `create place without alias and icon`() {
        val place = Place.create(member = newMember())

        assertNull(place.alias)
        assertNull(place.icon)
    }

    @Test
    @DisplayName("alias를 변경한다")
    fun `change alias`() {
        val place = Place.create(member = newMember())

        place.changeAlias("  new ")

        assertEquals("new", place.alias)
    }

    @Test
    @DisplayName("변경할 alias가 공백이면 예외가 발생한다")
    fun `throw when blank alias`() {
        val place = Place.create(member = newMember())

        assertFailsWith<IllegalArgumentException> { place.changeAlias("   ") }
    }

    @Test
    @DisplayName("icon을 변경한다")
    fun `change icon`() {
        val place = Place.create(member = newMember())

        place.changeIcon("  bell ")

        assertEquals("bell", place.icon)
    }

    @Test
    @DisplayName("변경할 icon이 공백이면 예외가 발생한다")
    fun `throw when blank icon`() {
        val place = Place.create(member = newMember())

        assertFailsWith<IllegalArgumentException> { place.changeIcon(" ") }
    }
}
