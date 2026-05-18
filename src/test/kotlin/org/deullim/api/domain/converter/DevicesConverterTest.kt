package org.deullim.api.domain.converter

import org.deullim.api.domain.Device
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DevicesConverterTest {
    private val converter = DevicesConverter()

    @Test
    @DisplayName("device 리스트를 JSON 문자열로 변환한다")
    fun `convert devices to column`() {
        val devices = listOf(Device(token = "tok-1", os = "ios"), Device(token = "tok-2", os = "android"))

        val json = converter.convertToDatabaseColumn(devices)

        assertEquals(devices, converter.convertToEntityAttribute(json))
    }

    @Test
    @DisplayName("빈 device 리스트를 JSON 문자열로 변환한다")
    fun `convert empty devices to column`() {
        assertEquals("[]", converter.convertToDatabaseColumn(emptyList()))
    }

    @Test
    @DisplayName("null device 입력을 빈 JSON 배열로 변환한다")
    fun `convert null devices to column`() {
        assertEquals("[]", converter.convertToDatabaseColumn(null))
    }

    @Test
    @DisplayName("JSON 문자열을 device 리스트로 변환한다")
    fun `convert column to devices`() {
        val devices = listOf(Device(token = "tok-1", os = "ios"), Device(token = "tok-2", os = "android"))
        val json = converter.convertToDatabaseColumn(devices)

        assertEquals(devices, converter.convertToEntityAttribute(json))
    }

    @Test
    @DisplayName("null 컬럼을 빈 리스트로 변환한다")
    fun `convert null column`() {
        assertEquals(emptyList(), converter.convertToEntityAttribute(null))
    }

    @Test
    @DisplayName("공백 컬럼을 빈 리스트로 변환한다")
    fun `convert blank column`() {
        assertEquals(emptyList(), converter.convertToEntityAttribute(""))
        assertEquals(emptyList(), converter.convertToEntityAttribute("   "))
    }
}
