package org.deullim.api.domain.converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.deullim.api.domain.Device
import kotlin.collections.mutableListOf

@Converter
class DevicesConverter : AttributeConverter<MutableList<Device>, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: MutableList<Device>?): String {
        return objectMapper.writeValueAsString(attribute ?: mutableListOf<Device>())
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<Device> {
        if (dbData.isNullOrBlank()) return mutableListOf()
        return objectMapper.readValue(dbData)
    }
}
